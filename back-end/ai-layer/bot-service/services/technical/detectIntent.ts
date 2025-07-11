import axios from 'axios';
import { logInfo, logError } from '../../utils/logger';

const dummy = process.env.USE_MOCK_SERVICE === 'true';

export async function detectIntent(text: string): Promise<string> {
  if (dummy) {
    logInfo('🧪 Mock intent detection used.', { text });
    return 'CreateShipment';
  }

  try {
    const response = await axios.post('http://localhost:5002/intent', { text });
    logInfo('✅ Intent detected', { text, intent: response.data.intent });
    return response.data.intent;
  } catch (err: any) {
    logError('❌ Intent detection failed', { text, error: err.message });
    return 'Unknown';
  }
}