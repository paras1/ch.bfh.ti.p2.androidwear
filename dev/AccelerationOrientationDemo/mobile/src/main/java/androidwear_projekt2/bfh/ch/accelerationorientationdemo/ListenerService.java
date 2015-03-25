package androidwear_projekt2.bfh.ch.accelerationorientationdemo;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.WearableListenerService;

/**
 * Created by Fanky on 20.03.15.
 */
public class ListenerService extends WearableListenerService {

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        System.out.println("Msg received.");

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        intent.putExtra("messagePath", messageEvent.getPath());
        intent.putExtra("values", new String(messageEvent.getData()));
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

}
