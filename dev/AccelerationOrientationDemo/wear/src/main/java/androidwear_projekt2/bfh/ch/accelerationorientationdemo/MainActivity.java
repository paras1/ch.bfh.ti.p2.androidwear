package androidwear_projekt2.bfh.ch.accelerationorientationdemo;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements SensorEventListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final static long CONNECTION_TIME_OUT_MS = 100;
    private final static String MESSAGE_PATH_ACCEL = "accelValues";
    private final static String MESSAGE_PATH_ORIENT = "orientValues";

    private SensorManager mSensorManager = null;
    private Sensor mOrientation = null;
    private Sensor mAccelerometer = null;

    private GoogleApiClient client = null;
    private String nodeId = null;

    private Button mBtnStartLogging = null;
    private Button mBtnStopLogging = null;
    private TextView mXAccelView = null;
    private TextView mYAccelView = null;
    private TextView mZAccelView = null;
    private TextView mXOrientView = null;
    private TextView mYOrientView = null;
    private TextView mZOrientView = null;

    private boolean started = false;
    private String xValue = "";
    private String yValue = "";
    private String zValue = "";


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        client = getGoogleApiClient(this);
        retrieveDeviceNode();

        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mXAccelView = (TextView) stub.findViewById(R.id.xAccelView);
                mYAccelView = (TextView) stub.findViewById(R.id.yAccelView);
                mZAccelView = (TextView) stub.findViewById(R.id.zAccelView);
                mXOrientView = (TextView) stub.findViewById(R.id.xOrientView);
                mYOrientView = (TextView) stub.findViewById(R.id.yOrientView);
                mZOrientView = (TextView) stub.findViewById(R.id.zOrientView);

                mBtnStartLogging = (Button) stub.findViewById(R.id.btnStartLogging);
                mBtnStartLogging.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        started = true;
                    }
                });
                mBtnStopLogging = (Button) stub.findViewById(R.id.btnStopLogging);
                mBtnStopLogging.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View view) {
                        started = false;
                    }
                });
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        client.connect();
    }

    @Override
    protected void onStop() {
        if (client != null && client.isConnected()) {
            client.disconnect();
        }
        super.onStop();
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private void retrieveDeviceNode() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    nodeId = nodes.get(0).getId();
                    System.out.println(nodeId);
                }
                //client.disconnect();
            }
        }).start();
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        final int sensorType = event.sensor.getType();

        if (started) {
            xValue = String.valueOf(event.values[0]);
            yValue = String.valueOf(event.values[1]);
            zValue = String.valueOf(event.values[2]);

            String values = xValue + ";" + yValue + ";" + zValue;

            if (nodeId != null) {
                if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                    Wearable.MessageApi.sendMessage(client, nodeId, MESSAGE_PATH_ACCEL, values.getBytes());
                } else if (sensorType == Sensor.TYPE_ORIENTATION) {
                    Wearable.MessageApi.sendMessage(client, nodeId, MESSAGE_PATH_ORIENT, values.getBytes());
                }

            }

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                        mXAccelView.setText("x = " + xValue);
                        mYAccelView.setText("y = " + yValue);
                        mZAccelView.setText("z = " + zValue);
                    } else if (sensorType == Sensor.TYPE_ORIENTATION) {
                        mXOrientView.setText("x = " + xValue);
                        mYOrientView.setText("y = " + yValue);
                        mZOrientView.setText("z = " + zValue);
                    }
                }
            });
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }
}
