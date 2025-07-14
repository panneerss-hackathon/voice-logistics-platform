import axios from 'axios';
import { logInfo, logError } from '../../utils/logger';
import { AppConfig } from '../../config/config';

export async function trackShipment(trackingId: string): Promise<string> {
  try {
    logInfo('Tracking shipment', { trackingId });

    const response = await axios.get(`${AppConfig.TRACK_SERVICE_URL}/${trackingId}`);

    logInfo('Tracking info fetched', { trackingId, status: response.data.status });
    return response.data.status || 'In Transit';
  } catch (err: any) {
    logError('TrackShipment failed', { trackingId, error: err.message });
    return 'Unknown';
  }
}
