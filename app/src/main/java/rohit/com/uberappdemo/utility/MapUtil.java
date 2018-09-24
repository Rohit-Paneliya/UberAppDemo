package rohit.com.uberappdemo.utility;

import com.google.android.gms.maps.model.LatLng;

import static rohit.com.uberappdemo.utility.Constants.URL_DIRECTION_API;

public class MapUtil {

    /*
     *   This function is used to generate the getDirectionAPI URL.
     * */
    public static String getDirectionsUrl(LatLng origin, LatLng dest) {

        // Origin of route
        String strOrigin = "origin=" + origin.latitude + "," + origin.longitude;

        // Destination of route
        String strDestination = "destination=" + dest.latitude + "," + dest.longitude;

        // Building the parameters to the web service
        String parameters = strOrigin + "&" + strDestination;

        // Output format
        String output = "json";

        // Building the url to the web service
        return URL_DIRECTION_API + output + "?" + parameters;
    }

    /*
     *   This function is used to change the direction of Car.
     * */
    public static float getBearing(LatLng begin, LatLng end) {
        double lat = Math.abs(begin.latitude - end.latitude);
        double lng = Math.abs(begin.longitude - end.longitude);

        if (begin.latitude < end.latitude && begin.longitude < end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)));
        else if (begin.latitude >= end.latitude && begin.longitude < end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 90);
        else if (begin.latitude >= end.latitude && begin.longitude >= end.longitude)
            return (float) (Math.toDegrees(Math.atan(lng / lat)) + 180);
        else if (begin.latitude < end.latitude && begin.longitude >= end.longitude)
            return (float) ((90 - Math.toDegrees(Math.atan(lng / lat))) + 270);
        return -1;
    }
}
