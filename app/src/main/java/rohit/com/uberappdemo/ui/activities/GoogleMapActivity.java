package rohit.com.uberappdemo.ui.activities;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Handler;
import android.text.TextUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import rohit.com.uberappdemo.R;
import rohit.com.uberappdemo.data.DirectionApiResponse;
import rohit.com.uberappdemo.interfaces.IGetDataCallBack;
import rohit.com.uberappdemo.presenter.MapActivityPresenter;
import rohit.com.uberappdemo.ui.BaseActivity;
import rohit.com.uberappdemo.utility.MapUtil;
import rohit.com.uberappdemo.utility.NotificationUtil;

public class GoogleMapActivity extends BaseActivity implements OnMapReadyCallback, IGetDataCallBack<String> {

    private List<LatLng> markerPoints = new ArrayList<>();
    private Marker carMarker; // Car as marker
    private GoogleMap uberMap;
    private float v;
    private int i = 0;
    private MapActivityPresenter mapActivityPresenter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_google_map;
    }

    @Override
    protected void initPresenter() {
        mapActivityPresenter = new MapActivityPresenter(this);
    }

    @Override
    protected void init() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.uberMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        uberMap = googleMap;

        //Pune latlong
        uberMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(18.5204, 73.8567), 16));

        uberMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (markerPoints.size() > 1) {
                    markerPoints.clear();
                    uberMap.clear();
                }

                markerPoints.add(latLng);

                MarkerOptions options = new MarkerOptions();
                options.position(latLng);

                if (markerPoints.size() == 1) { // Source (Green carMarker)
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .snippet(getString(R.string.source));

                } else if (markerPoints.size() == 2) { // Destination (Red carMarker)
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA))
                            .snippet(getString(R.string.destination));
                }

                // Add markers to map.
                uberMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng origin = markerPoints.get(0);
                    LatLng dest = markerPoints.get(1);

                    // Generated Direction API url and perform API call.
                    String directionsUrl = MapUtil.getDirectionsUrl(origin, dest);
                    mapActivityPresenter.callDirectionAPI(directionsUrl);
                }

            }
        });
    }

    @Override
    public void onSuccess(String data) {
        if (!TextUtils.isEmpty(data)) {
            observableParseToJson(data);
        } else {
            Toast.makeText(this, R.string.error_direction_api, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onFailure(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    /*
     *   This function is used to parse data Direction API data to JSON.
     * */
    private void observableParseToJson(String s) {

        Gson gson = new Gson();
        DirectionApiResponse apiResponse = gson.fromJson(s, DirectionApiResponse.class);

        List<DirectionApiResponse.Route> routes = apiResponse.getRoutes();
        List<String> points = new ArrayList<>();
        for (DirectionApiResponse.Route route : routes) {
            points.add(route.getOverviewPolyline().getPoints());
        }

        List<LatLng> pointList = new ArrayList<>();
        for (String point : points) {
            pointList = PolyUtil.decode(point);
        }
        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(pointList);
        lineOptions.width(10);
        lineOptions.color(Color.BLUE);
        lineOptions.geodesic(true);

        if (!pointList.isEmpty()) {
            uberMap.addPolyline(lineOptions);
            animateCarOnMap(pointList);
        } else { // Got unexpected data from the server.
            Toast.makeText(GoogleMapActivity.this, R.string.error_direction_api, Toast.LENGTH_SHORT).show();
        }
    }

    /*
     *   This function is used animate the car on Map from source to destination.
     * */

    private void animateCarOnMap(final List<LatLng> latLongs) {

        final Handler handler = new Handler();
        Timer timer = new Timer();

        i = 0;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : latLongs) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
        uberMap.animateCamera(mCameraUpdate);

        // Added Car icon as carMarker.
        carMarker = uberMap.addMarker(new MarkerOptions().position(latLongs.get(i))
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car)));
        carMarker.setPosition(latLongs.get(i));

        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 100);
                            valueAnimator.setDuration(5000);
                            valueAnimator.setInterpolator(new LinearInterpolator());
                            valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                @Override
                                public void onAnimationUpdate(ValueAnimator valueAnimator) {
                                    v = valueAnimator.getAnimatedFraction();

                                    if (i > 2)
                                        if (i <= latLongs.size()) {
                                            double lng = v * latLongs.get(i - 1).longitude + (1 - v)
                                                    * latLongs.get(i - 2).longitude;
                                            double lat = v * latLongs.get(i - 1).latitude + (1 - v)
                                                    * latLongs.get(i - 2).latitude;

                                            LatLng newPosition = new LatLng(lat, lng);
                                            carMarker.setPosition(newPosition);
                                            carMarker.setAnchor(0.5f, 0.5f);
                                            carMarker.setRotation(MapUtil.getBearing(latLongs.get(i - 1), newPosition));
                                            uberMap.animateCamera(CameraUpdateFactory.newLatLng(newPosition));

                                        } else {
                                            valueAnimator.cancel();
                                            cancel(); // cancel the timer.
                                            //Show notification after car being reached the destination.
                                            NotificationUtil.showNotification(GoogleMapActivity.this, getString(R.string.notification_title), getString(R.string.notification_msg));
                                        }
                                    i++;
                                }
                            });

                            valueAnimator.start();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        };

        timer.schedule(task, 1000, 3000); // Schedule the task.
    }
}
