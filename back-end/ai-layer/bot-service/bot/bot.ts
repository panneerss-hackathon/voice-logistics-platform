import { ActivityHandler, TurnContext } from 'botbuilder';
import { conversationState, userStateAccessor } from '../state/storage';
import * as Technical from '../services/technical';
import * as Business from '../services/business';

export class CustomVoiceBot extends ActivityHandler {
  constructor() {
    super();

    this.onMessage(async (context: TurnContext, next) => {
      const userText = context.activity.text;
      const userState = await userStateAccessor.get(context, () => ({
        lastIntent: null,
        lastEntities: {},
        missingFields: []
      }));

      if (!userText?.trim()) {
        await context.sendActivity('Sorry, I could not hear anything.');
        return await next();
      }

      const intent = await Technical.detectIntent(userText);
      if (!intent || intent === 'Unknown') {
        await context.sendActivity('Sorry, I could not understand your request.');
        return await next();
      }

      const entities = await Technical.extractEntities(userText);
      const missing = await Technical.checkMissingFields(intent, entities);

      userState.lastIntent = intent;
      userState.lastEntities = entities;
      userState.missingFields = missing;

      let reply = '';

      if (missing.length > 0) {
        reply = `Please provide the following details: ${missing.join(', ')}`;
      } else {
        switch (intent) {
          case 'CreateShipment':
            reply = `Shipment created: ${await Business.createShipment(entities)}`;
            break;
          case 'TrackShipment':
            reply = `Status: ${await Business.trackShipment(entities.trackingId)}`;
            break;
          case 'RescheduleDelivery':
            reply = await Business.rescheduleDelivery(entities.shipmentId, entities.newDate);
            break;
          case 'ReportIssue':
            reply = await Business.reportIssue(entities.shipmentId, entities.issueDescription);
            break;
          case 'ReturnShipment':
            reply = await Business.returnShipment(entities.orderId, entities.reason);
            break;
          default:
            reply = `Intent: ${intent}`;
        }
      }

      await context.sendActivity(reply);
      await conversationState.saveChanges(context);
      await next();
    });

    this.onMembersAdded(async (context, next) => {
      await context.sendActivity('Welcome! I am your voice + text assistant.');
      await next();
    });
  }
}

export const bot = new CustomVoiceBot();
