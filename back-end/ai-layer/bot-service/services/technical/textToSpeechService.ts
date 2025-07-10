import axios from 'axios';

const dummy = process.env.USE_MOCK_SERVICE === 'true';

export async function synthesizeSpeech(text: string, returnBuffer = false): Promise<Buffer | string> {
  if (dummy) {
    return returnBuffer ? Buffer.from('MOCK_AUDIO') : 'mock-audio-url.wav';
  }

  const response = await axios.post(
    'http://localhost:5005/speak',
    { text },
    { responseType: returnBuffer ? 'arraybuffer' : 'json' }
  );

  if (returnBuffer) {
    return Buffer.from(response.data); // ✅ Always a Buffer
  }

  return response.data.audioUrl;
}
