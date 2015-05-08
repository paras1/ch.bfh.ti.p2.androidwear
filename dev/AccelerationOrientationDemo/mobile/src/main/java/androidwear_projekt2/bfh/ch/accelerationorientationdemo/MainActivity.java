package androidwear_projekt2.bfh.ch.accelerationorientationdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.text.DecimalFormat;


public class MainActivity extends Activity implements SensorEventListener {

    private static final int HISTORY_SIZE = 400;

    private XYPlot mOrientPlot = null;
    private XYPlot mAccelPlot = null;

    private SimpleXYSeries mXOrientSeries = null;
    private SimpleXYSeries mYOrientSeries = null;
    private SimpleXYSeries mZOrientSeries = null;
    private SimpleXYSeries mXAccelSeries = null;
    private SimpleXYSeries mYAccelSeries = null;
    private SimpleXYSeries mZAccelSeries = null;

    private TextView mClientId = null;
    private TextView mXOrientView = null;
    private TextView mYOrientView = null;
    private TextView mZOrientView = null;
    private TextView mXAccelView = null;
    private TextView mYAccelView = null;
    private TextView mZAccelView = null;

    private Switch mSwitchSend = null;
    private Switch mSwitchReceive = null;

    private NetworkListener networkListener;

    private SensorManager mSensorManager = null;
    private Sensor mOrientation = null;
    private Sensor mAccelerometer = null;

    private String xValue = "";
    private String yValue = "";
    private String zValue = "";

    private MqttClient mqttClient;

    private long lastUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mClientId = (TextView) findViewById(R.id.clientId);
        mSwitchSend = (Switch) findViewById(R.id.switchSend);
        mSwitchReceive = (Switch) findViewById(R.id.switchReceive);

        mSwitchSend.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    try {
                        MemoryPersistence persistence = new MemoryPersistence();
                        mqttClient = new MqttClient("tcp://smoje.ch:1883", "ch.bfh.mqtt.android." + mClientId.getText() + ".accelerometer", persistence);
                        MqttConnectOptions options = new MqttConnectOptions();
                        options.setCleanSession(false);
                        mqttClient.connect(options);
                        System.out.println("WE ARE CONNECTED");
                        System.out.println("ClientId: " + mqttClient.getClientId());
                    } catch (MqttException e) {
                        Log.e("main", e.getMessage(), e);
                    }
                } else {
                    try {
                        mqttClient.disconnect();
                    } catch (MqttException e) {
                        Log.e("main", e.getMessage(), e);
                    }
                }
            }
        });

        networkListener = new NetworkListener(this);
        mSwitchReceive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    networkListener.start(mClientId.getText().toString());
                } else {
                    networkListener.stop();
                }
            }
        });

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);

        mXOrientView = (TextView) findViewById(R.id.xOrientView);
        mYOrientView = (TextView) findViewById(R.id.yOrientView);
        mZOrientView = (TextView) findViewById(R.id.zOrientView);
        mXAccelView = (TextView) findViewById(R.id.xAccelView);
        mYAccelView = (TextView) findViewById(R.id.yAccelView);
        mZAccelView = (TextView) findViewById(R.id.zAccelView);

        initOrientPlot();
        initAccelPlot();
    }


    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mOrientation, 1000000);
        mSensorManager.registerListener(this, mAccelerometer, 1000000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(final SensorEvent event) {
        final int sensorType = event.sensor.getType();

        xValue = String.valueOf(event.values[0]);
        yValue = String.valueOf(event.values[1]);
        zValue = String.valueOf(event.values[2]);

        String values = xValue + ";" + yValue + ";" + zValue;

        if (mqttClient != null && mqttClient.isConnected()) {
            try {
                MqttMessage message = new MqttMessage(values.getBytes());
                if (sensorType == Sensor.TYPE_ACCELEROMETER) {
                    mqttClient.publish("ch.bfh.mqtt.android.accelerometer", message);
                } /*else if (sensorType == Sensor.TYPE_ORIENTATION) {
                mqttClient.publish("ch/bfh/mqtt/android/1/orientation", message);
            }*/
                System.out.println(message.toString());
            } catch (Exception e) {
                Log.e("main", e.getMessage(), e);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void initOrientPlot() {
        mOrientPlot = (XYPlot) findViewById(R.id.orientPlot);

        mXOrientSeries = new SimpleXYSeries("X (Pitch)");
        mXOrientSeries.useImplicitXVals();
        mYOrientSeries = new SimpleXYSeries("Y (Roll)");
        mYOrientSeries.useImplicitXVals();
        mZOrientSeries = new SimpleXYSeries("Z (Azimuth)");
        mZOrientSeries.useImplicitXVals();

        mOrientPlot.setBackgroundColor(Color.BLACK);
        mOrientPlot.getBackgroundPaint().setColor(Color.BLACK);
        mOrientPlot.getGraphWidget().getBackgroundPaint().setColor(Color.BLACK);
        mOrientPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.BLACK);

        mOrientPlot.setRangeBoundaries(-180, 359, BoundaryMode.FIXED);
        mOrientPlot.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);
        mOrientPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 50);
        mOrientPlot.getLegendWidget().setSize(new SizeMetrics(50, SizeLayoutType.ABSOLUTE, 750, SizeLayoutType.ABSOLUTE));
        mOrientPlot.setDomainValueFormat(new DecimalFormat("#"));
        mOrientPlot.setDomainLabel(null);
        mOrientPlot.setRangeLabel(null);
        mOrientPlot.setRangeValueFormat(new DecimalFormat("#"));

        mOrientPlot.addSeries(mXOrientSeries, new LineAndPointFormatter(Color.rgb(100, 100, 200), null, null, null));
        mOrientPlot.addSeries(mYOrientSeries, new LineAndPointFormatter(Color.rgb(100, 200, 100), null, null, null));
        mOrientPlot.addSeries(mZOrientSeries, new LineAndPointFormatter(Color.rgb(200, 100, 200), null, null, null));
    }

    public void initAccelPlot() {
        mAccelPlot = (XYPlot) findViewById(R.id.accelPlot);

        mXAccelSeries = new SimpleXYSeries("X (Pitch)");
        mXAccelSeries.useImplicitXVals();
        mYAccelSeries = new SimpleXYSeries("Y (Roll)");
        mYAccelSeries.useImplicitXVals();
        mZAccelSeries = new SimpleXYSeries("Z (Azimuth)");
        mZAccelSeries.useImplicitXVals();

        mAccelPlot.setBackgroundColor(Color.BLACK);
        mAccelPlot.getBackgroundPaint().setColor(Color.BLACK);
        mAccelPlot.getGraphWidget().getBackgroundPaint().setColor(Color.BLACK);
        mAccelPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.BLACK);

        mAccelPlot.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);
        mAccelPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 50);
        mAccelPlot.getLegendWidget().setSize(new SizeMetrics(50, SizeLayoutType.ABSOLUTE, 750, SizeLayoutType.ABSOLUTE));
        mAccelPlot.setDomainValueFormat(new DecimalFormat("#"));
        mAccelPlot.setDomainLabel(null);
        mAccelPlot.setRangeLabel(null);
        mAccelPlot.setRangeValueFormat(new DecimalFormat("#"));

        mAccelPlot.addSeries(mXAccelSeries, new LineAndPointFormatter(Color.rgb(100, 100, 200), null, null, null));
        mAccelPlot.addSeries(mYAccelSeries, new LineAndPointFormatter(Color.rgb(100, 200, 100), null, null, null));
        mAccelPlot.addSeries(mZAccelSeries, new LineAndPointFormatter(Color.rgb(200, 100, 200), null, null, null));
    }

    public void updateOrientation(String values) {
        String[] parts = values.split(";");

        mXOrientView.setText("x = " + parts[0]);
        mYOrientView.setText("y = " + parts[1]);
        mZOrientView.setText("z = " + parts[2]);

        if (mXOrientSeries.size() > HISTORY_SIZE) {
            mXOrientSeries.removeFirst();
            mYOrientSeries.removeFirst();
            mZOrientSeries.removeFirst();
        }
        mXOrientSeries.addLast(null, Float.parseFloat(parts[0]));
        mYOrientSeries.addLast(null, Float.parseFloat(parts[1]));
        mZOrientSeries.addLast(null, Float.parseFloat(parts[2]));

        mOrientPlot.redraw();
    }

    public void updateAcceleration(String values) {
        String[] parts = values.split(";");
        mXAccelView.setText("x = " + parts[0]);
        mYAccelView.setText("y = " + parts[1]);
        mZAccelView.setText("z = " + parts[2]);

        if (mXAccelSeries.size() > HISTORY_SIZE) {
            mXAccelSeries.removeFirst();
            mYAccelSeries.removeFirst();
            mZAccelSeries.removeFirst();
        }
        mXAccelSeries.addLast(null, Float.parseFloat(parts[0]));
        mYAccelSeries.addLast(null, Float.parseFloat(parts[1]));
        mZAccelSeries.addLast(null, Float.parseFloat(parts[2]));

        mAccelPlot.redraw();
    }
}
