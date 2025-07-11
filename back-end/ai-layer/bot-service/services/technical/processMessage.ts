import * as Technical from '.';
import * as Business from '../business';
import { conversationState, userStateAccessor } from '../../state/storage';
import { Messages } from '../../utils/messages';
import { isYes, isNo } from '../../utils/confirmationUtils';
import { stripEmojis } from '../../utils/ttsUtils';

export async function processMessage(userId: string, userText: string): Promise<{ textReply: string; audioReply: Buffer }> {
  const dummyContext = {
    activity: {
      type: 'message',
      text: userText,
      from: { id: userId },
      channelId: 'custom-channel', // ✅ Required for ConversationState
      conversation: { id: `conv-${userId}` } // optional but helpful
    },
    sendActivity: async () => {},
    turnState: new Map()
  };
  dummyContext.turnState.set(conversationState, conversationState);

  const userState = await userStateAccessor.get(dummyContext as any, () => ({
    lastIntent: null,
    lastEntities: {},
    missingFields: [],
    awaitingConfirmation: false
  }));

  if (userState.awaitingConfirmation) {
    const confirmIntent = await Technical.detectIntent(userText);
    const confirmation = userText.trim().toLowerCase();
    let textReply = '';

    const isConfirmed = confirmIntent === 'ConfirmIntent' || isYes(confirmation);
    const isCancelled = confirmIntent === 'CancelIntent' || isNo(confirmation);

    if (isConfirmed) {
      switch (userState.lastIntent) {
        case 'CreateShipment':
          textReply = `✅ Shipment created: ${await Business.createShipment(userState.lastEntities)}`;
          break;
        case 'ReturnShipment':
          textReply = `🔁 Return created: ${await Business.returnShipment(userState.lastEntities.orderId, userState.lastEntities.reason)}`;
          break;
        case 'RescheduleDelivery':
          textReply = `📅 Rescheduled: ${await Business.rescheduleDelivery(userState.lastEntities.shipmentId, userState.lastEntities.newDate)}`;
          break;
        default:
          textReply = Messages.Confirmed.default;
      }
      userState.awaitingConfirmation = false;
    } else if (isCancelled) {
      textReply = Messages.Cancellations.default;
      userState.awaitingConfirmation = false;
    } else {
      textReply = Messages.UnknownConfirm;
    }

    await conversationState.saveChanges(dummyContext as any);
    const spokenText = stripEmojis(textReply);
    const audioReply = await Technical.synthesizeSpeech(spokenText, true);
    return { textReply, audioReply: audioReply as Buffer };
  }

  const intent = await Technical.detectIntent(userText);
  if (!intent || intent === 'Unknown') {
    const fallback = Messages.Fallback;
    const buffer = await Technical.synthesizeSpeech(stripEmojis(fallback), true);
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
        textReply = Messages.Confirmations.CreateShipment(entities);
        userState.awaitingConfirmation = true;
        break;
      case 'ReturnShipment':
        textReply = Messages.Confirmations.ReturnShipment(entities);
        userState.awaitingConfirmation = true;
        break;
      case 'RescheduleDelivery':
        textReply = Messages.Confirmations.RescheduleDelivery(entities);
        userState.awaitingConfirmation = true;
        break;
      case 'TrackShipment':
        textReply = `📦 Status: ${await Business.trackShipment(entities.trackingId)}`;
        break;
      case 'ReportIssue':
        textReply = await Business.reportIssue(entities.shipmentId, entities.issueDescription);
        break;
      default:
        textReply = `Intent detected: ${intent}`;
    }
  }

  await conversationState.saveChanges(dummyContext as any);
  const spokenText = stripEmojis(textReply);
  const audioReply = await Technical.synthesizeSpeech(spokenText, true);
  return { textReply, audioReply: audioReply as Buffer };
}