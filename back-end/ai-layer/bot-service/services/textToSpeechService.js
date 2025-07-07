const axios = require('axios');
module.exports = async function synthesizeSpeech(text, returnBuffer = false) {
    try {
        const response = await axios.post('http://localhost:5005/speak', { text }, { responseType: returnBuffer ? 'arraybuffer' : 'json' });
        return returnBuffer ? Buffer.from(response.data) : response.data.audioUrl;
    } catch (err) {
        console.error('TTS error:', err.message);
        return returnBuffer ? Buffer.from('') : null;
    }
};