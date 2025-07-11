import axios from 'axios';
import { logInfo, logError } from '../../utils/logger';
import { AppConfig } from '../../config/config';

export async function detectIntent(text: string): Promise<string> {
  if (AppConfig.USE_MOCK_SERVICE) {
    logInfo('🧪 Mock intent detection used.', { text });
    return 'CreateShipment';
  }

  try {
    const response = await axios.post(AppConfig.INTENT_DETECTOR_URL, { text });
    logInfo('✅ Intent detected', { text, intent: response.data.intent });
    return response.data.intent;
  } catch (err: any) {
    logError('❌ Intent detection failed', { text, error: err.message });
    return 'Unknown';
  }
}