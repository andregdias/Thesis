package com.opt.mobipag.data;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;

import com.opt.mobipag.R;

public class GPSTracker extends Service implements LocationListener {

    private final Context mContext;

    private Location location = null; // location

    // Declaring a Location Manager
    private LocationManager locationManager;

    public GPSTracker(Context context) {
        this.mContext = context;
        getLocation();
    }

    public Location getLocation() {
        try {
            locationManager = (LocationManager) mContext
                    .getSystemService(LOCATION_SERVICE);

            // getting GPS status
            boolean GPSEnabled = locationManager
                    .isProviderEnabled(LocationManager.GPS_PROVIDER);

            // getting network status
            boolean networkEnabled = locationManager
                    .isProviderEnabled(LocationManager.NETWORK_PROVIDER);

            if (GPSEnabled || networkEnabled) {
                // First get location from Network Provider
                if (networkEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            getResources().getInteger(R.integer.MIN_TIME_BW_UPDATES),
                            getResources().getInteger(R.integer.MIN_DISTANCE_CHANGE_FOR_UPDATES), this);
                    location = locationManager
                            .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }
                // if GPS Enabled get lat/long using GPS Services
                if (GPSEnabled) {
                    locationManager.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            getResources().getInteger(R.integer.MIN_TIME_BW_UPDATES),
                            getResources().getInteger(R.integer.MIN_DISTANCE_CHANGE_FOR_UPDATES), this);
                    Location temp = locationManager
                            .getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if(location!=null && temp.getAccuracy()<location.getAccuracy())
                        location=temp;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return location;
    }

    /**
     * Stop using GPS listener
     * Calling this function will stop using GPS in your app
     * */
    public void stopUsingGPS(){
        if(locationManager != null){
            locationManager.removeUpdates(GPSTracker.this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    }

    @Override
    public void onProviderDisabled(String provider) {
    }

    @Override
    public void onProviderEnabled(String provider) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}