package com.example.lenovo.correctly.fragments;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import android.app.Fragment;
import android.graphics.Color;
import android.media.AudioRecord;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.Spannable;
import android.text.SpannableString;
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
import android.widget.TextView;

import com.example.lenovo.correctly.MainActivity;
import com.example.lenovo.correctly.R;
import com.example.lenovo.correctly.clients.StreamingRecognizeClient;
import com.example.lenovo.correctly.models.Challenge;
import com.example.lenovo.correctly.utils.ChallengeManager;
import com.example.lenovo.correctly.utils.GoogleAudioFormat;

import java.util.Locale;

public class WordLearnFragment extends Fragment {

    public ProgressBar progressBarLearning;
    public ProgressBar progressBarNew;
    public ProgressBar progressBarMastered;
    public ImageView correctImage;
    public TextView TranslationText;
    public TextView ChallengeText;
    public TextView mAction;
    public int i = 0;
    public String confidence = "";
    public String transcript = "";
    public String topic;
    public String level;
    String text = "";
    TextToSpeech t1;
    private ImageButton btnPlay;
    private AudioRecord mAudioRecord = null;
    private boolean mIsRecording = false;
    private ImageButton mRecordingBt;
    private StreamingRecognizeClient mStreamingClient;
    private ChallengeManager challengeManager;
    private Challenge challenge;
    private UtteranceProgressListener mProgressListener = new
            UtteranceProgressListener() {
        @Override
        public void onStart(String s) {
        } // Do Nothing

        @Override
        public void onDone(String utteranceId) {
            btnPlay.clearAnimation();
        }

        @Override
        public void onError(String s) {
        } // Do Nothing
    };


    public WordLearnFragment() {
        // Required empty public constructor
    }

    private void startRecording() {
        mAudioRecord.startRecording();
        mIsRecording = true;
        //mAction.setText("I am Listening!");
        Thread mRecordingThread = new Thread(new Runnable() {
            public void run() {
                readData();
            }
        }, "AudioRecorder Thread");
        mRecordingThread.start();
    }

    public void changeColorOfWord() {
        String str = ChallengeText.getText().toString();
        SpannableString spannable = new SpannableString(str);
        str = str.replace(" ", "");
        transcript = transcript.replace("\"", "").replace(" ", "");
        if (Float.parseFloat(confidence) >= GoogleAudioFormat.CONFIDENCE  &&
                (transcript.compareToIgnoreCase(str) == 0)) {
            spannable.setSpan(new ForegroundColorSpan(Color.parseColor
                    ("#008744")), 0, ChallengeText.getText().toString()
                    .length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            correctImage.setImageResource(R.mipmap.launcher);
            correctImage.setAnimation(AnimationUtils.loadAnimation(getContext(),
                    R.anim.zoom_in));
        } else {
            spannable.setSpan(
                    new ForegroundColorSpan(
                            Color.parseColor(
                                    getString(R.string.foreground_span_color))),
                    0,
                    ChallengeText.getText().toString().length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            correctImage.setImageResource(R.mipmap.incorrect);
            correctImage.setAnimation(
                    AnimationUtils.loadAnimation(getContext(), R.anim.zoom_in));
        }
        ChallengeText.setText(spannable);

        // Loading new challenge
        challenge = challengeManager.getNextChallenge(challenge.order);
        Runnable r = new Runnable() {
            @Override
            public void run() {
                ChallengeText.setText(challenge.challenge);
                TranslationText.setText(challenge.challenge_translation);
                correctImage.setImageResource(R.drawable.transparent);
            }
        };
        Handler h = new Handler();
        h.postDelayed(r, 3000);
        ChallengeText.setAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.zoom_in));
        ChallengeText.animate().start();
        TranslationText.setAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.zoom_in));
        TranslationText.animate().start();

    }

    private void readData() {
        byte sData[] = new byte[GoogleAudioFormat.BufferSize];
        while (mIsRecording) {
            int bytesRead = mAudioRecord.read(sData, 0, GoogleAudioFormat
                    .BufferSize);
            if (bytesRead > 0) {
                try {
                    mStreamingClient.recognizeBytes(sData, bytesRead);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Log.e(getClass().getSimpleName(), "Error while reading bytes:" +
                        " " + bytesRead);
            }
        }
    }

    private void initialize() {
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg != null) {
                    String[] help = msg.obj.toString().split("\n");
                    for (int i = 0; i < help.length; i++) {
                        if (help[i].contains("confidence:")) {
                            confidence = help[i].replace("confidence:", "");
                            transcript = help[i - 1].replace("transcript:", "");
                            mIsRecording = false;
                            mAudioRecord.stop();
                            mStreamingClient.finish();
                            mRecordingBt.clearAnimation();
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
                try {
                    ProviderInstaller.installIfNeeded(getContext());
                } catch (GooglePlayServicesRepairableException e) {

                    // Indicates that Google Play services is out of date,
                    // disabled, etc.
                    e.printStackTrace();
                    // Prompt the user to install/update/enable Google Play
                    // services.
                    GooglePlayServicesUtil.showErrorNotification(
                            e.getConnectionStatusCode(), getContext());
                    return;

                } catch (GooglePlayServicesNotAvailableException e) {
                    // Indicates a non-recoverable error; the
                    // ProviderInstaller is not able
                    // to install an up-to-date Provider.
                    e.printStackTrace();
                    return;
                }

                try {
                    mStreamingClient = GoogleAudioFormat.getStreamRecognizer
                            (getActivity(), handler);
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
        Bundle args = getArguments();
        this.topic = (String) args.get("topic");
        this.level = (String) args.get("level");
        this.challengeManager = new ChallengeManager(this.topic, this.level);
        this.challenge = challengeManager.getNextChallenge(-1);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View myFragmentView = inflater.inflate(R.layout.fragment_word_learn,
                container, false);
        correctImage = (ImageView) myFragmentView.findViewById(R.id
                .correctImage);
        correctImage.setVisibility(View.INVISIBLE);
        progressBarLearning = (ProgressBar) myFragmentView.findViewById(R.id
                .progressBar_Learning_Words);
        progressBarNew = (ProgressBar) myFragmentView.findViewById(R.id
                .progressBar_New_Words);
        progressBarMastered = (ProgressBar) myFragmentView.findViewById(R.id
                .progressBar_Of_Mastered_Words);
        progressBarNew.setProgress(100);
        progressBarLearning.setProgress(50);
        progressBarMastered.setProgress(50);
        TranslationText = (TextView) myFragmentView.findViewById(R.id
                .TranslationText);
        ChallengeText = (TextView) myFragmentView.findViewById(R.id
                .ChallengeText);
        ChallengeText.setAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.zoom_in));
        TranslationText.setAnimation(AnimationUtils.loadAnimation(getContext(),
                R.anim.zoom_in));
        mAction = (TextView) myFragmentView.findViewById
                (R.id.mAction);
        final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb
                (158, 158, 158));
        TranslationText.setText(challenge.challenge_translation);
        ChallengeText.setText(challenge.challenge);
        t1 = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                t1.setLanguage(Locale.FRENCH);
                // t1.setOnUtteranceProgressListener(mProgressListener);

            }


        });
        btnPlay = (ImageButton) myFragmentView.findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!mIsRecording) {
                    Animation pulse = AnimationUtils.loadAnimation(getContext
                            (), R.anim.pulse);
                    // btnPlay.startAnimation(pulse);

                    String toSpeak = ChallengeText.getText().toString();
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        });

        mAudioRecord = GoogleAudioFormat.getAudioRecorder();

        initialize();

        mRecordingBt = (ImageButton) myFragmentView.findViewById(R.id.btn_mic);
        mRecordingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mIsRecording) {
                    mRecordingBt.clearAnimation();
                    mIsRecording = false;
                    mAudioRecord.stop();
                    mStreamingClient.finish();
                } else {
                    if (mAudioRecord.getState() == AudioRecord
                            .STATE_INITIALIZED) {
                        // mRecordingBt.setText(R.string.stop_recording);
                        Animation pulse = AnimationUtils.loadAnimation
                                (getContext(), R.anim.pulse);
                        mRecordingBt.startAnimation(pulse);
                        startRecording();
                    } else {
                        Log.i(this.getClass().getSimpleName(), "Not " +
                                "Initialized yet.");
                    }
                }
            }
        });

        // Inflate the layout for this fragment
        return myFragmentView;
    }
}