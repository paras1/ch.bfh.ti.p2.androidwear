package androidwear_projekt2.bfh.ch.accelerationorientationdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.widget.TextView;

import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

import java.text.DecimalFormat;
import java.util.List;

public class MainActivity extends Activity implements SensorEventListener {

    private static final int HISTORY_SIZE = 400;

    private SensorManager mSensorManager;
    private Sensor mOrientation;

    private XYPlot orientPlot = null;

    private SimpleXYSeries xSeries = null;
    private SimpleXYSeries ySeries = null;
    private SimpleXYSeries zSeries = null;

    private TextView mXView;
    private TextView mYView;
    private TextView mZView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.rect_activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        mXView = (TextView) findViewById(R.id.xView);
        mYView = (TextView) findViewById(R.id.yView);
        mZView = (TextView) findViewById(R.id.zView);

        initOrientPlot();

        List<Sensor> mListSensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        for (Sensor sensor:mListSensors) {
            System.out.println(sensor.getName());
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        float mXValue = event.values[0];
        float mYValue = event.values[1];
        float mZValue = event.values[2];

        System.out.println(mXValue);

        mXView.setText("X axis=" + String.valueOf(mXValue));
        mYView.setText("Y axis=" + String.valueOf(mYValue));
        mZView.setText("Z axis=" + String.valueOf(mZValue));

        if (xSeries.size() > HISTORY_SIZE) {
            xSeries.removeFirst();
            ySeries.removeFirst();
            zSeries.removeFirst();
        }
        xSeries.addLast(null, event.values[0]);
        ySeries.addLast(null, event.values[1]);
        zSeries.addLast(null, event.values[2]);

        orientPlot.redraw();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mOrientation, SensorManager.SENSOR_DELAY_FASTEST);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void initOrientPlot() {
        orientPlot = (XYPlot) findViewById(R.id.orientPlot);

        xSeries = new SimpleXYSeries("X (Pitch)");
        xSeries.useImplicitXVals();
        ySeries = new SimpleXYSeries("Y (Azimuth)");
        ySeries.useImplicitXVals();
        zSeries = new SimpleXYSeries("Z (Roll)");
        zSeries.useImplicitXVals();

        orientPlot.setBackgroundColor(Color.BLACK);
        orientPlot.getBackgroundPaint().setColor(Color.BLACK);
        orientPlot.getGraphWidget().getBackgroundPaint().setColor(Color.BLACK);
        orientPlot.getGraphWidget().getGridBackgroundPaint().setColor(Color.BLACK);

        orientPlot.setRangeBoundaries(-180, 360, BoundaryMode.FIXED);
        orientPlot.setDomainBoundaries(0, HISTORY_SIZE, BoundaryMode.FIXED);
        orientPlot.setDomainStep(XYStepMode.INCREMENT_BY_VAL, 50);
        orientPlot.getLegendWidget().setSize(new SizeMetrics(50, SizeLayoutType.ABSOLUTE, 750, SizeLayoutType.ABSOLUTE));
        //accelPlot.setDomainStepValue(HISTORY_SIZE / 3);
        //accelPlot.setTicksPerDomainLabel(10);
        //accelPlot.setTicksPerRangeLabel(1);
        orientPlot.setDomainValueFormat(new DecimalFormat("#"));
        orientPlot.setDomainLabel(null);
        orientPlot.setRangeLabel(null);
        orientPlot.setRangeValueFormat(new DecimalFormat("#"));

        orientPlot.addSeries(xSeries, new LineAndPointFormatter(Color.rgb(100, 100, 200), null, null, null));
        orientPlot.addSeries(ySeries, new LineAndPointFormatter(Color.rgb(100, 200, 100), null, null, null));
        orientPlot.addSeries(zSeries, new LineAndPointFormatter(Color.rgb(200, 100, 200), null, null, null));
    }
}
