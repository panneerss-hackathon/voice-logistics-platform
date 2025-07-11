import axios from 'axios';
import { logInfo, logError } from '../../utils/logger';

export async function trackShipment(trackingId: string): Promise<string> {
  try {
    logInfo('Tracking shipment', { trackingId });

    const response = await axios.get(`http://localhost:6002/api/track/${trackingId}`);

    logInfo('Tracking info fetched', { trackingId, status: response.data.status });
    return response.data.status || 'In Transit';
  } catch (err: any) {
    logError('TrackShipment failed', { trackingId, error: err.message });
    return 'Unknown';
  }
}
