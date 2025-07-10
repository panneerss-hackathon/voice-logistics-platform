import express from 'express';
import fs from 'fs';
import path from 'path';
import FormData from 'form-data';
import axios from 'axios';

import { processMessage } from '../services/technical/processMessage';
import { sendMultipartResponse } from '../utils/responseBuilder';

export function registerConverseRoute(app: express.Express, upload: any) {
  app.post('/api/converse', upload.single('audio'), async (req, res) => {
    try {
      const isAudio = req.is('multipart/form-data');
      const userId = req.headers['x-user-id']?.toString() || 'default-user';
      let userText = '';

      // 🎤 Handle speech input
      if (isAudio && req.file) {
        const formData = new FormData();
        formData.append('audio', fs.createReadStream(path.resolve(req.file.path)));
        const sttResponse = await axios.post('http://localhost:5001/transcribe', formData, {
          headers: formData.getHeaders()
        });
        userText = sttResponse.data.text;
      }

      // 💬 Handle typed input
      if (req.body?.text) {
        userText = req.body.text;
      }

      if (!userText || userText.trim().length === 0) {
        const fallback = 'Sorry, I could not hear anything.';
        const { textReply, audioReply } = await processMessage(userId, fallback);
        return sendMultipartResponse(res, textReply, audioReply);
      }

      const { textReply, audioReply } = await processMessage(userId, userText);
      return sendMultipartResponse(res, textReply, audioReply);
    } catch (err: any) {
      console.error('❌ Error in /api/converse:', err.message);
      const fallback = 'Something went wrong. Please try again later.';
      const { textReply, audioReply } = await processMessage('system', fallback);
      return sendMultipartResponse(res, textReply, audioReply);
    } finally {
      if (req.file?.path) fs.unlink(req.file.path, () => {});
    }
  });
}
