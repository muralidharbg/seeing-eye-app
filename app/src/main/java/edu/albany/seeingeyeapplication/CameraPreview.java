package edu.albany.seeingeyeapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import static android.content.ContentValues.TAG;
import static android.content.Context.WINDOW_SERVICE;
import static android.provider.MediaStore.Files.FileColumns.MEDIA_TYPE_IMAGE;
import static java.util.Collections.rotate;

import edu.albany.seeingeyeapplication.data.remote.*;
import edu.albany.seeingeyeapplication.data.model.*;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Multipart;

/**
 * Created by muralidhar on 20/10/17.
 */

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PictureCallback{
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Camera.Parameters mParameters;
    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int JPEG = 256;
    private Context mContext;
    private int previewWidth;
    private int previewHeight;
    private APIService apiService;
    private boolean isPreviewRunning = false;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;
        mParameters = mCamera.getParameters();
        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        mContext = context;
        previewHeight = previewWidth = 0;

        apiService = ApiUtils.getAPIService();
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the preview.
        try {
            if (mCamera != null){
                mParameters.setPreviewFormat(JPEG);
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
                //mCamera.setPreviewCallback(this);
            }
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
//        try {
//            if (mCamera != null) {
//                mCamera.stopPreview();
//                mCamera.release();
//            }
//        } catch (Exception e) {
//            Log.e("Camera", e.getMessage());
//        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        if(isPreviewRunning){
            Log.d(TAG, "surfaceChanged: asdasdasd");
            mCamera.stopPreview();
        }
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        if (mHolder.getSurface() == null){
            // preview surface does not exist
            return;
        }

        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            Log.d(TAG, "surfaceChanged: stopPreview failed");
        }

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {

            Display display = ((WindowManager)mContext.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

            if (display.getRotation() == Surface.ROTATION_0){
                previewWidth = h;
                previewHeight = w;
                mParameters.setPreviewSize(h,w);
                mCamera.setDisplayOrientation(90);
            } else if (display.getRotation() == Surface.ROTATION_90){
                previewWidth = w;
                previewHeight = h;
                mParameters.setPreviewSize(w,h);
            } else if (display.getRotation() == Surface.ROTATION_180){
                previewWidth = h;
                previewHeight = w;
                mParameters.setPreviewSize(h,w);
            } else if (display.getRotation() == Surface.ROTATION_270){
                previewWidth = w;
                previewHeight = h;
                mParameters.setPreviewSize(w,h);
                mCamera.setDisplayOrientation(180);
            }
            mParameters.setPreviewFormat(JPEG);
            mCamera.setPreviewDisplay(mHolder);
            previewCamera();
           // mCamera.setPreviewCallback(this);

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    private void createAPI()
    {
        Gson gson = new GsonBuilder().setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").create();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(ApiUtils.BASE_URL).addConverterFactory(GsonConverterFactory.create(gson)).build();
//        apiService = retrofit.create(APIService.class);
    }

    public void sendPost(MultipartBody.Part file) {
//        apiService.postFile(file).enqueue(new Callback<Post>() {
//            @Override
//            public void onResponse(Call<Post> call, Response<Post> response) {
//
//                if(response.isSuccessful()) {
//                    showResponse(response.body().toString());
//                    Log.i(TAG, "post submitted to API." + response.body().toString());
//                }
//            }
//
//            @Override
//            public void onFailure(Call<Post> call, Throwable t) {
//                Log.e(TAG, "Unable to submit post to API.");
//            }
//        });
    }

    public void showResponse(String response) {
        //if(mResponseTv.getVisibility() == View.GONE) {
        //    mResponseTv.setVisibility(View.VISIBLE);
        //}
        //mResponseTv.setText(response);
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(mContext, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(mContext);
        }
        builder.setTitle("Response")
                .setMessage(response)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // do nothing
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
//        Log.d(TAG, "onPreviewFrame: "+ mParameters.getPreviewFormat());
        Log.d("MainActivity", "takepicture");
        Display display = ((WindowManager) mContext.getSystemService(mContext.WINDOW_SERVICE)).getDefaultDisplay();
        int rotation = display.getRotation();
        try {
            mParameters = camera.getParameters();
            int height = mParameters.getPreviewSize().height;
            int width = mParameters.getPreviewSize().width;
            int quality = 100;

            YuvImage ym = new YuvImage(data, ImageFormat.NV21, width, height, null);

            Rect rect = new Rect(0,0,width,height);
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            ym.compressToJpeg(rect,quality,os);
            byte[] yuvByteData = os.toByteArray();

            Bitmap bitmapCameraImage = BitmapFactory.decodeByteArray(yuvByteData, 0, yuvByteData.length);
            Matrix m = new Matrix();

            if(rotation == Surface.ROTATION_0){
//                bitmapCameraImage = BitmapRotate(bitmapCameraImage, 90);
                m.postRotate(90);
            } else if(rotation == Surface.ROTATION_90){
//                bitmapCameraImage = BitmapRotate(bitmapCameraImage, 180);
                m.postRotate(180);
            } else if(rotation == Surface.ROTATION_180){
//                bitmapCameraImage = BitmapRotate(bitmapCameraImage, 270);
                m.postRotate(270);
            }
            bitmapCameraImage = Bitmap.createBitmap(bitmapCameraImage, 0,0,width,height,m,false);
//            fos.write(os.toByteArray());
//            fos.write(data);


            if(bitmapCameraImage != null){
                File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
                if (pictureFile == null){
                    Log.d(TAG, "Error creating media file, check storage permissions: ");
                    return;
                }
                FileOutputStream fos = null;
                fos = new FileOutputStream(pictureFile);
                boolean status = bitmapCameraImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
                fos.close();

//                createAPI();
//                MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", pictureFile.getName(), RequestBody.create(MediaType.parse("image/*"), pictureFile));
//                apiService.postFile(filePart);




            } else{
                Log.d(TAG, "onPreviewFrame: bitmap is null");
            }


//            ExifInterface exifInt = new ExifInterface(pictureFile.toString());
//            Log.d(TAG, "onPreviewFrame: orientation is " + exifInt.getAttribute(ExifInterface.TAG_ORIENTATION));
//
//            if (exifInt.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
////                rotate 90
//                bitmapCameraImage = BitmapRotate(bitmapCameraImage, 90);
//            } else if (exifInt.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
////                rotate 270
//                bitmapCameraImage = BitmapRotate(bitmapCameraImage, 270);
//            } else if (exifInt.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")){
////                rotaot
//                bitmapCameraImage = BitmapRotate(bitmapCameraImage, 180);
//            } else if (exifInt.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("0")){
//                bitmapCameraImage = BitmapRotate(bitmapCameraImage, 90);
//            }

//            Log.d(TAG, "onPreviewFrame: compress status is " + status);

            //File file = // initialize file here




        } catch (FileNotFoundException e) {
            Log.d(TAG, "Error saving the file");
        } catch (IOException e) {
            Log.d(TAG, "Error saving the file");
        }

    }

    /** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image or video */
    private static File getOutputMediaFile(int type){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "SeeingEyeApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("SeeingEyeApp", "failed to create directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE){
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "IMG_"+ timeStamp + ".jpeg");
        } else if(type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                    "VID_"+ timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    public static Bitmap BitmapRotate(Bitmap bitmap, int degree) {
        if(bitmap != null){
            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            Matrix mtrx = new Matrix();
            //       mtx.postRotate(degree);
            mtrx.setRotate(degree);

            return Bitmap.createBitmap(bitmap, 0, 0, width, height, mtrx, true);
        } else{
            return  null;
        }

    }

    public static byte[] YUVrotate90(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        // Rotate the Y luma
        int i = 0;
        for (int x = 0; x < imageWidth; x++) {
            for (int y = imageHeight - 1; y >= 0; y--) {
                yuv[i] = data[y * imageWidth + x];
                i++;
            }
        }
        // Rotate the U and V color components
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (int x = imageWidth - 1; x > 0; x = x - 2) {
            for (int y = 0; y < imageHeight / 2; y++) {
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth) + x];
                i--;
                yuv[i] = data[(imageWidth * imageHeight) + (y * imageWidth)
                        + (x - 1)];
                i--;
            }
        }
        return yuv;
    }

    private static byte[] YUVrotate180(byte[] data, int imageWidth, int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int i = 0;
        int count = 0;
        for (i = imageWidth * imageHeight - 1; i >= 0; i--) {
            yuv[count] = data[i];
            count++;
        }
        i = imageWidth * imageHeight * 3 / 2 - 1;
        for (i = imageWidth * imageHeight * 3 / 2 - 1; i >= imageWidth
                * imageHeight; i -= 2) {
            yuv[count++] = data[i - 1];
            yuv[count++] = data[i];
        }
        return yuv;
    }

    public static byte[] YUVrotate270(byte[] data, int imageWidth,
                                               int imageHeight) {
        byte[] yuv = new byte[imageWidth * imageHeight * 3 / 2];
        int nWidth = 0, nHeight = 0;
        int wh = 0;
        int uvHeight = 0;
        if (imageWidth != nWidth || imageHeight != nHeight) {
            nWidth = imageWidth;
            nHeight = imageHeight;
            wh = imageWidth * imageHeight;
            uvHeight = imageHeight >> 1;// uvHeight = height / 2
        }
        // ??Y
        int k = 0;
        for (int i = 0; i < imageWidth; i++) {
            int nPos = 0;
            for (int j = 0; j < imageHeight; j++) {
                yuv[k] = data[nPos + i];
                k++;
                nPos += imageWidth;
            }
        }
        for (int i = 0; i < imageWidth; i += 2) {
            int nPos = wh;
            for (int j = 0; j < uvHeight; j++) {
                yuv[k] = data[nPos + i];
                yuv[k + 1] = data[nPos + i + 1];
                k += 2;
                nPos += imageWidth;
            }
        }
        return YUVrotate180(YUVrotate90(yuv, imageWidth, imageHeight), imageWidth, imageHeight);
    }

    public void previewCamera() {
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            isPreviewRunning = true;
        } catch(Exception e) {
            Log.d(TAG, "Cannot start preview", e);
        }
    }
}
