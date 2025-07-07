const axios = require('axios');
module.exports = async function detectIntent(text) {
    const response = await axios.post('http://localhost:5002/intent', { text });
    return response.data.intent;
};