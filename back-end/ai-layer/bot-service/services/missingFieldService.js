const axios = require('axios');
module.exports = async function checkMissingFields(intent, entities) {
    const response = await axios.post('http://localhost:5004/missing-fields', { intent, entities });
    return response.data.missing || [];
};