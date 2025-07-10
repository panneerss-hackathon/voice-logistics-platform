import axios from 'axios';
import { logInfo, logError } from '../utils/logger';

export async function reportIssue(shipmentId: string, issueDescription: string): Promise<string> {
  try {
    const response = await axios.post('http://localhost:6004/api/issues', { shipmentId, issueDescription });
    logInfo('Issue reported');
    return response.data.issueId || 'ISSUE001';
  } catch (err: any) {
    logError('ReportIssue failed: ' + err.message);
    return 'ISSUE001';
  }
}
