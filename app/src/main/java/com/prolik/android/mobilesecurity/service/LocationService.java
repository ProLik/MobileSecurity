package com.prolik.android.mobilesecurity.service;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;

public class LocationService extends Service {

    private SharedPreferences mSP;
    private LocationManager mLocationManager;
    private MyLocationListener mLocationListener;

    public LocationService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mSP = getSharedPreferences("config", MODE_PRIVATE);
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        // List<String> allProviders = lm.getAllProviders();// 获取所有位置提供者
        // System.out.println(allProviders);
        Criteria criteria = new Criteria();
        criteria.setCostAllowed(true);// 是否支持付费 3G 网络
        criteria.setAccuracy(Criteria.ACCURACY_FINE);//精确
        criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
        String bestProvider = mLocationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mLocationManager.requestLocationUpdates(bestProvider, 0, 0, mLocationListener);

    }

    class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location location) {
            mSP.edit()
                    .putString(
                            "location",
                            "j:" + location.getLongitude() + "; w:"
                                    + location.getLatitude()).commit();
            stopSelf();//停掉service
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mLocationManager.removeUpdates(mLocationListener);// 当activity销毁时,停止更新位置, 节省电量

    }
}

