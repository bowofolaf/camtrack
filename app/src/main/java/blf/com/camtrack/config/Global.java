package blf.com.camtrack.config;

import android.app.Application;

/**
 * Created by bfadojutimi on 4/30/2015.
 */
public class Global extends Application {
    public static Boolean UPLOAD_SERVICE_RUNNING = false;

    public void SetUploadServiceStatus(boolean running){
        UPLOAD_SERVICE_RUNNING = running;
    }

    public boolean GetUploadServiceStatus(){
        return UPLOAD_SERVICE_RUNNING;
    }
}
