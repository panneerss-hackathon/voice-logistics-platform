const dummy = process.env.USE_MOCK_SERVICE === 'true';
module.exports = async function synthesizeSpeech(text, returnBuffer = false) {
  if (dummy) return returnBuffer ? Buffer.from("MOCK_AUDIO") : "mock-audio-url.wav";
  const axios = require('axios');
  const response = await axios.post('http://localhost:5005/speak', { text }, {
    responseType: returnBuffer ? 'arraybuffer' : 'json'
  });
  return returnBuffer ? Buffer.from(response.data) : response.data.audioUrl;
};
