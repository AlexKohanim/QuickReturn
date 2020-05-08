package p006ti.modules.titanium.media;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import org.appcelerator.kroll.common.Log;

/* renamed from: ti.modules.titanium.media.TiCamera */
public class TiCamera {
    private static final String TAG = "TiCamera";
    private static Camera camera;
    PictureCallback jpegCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            String photosPath = "/sdcard/CameraTest/photos/";
            try {
                File photosDirectory = new File(photosPath);
                if (!photosDirectory.exists()) {
                    photosDirectory.mkdirs();
                }
                FileOutputStream outputStream = new FileOutputStream(String.format(photosPath + "%d.jpg", new Object[]{Long.valueOf(System.currentTimeMillis())}));
                try {
                    outputStream.write(data);
                    outputStream.close();
                    camera.startPreview();
                    FileOutputStream fileOutputStream = outputStream;
                } catch (FileNotFoundException e) {
                    e = e;
                    FileOutputStream fileOutputStream2 = outputStream;
                    e.printStackTrace();
                } catch (IOException e2) {
                    e = e2;
                    FileOutputStream fileOutputStream3 = outputStream;
                    e.printStackTrace();
                }
            } catch (FileNotFoundException e3) {
                e = e3;
                e.printStackTrace();
            } catch (IOException e4) {
                e = e4;
                e.printStackTrace();
            }
        }
    };
    PictureCallback rawCallback = new PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            Log.m37i(TiCamera.TAG, "Picture taken: raw picture available", Log.DEBUG_MODE);
        }
    };
    ShutterCallback shutterCallback = new ShutterCallback() {
        public void onShutter() {
            Log.m37i(TiCamera.TAG, "onShutter() called. Capturing image.", Log.DEBUG_MODE);
        }
    };

    public TiCamera() {
        if (camera == null) {
            Log.m37i(TAG, "Camera created.", Log.DEBUG_MODE);
            camera = Camera.open();
        }
    }

    public Camera getCamera() {
        return camera;
    }
}
