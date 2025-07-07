const dummy = process.env.USE_MOCK_SERVICE === 'true';
module.exports = async function checkMissingFields(intent, entities) {
  if (dummy) return ['pickupDate'];
  const axios = require('axios');
  const response = await axios.post('http://localhost:5004/missing-fields', { intent, entities });
  return response.data.missing || [];
};
