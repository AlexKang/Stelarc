package orcaninjas.stelarc;

import android.app.Activity;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.util.Log;

public class IdActivity extends Activity {

    private static final String TAG = "IdActivity";

    private static final int BUFFER_SIZE = 4096;
    private static final int SAMPLE_RATE = 44100;
    private static final long RECORD_DURATION = 10000;

    private static IdActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id);

        instance = this;
        PhoneMessenger.getInstance(getBaseContext()).sendMessage("/startId");
    }

    public static IdActivity getInstance() {
        return instance;
    }

    public static void startRecording() {
        instance.startRecordThread();
    }

    private void startRecordThread() {
        new Thread(new RecordRunnable()).start();
    }

    private class RecordRunnable implements Runnable {

        @Override
        public void run() {
            AudioRecord recorder = new AudioRecord(
                    MediaRecorder.AudioSource.MIC,
                    SAMPLE_RATE,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    AudioRecord.getMinBufferSize(SAMPLE_RATE, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT)
            );

            Log.d(TAG, "Starting recording");
            byte[] buffer = new byte[BUFFER_SIZE];
            long endTime = System.currentTimeMillis() + RECORD_DURATION;
            recorder.startRecording();

            while (System.currentTimeMillis() <= endTime) {
                recorder.read(buffer, 0, BUFFER_SIZE);
                PhoneMessenger.getInstance(getBaseContext()).sendAsset(buffer);
            }

            Log.d(TAG, "Finishing recording");
            recorder.stop();
            recorder.release();
            PhoneMessenger.getInstance(getBaseContext()).sendMessage("/identifySong");
        }

    }

}
