import axios from 'axios';
import { logInfo, logError } from '../../utils/logger';
import { AppConfig } from '../../config/config';

export async function reportIssue(shipmentId: string, issueDescription: string): Promise<string> {
  try {
    logInfo('Reporting issue', { shipmentId, issueDescription });

    const response = await axios.post(`${AppConfig.ISSUE_SERVICE_URL}`, { shipmentId, issueDescription });

    logInfo('Issue reported', { issueId: response.data.issueId });
    return response.data.issueId || 'ISSUE001';
  } catch (err: any) {
    logError('ReportIssue failed', { shipmentId, error: err.message });
    return 'ISSUE001';
  }
}
