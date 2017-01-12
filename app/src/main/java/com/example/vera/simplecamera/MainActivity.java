package com.example.vera.simplecamera;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import java.io.IOException;
import java.util.concurrent.Semaphore;

public class MainActivity extends AppCompatActivity {
//    //private final static String DEBUG_TAG = "MainActivity";
//    private static Camera camera = null;
//    private Camera.Parameters camParams;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camera.ShutterCallback shutter;
    private Camera.PictureCallback jpegPic;
    private Camera.PreviewCallback preview;
    private SurfaceHolder.Callback surfaceHolderCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpCamera();
        Button recButton = (Button) findViewById(R.id.buttonID);
        recButton.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v){
                takePicture();
            }
        });
    }

    public void setUpCamera(){
//        int numOfCams = Camera.getNumberOfCameras();
//        int cameraID = -1;
//        if(numOfCams > 1){
//            for (int i = 0; i < numOfCams; i++) {
//                Camera.CameraInfo info = new Camera.CameraInfo();
//                Camera.getCameraInfo(i, info);
//                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                    Log.d(DEBUG_TAG, "Camera found");
//                    cameraID = i;
//                    break;
//                }
//            }
//        } else cameraID = numOfCams;
//        DevicePolicyManager dpm = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
//        if (dpm.getCameraDisabled(null)) {
//            System.out.println("Disabled Camera");
//        }
//        try {
//            camera = Camera.open(cameraID);
//            System.out.println("Camera opened");
//        } catch (RuntimeException e) {
//            e.printStackTrace();
//        }
//        camParams = camera.getParameters();
        setCameraDisplayOrientation(this, cameraID, camera);
        System.out.println("Set display");
        surfaceView = (SurfaceView)findViewById(R.id.surfaceVewID);
        surfaceHolder = surfaceView.getHolder();
        //surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceHolderCallback = new SurfaceHolder.Callback() {
            public void surfaceDestroyed(SurfaceHolder holder) {
                //surfaceView = null;
                System.out.println("===================== Video capture surface destroyed");
                //camera.stopPreview();
                //camera.release();
                //camera = null;
            }
            public void surfaceCreated(SurfaceHolder holder) {
                //surfaceView = (SurfaceView)findViewById(R.id.surfaceVewID);
                System.out.println("===================== Video capture surface created");
            }
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                System.out.println("===================== Video capture surface changed");
            }
        };
        surfaceHolder.addCallback(surfaceHolderCallback);
        System.out.println("Set view and holder");
        try {
            camera.setPreviewDisplay(surfaceHolder);
            System.out.println("Set preview display");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Camera.release();
    }

    public void takePicture(){
        System.out.println("Take picture");
        camera.startPreview();
        System.out.println("Start camera preview");
        shutter = new Camera.ShutterCallback(){
            public void onShutter() {
                AudioManager mgr = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                System.out.println("HERE1");
                mgr.playSoundEffect(AudioManager.FLAG_PLAY_SOUND);
                System.out.println("HERE2");
            }
        };
        System.out.println("HERE3");
        jpegPic = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] bytes, Camera camera) {
                System.out.println("HERE4");
            }
        };
        camera.takePicture(shutter, null, null, jpegPic);
        System.out.println("HERE5");
    }

    public static void setCameraDisplayOrientation(Activity activity, int cameraId, android.hardware.Camera camera) {
        android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(cameraId, info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0: degrees = 0; break;
            case Surface.ROTATION_90: degrees = 90; break;
            case Surface.ROTATION_180: degrees = 180; break;
            case Surface.ROTATION_270: degrees = 270; break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (info.orientation - degrees + 360) % 360;
        }
        camera.setDisplayOrientation(result);
    }
}
