package com.example.root.shoppingassistance.View;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.root.shoppingassistance.Controller.ShoppingAssistanceController;
import com.example.root.shoppingassistance.Database.DatabaseConnector;
import com.example.root.shoppingassistance.R;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Locale;

public class ShoppingAssistanceView extends AppCompatActivity implements TextToSpeech.OnInitListener {

    DatabaseConnector dbCon;

    private TextToSpeech tts;
    private Button btnSpeak;
    private EditText txtq;
    private EditText txta;
    String startText = "What are you looking for ?";
    String errorMessage = "Invalid request !";
    private final int REQ_CODE_SPEECH_INPUT = 100;
    ShoppingAssistanceController shoppingAssistanceController = new ShoppingAssistanceController();
    static boolean isStarted = false;
    boolean isNextItem = false;
    int index =1;
    String message;

    public ShoppingAssistanceView() throws ParseException {
        dbCon=DatabaseConnector.getInstance(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_assistance);

        tts = new TextToSpeech(this, this);

        txtq = (EditText) findViewById(R.id.txtQ);
        txta = (EditText) findViewById(R.id.txtA);

        // Refer 'Speak' button
        btnSpeak = (Button) findViewById(R.id.btnSpeak);
        // Handle onClick event for button 'Speak'
        btnSpeak.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Method yet to be defined
                isStarted = false;
                index =1;
                try {
                    shoppingAssistanceController = new ShoppingAssistanceController();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                try {
                    speakOut(startText);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

        });
    }

    //method to speak
    private void speakOut(String text) throws InterruptedException {
        txtq.setText(text);
        if (text.length() == 0) {
            txtq.setText(text);
            tts.speak("You haven't typed text", TextToSpeech.QUEUE_FLUSH, null);
        }
        else if(text.equals("success")){
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
        }
        else {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            Thread.sleep(2000);
            getSpeechInput();
        }

    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            // Setting speech language
            int result = tts.setLanguage(Locale.US);
            // If your device doesn't support language you set above
            if (result == TextToSpeech.LANG_MISSING_DATA
                    || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                // Cook simple toast message with message
                Toast.makeText(getApplicationContext(), "Language not supported",
                        Toast.LENGTH_LONG).show();
                Log.e("TTS", "Language is not supported");
            }
            // Enable the button - It was disabled in main.xml (Go back and
            // Check it)
            else {
                btnSpeak.setEnabled(true);
            }
            // TTS is not initialized properly
        } else {
            Toast.makeText(this, "TTS Initilization Failed", Toast.LENGTH_LONG)
                    .show();
            Log.e("TTS", "Initilization Failed");
        }
    }

    //method to listen
    public void getSpeechInput () {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                getString(R.string.speech_prompt));
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getApplicationContext(),
                    getString(R.string.speech_not_supported),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT:
                if (resultCode == RESULT_OK && data != null) {
                    ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String response = shoppingAssistanceController.selectBestResponse(result);
                    txta.setText(response);

                    Toast.makeText(getApplicationContext(), response,
                            Toast.LENGTH_LONG).show();

                    print(response);
                    try {
                        if(response.equals("yes")){
                            isNextItem = true;
                            isStarted = false;
                            index =1;
                            speakOut("What is your next item");
                        }
                        else if(response.equals("no")){
                            print(shoppingAssistanceController.getShoppingCart());
                            Toast.makeText(getApplicationContext(), shoppingAssistanceController.getShoppingCart(),
                                    Toast.LENGTH_LONG).show();
                            speakOut("success");
                        }
                        else if(isStarted) {
                            continueChat(response);
                        }
                        else {
                            startChat(response);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
    }

    public void startChat(String s) throws ParseException, InterruptedException {
        if(shoppingAssistanceController.getItemsOfCategory(s.toLowerCase()).size()>0){
            isStarted = true;
            print("started");
            message = "what is the "+shoppingAssistanceController.getFirstQ()+" ?";
            speakOut(message);
        }
        else{
            Toast.makeText(getApplicationContext(), errorMessage,
                    Toast.LENGTH_LONG).show();
            speakOut(errorMessage+" you said "+s.toLowerCase()+" !");
            print(errorMessage);

        }

    }

    public void continueChat(String s) throws ParseException, InterruptedException {

        if(shoppingAssistanceController.nextQuestion(index,s.toLowerCase()).equals("invalid")) {
            Toast.makeText(getApplicationContext(), errorMessage,
                    Toast.LENGTH_LONG).show();
            speakOut(errorMessage+" you said "+s.toLowerCase()+" !");
            print(errorMessage);

        }
        else if(shoppingAssistanceController.nextQuestion(index,s.toLowerCase()).equals("success")){
            shoppingAssistanceController.addToCart();
            print("item added");
            speakOut("Do you want to add more items ?");
//            Thread.sleep(8000);
//            if(isNextItem){
//                speakOut("What is your next item");
//            }else {
//                speakOut("success");
//            }
        }
        else{
            message = "what is "+shoppingAssistanceController.nextQuestion(index,s.toLowerCase())+" ?";
            print(message);
            index++;
            speakOut(message);
            Toast.makeText(getApplicationContext(), s,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void print(String s){
        Log.e("message",s);
    }

    public void isNextItem() throws InterruptedException {
        speakOut("Do you want to add more items ?");
    }
}
