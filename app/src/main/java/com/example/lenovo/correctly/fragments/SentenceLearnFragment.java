package com.example.lenovo.correctly.fragments;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.AudioRecord;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.speech.tts.TextToSpeech;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
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
import android.widget.Toast;

import com.example.lenovo.correctly.MainActivity;
import com.example.lenovo.correctly.R;
import com.example.lenovo.correctly.clients.StreamingRecognizeClient;
import com.example.lenovo.correctly.models.Challenge;
import com.example.lenovo.correctly.models.DataModelConstants;
import com.example.lenovo.correctly.utils.ChallengeManager;
import com.example.lenovo.correctly.utils.GoogleAudioFormat;

import java.util.Locale;
import java.util.Map;

import static android.content.ContentValues.TAG;


public class SentenceLearnFragment extends Fragment {

    public ProgressBar progressBarNew, progressBarLearned,
            progressBarMastered, progressBarRevising, progressBarDone;
    public TextView progressNew, progressLearned, progressMastered,
            progressRevising, progressDone;
    public int MainIterator = 0;
    public String sentence = "";
    public String[] MainSentence;
    public TextView TranslationText, ChallengeText, CurrentWordStatus;
    public String topic, level;
    public Map<String, Integer> progress;
    public int i = 0;
    public String wordToCheck = "";
    public String confidence = "";
    public String transcript = "";
    public String[] listOfWords;
    public ImageView correctImage;
    public int wTC_one = 0;
    public int wTC_two = 0;
    public int correctWords, incorrectWords;
    String text = "";
    TextToSpeech t1;
    private ChallengeManager challengeManager;
    private Challenge challenge;
    private SpannableStringBuilder sb;
    private ImageButton btnPlay;
    private View view;
    private AudioRecord mAudioRecord = null;
    private Thread mRecordingThread = null;
    private boolean mIsRecording = false;
    private ImageButton mRecordingBt;
    private StreamingRecognizeClient mStreamingClient;

    public SentenceLearnFragment() {
        // Required empty public constructor
    }

    private void startRecording() {
        mAudioRecord.startRecording();

        mRecordingThread = new Thread(new Runnable() {
            public void run() {
                readData();
            }
        }, "AudioRecorder Thread");
        mRecordingThread.start();
    }

    private SpannableStringBuilder addClickablePart(String str) {
        str = str.replace("$", " ");
        listOfWords = str.split(" ");
        MainSentence = sentence.split(" ");
        sb = new SpannableStringBuilder(str);
        int idx1 = 0;
        int idx2 = 0;
        for (int i = 0; i < listOfWords.length; i++) {
            idx1 = str.indexOf(listOfWords[i]);
            idx2 = str.lastIndexOf(listOfWords[i], idx1) + listOfWords[i].length();
            final String clickString = str.substring(idx1, idx2);
            sb.setSpan(new ClickableSpan() {

                @Override
                public void onClick(View widget) {
                    if (!mIsRecording)
                        t1.speak(clickString, TextToSpeech.QUEUE_FLUSH, null);
                }
            }, idx1, idx2, 0);

        }
        return sb;
    }


    public void changeColorOfWord() {

        try {
            String str = ChallengeText.getText().toString();
            int idx1 = wTC_one;
            int idx2 = wTC_two;

            //SpannableStringBuilder spannable = new SpannableStringBuilder(str);
            str = str.replace(" ", "");
            transcript = transcript.replace("\"", "");
            Log.v(TAG, "transcript=" + "!" + wordToCheck.replace("$", " ") + "!");
            if (Float.parseFloat(confidence) >= GoogleAudioFormat.CONFIDENCE &&
                    (transcript.compareToIgnoreCase(
                            wordToCheck.replace("$", " ")) == 0)) {
                correctWords++;
                sb.setSpan(new ForegroundColorSpan(Color.parseColor
                                (getString(R.string.correct_green))), wTC_one, wTC_two,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            } else {
                incorrectWords++;
                sb.setSpan(new ForegroundColorSpan(Color.parseColor
                                (getString(R.string.wrong_red))), wTC_one, wTC_two,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            ChallengeText.setText(sb);
            MainIterator++;
            after_micClick();
        } catch (Exception e) {
            Toast.makeText(getContext(), "wTC_one= " + wTC_one + "\nwTC_two= " + wTC_two,
                    Toast.LENGTH_SHORT).show();
        }

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
                    Log.w("1", msg.obj.toString());
                    for (int i = 0; i < help.length; i++) {
                        if (help[i].contains("is_final: true")) {
                            if (help[i - 2].contains("confidence:")) {
                                confidence = help[i - 2].replace("confidence:", "");
                                transcript = help[i - 3].replaceAll("\\s*transcript:\\s*", "");
                            } else {
                                confidence = "1";
                                transcript = help[i - 2].replaceAll("\\s*transcript:\\s*", "");
                            }
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

    public void updateChallenge() {
        sentence = challenge.challenge;
        MainSentence = sentence.split(" ");
        ChallengeText.setText(addClickablePart(challenge.challenge), TextView
                .BufferType.SPANNABLE);
        TranslationText.setText(challenge.challenge_translation);
        correctImage.setImageResource(R.drawable.transparent);
        String currentWordStatus = "";
        if (!challenge.isSeen) {
            currentWordStatus = "New Word";
        } else {
            if (challenge.state == DataModelConstants.CHALLENGE_STATE_NEW) {
                currentWordStatus = "Seen Before";
            } else if (challenge.state == DataModelConstants
                    .CHALLENGE_STATE_LEARNED) {
                currentWordStatus = "Learning Word";
            } else if (challenge.state == DataModelConstants
                    .CHALLENGE_STATE_MASTERED) {
                currentWordStatus = "Mastering Word";
            } else if (challenge.state == DataModelConstants
                    .CHALLENGE_STATE_REVISE) {
                currentWordStatus = "Revising Word";
            } else if (challenge.state == DataModelConstants
                    .CHALLENGE_STATE_DONE) {
                currentWordStatus = "You Know This Word!";
            }
        }
        CurrentWordStatus.setText(currentWordStatus);
    }

    public void updateProgress() {
        progress = challengeManager.getProgress();
        Log.v(TAG, String.valueOf(progress));
        int total = this.progress.get("total");
        int newWordCount = this.progress.get("new");
        int newWordProgress = (newWordCount * 100) / total;
        int learnedWordCount = this.progress.get("learned");
        int learnedWordProgress = (learnedWordCount * 100) / total;
        int masteredWordCount = this.progress.get("mastered");
        int masteredWordProgress = (masteredWordCount * 100) / total;
        int revisingWordCount = this.progress.get("revising");
        int revisingWordProgress = (revisingWordCount * 100) / total;
        int doneWordCount = this.progress.get("done");
        int doneWordProgress = (doneWordCount * 100) / total;
        progressNew.setText(String.format(Locale.getDefault(), "%s %d",
                getString(R.string.new_words), newWordCount));
        progressBarNew.setProgress(newWordProgress);
        progressLearned.setText(String.format(Locale.getDefault(), "%s %d",
                getString(R.string.learned_words), learnedWordCount));
        progressBarLearned.setProgress(learnedWordProgress);
        progressMastered.setText(String.format(Locale.getDefault(), "%s %d",
                getString(R.string.mastered_words), masteredWordCount));
        progressBarMastered.setProgress(masteredWordProgress);
        progressRevising.setText(String.format(Locale.getDefault(), "%s %d",
                getString(R.string.revising_words), revisingWordCount));
        progressBarRevising.setProgress(revisingWordProgress);
        progressDone.setText(String.format(Locale.getDefault(), "%s %d",
                getString(R.string.done_words), doneWordCount));
        progressBarDone.setProgress(doneWordProgress);
        Log.v(TAG, String.valueOf(newWordProgress));
        Log.v(TAG, String.valueOf(learnedWordProgress));
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        topic = (String) args.get("topic");
        level = (String) args.get("level");
        challengeManager = new ChallengeManager(this.topic, this.level);
        challenge = challengeManager.getFirstChallenge();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_challenge, container, false);
        getActivity().setTitle(topic + " > " + level);
        correctImage = (ImageView) view.findViewById(R.id
                .correctImage);
        correctImage.setVisibility(View.INVISIBLE);
        progressNew = (TextView) view.findViewById(R.id.new_words_text);
        progressBarNew = (ProgressBar) view.findViewById(R.id
                .new_words_progress);
        progressLearned = (TextView) view.findViewById(R.id
                .learned_words_text);
        progressBarLearned = (ProgressBar) view.findViewById(R.id
                .learned_words_progress);
        progressMastered = (TextView) view.findViewById(R.id
                .mastered_words_text);
        progressBarMastered = (ProgressBar) view.findViewById(R.id
                .mastered_words_progress);
        progressRevising = (TextView) view.findViewById(R.id
                .revising_words_text);
        progressBarRevising = (ProgressBar) view.findViewById(R.id
                .revising_words_progress);
        progressDone = (TextView) view.findViewById(R.id.done_words_text);
        progressBarDone = (ProgressBar) view.findViewById(R.id
                .done_words_progress);
        TranslationText = (TextView) view.findViewById(R.id
                .TranslationText);
        ChallengeText = (TextView) view.findViewById(R.id
                .ChallengeText);
        CurrentWordStatus = (TextView) view.findViewById(R.id
                .current_word_status);
        ChallengeText.setMovementMethod(LinkMovementMethod.getInstance());

        // Span to set text color to some RGB value
        final ForegroundColorSpan fcs = new ForegroundColorSpan(Color.rgb(158, 158, 158));
        t1 = new TextToSpeech(getContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    t1.setLanguage(Locale.FRENCH);
                    t1.setSpeechRate((float) 0.8);
                }
            }
        });
        btnPlay = (ImageButton) view.findViewById(R.id.btn_play);
        btnPlay.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String toSpeak = ChallengeText.getText().toString();
                if (!mIsRecording) {
                    t1.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
                }
                SpannableStringBuilder ssb = addClickablePart(ChallengeText.getText().toString());
                ChallengeText.setText(ssb);
            }
        });

        mAudioRecord = GoogleAudioFormat.getAudioRecorder();
        updateChallenge();
        updateProgress();
        initialize();
        mRecordingBt = (ImageButton) view.findViewById(R.id.btn_mic);
        mRecordingBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!t1.isSpeaking()) {

                    if (mIsRecording) {
                        mIsRecording = false;
                        mAudioRecord.stop();
                        mStreamingClient.finish();
                        mRecordingBt.clearAnimation();

                    } else {
                        MainIterator = 0;
                        Animation pulse = AnimationUtils.loadAnimation(getContext(), R.anim.pulse);
                        mRecordingBt.startAnimation(pulse);
                        after_micClick();
                }
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
    }


    public void after_micClick() {
        mIsRecording = true;
        if (mAudioRecord.getState() == AudioRecord.STATE_INITIALIZED) {
            if (MainIterator < MainSentence.length) {
                String str = sentence;
                int idx1 = 0;
                int idx2 = 0;
                wTC_one = str.indexOf(MainSentence[MainIterator]);
                wTC_two = str.lastIndexOf(MainSentence[MainIterator], wTC_one) + MainSentence[MainIterator].length();
                Log.wtf("3", "word wTC_one=" + wTC_one);
                Log.wtf("2", "word wTC_two=" + wTC_two);
                wordToCheck = MainSentence[MainIterator];
                Log.w("1", "word to check=" + wordToCheck);
                wordToCheck = wordToCheck.replace(" ", "");
                wordToCheck = wordToCheck.replace(" ", "");
                sb.setSpan(new StyleSpan(Typeface.BOLD), wTC_one, wTC_two, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ChallengeText.setText(sb);
                startRecording();
            } else {
                mRecordingBt.clearAnimation();
                ChallengeText.setAnimation(AnimationUtils.loadAnimation(getContext(),
                        R.anim.zoom_in_sentence));
                mIsRecording = false;
                if (incorrectWords <= 1) {
                    challengeManager.pushResult(challenge, true);
                } else {
                    challengeManager.pushResult(challenge, false);
                }
                updateProgress();
                challenge = challengeManager.getNextChallenge();
                incorrectWords = 0;
                Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        updateChallenge();
                    }
                };
                Handler h = new Handler();
                h.postDelayed(r, 3000);
            }
        } else {
            Log.i(this.getClass().getSimpleName(), "Not Initialized yet.");
        }
    }
}