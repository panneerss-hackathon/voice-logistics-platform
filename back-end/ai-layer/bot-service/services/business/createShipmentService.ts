import axios from 'axios';
import { logInfo, logError } from '../utils/logger';

export async function createShipment(entities: any): Promise<string> {
  try {
    const response = await axios.post('http://localhost:6001/api/shipments', entities);
    logInfo('Shipment created successfully');
    return response.data.shipmentId || 'SHIP123456';
  } catch (err: any) {
    logError('CreateShipment failed: ' + err.message);
    return 'SHIP123456';
  }
}
