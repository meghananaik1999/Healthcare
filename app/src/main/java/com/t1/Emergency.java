package com.t1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.internal.Constants;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsLeg;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;
import com.google.maps.model.Duration;
import com.google.maps.model.TravelMode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Emergency extends AppCompatActivity {

    private static final int REQUEST_CALL=1;

    RecyclerView recyclerView;
    ArrayList<ambmodel> amb_list;
    FirebaseFirestore db;
    private CollectionReference mref;
    FirebaseAuth firebaseAuth;
    Intent intent;
    String latitude,longitude;
    LocationManager locationManager;
    LocationListener locationListener;
    LatLng userlocation;
   // private static final String API_KEY ="AIzaSyCg0693hHjd0Pl9qMR8euPqK6N5DG_9FA8";





    /*public void bookAmb(View view)
    {
        mref=db.collection("doctors");
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {

                locationManager.removeUpdates(locationListener);

                userlocation = new LatLng(location.getLatitude(),location.getLongitude());
                latitude = String.valueOf(location.getLatitude());
                longitude = String.valueOf(location.getLongitude());



                mref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                final String doc_log=document.get("Longitude").toString();
                                final String doc_lat=document.get("Latitude").toString();
                                String clinic=document.get("clinicname").toString();
                                LatLng origin=new LatLng(Double.parseDouble(doc_lat),Double.parseDouble(doc_log));
                                LatLng dest=new LatLng(Double.parseDouble(doc_lat),Double.parseDouble(doc_log));

                                final String org=origin.toString();
                                final String dst=dest.toString();

                                Map<String,Object> data_user = new HashMap<>();
                                data_user.put("Patient Longitude",longitude);
                                data_user.put("Patient Latitude",latitude);

                                data_user.put("Doctor Longitude",doc_log);
                                data_user.put("Doctor Latitude",doc_lat);
                                data_user.put("Clinic name",clinic);
                                data_user.put("booked","true");

                                db.collection("ambulance")
                                        .add(data_user)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                String time=getDurationForRoute(org,dst);
                                                Toast.makeText(Emergency.this,"Ambulance arriving in "+time,Toast.LENGTH_LONG).show();

                                                Log.i("message","success");

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.i("message","failure");
                                            }
                                        });


                            }
                        }
                    }
                });



            }

            public String getDurationForRoute(String origin, String destination)
            {
            // - We need a context to access the API
            GeoApiContext geoApiContext = new GeoApiContext.Builder()
                    .apiKey(API_KEY)
                    .build();

            // - Perform the actual request
                DirectionsResult directionsResult = null;
                try {
                    directionsResult = DirectionsApi.newRequest(geoApiContext)
                            .mode(TravelMode.DRIVING)
                            .origin(origin)
                            .destination(destination)
                            .await();
                } catch (ApiException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // - Parse the result
            DirectionsRoute route = directionsResult.routes[0];
            DirectionsLeg leg = route.legs[0];
            Duration duration = leg.duration;
    return duration.humanReadable;
        }
            @Override
            public void onStatusChanged(String s, int i, Bundle bundle) {

            }

            @Override
            public void onProviderEnabled(String s) {

            }

            @Override
            public void onProviderDisabled(String s) {

            }
        };


        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);

        }
        else
        {

            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

        }

    }

*/
    //Finishes activity on back key pressed
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK)
        {
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.amblist);

        intent = getIntent();
        final Context mcontext = this;

        //Setting Up Recycler View and the adapter
        db = FirebaseFirestore.getInstance();
        mref =  db.collection("doctors");

        recyclerView = findViewById(R.id.rv1);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView.LayoutManager rvLiLayoutManager = layoutManager;
        recyclerView.setLayoutManager(rvLiLayoutManager);
        amb_list = new ArrayList<>();

        mref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful())
                {
                    for(QueryDocumentSnapshot document : task.getResult())
                    {
                        amb_list.add(new ambmodel(document.get("name").toString(),document.get("phone no").toString(),mcontext));




                    }

                    Log.d("Status","success");

                    amb_adapter ambadapter = new amb_adapter(Emergency.this,amb_list);
                    recyclerView.setAdapter(ambadapter);

                }
                else
                {

                    Log.d("Status","failed");
                }
            }
        });



    }

  /*  public void makePhoneCall()
    {
        mref=db.collection("doctors");

        mref.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {

                        String number=document.get("phone no").toString();
                        if(ContextCompat.checkSelfPermission(Emergency.this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED)
                        {
                            ActivityCompat.requestPermissions(Emergency.this,new String[]{Manifest.permission.CALL_PHONE},REQUEST_CALL);
                        }
                        else
                        {
                            String dial = "tel:"+ number;
                            startActivity(new Intent(Intent.ACTION_CALL,Uri.parse(dial)));
                        }
                    }
                }
            }
        });


    }

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == REQUEST_CALL)
        {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                makePhoneCall();
            }
            else
            {
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {


                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            }
        }
    }*/




}
