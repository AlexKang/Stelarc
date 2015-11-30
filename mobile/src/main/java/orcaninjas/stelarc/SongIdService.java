package orcaninjas.stelarc;

import android.app.Service;
import android.content.Intent;
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

public class SongIdService extends Service {

    private static final String TAG = "Stelarc";
    private static final String GNDSK_CLIENT_ID = "2027543146";
    private static final String GNDSDK_CLIENT_TAG = "93CDDBFB0DD5F46424380F8678D34BD8";
    private static final String GNSDK_CLIENT_LICENSE = "-- BEGIN LICENSE v1.0 0AA8A4D2 --\\r\\nname: \\r\\nnotes: Gracenote Open Developer Program\\r\\nstart_date: 0000-00-00\\r\\nclient_id: 2027543146\\r\\nmusicid_file: enabled\\r\\nmusicid_text: enabled\\r\\nmusicid_stream: enabled\\r\\nmusicid_cd: enabled\\r\\nplaylist: enabled\\r\\nvideoid: enabled\\r\\nvideo_explore: enabled\\r\\nlocal_images: enabled\\r\\nlocal_mood: enabled\\r\\nvideoid_explore: enabled\\r\\nacr: enabled\\r\\nepg: enabled\\r\\n-- SIGNATURE 0AA8A4D2 --\\r\\nlAADAgAfASKCnUydA08x9EmMZjYYv7KIvtBoD0rnzjrOWVHNtAAfAT62saAaWf7iL5/BnowDBFpe1T2buLskTUK2x2h+jg==\\r\\n-- END LICENSE 0AA8A4D2 --\\r\\n";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            GnManager gnManager = new GnManager(this, GNSDK_CLIENT_LICENSE, GnLicenseInputMode.kLicenseInputModeString);
            GnUser gnUser = new GnUser(new GnUserStore(this), GNDSK_CLIENT_ID, GNDSDK_CLIENT_TAG, TAG);
            IGnMusicIdStreamEvents streamEvents = new IGnMusicIdStreamEvents() {
                @Override
                public void musicIdStreamProcessingStatusEvent(GnMusicIdStreamProcessingStatus gnMusicIdStreamProcessingStatus, IGnCancellable iGnCancellable) {

                }

                @Override
                public void musicIdStreamIdentifyingStatusEvent(GnMusicIdStreamIdentifyingStatus gnMusicIdStreamIdentifyingStatus, IGnCancellable iGnCancellable) {

                }

                @Override
                public void musicIdStreamAlbumResult(GnResponseAlbums gnResponseAlbums, IGnCancellable iGnCancellable) {

                }

                @Override
                public void musicIdStreamIdentifyCompletedWithError(GnError gnError) {

                }

                @Override
                public void statusEvent(GnStatus gnStatus, long l, long l1, long l2, IGnCancellable iGnCancellable) {

                }
            };
            GnMusicIdStream stream = new GnMusicIdStream(gnUser, GnMusicIdStreamPreset.kPresetInvalid, streamEvents);
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
}
