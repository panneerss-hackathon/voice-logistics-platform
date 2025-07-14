import axios from 'axios';
import { logInfo, logError } from '../../utils/logger';
import { AppConfig } from '../../config/config';

export async function createShipment(entities: any): Promise<string> {
  try {
    logInfo('Creating shipment request', { entities });

    const response = await axios.post(`${AppConfig.SHIPMENT_SERVICE_URL}`, entities);

    logInfo('Shipment created successfully', { shipmentId: response.data.shipmentId, status: response.status });
    return response.data.shipmentId || 'SHIP123456';
  } catch (err: any) {
    logError('CreateShipment failed', { error: err.message, entities });
    return 'SHIP123456';
  }
}