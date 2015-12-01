package orcaninjas.stelarc;

import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.wearable.DataEvent;
import com.google.android.gms.wearable.DataEventBuffer;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

public class WatchListenerService extends WearableListenerService {

    private static final String TAG = "WatchListenerService";

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        Log.d(TAG, String.format("Messaged received: %s", messageEvent.getPath()));
        Toast.makeText(getBaseContext(), String.format("Messaged received: %s", messageEvent.getPath()), Toast.LENGTH_SHORT).show();

        switch (messageEvent.getPath()) {
            case "/startId":
                Intent intent = new Intent(this, SongIdService.class);
                startService(intent);
                WatchMessenger.getInstance(this).sendMessage("/ack");
                break;
            case "/identifySong":
                SongIdService.identify();
        }
    }

    @Override
    public void onDataChanged(DataEventBuffer dataEvents) {
        for (DataEvent event : dataEvents) {
            if (event.getType() == DataEvent.TYPE_CHANGED &&
                    event.getDataItem().getUri().getPath().equals("/song")) {
                SongIdService.sendAudio(event.getDataItem().getData());
            }
        }
    }

}