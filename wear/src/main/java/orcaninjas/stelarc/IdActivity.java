package orcaninjas.stelarc;

import android.os.Bundle;
import android.app.Activity;

import com.gracenote.gnsdk.GnMic;

import java.nio.ByteBuffer;

public class IdActivity extends Activity {

    private static final int BUFFER_SIZE = 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id);

        ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
        GnMic mic = new GnMic();
        mic.getData(buffer, BUFFER_SIZE);
    }

}
