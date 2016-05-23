package blf.com.camtrack.request;

import java.util.Date;

/**
 * Created by bfadojutimi on 4/8/2015.
 */
public class GPSRequest {
    int haccuracy,vaccuracy, speed;
    double longitude,latitude,altitude;
    Date captured;
    String unit;
    public GPSRequest(float hacc, float vacc, double alt, double longitude, double lat, float speed, String unit)
    {
        this.haccuracy = (int)hacc;
        this.vaccuracy = (int)vacc;
        this.altitude = alt;
        this.captured = new Date();
        this.longitude = longitude;
        latitude = lat;
        this.unit = unit;
        this.speed = (int)speed;
    }
}
