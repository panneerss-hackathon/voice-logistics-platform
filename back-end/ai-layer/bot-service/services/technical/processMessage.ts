import * as Technical from '.';
import * as Business from '../business';
import { conversationState, userStateAccessor } from '../../state/storage';

export async function processMessage(userId: string, userText: string): Promise<{ textReply: string; audioReply: Buffer }> {
  const dummyContext = {
    activity: { type: 'message', text: userText, from: { id: userId } },
    sendActivity: async () => {},
    turnState: new Map()
  };
  dummyContext.turnState.set(conversationState, conversationState);

  const userState = await userStateAccessor.get(dummyContext as any, () => ({
    lastIntent: null,
    lastEntities: {},
    missingFields: []
  }));

  const intent = await Technical.detectIntent(userText);
  if (!intent || intent === 'Unknown') {
    const fallback = 'Sorry, I could not understand your request. Please rephrase.';
    const buffer = await Technical.synthesizeSpeech(fallback, true);
    return { textReply: fallback, audioReply: buffer as Buffer };
  }

  const entities = await Technical.extractEntities(userText);
  const missing = await Technical.checkMissingFields(intent, entities);

  userState.lastIntent = intent;
  userState.lastEntities = entities;
  userState.missingFields = missing;

  let textReply = '';

  if (missing.length > 0) {
    textReply = `Please provide the following details: ${missing.join(', ')}`;
  } else {
    switch (intent) {
      case 'CreateShipment':
        textReply = `Shipment created: ${await Business.createShipment(entities)}`;
        break;
      case 'TrackShipment':
        textReply = `Status: ${await Business.trackShipment(entities.trackingId)}`;
        break;
      case 'RescheduleDelivery':
        textReply = await Business.rescheduleDelivery(entities.shipmentId, entities.newDate);
        break;
      case 'ReportIssue':
        textReply = await Business.reportIssue(entities.shipmentId, entities.issueDescription);
        break;
      case 'ReturnShipment':
        textReply = await Business.returnShipment(entities.orderId, entities.reason);
        break;
      default:
        textReply = `Intent detected: ${intent}`;
    }
  }

  await conversationState.saveChanges(dummyContext as any);
  const audioReply = await Technical.synthesizeSpeech(textReply, true);

  return { textReply, audioReply: audioReply as Buffer };
}