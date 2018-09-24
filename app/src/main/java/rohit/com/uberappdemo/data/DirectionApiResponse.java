package rohit.com.uberappdemo.data;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class DirectionApiResponse
{
    @SerializedName("geocoded_waypoints")
    @Expose
    private List<GeocodedWaypoint> geocodedWaypoints = null;
    @SerializedName("routes")
    @Expose
    private List<Route> routes = null;
    @SerializedName("status")
    @Expose
    private String status;

    public List<GeocodedWaypoint> getGeocodedWaypoints() {
        return geocodedWaypoints;
    }

    public List<Route> getRoutes() {
        return routes;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Example{" +
                "geocodedWaypoints=" + geocodedWaypoints +
                ", routes=" + routes +
                ", status='" + status + '\'' +
                '}';
    }
    
    
    public class GeocodedWaypoint implements Serializable
    {

        @SerializedName("geocoder_status")
        @Expose
        private String geocoderStatus;
        @SerializedName("place_id")
        @Expose
        private String placeId;

        public String getGeocoderStatus() {
            return geocoderStatus;
        }

        public String getPlaceId() {
            return placeId;
        }

        @Override
        public String toString() {
            return "GeocodedWaypoint{" +
                    "geocoderStatus='" + geocoderStatus + '\'' +
                    ", placeId='" + placeId + '\'' +
                    '}';
        }
    }
    
    public class Route implements Serializable
    {
        
        @SerializedName("overview_polyline")
        @Expose
        private OverviewPolyline overviewPolyline;

        public OverviewPolyline getOverviewPolyline() {
            return overviewPolyline;
        }

        @Override
        public String toString() {
            return "Route{" +
                    "overviewPolyline=" + overviewPolyline +
                    '}';
        }
    }
    
    public class OverviewPolyline implements Serializable
    {

        @SerializedName("points")
        @Expose
        private String points;

        public String getPoints() {
            return points;
        }

        public void setPoints(String points) {
            this.points = points;
        }

        @Override
        public String toString() {
            return "OverviewPolyline{" +
                    "points='" + points + '\'' +
                    '}';
        }
    }
}

