package orcaninjas.stelarc;

import android.app.Service;
import android.content.Intent;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Environment;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.gracenote.gnsdk.GnError;
import com.gracenote.gnsdk.GnException;
import com.gracenote.gnsdk.GnLicenseInputMode;
import com.gracenote.gnsdk.GnManager;
import com.gracenote.gnsdk.GnMusicIdStream;
import com.gracenote.gnsdk.GnMusicIdStreamIdentifyingStatus;
import com.gracenote.gnsdk.GnMusicIdStreamPreset;
import com.gracenote.gnsdk.GnMusicIdStreamProcessingStatus;
import com.gracenote.gnsdk.GnResponseAlbums;
import com.gracenote.gnsdk.GnStatus;
import com.gracenote.gnsdk.GnUser;
import com.gracenote.gnsdk.GnUserStore;
import com.gracenote.gnsdk.IGnCancellable;
import com.gracenote.gnsdk.IGnMusicIdStreamEvents;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class SongIdService extends Service {

    private static final String TAG = "Stelarc";
    private static final String GNDSK_CLIENT_ID = "2027543146";
    private static final String GNDSDK_CLIENT_TAG = "93CDDBFB0DD5F46424380F8678D34BD8";
    private static final String GNSDK_CLIENT_LICENSE = "-- BEGIN LICENSE v1.0 0AA8A4D2 --\\r\\nname: \\r\\nnotes: Gracenote Open Developer Program\\r\\nstart_date: 0000-00-00\\r\\nclient_id: 2027543146\\r\\nmusicid_file: enabled\\r\\nmusicid_text: enabled\\r\\nmusicid_stream: enabled\\r\\nmusicid_cd: enabled\\r\\nplaylist: enabled\\r\\nvideoid: enabled\\r\\nvideo_explore: enabled\\r\\nlocal_images: enabled\\r\\nlocal_mood: enabled\\r\\nvideoid_explore: enabled\\r\\nacr: enabled\\r\\nepg: enabled\\r\\n-- SIGNATURE 0AA8A4D2 --\\r\\nlAADAgAfASKCnUydA08x9EmMZjYYv7KIvtBoD0rnzjrOWVHNtAAfAT62saAaWf7iL5/BnowDBFpe1T2buLskTUK2x2h+jg==\\r\\n-- END LICENSE 0AA8A4D2 --\\r\\n";
    private static final String STELARC_DIR = Environment.getExternalStorageDirectory() + "/Stelarc";

    private static final long SAMPLE_RATE = 44100;
    private static final int BIT_RATE = 16;
    private static final int CHANNELS = 1;

    private static SongIdService instance;
    private static GnMusicIdStream stream;
    private static File audioFile;
    private static BufferedOutputStream writer;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        instance = this;

        File folder = new File(STELARC_DIR);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdir();
        }

        if (!success) {
            stopSelf();
            return -1;
        }

        try {
            audioFile = new File(STELARC_DIR + "/" + System.currentTimeMillis() + ".pcm");
            success = audioFile.createNewFile();
            if (!success) {
                stopSelf();
                return -1;
            }
            writer = new BufferedOutputStream(new FileOutputStream(audioFile));
        } catch (IOException e) {
            Log.e(TAG, e.toString());
        }

        try {
            new GnManager(this, GNSDK_CLIENT_LICENSE, GnLicenseInputMode.kLicenseInputModeString);
            GnUser gnUser = new GnUser(new GnUserStore(this), GNDSK_CLIENT_ID, GNDSDK_CLIENT_TAG, TAG);
            IGnMusicIdStreamEvents streamEvents = new StreamEvents();
            stream = new GnMusicIdStream(gnUser, GnMusicIdStreamPreset.kPresetInvalid, streamEvents);
            stream.audioProcessStart(SAMPLE_RATE, BIT_RATE, CHANNELS);
        } catch (GnException e) {
            Log.e(TAG, e.toString());
        }

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public static void sendAudio(byte[] data) {
        if (instance != null && stream != null) {
            try {
                writer.write(data);
                stream.audioProcess(data);
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    public static void identify() {
        if (instance != null && stream != null) {
            try {
                writer.flush();
                stream.identifyAlbumAsync();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    private class StreamEvents implements IGnMusicIdStreamEvents {

        @Override
        public void musicIdStreamProcessingStatusEvent(GnMusicIdStreamProcessingStatus gnMusicIdStreamProcessingStatus, IGnCancellable iGnCancellable) {

        }

        @Override
        public void musicIdStreamIdentifyingStatusEvent(GnMusicIdStreamIdentifyingStatus gnMusicIdStreamIdentifyingStatus, IGnCancellable iGnCancellable) {

        }

        @Override
        public void musicIdStreamAlbumResult(GnResponseAlbums gnResponseAlbums, IGnCancellable iGnCancellable) {
            try {
                Log.d(TAG, "Identified songs found: " + gnResponseAlbums.resultCount());
                Log.d(TAG, "Identified song: " +
                                gnResponseAlbums.albums().getIterator().next().artist().name().display() +
                                " - " +
                                gnResponseAlbums.albums().getIterator().next().trackMatched().title().display()
                );

                int bufferSize = AudioTrack.getMinBufferSize((int) SAMPLE_RATE, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
                AudioTrack audioTrack = new AudioTrack(
                        AudioManager.STREAM_MUSIC,
                        (int) SAMPLE_RATE,
                        AudioFormat.CHANNEL_OUT_MONO,
                        AudioFormat.ENCODING_PCM_16BIT,
                        bufferSize,
                        AudioTrack.MODE_STREAM
                );
                audioTrack.play();
                byte[] buffer = new byte[bufferSize];
                BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(audioFile));
                int bytesRead = inputStream.read(buffer, 0, bufferSize);
                while (bytesRead != -1) {
                    audioTrack.write(buffer, 0, bytesRead);
                    bytesRead = inputStream.read(buffer, 0, bufferSize);
                }
                audioTrack.stop();

                stopSelf();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }

        @Override
        public void musicIdStreamIdentifyCompletedWithError(GnError gnError) {
            Log.d(TAG, "No song found: " + gnError.errorDescription());
            stopSelf();
        }

        @Override
        public void statusEvent(GnStatus gnStatus, long l, long l1, long l2, IGnCancellable iGnCancellable) {

        }

    }

}
