package rohit.com.uberappdemo;

import android.animation.ValueAnimator;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class GoogleMapActivity extends FragmentActivity implements OnMapReadyCallback {

    List<LatLng> markerPoints = new ArrayList<>();
    Marker marker;
    private GoogleMap mMap;
    private float v;
    private int i = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.uberMap);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng sydney = new LatLng(18.5204, 73.8567); // Pune latlong
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(sydney, 16));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                if (markerPoints.size() > 1) {
                    markerPoints.clear();
                    mMap.clear();
                }

                markerPoints.add(latLng);

                MarkerOptions options = new MarkerOptions();
                options.position(latLng);

                if (markerPoints.size() == 1) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                            .snippet("Source");

                } else if (markerPoints.size() == 2) {
                    options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                            .snippet("Destination");
                }

                // Add new marker to the Google Map Android API V2
                mMap.addMarker(options);

                // Checks, whether start and end locations are captured
                if (markerPoints.size() >= 2) {
                    LatLng origin = markerPoints.get(0);
                    LatLng dest = markerPoints.get(1);

                    // Getting URL to the Google Directions API
                    String url = MapUtil.getDirectionsUrl(origin, dest);

                    DirectionApiCall.getDirectionDataFromAPI(url, new IGetDataCallBack() {
                        @Override
                        public void onSuccess(String data) {
                            observableParseToJson(data);
                        }

                        @Override
                        public void onFailure(String msg) {

                        }
                    });

                }

            }
        });
    }

    private void animateCarOnMap(final List<LatLng> latLongs) {

        final Handler handler = new Handler();
        Timer gpsTrackTimer = new Timer();

        i = 0;

        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng latLng : latLongs) {
            builder.include(latLng);
        }
        LatLngBounds bounds = builder.build();
        CameraUpdate mCameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, 2);
        mMap.animateCamera(mCameraUpdate);

        marker = mMap.addMarker(new MarkerOptions().position(latLongs.get(i))
                .flat(true)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_car_90)));

        marker.setPosition(latLongs.get(i));

        final TimerTask task = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 5000);
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
                                            LatLng newPos = new LatLng(lat, lng);
                                            marker.setPosition(newPos);
                                            marker.setAnchor(0.5f, 0.5f);
                                            marker.setRotation(MapUtil.getBearing(latLongs.get(i - 1), newPos));
                                            mMap.animateCamera(CameraUpdateFactory.newLatLng(newPos));

                                        } else {
                                            valueAnimator.cancel();
                                            cancel();
                                            NotificationUtil.showNotification(GoogleMapActivity.this, GoogleMapActivity.class, "UberAppDemo", "You have reached destination successfully.");
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

        gpsTrackTimer.schedule(task, 1000, 5000);
    }

    private void observableParseToJson(String s) {
        Observable.just(MapUtil.parseToJSON(s))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<List<HashMap<String, String>>>>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(List<List<HashMap<String, String>>> result) {
                        List<LatLng> points = new ArrayList<>();
                        points.clear();
                        PolylineOptions lineOptions = null;

                        for (int i = 0; i < result.size(); i++) {
                            points = new ArrayList<>();
                            lineOptions = new PolylineOptions();

                            List<HashMap<String, String>> path = result.get(i);

                            for (int j = 0; j < path.size(); j++) {
                                HashMap<String, String> point = path.get(j);

                                double lat = Double.parseDouble(point.get("lat"));
                                double lng = Double.parseDouble(point.get("lng"));

                                LatLng position = new LatLng(lat, lng);
                                points.add(position);
                            }

                            lineOptions.addAll(points);
                            lineOptions.width(10);
                            lineOptions.color(Color.BLUE);
                            lineOptions.geodesic(true);

                        }

                        if (lineOptions != null) {
                            mMap.addPolyline(lineOptions);
                            animateCarOnMap(points);
                        } else {
                            Toast.makeText(GoogleMapActivity.this, "Couldn't get the data from an API.", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }
}
