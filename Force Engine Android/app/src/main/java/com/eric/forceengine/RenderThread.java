package com.eric.forceengine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.SystemClock;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.eric.forceengine.objects.ColoredForceCircle;

import forceengine.objects.ForceCircle;
import forceengine.objects.StaticCircle;
import forceengine.objects.StaticLine;
import forceengine.physics.PhysicsEngine;

/**
 * Renders the objects in the {@link forceengine.physics.PhysicsEngine} onto a
 * {@link android.view.SurfaceView}.
 *
 * Created by Eric on 8/25/2014.
 */
public class RenderThread implements Runnable {

	private static final String TAG = RenderThread.class.getSimpleName();

	private PhysicsEngine mEngine;
	private SurfaceView mSurface;
	private Handler mHandler;

	private Paint mPaint;

	public RenderThread(PhysicsEngine engine, SurfaceView surfaceView, Handler handler) {
		mEngine = engine;
		mSurface = surfaceView;
		mHandler = handler;

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
	}

	@Override
	public void run() {
		long start = SystemClock.uptimeMillis();

		try {
			mEngine.components();

			render();
		} catch (Exception e) {
			Log.e(TAG, "Error on main thread.", e);
		} finally {
			if (mHandler != null) {
				mHandler.postDelayed(this,
						Math.max(ForceEngineActivity.FRAME_DURATION - SystemClock.uptimeMillis() - start, 0));
			}
		}
	}

	private void render() {
		if (mSurface != null && mSurface.getHolder() != null) {
			Canvas canvas = null;
			SurfaceHolder holder = mSurface.getHolder();

			try {
				synchronized (holder) {
					canvas = holder.lockCanvas();

					renderCanvas(canvas);
				}
			} finally {
				if (canvas != null && holder != null) {
					holder.unlockCanvasAndPost(canvas);
				}
			}
		}
	}

	private void renderCanvas(Canvas canvas) {
		if (canvas != null) {
			mPaint.setColor(Color.argb(128, 255, 255, 255));
			canvas.drawRect(0, 0, mSurface.getMeasuredWidth(), mSurface.getMeasuredHeight(), mPaint);

			mPaint.setColor(Color.GRAY);
			mPaint.setAntiAlias(true);

			for (StaticCircle sc : mEngine.getStaticCircles()) {
				canvas.drawCircle((float) sc.getX(), (float) sc.getY(), (float) sc.getRadius(), mPaint);
			}
			for (StaticLine l : mEngine.getLines()) {
				canvas.drawLine((float) l.getX1(), (float) l.getY1(), (float) l.getY1(), (float) l.getY2(), mPaint);
			}
			for (ForceCircle fc : mEngine.getForceCircles()) {
				if (fc instanceof ColoredForceCircle) {
					mPaint.setColor(((ColoredForceCircle) fc).getColor());
				} else {
					mPaint.setColor(Color.GRAY);
				}
				canvas.drawCircle((float) fc.getX(), (float) fc.getY(), (float) fc.getRadius(), mPaint);
			}
		}
	}
}
