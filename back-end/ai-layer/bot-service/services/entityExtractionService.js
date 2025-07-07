const dummy = process.env.USE_MOCK_SERVICE === 'true';
module.exports = async function extractEntities(text) {
  if (dummy) return { from: "Chennai", to: "Bangalore", weight: "5kg" };
  const axios = require('axios');
  const response = await axios.post('http://localhost:5003/entities', { text });
  return response.data.entities;
};
