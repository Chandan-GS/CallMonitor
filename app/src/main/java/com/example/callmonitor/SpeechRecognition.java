package com.example.callmonitor;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.speech.RecognitionListener;
import android.speech.RecognitionService;
import android.speech.SpeechRecognizer;
import android.speech.RecognizerIntent;
import android.util.Log;

import androidx.core.app.ActivityCompat;

public class SpeechRecognition {

    private static final String TAG = "SpeechRecognition";

    private static final int SAMPLE_RATE = 16000;
    private static final int CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO;
    private static final int AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord audioRecord;
    private SpeechRecognizer speechRecognizer;
    private Context context;
    private RecognitionListener recognitionListener;

    private boolean isRecording = false;

    public SpeechRecognition(Context context, RecognitionListener listener) {
        this.context = context;
        this.recognitionListener = listener;
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        speechRecognizer.setRecognitionListener(recognitionListener);
    }

    public void startRecognition() {
        int bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT);
        if (ActivityCompat.checkSelfPermission(context, android.Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Record audio permission not granted.");
            return;
        }

        audioRecord = new AudioRecord(MediaRecorder.AudioSource.VOICE_RECOGNITION, SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT, bufferSize);

        audioRecord.startRecording();
        isRecording = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (isRecording) {
                        short[] buffer = new short[bufferSize];
                        int read = audioRecord.read(buffer, 0, bufferSize);
                        if (read > 0) {
                            // Process the audio data (optional)
                            // Pass audio data to SpeechRecognizer using RecognizerIntent
                            Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-US");
                            speechRecognizer.startListening(intent);
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Error during audio recording: " + e.getMessage());
                }
            }
        }).start();
    }

    public void stopRecognition() {
        isRecording = false;
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
    }

    public void destroy() {
        stopRecognition();
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }
}
