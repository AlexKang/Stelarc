package orcaninjas.stelarc;

import android.util.Log;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class WatchListenerService extends WearableListenerService {

    private static final String TAG = "WatchListenerService";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, String.format("Messaged received: %s", messageEvent.getPath()));
    }

}