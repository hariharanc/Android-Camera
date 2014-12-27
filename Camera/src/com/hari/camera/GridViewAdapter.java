package com.hari.camera;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore.MediaColumns;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.camera.R;

public class GridViewAdapter extends CursorAdapter {
	Cursor imageCursor;
	int changegrid;


	@SuppressWarnings("deprecation")
	public GridViewAdapter(Context ctx, int livGridImage,
			Cursor imageCursor) {
		super(ctx, imageCursor);
		this.imageCursor = imageCursor;
	}

	@Override
	public void bindView(View convertView, Context context, final Cursor cursor) {
		final ViewHolder viewHolder = (ViewHolder) convertView.getTag();

		if (cursor.getCount() > 0) {

			String absolutePathOfImage = cursor.getString(cursor
					.getColumnIndexOrThrow(MediaColumns.DATA));
			final BitmapFactory.Options options = new BitmapFactory.Options();
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(absolutePathOfImage, options);

			options.inSampleSize = calculateInSampleSize(options, 200, 200);
			// Decode bitmap with inSampleSize set
			options.inJustDecodeBounds = false;
			Bitmap bmp = BitmapFactory.decodeFile(absolutePathOfImage, options);

			viewHolder.imageView.setImageBitmap(bmp);
          
		}

	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView != null) {
			
		}
		return super.getView(position, convertView, parent);
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
	public View newView(Context context, Cursor cursor, ViewGroup parent) {

		View convertView = null;

		if (changegrid == 0) {
			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = (RelativeLayout) inflater.inflate(
					R.layout.grid_image, parent, false);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.gallery);
			viewHolder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);

			convertView.setTag(viewHolder);
		} else if (changegrid == 1) {

			LayoutInflater inflater = LayoutInflater.from(context);
			convertView = (RelativeLayout) inflater.inflate(
					R.layout.gallery_images, parent, false);
			ViewHolder viewHolder = new ViewHolder();
			viewHolder.imageView = (ImageView) convertView
					.findViewById(R.id.gallery);
			viewHolder.imageView.setScaleType(ImageView.ScaleType.FIT_XY);
			convertView.setTag(viewHolder);

		}
		return convertView;

	}

	class ViewHolder {
		ImageView imageView;
	}
}