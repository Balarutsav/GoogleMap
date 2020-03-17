package com.eagal.googlemaps;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.eagal.googlemaps.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    public static String TAG = MainActivity.class.getName();
    ActivityMainBinding binding;
    GpsTracker gpsTracker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        gpsTracker = new GpsTracker(MainActivity.this);
        initlist();

    }

    private void initlist() {
        binding.btnGetLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                {
                    Location gpsLocation = gpsTracker
                            .getLocation(LocationManager.GPS_PROVIDER);
                    if (gpsLocation != null) {
                        double latitude = gpsLocation.getLatitude();
                        double longitude = gpsLocation.getLongitude();
                        String result = "Latitude: " + gpsLocation.getLatitude() +
                                " Longitude: " + gpsLocation.getLongitude();
                        binding.tvAdd.setText(result);
                    } else {
                        showSettingsAlert();
                    }

                    Location location = gpsTracker
                            .getLocation(LocationManager.GPS_PROVIDER);

                    //you can hard-code the lat & long if you have issues with getting it
                    //remove the below if-condition and use the following couple of lines
                    //double latitude = 37.422005;
                    //double longitude = -122.084095

                    if (location != null) {
                        double latitude = location.getLatitude();
                        double longitude = location.getLongitude();
                        Utils locationAddress = new Utils();
                        locationAddress.getAddressFromLocation(latitude, longitude,
                                getApplicationContext(), new GeocoderHandler());
                    } else {
                        showSettingsAlert();
                    }

                }
            }
        });
    }

    private class GeocoderHandler extends Handler {
        @Override
        public void handleMessage(Message message) {
            String locationAddress;
            switch (message.what) {
                case 1:
                    Bundle bundle = message.getData();
                    locationAddress = bundle.getString("address");
                    break;
                default:
                    locationAddress = null;
            }
            binding.tvAdd.append("Add /n"+locationAddress);
        }
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(
                MainActivity.this);
        alertDialog.setTitle("SETTINGS");
        alertDialog.setMessage("Enable Location Provider! Go to settings menu?");
        alertDialog.setPositiveButton("Settings",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(
                                Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        MainActivity.this.startActivity(intent);
                    }
                });
        alertDialog.setNegativeButton("Cancel",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        alertDialog.show();
    }
}
