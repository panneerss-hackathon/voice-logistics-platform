import axios from 'axios';
import { logInfo, logError } from '../../utils/logger';
import { AppConfig } from '../../config/config';

export async function checkMissingFields(intent: string, entities: Record<string, any>): Promise<string[]> {
  if (AppConfig.USE_MOCK_SERVICE) {
    logInfo('🧪 Mock missing field detection used', { intent, entities });
    return ['pickupDate'];
  }

  try {
    const response = await axios.post(AppConfig.MISSING_FIELD_URL, { intent, entities });
    logInfo('✅ Missing fields checked', { intent, missing: response.data.missing });
    return response.data.missing || [];
  } catch (err: any) {
    logError('❌ Missing field check failed', { intent, error: err.message });
    return [];
  }
}
