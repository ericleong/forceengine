package com.eric.forceengine;

import android.content.Context;
import android.util.AttributeSet;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import forceengine.physics.PhysicsEngine;

/**
 * Created by Eric on 8/24/2014.
 */
public class ForceSurfaceView extends SurfaceView implements SurfaceHolder.Callback2 {

	private PhysicsEngine mEngine;

	public ForceSurfaceView(Context context) {
		super(context);
		init();
	}

	public ForceSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ForceSurfaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public void init() {
		if (getHolder() != null) {
			getHolder().addCallback(this);
		}
	}

	public void setEngine(PhysicsEngine engine) {
		this.mEngine = engine;
	}

	@Override
	public void surfaceRedrawNeeded(SurfaceHolder holder) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {

	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
		mEngine.setBounds(width, height);
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {

	}
}
