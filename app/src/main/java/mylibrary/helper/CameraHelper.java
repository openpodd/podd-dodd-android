package mylibrary.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import org.cm.podd.urban.report.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class CameraHelper {
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final String IMAGE_DIRECTORY_NAME = "Camera";
    public static final String TAG = CameraHelper.class.getSimpleName();

    public static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    public static final int CHOOSE_PHOTO_FROM_ALBUM_REQUEST_CODE = 110;

    private Uri cameraFileUri; // file url to store image/video

    String mCurrentPhotoPath;
    Uri mCurrentPhotoUri;

    private Context mContext;
    private Activity mActivity;
    private cameraHelperLister mListener;
    public static int RESULT_GET_CONTENT = 3;

    public CameraHelper(Activity mContext) {
        this.mContext = mContext;
        this.mActivity = (Activity) mContext;
        mListener = null;
    }

    public void setListener(cameraHelperLister listener) {
        mListener = listener;
    }

    public void showPopUp() {
        final FrameLayout frameView = new FrameLayout(mContext);
        final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        final AlertDialog dialog = builder.create();

        LayoutInflater li = dialog.getLayoutInflater();
        View promptsView = li.inflate(R.layout.dialog_select_photo, frameView);

        dialog.setView(promptsView);
        dialog.show();

        // Event Listeners
        promptsView.findViewById(R.id.btn_dialog_take_a_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                dispatchTakePictureIntent();
            }
        });

        promptsView.findViewById(R.id.btn_dialog_choose_photo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage(CHOOSE_PHOTO_FROM_ALBUM_REQUEST_CODE);
                dialog.dismiss();
            }
        });
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = "file:" + image.getAbsolutePath();
        return image;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();
                Toast.makeText(mContext, "Open camera filed.", Toast.LENGTH_SHORT).show();
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                mCurrentPhotoUri = Uri.fromFile(photoFile);
                CameraFileUriKeeper.getInstance().setUri(mCurrentPhotoUri);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
                        Uri.fromFile(photoFile));
                mActivity.startActivityForResult(takePictureIntent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
                galleryAddPic();
            } else {
                Log.d(TAG, "photoFile = NULL (line ): ");
            }
        }
    }

    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        mActivity.sendBroadcast(mediaScanIntent);
    }

    private void chooseImage(int requestCode) {
        Intent intent;
        switch (requestCode) {
            case CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                break;
            case CHOOSE_PHOTO_FROM_ALBUM_REQUEST_CODE:
                intent = new Intent();
//                intent = new Intent(Intent.ACTION_PICK,
//                        MediaStore.Images.Media.INTERNAL_CONTENT_URI);

                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                mActivity.startActivityForResult(Intent.createChooser(intent, "Select Picture"), RESULT_GET_CONTENT);
                break;
        }
    }


    public void handleActivityResult(int requestCode, int resultCode, Intent resultData) {
        Uri photoUri = null;

        Log.d("handleActivityResult", String.format("requestCode: %d, resultCode %d", requestCode, resultCode));
        switch (requestCode) {
            case CameraHelper.CAMERA_CAPTURE_IMAGE_REQUEST_CODE:
                Log.d(TAG, "resultCode (line ): " + resultCode);
                if (resultCode == Activity.RESULT_OK) {
//                    if (resultData != null) {
//                        photoUri = resultData.getData();
//                        if (photoUri != null) {
////                            Toast.makeText(mContext, "step-1", Toast.LENGTH_SHORT).show();
//                        }
//                    } else {
                    if (mCurrentPhotoUri != null) {
//                            Toast.makeText(mContext, "step-2", Toast.LENGTH_SHORT).show();
                        photoUri = mCurrentPhotoUri;
                    } else {
                        photoUri = CameraFileUriKeeper.getInstance().getUri();
//                            Toast.makeText(mContext, "step-3", Toast.LENGTH_SHORT).show();
                    }
//                }
                    mListener.onCameraSuccess(photoUri);
                }
                break;
            case CameraHelper.CHOOSE_PHOTO_FROM_ALBUM_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK) {
                    photoUri = resultData.getData();
                }
                break;
        }

        mListener.onSuccess(photoUri);
    }


    public static interface cameraHelperLister {
        public void onSuccess(Uri u);

        public void onCameraSuccess(Uri u);

    }
}
