import axios from 'axios';

const dummy = process.env.USE_MOCK_SERVICE === 'true';

export async function checkMissingFields(intent: string, entities: Record<string, any>): Promise<string[]> {
  if (dummy) return ['pickupDate'];

  const response = await axios.post('http://localhost:5004/missing-fields', { intent, entities });
  return response.data.missing || [];
}
