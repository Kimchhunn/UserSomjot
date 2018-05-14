package com.blog.chhun.usersomjot;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class AddVehicleActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;

    private String user_id;
    private EditText veh_name, veh_type, veh_pro, veh_let, veh_plate_num;
    private Button addVehBtn;

    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_vehicle);

        firebaseAuth = FirebaseAuth.getInstance();
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_id = firebaseAuth.getCurrentUser().getUid();
        }

        firebaseFirestore = FirebaseFirestore.getInstance();

        veh_name = findViewById(R.id.veh_name);
        veh_type = findViewById(R.id.veh_type);
        veh_pro = findViewById(R.id.veh_pro);
        veh_let = findViewById(R.id.veh_letter);
        veh_plate_num = findViewById(R.id.veh_plate_num);

        addVehBtn = findViewById(R.id.add_veh_btn);

        addVehBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = veh_name.getText().toString();
                String type = veh_type.getText().toString();
                String pro = veh_pro.getText().toString();
                String let = veh_let.getText().toString();
                String pl_num = veh_plate_num.getText().toString();

                Map<String, String> vehicle1 = new HashMap<>();
                vehicle1.put("name", name);
                vehicle1.put("type", type);
                vehicle1.put("province", pro);
                vehicle1.put("letter", let);
                vehicle1.put("plate_number", pl_num);


                firebaseFirestore.collection("users").document(user_id).collection("vehicle").
                        document(user_id + name).set(vehicle1).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(AddVehicleActivity.this, "Vehicle info is updated.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            String error = task.getException().getMessage();
                            Toast.makeText(AddVehicleActivity.this, "(FIRESTORE Error) : "+error , Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

    }
}
