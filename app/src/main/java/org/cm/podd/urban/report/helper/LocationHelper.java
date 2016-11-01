package org.cm.podd.urban.report.helper;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationAvailability;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.androidannotations.api.view.OnViewChangedNotifier;

/**
 * Created by nat on 11/9/15 AD.
 */
public class LocationHelper implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    private static LocationHelper instance_;
    private final Context context_;
    private Context mContext;
    private GoogleApiClient mGoogleApiClient;
    private LocationListener _locationLisener;
    private LocationListener mLocationListenerCallback;

    GoogleApiClient.ConnectionCallbacks connectionCallbacks;
    public static final String TAG = LocationHelper.class.getSimpleName();
    private Location mLocation;

    private LocationHelper(Context context) {
        context_ = context;
        mContext = context;

        _locationLisener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                String provider = location.getProvider();
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                double altitude = location.getAltitude();
                float accuracy = location.getAccuracy();
                float bearing = location.getBearing();
                float speed = location.getSpeed();
                long time = location.getTime();

                mLocation = location;

                if (mLocationListenerCallback != null) {
                    mLocationListenerCallback.onLocationChanged(location);
                }
                else {
//                    Log.d(TAG, "mCurrentLocation NULL (line ): ");
                }


            }
        };

        buildGoogleApiClient();
    }


    public static LocationHelper getInstance_(Context context) {
        if (instance_ == null) {
            OnViewChangedNotifier previousNotifier = OnViewChangedNotifier.replaceNotifier(null);
            instance_ = new LocationHelper(context.getApplicationContext());
            instance_.init_();
            OnViewChangedNotifier.replaceNotifier(previousNotifier);
        }

        return instance_;

    }

    private void init_() {
        mContext = context_;
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        connect();

    }

    public void disconnect() {
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            // Disconnect Google API Client if available and connected
            mGoogleApiClient.disconnect();
        }
    }

    public void connect() {
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (connectionCallbacks != null) {
            connectionCallbacks.onConnected(bundle);
        }
        LocationAvailability locationAvailability =
                LocationServices.FusedLocationApi.getLocationAvailability(mGoogleApiClient);
        if (locationAvailability.isLocationAvailable()) {
            LocationRequest locationRequest = new LocationRequest()
                    .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY
                    )
                    .setInterval(1000);

            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                    locationRequest, _locationLisener);


        } else {
            // Do something when location provider not available
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (connectionCallbacks != null) {
            connectionCallbacks.onConnectionSuspended(i);
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    public void setCurrentLocation(Location location) {
        mLocation = location;
    }

    public Location getCurrentLocation() {
        return mLocation;
    }

    public void setOnLocationChanged(LocationListener callback) {
        mLocationListenerCallback = callback;
    }
}
