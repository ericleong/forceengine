package com.eric.forceengine;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;

import forceengine.objects.Force;
import forceengine.objects.ForceCircle;
import forceengine.objects.PointVector;
import forceengine.objects.RectVector;
import forceengine.objects.StaticCircle;
import forceengine.objects.StaticLine;
import forceengine.objects.Vector;
import forceengine.physics.PhysicsEngine;


public class ForceEngineActivity extends Activity implements View.OnTouchListener, SensorEventListener {

	private static final String TAG = ForceEngineActivity.class.getSimpleName();

	private static final long FRAME_DURATION = 16;
	private static final float RADIUS = UiUtils.getPxFromDp(40);
	private static final float MASS = 100;
	private static final double RESTITUTION = 0.9;

	private PhysicsEngine mEngine;
	private ForceSurfaceView mForceSurface;
	private Vector mGravity;

	private SensorManager mSensorManager;
	private Sensor mSensor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_force_engine);

		// sensors
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);

		// create engine
		Point size = new Point();
		getWindowManager().getDefaultDisplay().getSize(size);

		mGravity = new RectVector(0, 0);

		mEngine = new PhysicsEngine(size.x, size.y) {
			@Override
			public Vector accelerate(Force f, PointVector pv, double t) {
				Vector v = super.accelerate(f, pv, t);

				return v.add(mGravity);
			}
		};
		mForceSurface = (ForceSurfaceView) findViewById(R.id.surface);

		mForceSurface.setOnTouchListener(this);

		if (mForceSurface != null) {
			mForceSurface.setEngine(mEngine);
		}

		final Handler handler = new Handler();

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				long start = SystemClock.uptimeMillis();

				try {
					mEngine.components();

					if (mForceSurface != null) {
						render();
					}
				} catch (Exception e) {
					Log.e(TAG, "Error on main thread.", e);
				} finally {
					handler.postDelayed(this, Math.max(FRAME_DURATION - SystemClock.uptimeMillis() - start, 0));
				}
			}
		};

		runnable.run();
	}

	private void render() {
		if (mForceSurface != null && mForceSurface.getHolder() != null) {
			SurfaceHolder holder = mForceSurface.getHolder();

			Canvas canvas = holder.lockCanvas();

			if (canvas != null) {
				Paint paint = new Paint();

				paint.setColor(Color.WHITE);
				canvas.drawRect(0, 0, mForceSurface.getMeasuredWidth(), mForceSurface.getMeasuredHeight(), paint);

				paint.setColor(Color.BLUE);

				for (StaticCircle sc : mEngine.getStaticCircles()) {
					canvas.drawCircle((float) sc.getX(), (float) sc.getY(), (float) sc.getRadius(), paint);
				}
				for (StaticLine l : mEngine.getLines()) {
					canvas.drawLine((float) l.getX1(), (float) l.getY1(), (float) l.getY1(), (float) l.getY2(), paint);
				}
				for (ForceCircle fc : mEngine.getForceCircles()) {
					canvas.drawCircle((float) fc.getX(), (float) fc.getY(), (float) fc.getRadius(), paint);
				}

				holder.unlockCanvasAndPost(canvas);
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.force_engine, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_clear) {
			mEngine.clear();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mEngine.addForceCircle(new ForceCircle(event.getX(), event.getY(), 0, 0, RADIUS, MASS, RESTITUTION));
				break;
		}

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.values != null) {
			mGravity.setvx(-event.values[0]);
			mGravity.setvy(event.values[1]);
		}
	}
}
