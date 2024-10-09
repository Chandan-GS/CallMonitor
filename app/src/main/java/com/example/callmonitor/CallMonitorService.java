package com.example.callmonitor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;

public class CallMonitorService extends Service {

    private PhoneStateListener phoneStateListener;
    private SpeechRecognition speechRecognition;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startCallMonitoring();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopCallMonitoring();
        stopSpeechRecognition();
        super.onDestroy();
    }

    private void startCallMonitoring() {
        phoneStateListener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String phoneNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        // Call started or ongoing
                        startSpeechRecognition();
                        break;
                    case TelephonyManager.CALL_STATE_IDLE:
                        // Call ended
                        stopSpeechRecognition();
                        break;
                }
            }
        };

        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }

    private void stopCallMonitoring() {
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (telephonyManager != null) {
            telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_NONE);
        }
    }

    private void startSpeechRecognition() {
        speechRecognition = new SpeechRecognition(getApplicationContext(), new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {}

            @Override
            public void onBeginningOfSpeech() {}

            @Override
            public void onRmsChanged(float rmsdB) {}

            @Override
            public void onBufferReceived(byte[] buffer) {}

            @Override
            public void onEndOfSpeech() {}

            @Override
            public void onError(int error) {}

            @Override
            public void onResults(Bundle results) {
                handleSpeechResults(results);
            }

            @Override
            public void onPartialResults(Bundle partialResults) {}

            @Override
            public void onEvent(int eventType, Bundle params) {}
        });
        speechRecognition.startRecognition();
    }

    private void stopSpeechRecognition() {
        if (speechRecognition != null) {
            speechRecognition.stopRecognition();
            speechRecognition = null;
        }
    }

    private void handleSpeechResults(Bundle results) {
        ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (matches != null) {
            for (String match : matches) {
                if (isSensitiveWord(match)) {
                    // Launch new activity if sensitive word detected
                    Intent intent = new Intent(this, SensitiveActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Add this line
                    startActivity(intent);
                    break;
                }
            }
        }
    }

    private boolean isSensitiveWord(String text) {
        // List of sensitive words
        String[] sensitiveWords = {"fraud", "scam", "password", "ssn"};
        for (String word : sensitiveWords) {
            if (text.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
