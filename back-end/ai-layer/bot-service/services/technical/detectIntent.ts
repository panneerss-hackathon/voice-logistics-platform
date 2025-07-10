import axios from 'axios';

const dummy = process.env.USE_MOCK_SERVICE === 'true';

export async function detectIntent(text: string): Promise<string> {
  if (dummy) return 'CreateShipment';

  const response = await axios.post('http://localhost:5002/intent', { text });
  return response.data.intent;
}
