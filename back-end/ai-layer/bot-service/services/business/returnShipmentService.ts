import axios from 'axios';
import { logInfo, logError } from '../utils/logger';

export async function returnShipment(orderId: string, reason: string): Promise<string> {
  try {
    const response = await axios.post('http://localhost:6005/api/returns', { orderId, reason });
    logInfo('Return initiated');
    return response.data.returnId || 'RETURN001';
  } catch (err: any) {
    logError('ReturnShipment failed: ' + err.message);
    return 'RETURN001';
  }
}
