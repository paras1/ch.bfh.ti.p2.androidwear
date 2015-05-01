package androidwear_projekt2.bfh.ch.accelerationorientationdemo;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

/**
 * Created by Christian Basler on 01.05.15.
 */
public class NetworkListener {
    private MqttClient mqttClient;
    private MainActivity ctx;

    public NetworkListener(MainActivity ctx) {
        this.ctx = ctx;
        try {
            MemoryPersistence persistence = new MemoryPersistence();
            mqttClient = new MqttClient("tcp://smoje.ch:1883", "ch.bfh.mqtt.android", persistence);
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
                    ctx.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (topic.endsWith("accelerometer"))
                                ctx.updateAcceleration(value);
                            if (topic.contains("orientation"))
                                ctx.updateOrientation(value);
                        }
                    });
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken imdt) {
                }
            });

            mqttClient.connect(options);
            mqttClient.subscribe("ch/bfh/mqtt/android/" + user + "/+");
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
