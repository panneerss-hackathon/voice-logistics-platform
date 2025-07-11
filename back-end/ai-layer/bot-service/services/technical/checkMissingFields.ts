import axios from 'axios';
import { logInfo, logError } from '../../utils/logger';

const dummy = process.env.USE_MOCK_SERVICE === 'true';

export async function checkMissingFields(intent: string, entities: Record<string, any>): Promise<string[]> {
  if (dummy) {
    logInfo('🧪 Mock missing field detection used', { intent, entities });
    return ['pickupDate'];
  }

  try {
    const response = await axios.post('http://localhost:5004/missing-fields', { intent, entities });
    logInfo('✅ Missing fields checked', { intent, missing: response.data.missing });
    return response.data.missing || [];
  } catch (err: any) {
    logError('❌ Missing field check failed', { intent, error: err.message });
    return [];
  }
}
