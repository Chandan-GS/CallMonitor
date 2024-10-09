package com.example.callmonitor;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Vibrator;
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

//    private void startSpeechRecognition() {
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak something...");
//
//        // Set the timeout for speech recognition
//        intent.putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 5000); // 5 seconds
//
//
//        speechRecognizer.startListening(intent);
//    }


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
        String[] sensitiveWords = {"CVV","bank details","bank account","pin","debit", "credit card number","account verification" ,"digits", "digit","OTP", "password","account","unauthorized access", "verification code", "password reset",
                "Your account has been compromised",

                "We need to verify your information",
                "You've won a prize",
                "Please provide your details to claim it",
                "This is an urgent matter",
                "Failure to act will result in legal action",
                "I'm from the IRS",
                "You owe back taxes",
                "You qualify for a low-interest loan",
                "Just give me your bank details",
                "Please confirm your credit card number",
                "For security purposes",
                "We can help you recover lost funds",
                "Just send a small fee first",
                "Act now",
                "This offer expires soon",
                "You've been selected",
                "Your warranty is about to expire",
                "Call this number for support",
                "Your payment is overdue",
                "We need to confirm your identity",
                "You've been pre-approved",
                "Your subscription has been renewed",
                "This is your final notice",
                "You can earn money from home",
                "Your application has been approved",
                "You need to pay a small fee",
                "You can get a free trial",
                "There’s a problem with your account",
                "Please don’t hang up",
                "Your payment information is required",
                "This is not a sales call",
                "You can save money on your bills",
                "We have a special offer just for you",
                "You can double your income",
                "You’ve been chosen for an exclusive deal",
                "This is an automated message",
                "Your computer has a virus",
                "We detected unusual activity on your account",
                "We need your help to stop fraud",
                "You will be arrested if you don’t respond",
                "You can access funds now",
                "It’s a limited time offer",
                "Your credit score has been affected",
                "We’re calling about your recent transaction",
                "You can win a gift card",
                "This is a matter of national security",
                "You must act immediately",
                "Your family is in danger",
                "We’re here to help you",
                "Don’t tell anyone about this",
                "You can trust me",
                "Everything will be fine",
                "This is a one-time offer",
                "You don’t need to worry",
                "We’re just verifying some information",
                "I can guarantee you a profit",
                "You’ve been referred by a friend",
                "This is a free service",
                "You’ve been selected for a bonus",
                "Your last payment was unsuccessful",
                "We’re conducting a survey",
                "I need to confirm your social security number",
                "Your recent purchase was flagged",
                "You can get rich quick",
                "This is a confidential matter",
                "You need to update your account information",
                "You can cancel anytime",
                "There’s no risk involved",
                "You have nothing to lose",
                "Your cooperation is appreciated"};

        // Check if recognized text contains sensitive words
        for (String word : sensitiveWords) {
            if (recognizedText.toLowerCase().contains(word.toLowerCase())) {
                // Display Toast message
                Toast.makeText(getApplicationContext(), "Vishing Detected", Toast.LENGTH_SHORT).show();

                // Vibrate the device (optional)
                Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                if (vibrator != null && vibrator.hasVibrator()) {
                    vibrator.vibrate(500); // Vibrate for 500 milliseconds
                }

                // Launch new activity if sensitive word detected
                Intent intent = new Intent(getApplicationContext(), SensitiveActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break; // Stop checking once a sensitive word is detected
            }
        }
    }
}
