import express from 'express';

export function sendMultipartResponse(res: express.Response, text: string, audioBuffer: Buffer) {
  res.setHeader('Content-Type', 'multipart/form-data; boundary=VOICE-REPLY');

  return res.end(Buffer.concat([
    Buffer.from(`--VOICE-REPLY
Content-Disposition: form-data; name="text"

${text}
--VOICE-REPLY
Content-Disposition: form-data; name="audio"; filename="reply.wav"
Content-Type: audio/wav

`),
    audioBuffer,
    Buffer.from('\r\n--VOICE-REPLY--')
  ]));
}
