package com.eric.forceengine;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.util.SparseArray;
import android.view.Choreographer;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import com.eric.forceengine.objects.ColoredForceCircle;

import forceengine.objects.Force;
import forceengine.objects.ForceCircle;
import forceengine.objects.PointVector;
import forceengine.objects.RectVector;
import forceengine.objects.Vector;
import forceengine.physics.PhysicsEngine;

/**
 * Demos the capabilities of the Force Engine.
 */
public class ForceEngineActivity extends Activity implements View.OnTouchListener,
		SensorEventListener, SettingsFragment.OnSettingsInteractionListener {

	private static final String TAG = ForceEngineActivity.class.getSimpleName();

	private static final float RADIUS = UiUtils.getPxFromDp(36);
	private static final float MASS = 100;
	public static final float DEFAULT_RESTITUTION = 0.9f;
	public static final float DEFAULT_FRICTION = 0.1f;
	public static final boolean DEFAULT_GRAVITY_ENABLED = true;
	public static final boolean DEFAULT_TRAILS_ENABLED = true;

	private static final double DRAG_SPRING_CONSTANT = 1.0 / 5.0;
	private static final double DRAG_FRICTION = 1.0;
	private static final long DRAG_MIN_TIME = 200; // ms
	private static final double SELECT_SLOP = UiUtils.getPxFromDp(10);

	private RenderThread mRenderThread;

	private PhysicsEngine mEngine;
	private Vector mGravity;

	private SensorManager mSensorManager;
	private Sensor mSensor;

	private SparseArray<Pair<PointVector, forceengine.objects.Point>> mDragging = new SparseArray<>();

	private boolean mGravityEnabled = DEFAULT_GRAVITY_ENABLED;
	private boolean mTrailsEnabled = DEFAULT_TRAILS_ENABLED;
	private float mRestitution = DEFAULT_RESTITUTION;
	private float mFriction = DEFAULT_FRICTION;

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

				for (int i = 0; i < mDragging.size(); i++) {
					Pair<PointVector, forceengine.objects.Point> drag = mDragging.get(mDragging.keyAt(i));

					if (f == drag.first && drag.second != null) {
						v.add(new RectVector(
								(drag.second.getX() - f.getX()) * DRAG_SPRING_CONSTANT - f.getvx() * DRAG_FRICTION,
								(drag.second.getY() - f.getY()) * DRAG_SPRING_CONSTANT - f.getvy() * DRAG_FRICTION));
					}
				}

				v.add(new RectVector(-mFriction * pv.getvx(), -mFriction * pv.getvy()));

				if (mGravityEnabled) {
					v.add(mGravity);
				}

				return v;
			}
		};

		SurfaceView surface = (SurfaceView) findViewById(R.id.surface);
		if (surface != null) {
			surface.setOnTouchListener(this);
			surface.getHolder().addCallback(new SurfaceHolder.Callback() {
				@Override
				public void surfaceCreated(SurfaceHolder holder) {

				}

				@Override
				public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
					if (mEngine != null) {
						mEngine.setBounds(width, height);
					}
				}

				@Override
				public void surfaceDestroyed(SurfaceHolder holder) {

				}
			});
		}

		mRenderThread = new RenderThread(mEngine, surface, new Handler());
		mRenderThread.run();
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
		if (id == android.R.id.home) {
			onBackPressed();
		} else if (id == R.id.action_clear) {
			mEngine.clear();
			return true;
		} else if (id == R.id.action_settings) {
			FragmentManager fragmentManager = getFragmentManager();

			if (fragmentManager.findFragmentById(R.id.overlay) == null) {
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				Fragment fragment = SettingsFragment.newInstance(mRestitution, mFriction, mGravityEnabled, mTrailsEnabled);

				View overlay = findViewById(R.id.overlay);

				if (fragment != null && overlay != null) {
					overlay.setVisibility(View.VISIBLE);

					fragmentTransaction.add(R.id.overlay, fragment);
					fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					fragmentTransaction.addToBackStack(null);
					fragmentTransaction.commit();

					if (getActionBar() != null) {
						getActionBar().setDisplayHomeAsUpEnabled(true);
						getActionBar().setDisplayUseLogoEnabled(false);
						getActionBar().setTitle(R.string.title_fragment_settings);
					}
				}
			} else {
				onBackPressed();
			}
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onBackPressed() {
		FragmentManager fragmentManager = getFragmentManager();

		if (fragmentManager.findFragmentById(R.id.overlay) != null && getActionBar() != null) {
			getActionBar().setDisplayHomeAsUpEnabled(false);
			getActionBar().setTitle(R.string.app_name);
		}

		super.onBackPressed();
	}

	/**
	 * @param x the x dimension of a point
	 * @param y the y dimension of a point
	 * @return whether or not the point is near or inside a {@link forceengine.objects.ForceCircle}
	 * in the engine
	 */
	public PointVector isNearCircle(float x, float y) {
		double minDistSq = -1;
		double distSq;
		PointVector point = null;

		for (ForceCircle forceCircle : mEngine.getForceCircles()) {
			distSq = forceengine.objects.Point.distanceSq(x, y, forceCircle.getX(), forceCircle.getY());
			if (distSq < Math.pow(RADIUS + SELECT_SLOP, 2)
					&& (minDistSq == -1 || distSq <= minDistSq)) {
				point = forceCircle;
				minDistSq = distSq;
			}
		}

		return point;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		final int action = event.getAction();

		switch (action & MotionEvent.ACTION_MASK) {
			case MotionEvent.ACTION_POINTER_DOWN:
			case MotionEvent.ACTION_DOWN:
				for (int i = 0; i < event.getPointerCount(); i++) {
					int id = event.getPointerId(i);

					if (mDragging.get(id) == null) {
						PointVector touching = isNearCircle(event.getX(i), event.getY(i));

						if (touching != null) {
							mDragging.put(id, new Pair<>(
									touching,
									new forceengine.objects.Point(event.getX(i), event.getY(i))
							));
						} else {
							mDragging.put(id, new Pair<PointVector, forceengine.objects.Point>(
									null,
									new forceengine.objects.Point(event.getX(i), event.getY(i))
							));
						}
					}
				}

				break;

			case MotionEvent.ACTION_MOVE:

				for (int i = 0; i < event.getPointerCount(); i++) {
					int id = event.getPointerId(i);

					if (mDragging.get(id) != null) {
						Pair<PointVector, forceengine.objects.Point> dragging = mDragging.get(id);

						if (dragging.first != null && dragging.second != null) {
							dragging.second.setX(event.getX(i));
							dragging.second.setY(event.getY(i));
						}
					}
				}

				break;

			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
			case MotionEvent.ACTION_CANCEL:

				// Extract the index of the pointer that left the touch sensor
				final int pointerIndex = (action & MotionEvent.ACTION_POINTER_INDEX_MASK)
						>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;

				int id = event.getPointerId(pointerIndex);

				if (mDragging.get(id) != null) {

					Pair<PointVector, forceengine.objects.Point> dragging = mDragging.get(id);

					if (dragging.first == null && dragging.second != null &&
							event.getEventTime() - event.getDownTime() < DRAG_MIN_TIME) {
						mEngine.addForceCircle(new ColoredForceCircle(
								event.getX(pointerIndex), event.getY(pointerIndex), 0, 0,
								RADIUS, MASS, mRestitution, UiUtils.randomColor(mEngine)));
					}

					mDragging.remove(id);
					break;
				}

				break;
		}

		return true;
	}

	@Override
	protected void onResume() {
		super.onResume();
		mSensorManager.registerListener(this, mSensor, SensorManager.SENSOR_DELAY_NORMAL);

		// If we already have a Surface, we just need to resume the frame notifications.
		if (mRenderThread != null) {
			mRenderThread.clearNext();
			Choreographer.getInstance().postFrameCallback(mRenderThread);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		mSensorManager.unregisterListener(this);
		Choreographer.getInstance().removeFrameCallback(mRenderThread);
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		if (mGravity != null) {
			mGravity.setvx(-event.values[0]);
			mGravity.setvy(event.values[1]);
		}
	}

	@Override
	public void onRestitutionChanged(float restitution) {
		mRestitution = restitution;

		for (ForceCircle forceCircle : mEngine.getForceCircles()) {
			forceCircle.setRestitution(restitution);
		}
	}

	@Override
	public void onFrictionChanged(float friction) {
		mFriction = friction;
	}

	@Override
	public void onGravityEnabledChanged(boolean gravityEnabled) {
		mGravityEnabled = gravityEnabled;
	}

	@Override
	public void onTrailsEnabledChanged(boolean trailsEnabled) {
		mTrailsEnabled = trailsEnabled;

		mRenderThread.setClearColor(
				mTrailsEnabled ? RenderThread.CLEAR_TRAILS : RenderThread.CLEAR_WHITE);
	}
}