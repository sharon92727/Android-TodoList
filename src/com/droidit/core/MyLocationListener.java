package com.droidit.core;

/**
 * Created with IntelliJ IDEA.
 * User: ravikumar
 * Date: 10/8/12
 * Time: 7:59 PM
 * To change this template use File | Settings | File Templates.
 */

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.widget.Toast;

public class MyLocationListener implements LocationListener {

    public static double latitude;
    public static double longitude;

    @Override
    public void onLocationChanged(Location loc)
    {

        latitude=loc.getLatitude();
        longitude=loc.getLongitude();
    }

    @Override
    public void onProviderDisabled(String provider)
    {
        //print "Currently GPS is Disabled";
        Toast.makeText(null,"GPS is disabled",Toast.LENGTH_LONG).show();
    }
    @Override
    public void onProviderEnabled(String provider)
    {
        //print "GPS got Enabled";
        Toast.makeText(null,"GPS enabled",Toast.LENGTH_LONG).show();

    }
    @Override
    public void onStatusChanged(String provider, int status, Bundle extras)
    {
    }
}

