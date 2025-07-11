import React, { useState, useRef } from 'react';
import {
  View,
  Text,
  TextInput,
  ScrollView,
  StyleSheet,
  TouchableOpacity,
  PermissionsAndroid,
  Platform,
  Alert,
} from 'react-native';
import AudioRecorderPlayer from 'react-native-audio-recorder-player';
import RNFS from 'react-native-fs';

export default function App() {
  const [messages, setMessages] = useState([
    { id: 1, type: 'assistant', text: "Hi! I'm your UPS Assistant. How can I help you today?" },
  ]);
  const [input, setInput] = useState('');
  const [recording, setRecording] = useState(false);
  const audioRecorderPlayer = useRef(new AudioRecorderPlayer()).current;
  const [audioPath, setAudioPath] = useState('');

  const getAudioPath = () => {
    const fileName = `test_1.m4a`;
    const path = `${RNFS.DownloadDirectoryPath}/${fileName}`;
    setAudioPath(path);
    return path;
  };

  const requestPermissions = async () => {
    if (Platform.OS === 'android') {
      try {
        const granted = await PermissionsAndroid.requestMultiple([
          PermissionsAndroid.PERMISSIONS.RECORD_AUDIO,
          PermissionsAndroid.PERMISSIONS.READ_MEDIA_AUDIO,
        ]);
        return Object.values(granted).every(
          (status) => status === PermissionsAndroid.RESULTS.GRANTED
        );
      } catch (err) {
        console.warn('Permission error:', err);
        return false;
      }
    } else {
      return true;
    }
  };

  const startRecording = async () => {
    const hasPermission = await requestPermissions();
    if (!hasPermission) {
      Alert.alert('Permission Denied', 'Cannot record without permissions.');
      return;
    }

    const path = getAudioPath();
    console.log('Recording to public file:', path);

    try {
      await audioRecorderPlayer.startRecorder(path);
      setRecording(true);
    } catch (err) {
      console.error('Start recording error:', err);
    }
  };

  const stopRecording = async () => {
    try {
      const result = await audioRecorderPlayer.stopRecorder();
      setRecording(false);
      const voiceMessage = {
        id: Date.now(),
        type: 'user',
        text: `[Voice Message] Saved at: ${result}`,
      };
      const response = {
        id: Date.now() + 1,
        type: 'assistant',
        text: 'Voice message saved to Downloads folder.',
      };
      setMessages((prev) => [...prev, voiceMessage, response]);
    } catch (err) {
      console.error('Stop recording error:', err);
    }
  };

  const sendMessage = () => {
    if (input.trim() === '') return;
    const newMsg = { id: Date.now(), type: 'user', text: input };
    const response = {
      id: Date.now() + 1,
      type: 'assistant',
      text: `Simulated reply: "${input}" received.`,
    };
    setMessages((prev) => [...prev, newMsg, response]);
    setInput('');
  };

  return (
    <View style={styles.container}>
      <ScrollView style={styles.chatBox} contentContainerStyle={{ paddingBottom: 100 }}>
        {messages.map((msg) => (
          <View
            key={msg.id}
            style={[
              styles.messageBubble,
              msg.type === 'user' ? styles.userBubble : styles.assistantBubble,
            ]}
          >
            <Text style={styles.messageText}>{msg.text}</Text>
          </View>
        ))}
      </ScrollView>
      <View style={styles.inputBar}>
        <TextInput
          value={input}
          onChangeText={setInput}
          placeholder="Ask about a parcel..."
          style={styles.input}
        />
        <TouchableOpacity onPress={sendMessage} style={styles.sendButton}>
          <Text style={styles.sendButtonText}>Send</Text>
        </TouchableOpacity>
        <TouchableOpacity
          onPress={recording ? stopRecording : startRecording}
          style={[
            styles.sendButton,
            { marginLeft: 5, backgroundColor: recording ? '#C0392B' : '#2980B9' },
          ]}
        >
          <Text style={styles.sendButtonText}>{recording ? 'Stop' : 'Voice'}</Text>
        </TouchableOpacity>
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: { flex: 1, backgroundColor: '#EFE6DD' },
  chatBox: { flex: 1, padding: 16 },
  messageBubble: {
    padding: 12,
    borderRadius: 20,
    marginVertical: 6,
    maxWidth: '80%',
  },
  userBubble: { backgroundColor: '#A47148', alignSelf: 'flex-end' },
  assistantBubble: { backgroundColor: '#D3BAA4', alignSelf: 'flex-start' },
  messageText: { color: '#fff' },
  inputBar: {
    position: 'absolute',
    bottom: 0,
    left: 0,
    right: 0,
    backgroundColor: '#C7B198',
    flexDirection: 'row',
    alignItems: 'center',
    padding: 10,
  },
  input: {
    flex: 1,
    padding: 10,
    backgroundColor: '#fff',
    borderRadius: 20,
    marginRight: 10,
  },
  sendButton: {
    backgroundColor: '#7C4A2D',
    paddingVertical: 10,
    paddingHorizontal: 16,
    borderRadius: 20,
  },
  sendButtonText: { color: '#fff', fontWeight: 'bold' },
});
