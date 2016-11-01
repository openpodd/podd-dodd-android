package org.cm.podd.urban.report.helper;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferType;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.repacked.apache.commons.io.IOUtils;

import org.cm.podd.urban.report.BuildConfig;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by nat on 9/15/15 AD.
 */
public class S3Helper {
    private S3HelperCallback mCallback = null;
    private ArrayList<String> fileNames;

    public S3Helper(Context mContext) {
        this.mContext = mContext;
        transferUtility = Util.getTransferUtility(mContext);


        initData();
    }


    private Context mContext;
    public static final String TAG = S3Helper.class.getSimpleName();
    // The TransferUtility is the primary class for managing transfer to S3
    private TransferUtility transferUtility;
    // A List of all transfers
    /**
     * This map is used to provide data to the SimpleAdapter above. See the
     * fillMap() function for how it relates observers to rows in the displayed
     * activity.
     */
    private ArrayList<HashMap<String, Object>> transferRecordMaps;
    private List<TransferObserver> observers;
    // Which row in the UI is currently checked (if any)
    private int checkedIndex;
    private int INDEX_NOT_CHECKED = -1;

    private int uploadCount = 0;


    private void initData() {
        fileNames = new ArrayList<String>();
        checkedIndex = INDEX_NOT_CHECKED;
        transferRecordMaps = new ArrayList<HashMap<String, Object>>();

        // Use TransferUtility to get all upload transfers.
        observers = transferUtility.getTransfersWithType(TransferType.UPLOAD);
        for (TransferObserver observer : observers) {

            // For each transfer we will will create an entry in
            // transferRecordMaps which will display
            // as a single row in the UI
            HashMap<String, Object> map = new HashMap<String, Object>();
            Util.fillMap(map, observer, false);
            transferRecordMaps.add(map);

            // We only care about updates to transfers that are in a
            // non-terminal state
            if (!TransferState.COMPLETED.equals(observer.getState())
                    && !TransferState.FAILED.equals(observer.getState())
                    && !TransferState.CANCELED.equals(observer.getState())) {

                observer.setTransferListener(new UploadListener());
            }
        }

    }

    public void beginUpload(String filePath, Bitmap thumbnail) {
        Log.d(TAG, "beginUpload ");
        if (filePath == null) {
            Toast.makeText(mContext, "ไม่สามารถอัพโหลดรูปภาพนี้ได้ กรุณาลองใหม่",
                    Toast.LENGTH_LONG).show();
            return;
        }

        File file = new File(filePath);

        String fileName = getHashFileName(file.getName());

        fileNames.add(fileName);
//        var uploadRequest = new TransferUtilityUploadRequest();
//        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
//        StrictMode.setThreadPolicy(policy);


        if (thumbnail != null) {

            // upload thumbnail
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            thumbnail.compress(Bitmap.CompressFormat.JPEG, 80, bos);
            byte[] bitmapData = bos.toByteArray();


            try {

                File thumbnailFile = File.createTempFile(fileName, "-thumbnail", mContext.getCacheDir());

                FileOutputStream fos = new FileOutputStream(thumbnailFile);
                fos.write(bitmapData);
                fos.close();

                ObjectMetadata meta = new ObjectMetadata();
                meta.setContentLength(bitmapData.length);
                meta.setContentType("image/jpeg");

                TransferObserver observer_thumbnail = transferUtility.upload(BuildConfig.BUCKET_NAME,
                        fileName + "-thumbnail",
                        thumbnailFile);

                HashMap<String, Object> map_thumbnail = new HashMap<String, Object>();
                observers.add(observer_thumbnail);

                Util.fillMap(map_thumbnail, observer_thumbnail, false);
                transferRecordMaps.add(map_thumbnail);
                observer_thumbnail.setTransferListener(new UploadListener());

                Log.e("S3Helper", thumbnailFile.getTotalSpace() + "");
                Log.e("S3Helper", thumbnailFile.getPath() + "");

                uploadCount++;
            } catch (IOException e) {

                Log.e("S3Helper", e.getMessage());
                e.printStackTrace();
            }
        }

        Log.d(TAG, "beginUpload " + file.getName());
        TransferObserver observer = transferUtility.upload(BuildConfig.BUCKET_NAME, fileName, file);
        observers.add(observer);

        HashMap<String, Object> map = new HashMap<String, Object>();
        Util.fillMap(map, observer, false);
        transferRecordMaps.add(map);
        observer.setTransferListener(new UploadListener());

        uploadCount++;

    }


    public static String getHashFileName (String name) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.reset();
            byte[] buffer = name.getBytes("UTF-8");
            md.update(buffer);
            byte[] digest = md.digest();

            String hexStr = "";
            for (int i = 0; i < digest.length; i++) {
                hexStr +=  Integer.toString( ( digest[i] & 0xff ) + 0x100, 16).substring( 1 );
                if (i > 0 && i % 4 == 0) {
                    hexStr += "-";
                }
            }
            return hexStr;

        } catch (NoSuchAlgorithmException ex) {
            return name.replaceAll(" ", "_");
        } catch (UnsupportedEncodingException ex) {
            return name.replaceAll(" ", "_");
        }
    }

    private void updateList() {
        TransferObserver observer = null;
        HashMap<String, Object> map = null;
        Log.d(TAG, "observers: " + observers.size());
        for (int i = 0; i < observers.size(); i++) {
            observer = observers.get(i);
            map = transferRecordMaps.get(i);
            Util.fillMap(map, observer, i == checkedIndex);
        }
//        simpleAdapter.notifyDataSetChanged();
    }

    private class UploadListener implements TransferListener {

        // Simply updates the UI list when notified.
        @Override
        public void onError(int id, Exception e) {
            Log.e(TAG, "Error during upload: " + id, e);
            updateList();
        }

        @Override
        public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            Log.d(TAG, "onProgressChanged: " + id);
            Log.d(TAG, String.format("%d/%d", bytesCurrent, bytesTotal));
            updateList();
        }

        @Override
        public void onStateChanged(int id, TransferState newState) {
            Log.d(TAG, "onStateChanged " + newState.name());
            if (newState == TransferState.COMPLETED) {
                uploadCount--;
                if (uploadCount == 0) {
//                    allUploaded();
                    Log.d(TAG, "UPLOAD COUNT = 0 (line ):");
                    if (mCallback != null) {
                        mCallback.onAllFilesUploaded(fileNames);
                    }
                }
            }

            Log.d(TAG, "onStateChanged count =  " + uploadCount);
            updateList();
        }
    }

    public S3Helper setCallback(S3HelperCallback callback) {
        mCallback = callback;
        return this;
    }


    @SuppressLint("NewApi")
    public String getPath(Uri uri) throws URISyntaxException {
        final boolean needToCheckUri = Build.VERSION.SDK_INT >= 19;
        String selection = null;
        String[] selectionArgs = null;
        // Uri is different in versions after KITKAT (Android 4.4), we need to
        // deal with different Uris.
        if (needToCheckUri && DocumentsContract.isDocumentUri(mContext.getApplicationContext(), uri)) {
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                return Environment.getExternalStorageDirectory() + "/" + split[1];
            } else if (isDownloadsDocument(uri)) {
                final String id = DocumentsContract.getDocumentId(uri);
                uri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));
            } else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];
                if ("image".equals(type)) {
                    uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }
                selection = "_id=?";
                selectionArgs = new String[]{
                        split[1]
                };
            }
        }
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] projection = {
                    MediaStore.Images.Media.DATA
            };
            Cursor cursor = null;
            try {
                cursor = mContext.getContentResolver()
                        .query(uri, projection, selection, selectionArgs, null);
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                if (cursor.moveToFirst()) {
                    return cursor.getString(column_index);
                }
            } catch (Exception e) {
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }
        return null;
    }


    public String getRealPathFromURI(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    public interface S3HelperCallback {
        public void onAllFilesUploaded(ArrayList<String> fileNames);
    }

}
