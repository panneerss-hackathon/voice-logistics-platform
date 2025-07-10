import axios from 'axios';

const dummy = process.env.USE_MOCK_SERVICE === 'true';

export interface Entities {
  [key: string]: string;
}

export async function extractEntities(text: string): Promise<Entities> {
  if (dummy) return { from: 'Chennai', to: 'Bangalore', weight: '5kg' };

  const response = await axios.post('http://localhost:5003/entities', { text });
  return response.data.entities;
}
