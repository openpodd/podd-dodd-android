package mylibrary.helper;

import android.net.Uri;

public class CameraFileUriKeeper {
    private static CameraFileUriKeeper mInstance = null;
    private static Uri mUri = null;
    public static CameraFileUriKeeper getInstance(){
        if(mInstance == null) {
            mInstance = new CameraFileUriKeeper();
        }
        return mInstance;
    }

    public void setUri(Uri uri) {
        mUri = uri;
    }

    public Uri getUri() {
        return mUri;
    }
}
