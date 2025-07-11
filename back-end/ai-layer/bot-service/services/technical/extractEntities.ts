import axios from 'axios';
import { logInfo, logError } from '../../utils/logger';
import { AppConfig } from '../../config/config';

const dummy = process.env.USE_MOCK_SERVICE === 'true';

export interface Entities {
  [key: string]: string;
}

export async function extractEntities(text: string): Promise<Entities> {
  if (AppConfig.USE_MOCK_SERVICE) {
    logInfo('🧪 Mock entity extraction used', { text });
    return { from: 'Chennai', to: 'Bangalore', weight: '5kg' };
  }

  try {
    const response = await axios.post(AppConfig.ENTITY_EXTRACTOR_URL, { text });
    logInfo('✅ Entities extracted', { text, entities: response.data.entities });
    return response.data.entities;
  } catch (err: any) {
    logError('❌ Entity extraction failed', { text, error: err.message });
    return {};
  }
}
