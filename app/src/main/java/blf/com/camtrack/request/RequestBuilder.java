package blf.com.camtrack.request;

import blf.com.camtrack.R;
import android.content.Context;

import java.text.DateFormat;

/**
 * Created by bfadojutimi on 3/16/2015.
 */
public class RequestBuilder {
    Context context;

    public RequestBuilder(Context context)
    {
        this.context = context;
    }

    public String BuildLoginRequestBody(LoginRequest request)
    {
        final String requestString = "<LoginData xmlns=\"http://www.camtrack.com/\"><email>%s</email><password>%s</password></LoginData>";
        return String.format(requestString, request.username, request.password);
    }

    public String BuildUploadGPSRequestBody(GPSRequest request, String sessionid, String devicename)
    {
        final String requestString = "<GpsData><altitude>%s</altitude><captured>%s</captured><devicename>%s</devicename><haccuracy>%s</haccuracy><latitude>%s</latitude><longitude>%s</longitude><sessionid>%s</sessionid><speed>%s</speed><unit>%s</unit><vaccuracy>%s</vaccuracy></GpsData>";
        if(request == null)
            return "";
        String captured = DateFormat.getDateTimeInstance().format(request.captured);
        return String.format(requestString, request.altitude, captured, devicename, request.haccuracy, request.latitude, request.longitude, sessionid, request.speed, request.unit, request.vaccuracy);
    }
}
