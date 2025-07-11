import axios from 'axios';
import { logInfo, logError } from '../../utils/logger';

const dummy = process.env.USE_MOCK_SERVICE === 'true'; // ✅ Add this

export async function synthesizeSpeech(text: string, returnBuffer = false): Promise<Buffer | string> {
  if (dummy) {
    logInfo('🧪 Mock TTS synthesis used', { text, returnBuffer });
    return returnBuffer ? Buffer.from('MOCK_AUDIO') : 'mock-audio-url.mp3';
  }

  try {
    const response = await axios.post(
      'http://localhost:5005/speak',
      { text },
      { responseType: returnBuffer ? 'arraybuffer' : 'json' }
    );

    const logPayload = {
      text,
      format: returnBuffer ? 'Buffer (MP3)' : 'audio URL',
      length: returnBuffer ? (response.data.byteLength || response.data.length) : undefined,
    };

    logInfo('✅ TTS synthesis completed', logPayload);

    if (returnBuffer) {
      const buf = Buffer.isBuffer(response.data)
        ? response.data
        : Buffer.from(response.data); // ✅ Enforce Buffer
      return buf;
    } else {
      return response.data.audioUrl as string;
    }
  } catch (err: any) {
    logError('❌ TTS synthesis failed', { text, error: err.message });
    return returnBuffer ? Buffer.from('') : '';
  }
}
