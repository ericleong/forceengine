package com.eric.forceengine;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.util.Log;
import android.view.Choreographer;
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
 * <p/>
 * Created by Eric on 8/25/2014.
 */
public class RenderThread implements Runnable, Choreographer.FrameCallback {

	private static final String TAG = RenderThread.class.getSimpleName();

	public static final int CLEAR_WHITE = Color.WHITE;
	public static final int CLEAR_TRAILS = Color.argb(128, 255, 255, 255);

	private PhysicsEngine mEngine;
	private SurfaceView mSurface;
	private Handler mHandler;

	private Paint mPaint;
	private int mClearColor;

	private boolean mClear;

	public RenderThread(PhysicsEngine engine, SurfaceView surfaceView, Handler handler) {
		mEngine = engine;
		mSurface = surfaceView;
		mHandler = handler;

		mPaint = new Paint();
		mPaint.setAntiAlias(true);

		mClearColor = CLEAR_TRAILS;
	}

	@Override
	public void run() {
		try {
			mEngine.components();
		} catch (Exception e) {
			Log.e(TAG, "Error on main thread.", e);
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
			if (mClear) {
				mPaint.setColor(Color.WHITE);
				mClear = false;
			} else {
				mPaint.setColor(mClearColor);
			}
			canvas.drawRect(0, 0, mSurface.getMeasuredWidth(), mSurface.getMeasuredHeight(), mPaint);

			mPaint.setColor(Color.GRAY);

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

	@Override
	public void doFrame(long frameTimeNanos) {
		render();

		if (mHandler != null) {
			mHandler.post(this);
		}

		Choreographer.getInstance().postFrameCallback(this);
	}

	public void clearNext() {
		mClear = true;
	}

	public void setClearColor(int mClearColor) {
		this.mClearColor = mClearColor;
	}
}
