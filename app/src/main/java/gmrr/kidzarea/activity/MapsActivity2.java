package gmrr.kidzarea.activity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

import gmrr.kidzarea.R;
import gmrr.kidzarea.helper.SQLiteHandler;
import gmrr.kidzarea.helper.SQLiteLocationHandler;
import gmrr.kidzarea.helper.SessionManager;

public class MapsActivity2 extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, com.google.android.gms.location.LocationListener {

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
   // private SessionManager session;
    //private SQLiteHandler db;
   // private Lokasi lokasiTerakhir;
    LocationRequest mLocationRequest;
    Location myLocation;
    private GoogleApiClient mGoogleApiClient;

    ImageButton btnViewMyLocation, btnSwitchMode, btnAddLocation;
    Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setLogo(R.drawable.ic_launcher);
        getSupportActionBar().setDisplayUseLogoEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        setContentView(R.layout.activity_maps2);
        buildGoogleApiClient();
        createLocationRequest();

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        btnViewMyLocation = (ImageButton) findViewById(R.id.btn_ViewMyLocation); //your button
        btnSwitchMode = (ImageButton) findViewById(R.id.btn_SwitchMode);
        btnAddLocation = (ImageButton) findViewById(R.id.btnAddLocation);
        btnLogout = (Button) findViewById(R.id.btnLogout);

        btnSwitchMode.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(),
                        SetLocationActivity.class);
                startActivity(i);
                finish();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutUser();
            }
        });

        btnAddLocation.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                //Location location = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                final Lokasi lokasi = new Lokasi(myLocation);
                // Input nama lokasi
                AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity2.this);
                final EditText txtNamaLokasi = new EditText(MapsActivity2.this);

                builder.setView(txtNamaLokasi).setTitle("Nama Lokasi").setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        lokasi.setName(txtNamaLokasi.getText().toString());
                        simpan(lokasi);

                    }
                }).setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                }).create().show();
            }
        });

        btnViewMyLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))      // Sets the center of the map to location user
                        .zoom(17)                   // Sets the zoom
                        .bearing(0)                // Sets the orientation of the camera
                        .tilt(10)                   // Sets the tilt of the camera to 30 degrees
                        .build();                   // Creates a CameraPosition from the builder
                mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);


    }

    public void simpan(final Lokasi lokasi) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                SQLiteLocationHandler dbHelper = new SQLiteLocationHandler(MapsActivity2.this);
                SQLiteDatabase db = dbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put("nama", lokasi.getName());
                values.put("longitude", lokasi.getLongitude());
                values.put("latitude", lokasi.getLatitude());
                db.insert("lokasi", null, values);
                MapsActivity2.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MapsActivity2.this, "Lokasi berhasil disimpan!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).start();
    }

    private void logoutUser() {
        // Launching the login activity
        Intent intent = new Intent(MapsActivity2.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        if(mGoogleApiClient != null)
        {
            mGoogleApiClient.connect();
        }
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    boolean doubleBackToExitPressedOnce = false;
    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Silakan klik dua kali untuk keluar.", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        myLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (myLocation != null) {
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))      // Sets the center of the map to location user
                    .zoom(17)                   // Sets the zoom
                    .bearing(0)                // Sets the orientation of the camera
                    .tilt(10)                   // Sets the tilt of the camera to 30 degrees
                    .build();                   // Creates a CameraPosition from the builder
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            MarkerOptions myMarker = new MarkerOptions()
                    .position(new LatLng(myLocation.getLatitude(), myLocation.getLongitude()))
                    .title("MyLocation");
            myMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.logokidz_kecil));
            mMap.addMarker(myMarker);
        }
        startLocationUpdates();
    }

    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();

    }

    @Override
    public void onLocationChanged(Location location) {
        myLocation = location;


    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(MapsActivity2.this, "Koneksi Gagal!", Toast.LENGTH_SHORT).show();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    protected void startLocationUpdates() {
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }
}
