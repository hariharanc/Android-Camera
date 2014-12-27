package com.hari.camera;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraPreview extends SurfaceView implements
		SurfaceHolder.Callback {
	public SurfaceHolder objSurfaceHolder;
	private Camera objCamera;

	public CameraPreview(Context context, Camera camera) {
		super(context);

		Log.i("hari", "CameraPreview constructor" + camera);
		this.objSurfaceHolder = this.getHolder();
		this.objSurfaceHolder.addCallback(this);

		objCamera = camera;

	}

	@Override
	public void surfaceCreated(SurfaceHolder objSurfaceHolder) {
		try {
			if (objCamera == null) {
				Log.i("hari", "CameraPreView is surfaceHolder" + objCamera);
			}
			objCamera.setPreviewDisplay(objSurfaceHolder);
			objCamera.startPreview();

		} catch (Exception e) {
			Log.i("hari",
					"problem while creating surface for preview "
							+ e.getMessage());

		}

	}

	@Override
	public void surfaceChanged(SurfaceHolder objsurfaceHolder, int format,
			int w, int h) {

		if (objCamera != null) {
			Camera.Parameters parameters = objCamera.getParameters();
			rotatePreview(objCamera);
			objCamera.setParameters(parameters);
			objCamera.startPreview();

		}

	}

	private void rotatePreview(Camera objCamera) {
		Method rotateMethod;
		try {
			rotateMethod = android.hardware.Camera.class.getMethod(
					"setDisplayOrientation", int.class);
			try {
				rotateMethod.invoke(objCamera, 90);
			} catch (IllegalAccessException e) {

				e.printStackTrace();
			} catch (IllegalArgumentException e) {

				e.printStackTrace();
			} catch (InvocationTargetException e) {

				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {

			e.printStackTrace();
		}

	}

	@Override
	public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
		objCamera.release();
		objCamera.stopPreview();

		objCamera = null;
		Log.i("hari",
				"stop preview surfaceDestroyed " + surfaceHolder.toString());

	}
}