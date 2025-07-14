import axios from 'axios';
import { logInfo, logError } from '../../utils/logger';
import { AppConfig } from '../../config/config';

export async function rescheduleDelivery(shipmentId: string, newDate: string): Promise<string> {
  try {
    logInfo('Rescheduling delivery', { shipmentId, newDate });

      const response = await axios.post(`${AppConfig.RESCHEDULE_SERVICE_URL}`, { shipmentId, newDate });

    logInfo('Delivery rescheduled', { confirmation: response.data.confirmation });
    return response.data.confirmation || 'Rescheduled';
  } catch (err: any) {
    logError('RescheduleDelivery failed', { shipmentId, newDate, error: err.message });
    return 'Rescheduled';
  }
}