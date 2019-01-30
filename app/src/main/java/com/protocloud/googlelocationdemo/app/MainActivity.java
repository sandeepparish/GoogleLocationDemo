package com.protocloud.googlelocationdemo.app;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.protocloud.googlelocationdemo.R;
import com.protocloud.googlelocationdemo.base.BaseActivity;
import com.protocloud.googlelocationdemo.utils.LogHelper;

//Extends base activity for access base activity functions
public class MainActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final static int REQUEST_CODE_GPS_SETTINGS = 0;
    private final static int LOCATION_PERMISSIONS_REQUEST_CODE = 1;

    FusedLocationProviderClient mFusedLocationClient;
    LocationRequest mLocationRequest;
    LocationCallback mLocationCallback;

    private GoogleApiClient googleApiClient;
    private Location myLocation;
    private ProgressDialog locationDialog;
    private Double latitude, longitude;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bindUIViews();
    }

    //Init of views and variables
    private void bindUIViews() {

        //Just for show a dialog during getting location process
        locationDialog = new ProgressDialog(this);
        locationDialog.setMessage("Getting location...");

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        /*From base Activity*/
        setClick(R.id.textGetLocation);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.textGetLocation:
                if (isPlayServicesAvailable()) {
                    getCurrentLocation();
                } else {
                    //Google play services is not available show a message for user
                    setTextViewText(R.id.currentLocationText, "Google play services is not installed on this device");
                }
                break;
            default:
                break;
        }
    }

    /**
     * Setting google api client for get user location
     */
    private void setUpGClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .enableAutoManage(this, 0, this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            googleApiClient.connect();
        }
    }

    /**
     * Getting user location
     */


    public void getCurrentLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                locationDialog.show();

                if (mFusedLocationClient != null) {

                    mLocationRequest = new LocationRequest();
                    mLocationRequest.setInterval(120000); // two minute interval
                    mLocationRequest.setFastestInterval(120000);
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        mLocationCallback = new LocationCallback() {
                            @Override
                            public void onLocationResult(LocationResult locationResult) {
                                for (Location location : locationResult.getLocations()) {
                                    onLocationChanged(location);
                                }
                            }
                        };

                        /** No Need we are requesting location updates after checking gps*/
                        //  mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());

                        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                                .addLocationRequest(mLocationRequest);

                        PendingResult<LocationSettingsResult> result =
                                LocationServices.SettingsApi
                                        .checkLocationSettings(googleApiClient, builder.build());

                        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                            @Override
                            public void onResult(LocationSettingsResult result) {
                                Status status = result.getStatus();
                                switch (status.getStatusCode()) {
                                    case LocationSettingsStatusCodes.SUCCESS:
                                        /**
                                         * Get last known recent location using new Google Play Services SDK (v11+)
                                         */
                                        int permissionLocation = ContextCompat
                                                .checkSelfPermission(MainActivity.this,
                                                        Manifest.permission.ACCESS_FINE_LOCATION);
                                        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                            mFusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper());
                                        }

                                        break;
                                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                        try {
                                            status.startResolutionForResult(MainActivity.this,
                                                    REQUEST_CODE_GPS_SETTINGS);
                                        } catch (IntentSender.SendIntentException e) {
                                            // Ignore the error.
                                        }
                                        break;
                                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                                        break;
                                }
                            }
                        });
                    }
                }
            } else {
                googleApiClient.connect();
            }
        } else {
            /**Google api client is null, connect it again*/
            setUpGClient();
        }
    }


    @Override
    public void onLocationChanged(Location location) {
        // New location has now been determined
        myLocation = location;
        if (myLocation != null) {

            /**Remove location dialog*/
            if (locationDialog != null && locationDialog.isShowing())
                locationDialog.dismiss();

            latitude = myLocation.getLatitude();
            longitude = myLocation.getLongitude();

            /*Set current lat and long on TextView*/
            setTextViewText(R.id.currentLocationText, "" + getResources().getString(R.string.currentLocationIs) + " Lat:" + latitude + "\n" + " Long:" + longitude);
            LogHelper.e("Latitude", "" + latitude);
            LogHelper.e("Longitude", "" + longitude);


            /**Disconnect google api client after getting location first time
             * and if you want to track Location continuously just remove it
             * */
            if (googleApiClient.isConnected()) {
                googleApiClient.disconnect();
            }
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        checkPermissions();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //You can display a message here
        LogHelper.e("connection Suspended", "error in getting location");


        /**Remove location dialog*/
        if (locationDialog != null && locationDialog.isShowing())
            locationDialog.dismiss();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //You can display a message here
        LogHelper.e("connection result", connectionResult.getErrorMessage());

        /**Remove location dialog*/
        if (locationDialog != null && locationDialog.isShowing())
            locationDialog.dismiss();
    }

    /**
     * Check for permission
     */
    private void checkPermissions() {
        int permissionLocation = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_PERMISSIONS_REQUEST_CODE);
        } else {
            //Permission is granted get user location
            getCurrentLocation();
        }
    }

    /**
     * Activity request for turn on gps settings
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_GPS_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        getCurrentLocation();
                        break;
                    case Activity.RESULT_CANCELED:
                        makeToast("GPS Request is Canceled by user");
                        LogHelper.e("Canceled", "GPS request is canceled by user");
                        /**Remove location dialog*/
                        if (locationDialog != null && locationDialog.isShowing())
                            locationDialog.dismiss();
                        break;
                }
                break;
        }
    }


    /**
     * Check requested permission status after request permission
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[],
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permissionLocation = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            //Permissions is granted by user get user location
            getCurrentLocation();
        }

    }

    /**
     * Change location state according activity lifecycle
     */


    @Override
    public void onResume() {
        super.onResume();

        //We are getting user location on click of btn if want we can do something like:
        // getCurrentLocation();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopLocationUpdates();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    /**
     * Stop tracking gps location
     */
    public void stopLocationUpdates() {
        // stop location updates
        if (mFusedLocationClient != null && mLocationCallback != null) {
            mFusedLocationClient.removeLocationUpdates(mLocationCallback);
        }
    }

}
