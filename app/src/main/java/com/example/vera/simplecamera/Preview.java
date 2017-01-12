package com.example.vera.simplecamera;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

/**
 * Created by Vera on 2017-01-10.
 */

class Preview extends SurfaceView implements SurfaceHolder.Callback {
    private boolean previewIsRunning;
    private final static String DEBUG_TAG = "MainActivity";
    private static Camera camera = null;
    private Camera.Parameters camParams;

    public void surfaceCreated(SurfaceHolder holder) {
        int numOfCams = Camera.getNumberOfCameras();
        int cameraID = -1;
        if(numOfCams > 1){
            for (int i = 0; i < numOfCams; i++) {
                Camera.CameraInfo info = new Camera.CameraInfo();
                Camera.getCameraInfo(i, info);
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                    Log.d(DEBUG_TAG, "Camera found");
                    cameraID = i;
                    break;
                }
            }
        } else cameraID = numOfCams;
        try {
            camera = Camera.open(cameraID);
            System.out.println("Camera opened");
        } catch (RuntimeException e) {
            e.printStackTrace();
        }
        camParams = camera.getParameters();
        // ...
        // but do not start the preview here!
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // set preview size etc here ... then
        myStartPreview();
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        myStopPreview();
        camera.release();
        camera = null;
    }

    // safe call to start the preview
    // if this is called in onResume, the surface might not have been created yet
    // so check that the camera has been set up too.
    public void myStartPreview() {
        if (!previewIsRunning && (camera != null)) {
            camera.startPreview();
            previewIsRunning = true;
        }
    }

    // same for stopping the preview
    public void myStopPreview() {
        if (previewIsRunning && (camera != null)) {
            camera.stopPreview();
            previewIsRunning = false;
        }
    }
}
