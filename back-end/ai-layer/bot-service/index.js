require('dotenv').config();
const fs = require('fs');
const path = require('path');
const express = require('express');
const multer = require('multer');
const axios = require('axios');
const FormData = require('form-data');
const {
    ActivityHandler,
    MemoryStorage,
    ConversationState,
    ConfigurationServiceClientCredentialFactory,
    createBotFrameworkAuthenticationFromConfiguration,
    CloudAdapter
} = require('botbuilder');
const { CosmosDbPartitionedStorage } = require('botbuilder-azure');

const detectIntent = require('./services/intentDetectionService');
const extractEntities = require('./services/entityExtractionService');
const checkMissingFields = require('./services/missingFieldService');
const synthesizeSpeech = require('./services/textToSpeechService');

const app = express();
const upload = multer({ dest: 'uploads/' });

// Adapter setup
const credentialsFactory = new ConfigurationServiceClientCredentialFactory({
    MicrosoftAppId: process.env.MICROSOFT_APP_ID || '',
    MicrosoftAppPassword: process.env.MICROSOFT_APP_PASSWORD || '',
    MicrosoftAppType: 'MultiTenant'
});
const botFrameworkAuth = createBotFrameworkAuthenticationFromConfiguration(null, credentialsFactory);
const adapter = new CloudAdapter(botFrameworkAuth);

// Error handling
adapter.onTurnError = async (context, error) => {
    console.error(`[onTurnError] ${error}`);
    await context.sendActivity('The bot encountered an error.');
};

// State storage
const useMockDb = process.env.USE_MOCK_DB === 'true';
const storage = useMockDb
    ? new MemoryStorage()
    : new CosmosDbPartitionedStorage({
        cosmosDbEndpoint: process.env.COSMOS_DB_ENDPOINT,
        authKey: process.env.COSMOS_DB_KEY,
        databaseId: process.env.COSMOS_DB_DATABASE,
        containerId: process.env.COSMOS_DB_CONTAINER
    });

const conversationState = new ConversationState(storage);
const userStateAccessor = conversationState.createProperty('UserConversationState');

// Bot logic
class CustomVoiceBot extends ActivityHandler {
    constructor() {
        super();
        this.onMessage(async (context, next) => {
            const userText = context.activity.text;
            const userState = await userStateAccessor.get(context, () => ({
                lastIntent: null,
                lastEntities: {},
                missingFields: []
            }));

            if (!userText || userText.trim().length === 0) {
                await context.sendActivity('Sorry, I could not hear anything. Please try again.');
                return await next();
            }

            const intent = await detectIntent(userText);
            if (!intent || intent === 'Unknown') {
                await context.sendActivity('Sorry, I could not understand your request. Please rephrase.');
                return await next();
            }

            const entities = await extractEntities(userText);
            const missing = await checkMissingFields(intent, entities);

            userState.lastIntent = intent;
            userState.lastEntities = entities;
            userState.missingFields = missing;

            const reply = missing.length > 0
                ? `Please provide the following details: ${missing.join(', ')}`
                : `Got it. You want to ${intent}. Details: ${JSON.stringify(entities)}`;

            try {
                await context.sendActivity(reply);
            } catch (err) {
                console.warn("⚠️ Failed to send activity:", err.message);
            }

            await conversationState.saveChanges(context);
            await next();
        });

        this.onMembersAdded(async (context, next) => {
            await context.sendActivity('Welcome! I am your smart assistant. Please speak or type your request.');
            await next();
        });
    }
}

const bot = new CustomVoiceBot();

app.use(express.json());

app.post('/api/messages', async (req, res) => {
    await adapter.process(req, res, async (context) => {
        await bot.run(context);
    });
});

app.post('/api/audio', upload.single('audio'), async (req, res) => {
    try {
        const audioPath = path.resolve(req.file.path);
        const formData = new FormData();
        formData.append('audio', fs.createReadStream(audioPath));

        const sttResponse = useMockDb
            ? { data: { text: 'Mock user input text' } }
            : await axios.post('http://localhost:5001/transcribe', formData, {
                  headers: formData.getHeaders()
              });

        const userText = sttResponse.data.text;
        const userId = req.headers['x-user-id'] || 'default-user';

        const dummyContext = {
            activity: { type: 'message', text: userText, from: { id: userId } },
            sendActivity: async () => {},
            turnState: new Map()
        };
        dummyContext.turnState.set(conversationState, conversationState);

        const userState = await userStateAccessor.get(dummyContext, () => ({
            lastIntent: null,
            lastEntities: {},
            missingFields: []
        }));

        const intent = await detectIntent(userText);
        if (!intent || intent === 'Unknown') {
            const fallback = 'Sorry, I could not understand your request. Please rephrase.';
            const fallbackBuffer = await synthesizeSpeech(fallback, true);
            res.setHeader('Content-Type', 'multipart/form-data; boundary=VOICE-REPLY');
            return res.end(Buffer.concat([Buffer.from(`--VOICE-REPLY
Content-Disposition: form-data; name="text"

${fallback}
--VOICE-REPLY
Content-Disposition: form-data; name="audio"; filename="reply.wav"
Content-Type: audio/wav

`), fallbackBuffer, Buffer.from('--VOICE-REPLY--')]));
        }

        const entities = await extractEntities(userText);
        const missing = await checkMissingFields(intent, entities);

        userState.lastIntent = intent;
        userState.lastEntities = entities;
        userState.missingFields = missing;

        const reply = missing.length > 0
            ? `Please provide the following details: ${missing.join(', ')}`
            : `Got it. You want to ${intent}. Details: ${JSON.stringify(entities)}`;

        await conversationState.saveChanges(dummyContext);
        const audioBuffer = await synthesizeSpeech(reply, true);

        res.setHeader('Content-Type', 'multipart/form-data; boundary=VOICE-REPLY');
        res.end(Buffer.concat([Buffer.from(`--VOICE-REPLY
Content-Disposition: form-data; name="text"

${reply}
--VOICE-REPLY
Content-Disposition: form-data; name="audio"; filename="reply.wav"
Content-Type: audio/wav

`), audioBuffer, Buffer.from('--VOICE-REPLY--')]));
    } catch (err) {
        console.error(err);
        const fallback = 'Something went wrong while processing your request. Please try again later.';
        const fallbackBuffer = await synthesizeSpeech(fallback, true);
        res.setHeader('Content-Type', 'multipart/form-data; boundary=VOICE-REPLY');
        res.end(Buffer.concat([Buffer.from(`--VOICE-REPLY
Content-Disposition: form-data; name="text"

${fallback}
--VOICE-REPLY
Content-Disposition: form-data; name="audio"; filename="reply.wav"
Content-Type: audio/wav

`), fallbackBuffer, Buffer.from('--VOICE-REPLY--')]));
    } finally {
        fs.unlink(req.file.path, () => {});
    }
});

const PORT = process.env.PORT || 3978;
app.listen(PORT, () => console.log(`🚀 Future-proof Bot running on port ${PORT}`));
