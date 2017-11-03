package com.etcxc.android.ui.view.xccamera;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;


import com.etcxc.android.ui.view.xccamera.manager.AutoFocusManager;

import java.io.IOException;
import java.util.List;


/**
 * Preview of camera
 * Created by zhouzhuo810 on 2017/6/15.
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraView";
    private Activity mActivity;
    private Camera mCamera;
    AutoFocusManager autoFocusManager;

    public CameraView(Activity activity, Camera camera) {
        super(activity);
        this.mActivity = activity;
        mCamera = camera;
        getHolder().addCallback(this);
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        try {
            initCamera(mCamera);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initCamera(Camera camera) throws Exception {
        Camera.Parameters parameters = camera.getParameters();
        List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
        for (Camera.Size size : previewSizes) {
            Log.e("XXX", "preview:" + size.width + "," + size.height);
            if (size.width / 16 == size.height / 9) {
                parameters.setPreviewSize(size.width, size.height);
                break;
            }
        }
        List<Camera.Size> pictureSizes = parameters.getSupportedPictureSizes();
        for (Camera.Size size : pictureSizes) {
            Log.e("XXX", "picture:" + size.width + "," + size.height);
            if (size.width / 16 == size.height / 9) {
                parameters.setPictureSize(size.width, size.height);
                break;
            }
        }
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(CameraUtils.findCameraId(false), info);
        int rotation = info.orientation % 360;
        Log.d("XXX", "Rotation :" + rotation);
        parameters.setRotation(rotation);
        setCameraDisplayOrientation(mActivity, mCamera);
        parameters.setJpegQuality(100);
        camera.setParameters(parameters);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            if (autoFocusManager == null) {
                autoFocusManager = new AutoFocusManager(mCamera);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (getHolder().getSurface() == null) {
            return;
        }
        try {
            mCamera.stopPreview();
            if (autoFocusManager != null) {
                autoFocusManager.stop();
                autoFocusManager = null;
            }
        } catch (Exception e) {

        }
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            if (autoFocusManager == null) {
                autoFocusManager = new AutoFocusManager(mCamera);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        Log.i(TAG, "surfaceDestroyed: ");
        holder.removeCallback(this);
        if (autoFocusManager != null) {
            autoFocusManager.stop();
            autoFocusManager = null;
        }
    }

    /**
     * 设置预览
     *
     * @param activity
     * @param camera
     */
    private static void setCameraDisplayOrientation(Activity activity, android.hardware.Camera camera) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(CameraUtils.findCameraId(false), info);
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
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
