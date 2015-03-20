package androidwear_projekt2.bfh.ch.accelerationorientationdemo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

import java.text.DecimalFormat;


public class MainActivity extends Activity implements SensorEventListener {

    private static final int HISTORY_SIZE = 400;

    private SensorManager mSensorManager = null;
    private Sensor mOrientation = null;

    private XYPlot orientPlot = null;

    private SimpleXYSeries xSeries = null;
    private SimpleXYSeries ySeries = null;
    private SimpleXYSeries zSeries = null;

    private TextView xView = null;
    private TextView yView = null;
    private TextView zView = null;

    private Button btnStart = null;
    private Button btnStop = null;

    private boolean started = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mOrientation = mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION);

        xView = (TextView) findViewById(R.id.xView);
        yView = (TextView) findViewById(R.id.yView);
        zView = (TextView) findViewById(R.id.zView);

        btnStart = (Button) findViewById(R.id.btnStart);
        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                started = true;
            }
        });
        btnStop = (Button) findViewById(R.id.btnStop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                started = false;
            }
        });

        initOrientPlot();
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

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (started) {
            if (xSeries.size() > HISTORY_SIZE) {
                xSeries.removeFirst();
                ySeries.removeFirst();
                zSeries.removeFirst();
            }
            xSeries.addLast(null, event.values[0]);
            ySeries.addLast(null, event.values[1]);
            zSeries.addLast(null, event.values[2]);

            orientPlot.redraw();

            xView.setText("x = " + event.values[0]);
            yView.setText("y = " + event.values[1]);
            zView.setText("z = " + event.values[2]);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

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

        orientPlot.setRangeBoundaries(-180, 359, BoundaryMode.FIXED);
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
