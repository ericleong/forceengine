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
 * Created by Eric on 8/25/2014.
 */
public class RenderThread implements Runnable {

	private static final String TAG = RenderThread.class.getSimpleName();

	private PhysicsEngine mEngine;
	private SurfaceView mSurface;
	private Handler mHandler;

	public RenderThread(PhysicsEngine engine, SurfaceView surfaceView, Handler handler) {
		mEngine = engine;
		mSurface = surfaceView;
		mHandler = handler;
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
			Paint paint = new Paint();

			paint.setColor(Color.argb(128, 255, 255, 255));
			canvas.drawRect(0, 0, mSurface.getMeasuredWidth(), mSurface.getMeasuredHeight(), paint);

			paint.setColor(Color.GRAY);

			for (StaticCircle sc : mEngine.getStaticCircles()) {
				canvas.drawCircle((float) sc.getX(), (float) sc.getY(), (float) sc.getRadius(), paint);
			}
			for (StaticLine l : mEngine.getLines()) {
				canvas.drawLine((float) l.getX1(), (float) l.getY1(), (float) l.getY1(), (float) l.getY2(), paint);
			}
			for (ForceCircle fc : mEngine.getForceCircles()) {
				if (fc instanceof ColoredForceCircle) {
					paint.setColor(((ColoredForceCircle) fc).getColor());
				} else {
					paint.setColor(Color.GRAY);
				}
				canvas.drawCircle((float) fc.getX(), (float) fc.getY(), (float) fc.getRadius(), paint);
			}
		}
	}
}
