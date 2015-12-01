package orcaninjas.stelarc;

import android.content.Context;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.PutDataRequest;
import com.google.android.gms.wearable.Wearable;

public class PhoneMessenger {

    private static PhoneMessenger instance;

    private Context context;

    public static PhoneMessenger getInstance(Context context) {
        if (instance == null) {
            instance = new PhoneMessenger(context);
        }

        return instance;
    }

    private PhoneMessenger(Context context) {
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

    public void sendAsset(final byte[] data) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                GoogleApiClient client = getGoogleApiClient();
                PutDataRequest request = PutDataRequest.create("/song");
                request.setData(data);
                Wearable.DataApi.putDataItem(client, request);
                client.disconnect();
            }
        }).start();
    }

}
