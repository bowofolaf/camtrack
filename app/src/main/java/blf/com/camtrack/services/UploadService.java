package blf.com.camtrack.services;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.hardware.camera2.CameraDevice;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import android.view.WindowManager;

import java.io.IOException;

import blf.com.camtrack.activity.LoginActivity;
import blf.com.camtrack.activity.MainActivity;
import blf.com.camtrack.client.RestClient;
import blf.com.camtrack.config.Config;
import blf.com.camtrack.config.Global;
import blf.com.camtrack.request.GPSRequest;
import blf.com.camtrack.request.RequestBuilder;
import blf.com.camtrack.ui.CamCallback;
import blf.com.camtrack.ui.CamPreview;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions and extra parameters.
 */
public class UploadService extends IntentService {

//    // TODO: Rename parameters
    public static final String LOG_INTERVAL = "blf.com.camtrack.services.extra.LOG_INTERVAL";
    public static final String LOG_OPTIONS = "blf.com.camtrack.services.extra.LOG_OPTIONS";
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    Location lastLocation;
    LocationManager locationManager;
    LocationListener listener;
    CameraDevice _camera;
    Camera camera2;
    boolean running = true;
    String deviceName;

    public UploadService() {
        super("UploadService");

    }

    @Override
    public void onCreate(){
        super.onCreate();

        try {
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }

        deviceName =  GetDeviceName();
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            String [] options = intent.getStringArrayExtra(LOG_OPTIONS);
            int interval = intent.getIntExtra(LOG_INTERVAL, 15); //interval is in minutes
            String sessionid = intent.getStringExtra(LoginActivity.SESSION_ID);

            ((Global)this.getApplication()).SetUploadServiceStatus(running);
            Intent localBroadcast = new Intent("upload-service-change");
            LocalBroadcastManager.getInstance(this).sendBroadcast(localBroadcast);

            while(running)
                try {
                    for(int i = 0; i<options.length; i++)
                    {
                        switch (options[i])
                        {
                            case "GPS" :
                                handleActionLogGps(sessionid);
                                break;
                            case "PHOTOS" :
                                handleActionLogPhotos(sessionid);
                                break;
                            default:
                                continue;
                        }
                    }
                    Thread.sleep(1800 * interval);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    running = false;
                    ((Global)this.getApplication()).SetUploadServiceStatus(running);
                    LocalBroadcastManager.getInstance(this).sendBroadcast(localBroadcast);
                }
        }
    }

    private String GetDeviceName(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
        return prefs.getString("device_name","mydevice");
    }

    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionLogGps(String sessionid) {
        RestClient mAuthTask = null;
        String host = Config.CAMTRACK_WEBSERVICE_HOST + Config.CAMTRACK_UPLOAD_SVC + "/Gps";
        GPSRequest request = GetGPSData();
        String body = new RequestBuilder(this).BuildUploadGPSRequestBody(request,sessionid,deviceName);

        RestClient.DownloadListener listener = new RestClient.DownloadListener()
        {
            @Override
            public void onSuccesfulDownload(String result) {
                if(result!=null) {
                    if (result.compareTo("1") == 0)
                        Log.i("GPSUpload", "Successful upload");
                    else {
                        onFailedDownload(result);
                    }
                }
                else{
                    onFailedDownload(result);
                }
            }

            @Override
            public void onFailedDownload(String result) {
                //TODO: log result
                Log.i("GPSUpload","Failed upload");
            }


        };


        mAuthTask = new RestClient(body,host);
        mAuthTask.SetListener(listener);
        mAuthTask.execute();
    }


    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionLogPhotos(String sessionid) {
        // TODO: Handle action Baz
        Context context = getApplicationContext();
//        if(Build.VERSION.SDK_INT >= 21)
//        {
//            LogPhotoNew(context);
//        }
//        else {
//
            LogPhotoNew(context);
//        }
    }

    //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void LogPhotoNew(final Context context)
    {
        TakePhotoNew(context);
    }

    //@TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void TakePhotoNew(Context context)
    {
//        camera2 = Camera.open(0);
//        CamPreview camPreview = new CamPreview(context,camera2);
//        camPreview.setSurfaceTextureListener(camPreview);
//
//        CamCallback camCallback = new CamCallback();
//        camera2.setPreviewCallback(camCallback);
        SurfaceView view = new SurfaceView(this);
        try {
            camera2.setPreviewDisplay(view.getHolder());
        }
        catch(IOException ioException){
            Log.e("camera log","unable to set preview callback");
        }
        camera2.startPreview();
        Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
            @Override
            public void onShutter() {

            }
        };
        Camera.PictureCallback rawPictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {

            }
        };
        Camera.PictureCallback jpegPictureCallback = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                Log.i("camera log","picture taken");
            }
        };
        camera2.takePicture(shutterCallback, rawPictureCallback, jpegPictureCallback);
    }

    private void createCameraPreviewSession()
    {
//        SurfaceTexture texture = new SurfaceTexture();
//        Surface surface = new Surface();


        // Here, we create a CameraCaptureSession for camera preview.
//        ImageReader mImageReader;
//        _camera.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
//                new CameraCaptureSession.StateCallback() {
//
//                    @Override
//                    public void onConfigured(CameraCaptureSession cameraCaptureSession) {
//                        // The camera is already closed
//                        if (null == _camera) {
//                            return;
//                        }
//
//                        // When the session is ready, we start displaying the preview.
//                        mCaptureSession = cameraCaptureSession;
//                        try {
//                            mCaptureSession.setRepeatingRequest(mPreviewRequest,
//                                    mCaptureCallback, mBackgroundHandler);
//                        } catch (CameraAccessException e) {
//                            e.printStackTrace();
//                        }
//                    }
//
//                    @Override
//                    public void onConfigureFailed(CameraCaptureSession cameraCaptureSession) {
//                        //showToast("Failed");
//                    }
//                }, null
//        );
    }

//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    private void unlockFocus(CameraCaptureSession mCaptureSession,) {
//        try {
//            // Reset the autofucos trigger
//            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
//                    mBackgroundHandler);
//            // After this, the camera will go back to the normal state of preview.
//            mState = STATE_PREVIEW;
//            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
//                    mBackgroundHandler);
//        } catch (CameraAccessException e) {
//            e.printStackTrace();
//        }
//    }

    private void LogPhotoOld(Context context)
    {
//        final Intent intent = new Intent(this, SnapPhotoActivity.class);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        startActivity(intent);
        TakePhotoOld(context);

    }

    @SuppressWarnings("deprecation")
    private void TakePhotoOld(final Context context){
        final SurfaceView surfaceView = new SurfaceView(context);
        //final SurfaceTexture text2 = new SurfaceTexture();
        SurfaceHolder holder = surfaceView.getHolder();
        // deprecated setting, but required on Android versions prior to 3.0
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        holder.addCallback(new SurfaceHolder.Callback() {
            @Override
            //The preview must happen at or after this point or takePicture fails
            public void surfaceCreated(SurfaceHolder holder) {
//                showMessage("Surface created");
                Log.i("Test message", "Surface Created");
                Camera camera = null;

                try {
                    camera = Camera.open();
//                    showMessage("Opened camera");

                    try {
                        camera.setPreviewDisplay(holder);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                    camera.startPreview();
//                    showMessage("Started preview");

                    camera.takePicture(null, null, new Camera.PictureCallback() {

                        @Override
                        public void onPictureTaken(byte[] data, Camera camera) {
//                            showMessage("Took picture");
                            camera.release();
                        }
                    });
                } catch (Exception e) {
                    if (camera != null)
                        camera.release();
                    throw new RuntimeException(e);
                }
            }

            @Override public void surfaceDestroyed(SurfaceHolder holder) {}
            @Override public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {}
        });

        WindowManager wm = (WindowManager)context
                .getSystemService(Context.WINDOW_SERVICE);
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY,
                WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
                PixelFormat.TRANSLUCENT);

        //Don't set the preview visibility to GONE or INVISIBLE
        wm.addView(surfaceView, params);
        surfaceView.setZOrderOnTop(true);
        holder.setFormat(PixelFormat.TRANSPARENT);
        Log.i("TakePhotoOld", "surface view added");
    }

    private GPSRequest GetGPSData()
    {
        if (listener == null)
        {
                listener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    // Called when a new location is found by the network location provider.
                    lastLocation = location;
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {}

                public void onProviderEnabled(String provider) {}

                public void onProviderDisabled(String provider) {}
            };

        }
        if(lastLocation==null)
        {
            String locationProvider = LocationManager.GPS_PROVIDER;
            lastLocation = locationManager.getLastKnownLocation(locationProvider);
        }

        GPSRequest request = null;
        if(lastLocation!=null)
            request = new GPSRequest(lastLocation.getAccuracy(), lastLocation.getAccuracy(), lastLocation.getAltitude(), lastLocation.getLongitude(), lastLocation.getLatitude(), lastLocation.getSpeed(), "meter");
        return request;
    }
}
