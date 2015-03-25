package androidwear_projekt2.bfh.ch.accelerationorientationdemo;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.androidplot.ui.SizeLayoutType;
import com.androidplot.ui.SizeMetrics;
import com.androidplot.xy.BoundaryMode;
import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYStepMode;

import java.text.DecimalFormat;


public class MainActivity extends Activity {

    private static final int HISTORY_SIZE = 400;

    private XYPlot mOrientPlot = null;
    private XYPlot mAccelPlot = null;

    private SimpleXYSeries mXOrientSeries = null;
    private SimpleXYSeries mYOrientSeries = null;
    private SimpleXYSeries mZOrientSeries = null;
    private SimpleXYSeries mXAccelSeries = null;
    private SimpleXYSeries mYAccelSeries = null;
    private SimpleXYSeries mZAccelSeries = null;

    private TextView mXOrientView = null;
    private TextView mYOrientView = null;
    private TextView mZOrientView = null;
    private TextView mXAccelView = null;
    private TextView mYAccelView = null;
    private TextView mZAccelView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, intentFilter);

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

    public class MessageReceiver extends BroadcastReceiver {

        private final static String MESSAGE_PATH_ORIENT = "orientValues";
        private final static String MESSAGE_PATH_ACCEL = "accelValues";

        @Override
        public void onReceive(Context context, Intent intent) {
            System.out.println("MsgReceiver: onReceive");

            String messagePath = intent.getStringExtra("messagePath");

            if (intent.getStringExtra("messagePath").equals(MESSAGE_PATH_ORIENT)) {
                String values = intent.getStringExtra("values");
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
            } else if (intent.getStringExtra("messagePath").equals(MESSAGE_PATH_ACCEL)) {
                String values = intent.getStringExtra("values");
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
    }
}
