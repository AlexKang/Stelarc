package orcaninjas.stelarc;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

public class WatchMessenger {

    private static WatchMessenger instance;

    private Context context;

    public static WatchMessenger getInstance(Context context) {
        if (instance == null) {
            instance = new WatchMessenger(context);
        }

        return instance;
    }

    private WatchMessenger(Context context) {
        this.context = context;
    }

    private GoogleApiClient getGoogleApiClient() {
        GoogleApiClient client = new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
        client.blockingConnect();

        return client;
    }

    public void sendMessage(final String message) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                GoogleApiClient client = getGoogleApiClient();
                NodeApi.GetConnectedNodesResult result = Wearable.NodeApi.getConnectedNodes(client).await();
                for (Node node: result.getNodes()) {
                    Wearable.MessageApi.sendMessage(client, node.getId(), message, null);
                }
                client.disconnect();
            }
        }).start();
    }

}
