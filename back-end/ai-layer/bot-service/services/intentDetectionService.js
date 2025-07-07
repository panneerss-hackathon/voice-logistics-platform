const dummy = process.env.USE_MOCK_SERVICE === 'true';
module.exports = async function detectIntent(text) {
  if (dummy) return "CreateShipment";
  const axios = require('axios');
  const response = await axios.post('http://localhost:5002/intent', { text });
  return response.data.intent;
};
