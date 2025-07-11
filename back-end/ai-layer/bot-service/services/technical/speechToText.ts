import axios from 'axios';
import fs from 'fs';
import path from 'path';
import FormData from 'form-data';
import { AppConfig } from '../../config/config';
import { logInfo, logError } from '../../utils/logger';

export async function convertSpeechToText(audioPath: string, userId: string): Promise<string> {
  try {
    const formData = new FormData();
    formData.append('audio', fs.createReadStream(path.resolve(audioPath)));

    const response = await axios.post(AppConfig.STT_URL, formData, {
      headers: formData.getHeaders(),
      timeout: AppConfig.STT_TIMEOUT
    });

    logInfo('✅ STT successful', { userId, text: response.data.text });
    return response.data.text;
  } catch (err: any) {
    logError('❌ STT failed', { userId, error: err.message });
    return '';
  }
}
