package com.t1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.drm.DrmStore;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.annotations.Nullable;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.model.Document;

import java.util.HashMap;
import java.util.Map;

public class docdetail extends AppCompatActivity {
    FirebaseFirestore db;
    private CollectionReference mref,mref1;
    FirebaseAuth firebaseAuth;
    Intent intent,intent1;
    String id,doc_id,doctoruid;//id is patient document doc_id is doctor document id
    String doc_name,doc_loc,doc_type,doc_xp,doc_fees,doc_rating;
    TextView t_name,t_location,t_type,t_xp,t_fees,t_rating;

    public void bookappointment(View view)

    {
        //Use uid to get doctors document
        //Populate appoinmnet of patient side with doctor name

        //Here Ftech for patient uid from firebase auth and send in the request
        String useruid      = firebaseAuth.getCurrentUser().getUid();

        //Creating Document in patient side database and storing it in both doctor and patient side
        //On patient side it is used for notifactions and doctor side it is for updation
        mref1 = db.collection("patients");

        DocumentReference dref = mref1.document(useruid).collection("appointment").document();
        DocumentReference dref1 = db.collection("doctors").document(doctoruid)
                .collection("appointment").document();

        id = dref.getId();
        doc_id = dref1.getId();


        Map<String,String> appointmentData = new HashMap<>();
        appointmentData.put("patientuid",useruid);

        //Status implies if appointment is accepted by doctor

        appointmentData.put("status","false");

        //completed tells if appointment is completed or yet to be completed

        appointmentData.put("completed","false");

        appointmentData.put("Timing","Specifed by doctor");

        appointmentData.put("docref",id);


        dref1.set(appointmentData)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        if(task.isSuccessful())
                        {
                            Log.d("Status","Success");
                        }
                        else
                        {
                            Log.d("Status","failed");
                        }


                    }
                });

        /*mref.document(doctoruid).collection("appointment").document().set(appointmentData)
                 .addOnCompleteListener(new OnCompleteListener<Void>() {
                     @Override
                     public void onComplete(@NonNull Task<Void> task) {
                         if(task.isSuccessful())
                         {
                             Log.d("Status","Success");
                         }
                         else
                         {
                             Log.d("Status","failed");
                         }
                     }
                 });*/

         //Setting data to patients document as well



        //Initially it was pointing to doctors collection
        //mref1 = db.collection("patients");

       // DocumentReference dref = mref1.document(useruid).collection("appointment").document();

        //Setting data to patients document as well

        String docname = t_name.getText().toString();

        Map<String,String> appointmentData1 = new HashMap<>();
        appointmentData1.put("doctoruid",doctoruid);
        appointmentData1.put("docname",docname);


        //Status implies if appointment is accepted by doctor

        appointmentData1.put("status","false");

        //completed tells if appointment is completed or yet to be completed

        appointmentData1.put("completed","false");

        appointmentData1.put("Timing","Specifed by doctor");


        //Here im storing docref of same document so that it is easier to add data change listener
        // id = dref.getId();

        appointmentData1.put("docref",id);


        //Here im storing doctor appoiontment document for cancellation purpose
        appointmentData1.put("doctordocument",doc_id);





        dref.set(appointmentData1)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful())
                        {
                            Log.d("DOCUMENT",id);
                            Log.d("Status","Success");
                        }
                        else
                        {
                            Log.d("Status","failed");
                        }
                    }
                });

    }

    public void addreveiws(View view)
    {

        Intent intent_reveiws = new Intent(docdetail.this,insertreveiw.class);
        intent_reveiws.putExtra("doctoruid",doctoruid);
        startActivity(intent_reveiws);


    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_docdetail);

        db = FirebaseFirestore.getInstance();
        mref =  db.collection("doctors");
        intent = getIntent();
        firebaseAuth = FirebaseAuth.getInstance();

        //DoctorUID
        doctoruid    = intent.getStringExtra("uid");

        t_name = findViewById(R.id.docname);
        t_fees = findViewById(R.id.docfees);
        t_location = findViewById(R.id.doclocation);
        t_rating = findViewById(R.id.docrating);
        t_xp = findViewById(R.id.docxp);
        t_type = findViewById(R.id.doctype);

        mref.document(doctoruid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {

                    DocumentSnapshot document  = task.getResult();
                    if(document.exists())
                    {
                        t_name.setText(document.get("name").toString());
                        t_fees.setText(document.get("consultationfees").toString());
                        t_location.setText(document.get("Location").toString());
                        t_xp.setText(document.get("xp").toString() + " + years");
                        t_type.setText(document.get("Type").toString());
                        t_rating.setText(document.get("rating").toString());

                    }

                }
            }
        });



    }
}