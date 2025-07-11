import express from 'express';
import fs from 'fs';
import path from 'path';
import FormData from 'form-data';
import axios from 'axios';

import { processMessage } from '../services/technical/processMessage';
import { sendMultipartResponse } from '../utils/responseBuilder';
import { logInfo, logError } from '../utils/logger';

export function registerConverseRoute(app: express.Express, upload: any) {
  app.post('/api/converse', upload.single('audio'), async (req, res) => {
    const userId = req.headers['x-user-id']?.toString() || 'default-user';
    logInfo('Incoming /api/converse request', {
      userId,
      hasAudio: !!req.file,
      hasText: !!req.body?.text
    });

    try {
      const isAudio = req.is('multipart/form-data');
      let userText = '';

      if (isAudio && req.file) {
        const allowedTypes = ['audio/m4a', 'audio/x-m4a', 'audio/mp3', 'audio/mpeg', 'audio/wav'];
        if (!allowedTypes.includes(req.file.mimetype)) {
          logError('Unsupported audio format', { userId, mimetype: req.file.mimetype });
          return res.status(400).send('Unsupported audio format.');
        }

        try {
          const formData = new FormData();
          formData.append('audio', fs.createReadStream(path.resolve(req.file.path)));

          const sttResponse = await axios.post('http://localhost:5001/transcribe', formData, {
            headers: formData.getHeaders(),
            timeout: 5000
          });

          userText = sttResponse.data.text;
          logInfo('STT successful', { userId, userText });
        } catch (err: any) {
          logError('STT failed, falling back to typed text', { userId, error: err.message });
        }
      }

      if (req.body?.text) {
        userText = req.body.text;
        logInfo('Received typed text', { userId, userText });
      }

      if (!userText?.trim()) {
        const fallback = 'Sorry, I could not hear anything.';
        const { textReply, audioReply } = await processMessage(userId, fallback);
        if (typeof audioReply === 'string') {
          return res.json({ text: textReply, audioUrl: audioReply });
        }
        return sendMultipartResponse(res, textReply, audioReply);
      }

      const { textReply, audioReply } = await processMessage(userId, userText);
      if (typeof audioReply === 'string') {
        return res.json({ text: textReply, audioUrl: audioReply });
      }
      return sendMultipartResponse(res, textReply, audioReply);
    } catch (err: any) {
      logError('Error in /api/converse', { userId, error: err.message });
      const fallback = 'Something went wrong. Please try again later.';
      const { textReply, audioReply } = await processMessage(userId, fallback);
      if (typeof audioReply === 'string') {
        return res.json({ text: textReply, audioUrl: audioReply });
      }
      return sendMultipartResponse(res, textReply, audioReply);
    } finally {
      if (req.file?.path) {
        fs.unlink(req.file.path, (err) => {
          if (err) logError('Failed to delete temp file', { path: req.file?.path, error: err.message });
        });
      }
    }
  });
}
