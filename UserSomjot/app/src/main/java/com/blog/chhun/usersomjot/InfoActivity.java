package com.blog.chhun.usersomjot;

import android.Manifest;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.PermissionChecker;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InfoActivity extends AppCompatActivity {
    private Uri mainImageUri = null;

    private TextView balance;

    private FirebaseAuth firebaseAuth;

    private String user_id;

    private FirebaseFirestore firebaseFirestore;

    public static String docRef = null;

    private ListView list;

//    List<String> data = new ArrayList<>();

    private Button addNewVehBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        balance = findViewById(R.id.info_bal);

        list = findViewById(R.id.info_veh_list_view);


        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();

        user_id = firebaseAuth.getCurrentUser().getUid();

        addNewVehBtn = findViewById(R.id.add_veh_btn);

        Toast.makeText(this, firebaseAuth.getUid(), Toast.LENGTH_SHORT).show();

//        populateListView();
        getVehicleList();
        registerClickCallBack();
//        firebaseFirestore.collection("users")
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (DocumentSnapshot document : task.getResult()) {
//                                document.getReference();
//                                if (document.getReference().getId().equals(docRef)) {
//                                    String a = document.getData().get("balance").toString();
//                                    Toast.makeText(InfoActivity.this, a + "fffffffff", Toast.LENGTH_SHORT).show();
//                                    balance.setText(a);
//                                }
//                            }
//                        } else {
//                            Toast.makeText(InfoActivity.this, "(FIRESTORE Error) : "
//                                    + task.getException(), Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });

        firebaseFirestore.collection("users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        balance.setText(task.getResult().get("balance").toString().trim());
                    } else {
                        Toast.makeText(InfoActivity.this, "Data does not exists", Toast.LENGTH_SHORT).show();
                    }
                }
                    else {
                        String error = task.getException().getMessage();
                        Toast.makeText(InfoActivity.this, "(FIRESTORE Retrieve Error) : "+error , Toast.LENGTH_SHORT).show();
                    }
                }
            });

        addNewVehBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToAddVehicle();
            }
        });

//        Map<String, Float> userMap = new HashMap<>();
//        float amt = 10000;
//        userMap.put("balance", amt);
//
//        firebaseFirestore.collection("users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(InfoActivity.this, "Account info is updated.", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(InfoActivity.this, user_id, Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    String error = task.getException().getMessage();
//                    Toast.makeText(InfoActivity.this, "(FIRESTORE Error) : "+error , Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

//        // Create a new user with a first, middle, and last name
//        if (docRef == null) {
//            Map<String, Float> user = new HashMap<>();
//            float amt = 1000550;
//            user.put("balance", amt);
//
//            // Add a new document with a generated ID
//            firebaseFirestore.collection("users")
//                    .add(user)
//                    .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
//                        @Override
//                        public void onSuccess(DocumentReference documentReference) {
//                            Toast.makeText(InfoActivity.this, "DocumentSnapshot added with ID: " +
//                                    documentReference.getId(), Toast.LENGTH_SHORT).show();
//                            docRef = documentReference.getId();
//                        }
//                    })
//                    .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(InfoActivity.this, "Error adding document " +
//                                    e, Toast.LENGTH_SHORT).show();
//                        }
//                    });
//        }

    }

    private void sendToAddVehicle() {
        Intent addNewVehIntent = new Intent(this, AddVehicleActivity.class);
        startActivity(addNewVehIntent);
    }

    private void registerClickCallBack() {
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView textView = (TextView) view;
                Toast.makeText(InfoActivity.this, textView.getText().toString() + position, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void populateListView(List<String> myVeh) {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_view_item, myVeh);
        list.setAdapter(adapter);
     }

    private void getVehicleList() {
        final List<String> list = new ArrayList<>();
        if (user_id != null) {
            firebaseFirestore.collection("users")
                    .document(user_id)
                    .collection("vehicle")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot document : task.getResult()) {
                                    String a = document.getData().get("name").toString();
                                    list.add(a);
                                    populateListView(list);
                                }
                            } else {
                                Toast.makeText(InfoActivity.this, "(FIRESTORE Error) : "
                                        + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

//    private void storeFirestore(@NonNull Task<UploadTask.TaskSnapshot> task) {
//        Map<String, Float> userMap = new HashMap<>();
//        float amt = 10000;
//        userMap.put("name", amt);
//
//        firebaseFirestore.collection("Users").document(user_id).set(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                if (task.isSuccessful()) {
//                    Toast.makeText(InfoActivity.this, "Account info is updated.", Toast.LENGTH_SHORT).show();
//                }
//                else {
//                    String error = task.getException().getMessage();
//                    Toast.makeText(InfoActivity.this, "(FIRESTORE Error) : "+error , Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
}
