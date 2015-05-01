package androidwear_projekt2.bfh.ch.accelerationorientationdemo;

import org.eclipse.paho.client.mqttv3.*;

/**
 * Created by Christian Basler on 01.05.15.
 */
public class NetworkListener {
    private MqttClient mqttClient;
    private MainActivity ctx;

    public NetworkListener(MainActivity ctx) {
        this.ctx = ctx;
        try {
            mqttClient = new MqttClient("tcp://smoje.ch:1883", "AccelerationOrientationDemo");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void start(String user) {
        try {
            //Establish connection and its options
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(false);

            //Establish the callback (to be done before connect!)
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable thrwbl) {
                }

                @Override
                public void messageArrived(final String topic, final MqttMessage mm) throws Exception {
                    final String value = new String(mm.getPayload());
                    //Remember: GSON (Json) would be good
                    ctx.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (topic.contains("acc"))
                                ctx.updateAcceleration(value);
                            if (topic.contains("orient"))
                                ctx.updateOrientation(value);
                        }
                    });
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken imdt) {
                }
            });

            mqttClient.connect(options);
            mqttClient.subscribe("ch/bfh/fbi/" + user + "/sensors/+/+");
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        try {
            mqttClient.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }
}
