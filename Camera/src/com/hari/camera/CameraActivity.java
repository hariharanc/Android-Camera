package com.hari.camera;

import java.io.ByteArrayOutputStream;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.camera.R;

public class CameraActivity extends Activity implements OnClickListener {

	private Camera cameraObject;
	private CameraPreview showCamera;
	private ImageView imgCapture;
	TextView txtFlash, txtFace, txtUse;
	Boolean chekPreview = false;
	FrameLayout preview;
	RelativeLayout galleryImageShow;
	Boolean isFront = false;
	GridView gridForGalley;
	GridViewAdapter imageAdapterForGallery;
	BitmapDrawable bitmapDrawablefromGallery;
	Context ctx;
	ImageView imageView1;
	byte[] tmpBitmapByte;
	Cursor imageCursor;

	@SuppressLint("NewApi")
	public static Camera isCameraAvailiable(Boolean isCheck) {

		int cameraCount = 0;
		Camera cam = null;
		Camera.CameraInfo cameraInfo = new Camera.CameraInfo();
		cameraCount = Camera.getNumberOfCameras();

		for (int camIdx = 0; camIdx < cameraCount; camIdx++) {
			Camera.getCameraInfo(camIdx, cameraInfo);
			if (isCheck == true) {
				Log.i("hari", "checking for CAMERA_FACING_FRONT" + isCheck);
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
					try {
						cam = Camera.open(camIdx);
					} catch (RuntimeException e) {
						Log.e("HARI",
								"Camera failed to open: "
										+ e.getLocalizedMessage());
					}
				} else {
					Log.i("hari", "this feature not support" + isCheck);

				}

			} else if (isCheck == false) {
				Log.i("hari", "checking for CAMERA_FACING_BACK" + isCheck);
				if (cameraInfo.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
					try {
						cam = Camera.open(camIdx);
					} catch (RuntimeException e) {
						Log.e("HARI",
								"Camera failed to open: "
										+ e.getLocalizedMessage());
					}
				}
			}
		}

		return cam;
	}

	PictureCallback capturedIt = new PictureCallback() {

		@SuppressWarnings({ "deprecation", "unused" })
		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// camera.setDisplayOrientation(90);

			Bitmap compressed_photo = null;

			BitmapFactory.Options options = new BitmapFactory.Options();
			compressed_photo = BitmapFactory.decodeByteArray(data, 0,
					data.length, options);

			Matrix matrix = new Matrix();
			matrix.postRotate(270);
			compressed_photo = Bitmap.createBitmap(compressed_photo, 0, 0,
					compressed_photo.getWidth(), compressed_photo.getHeight(),
					matrix, true);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();

			compressed_photo.compress(CompressFormat.JPEG,
					80 /* ignored for PNG */, bos);
			tmpBitmapByte = bos.toByteArray();

			// TODO finally remove

			imageView1.setImageBitmap(compressed_photo);

			// no need check compressed_photo null
			preview.setDrawingCacheEnabled(true);
			preview.buildDrawingCache();
			compressed_photo = preview.getDrawingCache();
			preview.setBackgroundDrawable(new BitmapDrawable(compressed_photo));
			// preview.setVisibility(View.VISIBLE);
			cameraObject.stopPreview();

		}
	};

	@SuppressWarnings("unused")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.camera_main);

		imgCapture = (ImageView) findViewById(R.id.imgCapture);

		txtFace = (TextView) findViewById(R.id.txtFace);
		txtFlash = (TextView) findViewById(R.id.txtFlash);
		txtUse = (TextView) findViewById(R.id.txtUse);
		gridForGalley = (GridView) findViewById(R.id.grid);
		galleryImageShow = (RelativeLayout) findViewById(R.id.rrPreview);
		cameraObject = isCameraAvailiable(false);
		showCamera = new CameraPreview(this, cameraObject);
		Log.i("hari", "camera activity show obbj is" + showCamera
				+ cameraObject);
		imageView1 = (ImageView) findViewById(R.id.imageView1);

		preview = (FrameLayout) findViewById(R.id.camera_preview);

		preview.addView(showCamera);

		imgCapture.setOnClickListener(this);
		txtFace.setOnClickListener(this);
		txtFlash.setOnClickListener(this);
		txtUse.setOnClickListener(this);

		Uri uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

		String[] projection = { MediaColumns._ID, MediaColumns.DATA,
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME };

		imageCursor = this.getContentResolver().query(uri, projection, null,
				null, null);

		imageAdapterForGallery = new GridViewAdapter(this,
				R.layout.grid_image, imageCursor);
		gridForGalley.setAdapter(imageAdapterForGallery);

		gridForGalley.setOnItemClickListener(new OnItemClickListener() {

			@SuppressWarnings("deprecation")
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				preview.setVisibility(View.GONE);
				String imagePath = imageCursor.getString(imageCursor
						.getColumnIndex(MediaColumns.DATA));

				Log.i("hari", "imagePath:" + imagePath);

				final BitmapFactory.Options options = new BitmapFactory.Options();
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeFile(imagePath, options);

				options.inSampleSize = calculateInSampleSize(options, 200, 200);

				// Decode bitmap with inSampleSize set
				options.inJustDecodeBounds = false;
				Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				// TODO here to image quality size (0 to 100)...now give 80
				bitmap.compress(CompressFormat.JPEG, 80 /* ignored for PNG */,
						bos);
				tmpBitmapByte = bos.toByteArray();

				bitmapDrawablefromGallery = new BitmapDrawable(getResources(),
						bitmap);
				galleryImageShow.setDrawingCacheEnabled(true);
				galleryImageShow.buildDrawingCache();
				galleryImageShow
						.setBackgroundDrawable(bitmapDrawablefromGallery);
				

			}
		});

	}

	public static int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {

		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {
			if (width > height) {
				inSampleSize = Math.round((float) height / (float) reqHeight);
			} else {
				inSampleSize = Math.round((float) width / (float) reqWidth);
			}
		}
		return inSampleSize;

	}

	@Override
	public void onPause() {
		super.onPause();
		cameraObject.release();
	}

	@Override
	protected void onResume() {
		super.onResume();

		cameraObject.startPreview();
	}

	@Override
	public void onClick(View v) {
		if (v == imgCapture) {

//			 galleryImageShow
//			 .setBackgroundResource(Color.parseColor("#66ffffff"));

			preview.setVisibility(View.VISIBLE);

			if (bitmapDrawablefromGallery != null
					&& bitmapDrawablefromGallery.getBitmap().isRecycled()) {

				try {
				
					bitmapDrawablefromGallery.getBitmap().recycle();
					
				} catch (Exception e) {
					// TODO: handle exception
				}
				

			} else {

			}
			if (chekPreview == false) {

				cameraObject.takePicture(null, null, capturedIt);
				// cameraObject.startPreview();
				chekPreview = true;
			} else if (chekPreview == true) {
				// cameraObject.takePicture(null, null, capturedIt);
				try {
					cameraObject.startPreview();
					chekPreview = false;
					
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// cameraObject.takePicture(null, null, capturedIt);
			}
		} else if (v == txtFace) {
			// Toast.makeText(ctx, "click", Toast.LENGTH_SHORT).show();
			isCameraAvailiable(true);

		} else if (v == txtUse) {

			if (tmpBitmapByte != null) {

			}

		} else if (v == txtFlash) {

		}

	}

}
