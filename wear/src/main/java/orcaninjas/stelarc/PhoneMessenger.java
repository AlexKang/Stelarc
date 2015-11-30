package orcaninjas.stelarc;

import android.content.Context;
import android.os.Bundle;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;

public class PhoneMessenger implements GoogleApiClient.ConnectionCallbacks {

    private static PhoneMessenger instance;

    private Context context;
    private GoogleApiClient googleApiClient;
    private List<Node> nodes;
    private String pendingMessage;

    public static PhoneMessenger getInstance(Context context) {
        if (instance == null) {
            instance = new PhoneMessenger(context);
        }

        return instance;
    }

    private PhoneMessenger(Context context) {
        this.context = context;
    }

    @Override
    public void onConnected(Bundle bundle) {
        Wearable.NodeApi.getConnectedNodes(googleApiClient)
                .setResultCallback(new ResultCallback<NodeApi.GetConnectedNodesResult>() {
                    @Override
                    public void onResult(NodeApi.GetConnectedNodesResult getConnectedNodesResult) {
                        nodes = getConnectedNodesResult.getNodes();
                        if (pendingMessage != null) {
                            sendMessage(pendingMessage);
                            pendingMessage = null;
                        }
                    }
                });
    }

    @Override
    public void onConnectionSuspended(int i) {
        googleApiClient.disconnect();
        googleApiClient = null;
        nodes = null;
    }

    public void sendMessage(String message) {
        if (nodes == null) {
            pendingMessage = message;
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addApi(Wearable.API)
                    .addConnectionCallbacks(this)
                    .build();
            googleApiClient.connect();
        } else {
            for (Node node : nodes) {
                Wearable.MessageApi.sendMessage(
                        googleApiClient, node.getId(), message, null);
            }
        }
    }

}
