package org.cm.podd.urban.report.widget;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.cm.podd.urban.report.R;
import org.cm.podd.urban.report.helper.ExifUtils;
import org.cm.podd.urban.report.helper.S3Helper;
import org.cm.podd.urban.report.helper.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ImageWrapperLinearLayout extends LinearLayout {
    private final Context mContext;
    private View mRootView;

    @Bind(R.id.attach_img) ImageView imageView;
    @Bind(R.id.progressBar) ProgressBar progressBar;
    private Uri uri;
    private String path;
    private S3Helper s3Helper;

    public static final String TAG = ImageWrapperLinearLayout.class.getSimpleName();
    private Bitmap thumbnailImage;


    public ImageWrapperLinearLayout(Context context) {
        super(context);
        mRootView = LayoutInflater.from(context).inflate(R.layout.image_wrapper, this);
        ButterKnife.bind(this, mRootView);
        mContext = context;
    }

    public ImageWrapperLinearLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mRootView = LayoutInflater.from(context).inflate(R.layout.image_wrapper, this);
        mContext = context;
        ButterKnife.bind(this, mRootView);
    }

    public ImageWrapperLinearLayout setImageResource(String path) {
        Glide.with(mContext).load(path).into(imageView);
        return this;
    }

    public ImageWrapperLinearLayout setImageResource(Uri path) {
        int width = (int) mContext.getResources().getDimension(R.dimen.close_icon_width);
        int height = (int) mContext.getResources().getDimension(R.dimen.close_icon_height);
        Glide.with(mContext).load(path)
                .centerCrop()
                .override(width, height)
                .into(imageView);
        return this;
    }

    public void syncBitMapPath(final Uri uri) {

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {

                Bitmap tmpImage = null;

                try {
                    tmpImage = Glide.
                            with(mContext).
                            load(uri).
                            asBitmap().
                            into(-1, -1). // Width and height
                            get();
                } catch (final ExecutionException e) {
                    Log.e(TAG, e.getMessage());
                } catch (final InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                }

                FileOutputStream out = null;
                File tempFile = null;

                try {
                    File tempDir = mContext.getCacheDir();
                    tempFile = File.createTempFile(Util.currentTime(), null, tempDir);

                    out = new FileOutputStream(tempFile);
                    tmpImage.compress(Bitmap.CompressFormat.PNG, 100, out);

                } catch (Exception e) {
                    e.printStackTrace();
                    path = null;
                } finally {
                    try {
                        if (out != null) {
                            out.close();
                            path = tempFile.getPath();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        path  = null;
                    }
                }

                if (tmpImage != null && path != null) {
                    thumbnailImage = ThumbnailUtils.extractThumbnail(tmpImage, 400, 400);
                    int rotate = neededRotation(tempFile);
                    if (rotate != 0) {
                        Matrix m = new Matrix();
                        m.postRotate(rotate);
                        thumbnailImage = Bitmap.createBitmap(thumbnailImage, 0, 0, 400, 400, m, true);
                    }
                }
                return null;
            }

            @Override
            protected void onPostExecute(Void result) {
                hideProgressBar();
            }

        }.execute();
    }

    public ImageWrapperLinearLayout(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public static int neededRotation(File ff) {
        try {

            ExifInterface exif = new ExifInterface(ff.getAbsolutePath());
            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                return 270;
            }
            if (orientation == ExifInterface.ORIENTATION_ROTATE_180) {
                return 180;
            }
            if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                return 90;
            }
            return 0;

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    protected Bitmap createThumbnail(Uri uri) {
        Log.d(TAG, "image uri = " + uri.toString());
        String selectedImagePath = null;

        Cursor cursor = mContext.getContentResolver().query(uri, null, null, null, null);
        if (cursor == null) {
            selectedImagePath = uri.getPath();
        } else {
            cursor.moveToFirst();
            Log.d(TAG, "column0 = " + cursor.getColumnName(0) + ",column1 = " + cursor.getColumnName(1) + ",column2 = " + cursor.getColumnName(2));
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            if (idx >= 0) {
                selectedImagePath = cursor.getString(idx);
            }
//            int idx = cursor.getColumnIndex(MediaStore.Images.)
        }

        Bitmap thumb1, thumb2 = null;
        if (selectedImagePath == null) {
            ExifUtils ex  = new ExifUtils();
            thumb1 = ex.decodeFile(path);
            thumb2 = ThumbnailUtils.extractThumbnail(thumb1, 400, 400);
        } else {

            try {
                thumb1 = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(selectedImagePath), 400, 400);
            } catch (OutOfMemoryError ex) {
                try {
                    BitmapFactory.Options options = new BitmapFactory.Options();
                    options.inTempStorage = new byte[24 * 1024];
                    options.inJustDecodeBounds = false;
                    options.inSampleSize = 4;

                    thumb1 = ThumbnailUtils.extractThumbnail(BitmapFactory.decodeFile(selectedImagePath, options), 400, 400);
                } catch (Exception e) {
                    return thumb2;
                }
            }

            int rotate = neededRotation(new File(selectedImagePath));
            thumb2 = null;
            if (rotate != 0) {
                Matrix m = new Matrix();
                m.postRotate(rotate);
                thumb2 = thumb1.createBitmap(thumb1, 0, 0, 400, 400, m, true);
                thumb1.recycle();
            } else {
                thumb2 = thumb1;
            }
        }
        hideProgressBar();
        return thumb2;
    }

    public void showProgressBar() {
        progressBar.setVisibility(VISIBLE);
    }

    public void hideProgressBar() {
        progressBar.setVisibility(INVISIBLE);
    }

    public void setUri(Uri uri) {
        showProgressBar();

        s3Helper = new S3Helper(mContext);
        try {

            path = s3Helper.getPath(uri);

            // for google drive
            if (path == null) {
                syncBitMapPath(uri);
            } else {
                thumbnailImage = createThumbnail(uri);
            }

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        this.uri = uri;
    }

    public Bitmap getThumbnailImage() {
        return thumbnailImage;
    }

    public Uri getUri() {
        return uri;
    }

    public String getPath() {
        return path;
    }
}