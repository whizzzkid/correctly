package com.example.lenovo.correctly.fragments;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.google.protobuf.MessageOrBuilder;
import com.google.protobuf.TextFormat;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.app.Fragment;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.lenovo.correctly.MainActivity;
import com.example.lenovo.correctly.R;
import com.example.lenovo.correctly.clients.StreamingRecognizeClient;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Locale;

import io.grpc.ManagedChannel;


public class SentenceLearnFragment extends Fragment {

    private final int SPEECH_RECOGNITION_CODE = 1;
    public TextView textView;
    public TextView EnglishText;
    public TextView FrenchText;
    // public EditText editText;
    public RatingBar ratingBar;
    String text = "";
    TextToSpeech t1;
    private SpannableStringBuilder sb;
    private TextView txtOutput;
    private ImageButton btnMicrophone;
    private ImageButton btnPlay;
    private View myFragmentView;
    public int i=0;
    public String[] list_of_wordsFrench = new String[]{"Bonjour, je m’appelle Sam.",
            "J’ai douze ans.",
            "Vol numéro 210",
            "J’ai une carte de crédit",
            "Où est l’aéroport?"};

    private static final String HOSTNAME = "speech.googleapis.com";
    private static final int PORT = 443;
    private static final int RECORDER_SAMPLERATE = 16000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;

    private AudioRecord mAudioRecord = null;
    private Thread mRecordingThread = null;
    private boolean mIsRecording = false;
    private ImageButton mRecordingBt;
    private TextView mConsoleMsg;
    private StreamingRecognizeClient mStreamingClient;
    private int mBufferSize;


    public String[] list_of_wordsEnglish=new String[]{"Hello, my name is Sam.",
            "I am twelve years old.",
            "Flight No. 210",
            "I have a credit card.",
            "Where is the airport?"};
    public SentenceLearnFragment() {
        // Required empty public constructor
    }


    private void startRecording() {
        mAudioRecord.startRecording();
        mIsRecording = true;
        mRecordingThread = new Thread(new Runnable() {
            public void run() {
                readData();
            }
        }, "AudioRecorder Thread");
        mRecordingThread.start();
    }

    private void readData() {
        byte sData[] = new byte[mBufferSize];
        while (mIsRecording) {
            int bytesRead = mAudioRecord.read(sData, 0, mBufferSize);
            if (bytesRead > 0) {
                try {
                    mStreamingClient.recognizeBytes(sData, bytesRead);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(getClass().getSimpleName(), "Error while reading bytes: " + bytesRead);
            }
        }
    }

    private void initialize() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null) {
                    mConsoleMsg.setText(TextFormat.printToString(
                            (MessageOrBuilder) msg.obj)+"\n" + mConsoleMsg.getText());

                }
                super.handleMessage(msg);
            }
        };
        new Thread(new Runnable() {
            @Override
            public void run() {

                // Required to support Android 4.x.x (patches for OpenSSL from Google-Play-Services)
                try {
                    ProviderInstaller.installIfNeeded(getContext());
                } catch (GooglePlayServicesRepairableException e) {

                    // Indicates that Google Play services is out of date, disabled, etc.
                    e.printStackTrace();
                    // Prompt the user to install/update/enable Google Play services.
                    GooglePlayServicesUtil.showErrorNotification(
                            e.getConnectionStatusCode(), getContext());
                    return;

                } catch (GooglePlayServicesNotAvailableException e) {
                    // Indicates a non-recoverable error; the ProviderInstaller is not able
                    // to install an up-to-date Provider.
                    e.printStackTrace();
                    return;
                }

                try {
                    InputStream credentials = getActivity().getAssets().open
                            ("credentials.json");
                    ManagedChannel channel = StreamingRecognizeClient.createChannel(
                            HOSTNAME, PORT, credentials);
                    mStreamingClient = new StreamingRecognizeClient(channel,
                            RECORDER_SAMPLERATE, handler);
                } catch (Exception e) {
                    Log.e(MainActivity.class.getSimpleName(), "Error", e);
                }
            }
        }).start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mStreamingClient != null) {
            try {
                mStreamingClient.shutdown();
            } catch (InterruptedException e) {
                Log.e(MainActivity.class.getSimpleName(), "Error", e);
            }
        }
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {



        myFragmentView = inflater.inflate(R.layout.fragment_sentence_learn, container, false);
        EnglishText = (TextView) myFragmentView.findViewById(R.id.TranslationText);
        FrenchText = (TextView) myFragmentView.findViewById(R.id.ChallengeText);
        mConsoleMsg = (TextView) myFragmentView.findViewById(R.id.mConsoleMsg);

// Span to set text color to some RGB value
        final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(158, 158, 158));

        EnglishText.setText(list_of_wordsEnglish[0]);
        FrenchText.setText(list_of_wordsFrench[0]);
        // editText.setText(sb);




        t1 = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    //
                    t1.setLanguage(Locale.FRENCH);

                }
            }
        });
        btnPlay = (ImageButton) myFragmentView.findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                i++;
                if(i<list_of_wordsFrench.length) {
                    FrenchText.setText(list_of_wordsFrench[i]);
                    EnglishText.setText(list_of_wordsEnglish[i]);
                }
                else {
                    i = 0;
                    FrenchText.setText(list_of_wordsFrench[i]);
                    EnglishText.setText(list_of_wordsEnglish[i]);
                }
                String toSpeak = FrenchText.getText().toString();
                t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);

            }
        });

        mBufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE, AudioFormat
                .CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) * 2;

        mAudioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLERATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING,
                mBufferSize);

        initialize();

        mRecordingBt = (ImageButton) myFragmentView.findViewById(R.id.btn_mic);
        // mConsoleMsg = (TextView) view.findViewById(R.id.textView);
        mRecordingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsRecording) {
                    mIsRecording = false;
                    mAudioRecord.stop();
                    mStreamingClient.finish();
                    //mRecordingBt.setText(R.string.start_recording);
                } else {
                    if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
                        // mRecordingBt.setText(R.string.stop_recording);
                        startRecording();
                    } else {
                        Log.i(this.getClass().getSimpleName(), "Not Initialized yet.");
                    }
                }
            }
        });


        // Inflate the layout for this fragment
        return myFragmentView;
    }




    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case SPEECH_RECOGNITION_CODE: {
                if (resultCode == Activity.RESULT_OK && null != data) {

                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    float[] confidence = data.getFloatArrayExtra
                            (RecognizerIntent.EXTRA_CONFIDENCE_SCORES);
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
                            sb.setSpan(red, startPoint, startPoint + initialText[i].length(),
                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);


                        } else {
                            sb.setSpan(green, startPoint, startPoint + initialText[i].length(),
                                    Spannable.SPAN_INCLUSIVE_INCLUSIVE);


                        }
                        startPoint += initialText[i].length() + 1;
                    }


                }
                break;
            }

        }
    }


}
