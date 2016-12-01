package com.example.lenovo.correctly.fragments;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import android.app.Fragment;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lenovo.correctly.MainActivity;
import com.example.lenovo.correctly.R;
import com.example.lenovo.correctly.clients.StreamingRecognizeClient;

import java.io.InputStream;
import java.util.Locale;

import io.grpc.ManagedChannel;



public class WordLearnFragment extends Fragment {

    public ProgressBar progressBarLearning;
    public ProgressBar progressBarNew;
    public ProgressBar progressBarMastered;
    public ImageView correctImage;
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
    public TextView mAction;
    private ImageButton btnMicrophone;
    private ImageButton btnPlay;
    private View myFragmentView;
    public int i=0;
    public String[] list_of_wordsFrench = new String[]{"Bonjour", "Magasin"};

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

    public String confidence="";
    public String transcript="";
    public String[] list_of_wordsEnglish=new String[]{"Good morning" ,"Store"};
    public WordLearnFragment() {
        // Required empty public constructor
    }


    private void startRecording() {
        mAudioRecord.startRecording();
        mIsRecording = true;
        mAction.setText("I am Listening!");
        mRecordingThread = new Thread(new Runnable() {
            public void run() {
                readData();
            }
        }, "AudioRecorder Thread");
        mRecordingThread.start();
    }
    public void changeColorOfWord()
    {

        String str=FrenchText.getText().toString();
        SpannableString spannable = new SpannableString(str);
        str=str.replace(" ","");
        transcript=transcript.replace("\"","");
        transcript=transcript.replace(" ","");
        float confidenceF=Float.parseFloat(confidence);
        Toast.makeText(getContext(),"confidence= "+confidenceF+"\ntranscript= " +transcript,Toast.LENGTH_SHORT).show();
        if(Float.parseFloat(confidence)>=0.7&&(transcript.compareToIgnoreCase(str)==0)) {
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#008744")), 0,
                    FrenchText.getText().toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            correctImage.setImageResource(R.mipmap.launcher);
            correctImage.setAnimation(AnimationUtils.loadAnimation(getContext(),
                    R.anim.zoom_in));


        }
        else {
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor("#d62d20")), 0,
                    FrenchText.getText().toString().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            correctImage.setImageResource(R.mipmap.incorrect);
            correctImage.setAnimation(AnimationUtils.loadAnimation(getContext(),
                    R.anim.zoom_in));
        }
        FrenchText.setText(spannable);
        Runnable r = new Runnable() {
            @Override
            public void run(){
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
                correctImage.setImageResource(R.drawable.transparent);
                //correctImage.setVisibility(View.INVISIBLE);



            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 3000);
        FrenchText.setAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.zoom_in));
        FrenchText.animate().start();
        EnglishText.setAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.zoom_in));
        EnglishText.animate().start();

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
//                    mConsoleMsg.setText(TextFormat.printToString(
//                          (MessageOrBuilder) msg.obj)+"\n" + mConsoleMsg
//                            .getText());


                    String[] help=msg.obj.toString().split("\n");
                    for(int i=0;i<help.length;i++)
                    {

                        if(help[i].contains("confidence:"))
                        {
                            confidence=help[i].replace("confidence:","");
                            transcript=help[i-1].replace("transcript:","");
                            mIsRecording = false;
                            mAudioRecord.stop();
                            mStreamingClient.finish();

                            changeColorOfWord();
                        }
                    }



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



        myFragmentView = inflater.inflate(R.layout.fragment_word_learn, container,
                false);
        correctImage= (ImageView) myFragmentView.findViewById(R.id.correctImage);
        correctImage.setVisibility(View.INVISIBLE);
        progressBarLearning=(ProgressBar) myFragmentView.findViewById(R.id.progressBar_Learning_Words);
        progressBarNew=(ProgressBar) myFragmentView.findViewById(R.id.progressBar_New_Words);
        progressBarMastered=(ProgressBar) myFragmentView.findViewById(R.id.progressBar_Of_Mastered_Words);
        progressBarNew.setProgress(100);


        progressBarLearning.setProgress(50);

        progressBarMastered.setProgress(50);







        EnglishText = (TextView) myFragmentView.findViewById(R.id.TranslationText);
        FrenchText = (TextView) myFragmentView.findViewById(R.id.ChallengeText);

        FrenchText.setAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.zoom_in));
        EnglishText.setAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.zoom_in));


        mAction = (TextView) myFragmentView.findViewById
                (R.id.mAction);

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
                if(!mIsRecording) {
                    String toSpeak = FrenchText.getText().toString();
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
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
                    mAction.setText("I am Not Listening");
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
}