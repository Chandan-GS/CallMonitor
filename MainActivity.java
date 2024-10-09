package com.example.callmonitor;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.RecognitionListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 1;
    private SpeechRecognizer speechRecognizer;
    private TextView textViewRecognizedText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI components
        textViewRecognizedText = findViewById(R.id.textViewRecognizedText);
        Button buttonStartRecognition = findViewById(R.id.buttonStartRecognition);

        // Button click listener
        buttonStartRecognition.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(MainActivity.this,
                        new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
            } else {
                initializeSpeechRecognizer();
                startSpeechRecognition();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initializeSpeechRecognizer();
                startSpeechRecognition();
            } else {
                Toast.makeText(this, "Permission to record audio denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void initializeSpeechRecognizer() {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle params) {
                // Called when the recognizer is ready to start listening
            }

            @Override
            public void onBeginningOfSpeech() {
                // Called when the user starts to speak
            }

            @Override
            public void onRmsChanged(float rmsdB) {
                // Called when the RMS (Root Mean Square) changes during speech recognition
            }

            @Override
            public void onEndOfSpeech() {
                // Called when the user finishes speaking
            }

            @Override
            public void onError(int error) {
                // Called when an error occurs during recognition
                String errorMessage;
                switch (error) {
                    case SpeechRecognizer.ERROR_AUDIO:
                        errorMessage = "Audio recording error";
                        break;
                    case SpeechRecognizer.ERROR_CLIENT:
                        errorMessage = "Client side error";
                        break;
                    case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                        errorMessage = "Insufficient permissions";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK:
                        errorMessage = "Network error";
                        break;
                    case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                        errorMessage = "Network timeout";
                        break;
                    case SpeechRecognizer.ERROR_NO_MATCH:
                        errorMessage = "No match found";
                        break;
                    case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                        errorMessage = "RecognitionService busy";
                        break;
                    case SpeechRecognizer.ERROR_SERVER:
                        errorMessage = "Server sends error status";
                        break;
                    case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                        errorMessage = "No speech input";
                        break;
                    default:
                        errorMessage = "Unknown error";
                        break;
                }
                Toast.makeText(MainActivity.this, "Error during recognition: " + errorMessage, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResults(Bundle results) {
                // Called when recognition results are ready
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String recognizedText = matches.get(0);
                    // Display recognized text in TextView
                    textViewRecognizedText.setText(recognizedText);
                    // Process recognizedText (e.g., check for sensitive words)
                    monitorSpeech(recognizedText);
                }
            }

            @Override
            public void onPartialResults(Bundle partialResults) {
                // Called when partial recognition results are available
            }

            @Override
            public void onEvent(int eventType, Bundle params) {
                // Called when events related to recognition become available
            }

            @Override
            public void onBufferReceived(byte[] buffer) {
                // Called when partial recognition results are received
            }
        });
    }

    private void startSpeechRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");
        speechRecognizer.startListening(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Release SpeechRecognizer resources
        if (speechRecognizer != null) {
            speechRecognizer.destroy();
        }
    }

    private void monitorSpeech(String recognizedText) {
        // Dummy sensitive words detection
        String[] sensitiveWords = {"CVV", "bank details", "credit card number", "OTP", "password"};

        // Check if recognized text contains sensitive words
        for (String word : sensitiveWords) {
            if (recognizedText.toLowerCase().contains(word.toLowerCase())) {
                Toast.makeText(this, "Sensitive word detected in speech: " + word, Toast.LENGTH_LONG).show();
                // You can take further actions here (e.g., vibrate the device)
                break; // Stop checking once a sensitive word is detected
            }
        }
    }
}
