package com.example.lenovo.correctly.utils;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Handler;

import com.example.lenovo.correctly.clients.StreamingRecognizeClient;

import java.io.IOException;
import java.io.InputStream;

import io.grpc.ManagedChannel;

public class GoogleAudioFormat {
    public static float CONFIDENCE = (float) 0.65;
    public static String HOSTNAME = "speech.googleapis.com";
    public static int PORT = 443;
    public static int RECORDER_SAMPLE_RATE = 16000;
    public static int RECORDER_CHANNELS = android.media.AudioFormat.CHANNEL_IN_MONO;
    public static int RECORDER_AUDIO_ENCODING = android.media.AudioFormat
            .ENCODING_PCM_16BIT;
    public static int BufferSize = AudioRecord.getMinBufferSize
            (RECORDER_SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat
                    .ENCODING_PCM_16BIT) * 2;

    public static AudioRecord getAudioRecorder() {
        return new AudioRecord(MediaRecorder.AudioSource.MIC,
                RECORDER_SAMPLE_RATE,
                RECORDER_CHANNELS,
                RECORDER_AUDIO_ENCODING,
                BufferSize);
    }

    public static StreamingRecognizeClient getStreamRecognizer(Activity activity,
                                                               Handler handler) throws
            IOException {
        InputStream credentials = activity.getAssets().open("credentials.json");
        ManagedChannel channel = StreamingRecognizeClient.createChannel(
                HOSTNAME, PORT, credentials);
        return new StreamingRecognizeClient(channel, RECORDER_SAMPLE_RATE,
                handler);
    }
}
