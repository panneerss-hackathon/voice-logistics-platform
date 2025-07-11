import 'dotenv/config';
import express from 'express';
import multer from 'multer';
import { adapter } from './bot/adapter';
import { bot } from './bot/bot';
import { registerConverseRoute } from './routes/converseRoute';

const app = express();
const upload = multer({ dest: 'uploads/' });

app.use(express.json());

// Mount routes
app.post('/api/messages', async (req, res) => {
  await adapter.process(req, res, async (context) => {
    await bot.run(context);
  });
});

registerConverseRoute(app, upload); // ✅ Unified text/voice route

const PORT = process.env.PORT ?? 3978;
app.listen(PORT, () => console.log(`🚀 Bot running on port ${PORT}`));