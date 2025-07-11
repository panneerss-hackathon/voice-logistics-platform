import axios from 'axios';
import { logInfo, logError } from '../../utils/logger';

export async function returnShipment(orderId: string, reason: string): Promise<string> {
  try {
    logInfo('Initiating return shipment', { orderId, reason });

    const response = await axios.post('http://localhost:6005/api/returns', { orderId, reason });

    logInfo('Return initiated', { returnId: response.data.returnId });
    return response.data.returnId || 'RETURN001';
  } catch (err: any) {
    logError('ReturnShipment failed', { orderId, error: err.message });
    return 'RETURN001';
  }
}
