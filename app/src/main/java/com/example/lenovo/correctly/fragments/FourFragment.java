package com.example.lenovo.correctly.fragments;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.correctly.R;

import java.util.ArrayList;
import java.util.Locale;


public class FourFragment extends Fragment {

    private final int SPEECH_RECOGNITION_CODE = 1;
    public TextView textView;
    public EditText editText;
    public RatingBar ratingBar;
    String text = "";
    TextToSpeech t1;
    private SpannableStringBuilder sb;
    private TextView txtOutput;
    private ImageButton btnMicrophone;
    private ImageButton btnPlay;
    private View myFragmentView;

    public FourFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        myFragmentView = inflater.inflate(R.layout.fragment_four, container, false);
        textView = (TextView) myFragmentView.findViewById(R.id.textView);
        editText = (EditText) myFragmentView.findViewById(R.id.editText);
        text = "My name is Ben";

        editText.setFocusable(false);
        // make "Lorem" (characters 0 to 5) red
        sb = new SpannableStringBuilder("My name is Ben");

// Span to set text color to some RGB value
        final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(158, 158, 158));


        editText.setText(sb);

        ratingBar = (RatingBar) myFragmentView.findViewById(R.id.ratingBar);
        ratingBar.setProgress(9);

        t1 = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.UK);
                }
            }
        });
        btnPlay = (ImageButton) myFragmentView.findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String toSpeak = editText.getText().toString();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
            }
        });

        btnMicrophone = (ImageButton) myFragmentView.findViewById(R.id.btn_mic);
        btnMicrophone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startSpeechToText();
            }
        });


        // Inflate the layout for this fragment
        return myFragmentView;
    }

    private void startSpeechToText() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Speak something...");
        try {
            startActivityForResult(intent, SPEECH_RECOGNITION_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getContext(),
                    "Sorry! Speech recognition is not supported in this device.",
                    Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    float[] confidence = data.getFloatArrayExtra(RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
                    String resText = result.get(0);
                    String st = "";

                    resText.toLowerCase();
                    text.toLowerCase();
                    //Toast.makeText(this,text+"\n"+resText,Toast.LENGTH_LONG).show();
// Span to set text color to some RGB value
                    final ForegroundColorSpan red = new ForegroundColorSpan(Color.RED);
                    final ForegroundColorSpan green = new ForegroundColorSpan(Color.GREEN);

// Span to make text bold
                    final StyleSpan bss = new StyleSpan(android.graphics.Typeface.BOLD);
                    for (int i = 0; i < result.size(); i++)
                        st += text + "\n" + confidence[i] + "\n" + result.get(i) + "\n";
                    int startPoint = 0;
                    String[] resultText = resText.split(" ");
                    String[] initialText = text.split(" ");

                    for (int i = 0; i < initialText.length; i++) {
                        if (initialText[i].toString().compareTo(initialText[i].toString()) == 0) {
                            sb.setSpan(red, startPoint, startPoint + initialText[i].length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);

                        } else {
                            sb.setSpan(green, startPoint, startPoint + initialText[i].length(), Spannable.SPAN_INCLUSIVE_INCLUSIVE);


                        }
                        startPoint += initialText[i].length() + 1;
                    }
                    //textView.setText(Html.fromHtml("<i><small><font color=\"#c5c5c5\">" + "Competitor ID: " + "</font></small></i>" + "<font color=\"#47a842\">" +  + "</font>"));
                    editText.setText(sb);


// Set the text color for first 4 characters


// make them also bold


                }
                break;
            }

        }
    }


}
