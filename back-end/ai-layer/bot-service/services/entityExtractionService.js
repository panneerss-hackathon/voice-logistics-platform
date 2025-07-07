const axios = require('axios');
module.exports = async function extractEntities(text) {
    const response = await axios.post('http://localhost:5003/entities', { text });
    return response.data.entities;
};