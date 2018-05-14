package com.blog.chhun.usersomjot;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class MainActivity extends AppCompatActivity {
    ImageView image;
    String text2Qr;
    String name, acc_id, plate_num, plain, cipher;

    private String key = "ThisIsSomjotAppp"; // 128 bit key
    private String initVector = "RandomInitVector"; // 16 bytes IV

    private FirebaseFirestore firebaseFirestore;
    Spinner vList;


    ArrayList<String> paths = new ArrayList<String>();

    private Button checkBal;

    private FirebaseAuth firebaseAuth;

    private String user_id;

    private Button mapBtn;

    private Button addVehBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        firebaseAuth = FirebaseAuth.getInstance();

        firebaseFirestore = FirebaseFirestore.getInstance();

        Toast.makeText(this, firebaseAuth.toString(), Toast.LENGTH_SHORT).show();

//        System.out.println(firebaseAuth + "dddddddddddddd");
//        user_id = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            user_id = firebaseAuth.getCurrentUser().getUid();
        }

        checkBal = findViewById(R.id.main_check_bal_btn);

        image = (ImageView) findViewById(R.id.image);

        mapBtn = findViewById(R.id.main_map_btn);

        addVehBtn = findViewById(R.id.add_veh_btn);

        List<String> data;

        data = getVehicleList();

        for (String v: data){
            System.out.println(v + "===============");
        }
        String[] paths = {"PP-1BB-9854", "PP-1BK-9114", "PP-1FF-9900"};
//        System.out.println(data.size() + "-----------------");
//        String[] simpleArray = new String[10];
//        data.toArray(simpleArray);
//        System.out.println(simpleArray.length + "========================");

//        System.out.println(simpleArray.length + "[[[[[[[[[[[[[[");
        vList = (Spinner) findViewById(R.id.spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(MainActivity.this,
                android.R.layout.simple_spinner_item, paths);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vList.setAdapter(adapter);
//        if (vList.getSelectedItem() != null) {
//
//            String selected = vList.getSelectedItem().toString();
//            System.out.println(selected + "2222222222222222222");
//        }
        vList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("----------------");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                System.out.println("aaaaaaaaaaaaaa");
            }
        });


        vList.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
//                System.out.println("66666666666666666666666");
//                String item = adapter.getItem(pos);
//                System.out.println(item + "9999ppppppppp");
//                System.out.println(view + "==" + pos + "==" + id );
//                List<String> list = new ArrayList<String>()
// ;
//                list.add("Salary");
//                list.add("Money");
//                list.add("Income");
                switch (pos) {
                    case 0:
                        // Whatever you want to happen when the first item gets selected
//                        "[" + name + "," + acc_id + "," + plate_num + "," + user_id + "]"
//                        name + "|" + acc_id + "|" + plate_num + "|" + user_id
                        name = "Chhun";
                        acc_id = "0000234932";
                        plate_num = "PhnomPenh-1BB-9854";
                        plain = name + "--somjot--" + acc_id + "--somjot--" + plate_num + "--somjot--" + user_id;
                        cipher = encrypt(key, initVector, plain);
                        generateQRCode(cipher);
                        break;
                    case 1:
                        // Whatever you want to happen when the second item gets selected
                        name = "Kim";
                        acc_id = "0000333932";
                        plate_num = "PhnomPenh-1BK-9114";
                        plain = name + "--somjot--" + acc_id + "--somjot--" + plate_num + "--somjot--" + user_id;
                        cipher = encrypt(key, initVector, plain);
                        generateQRCode(cipher);
                        break;
                    case 2:
                        // Whatever you want to happen when the thrid item gets selected
                        name = "Tan";
                        acc_id = "00002343228";
                        plate_num = "PhnomPenh-PP-1FF-9900";
                        plain = name + "--somjot--" + acc_id + "--somjot--" + plate_num + "--somjot--" + user_id;
                        cipher = encrypt(key, initVector, plain);
                        generateQRCode(cipher);
                        break;

                }

            }
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        checkBal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToInfo();
            }
        });

        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToMap();
            }
        });

        addVehBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendToAddVehicle();
            }
        });
    }

    private List<String> getVehicleList() {
        final List<String> list = new ArrayList<String>();
        final String[] veh = new String[100];
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
                                }
                            } else {
                                Toast.makeText(MainActivity.this, "(FIRESTORE Error) : "
                                        + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
        return list;
    }

    private void sendToAddVehicle() {
        Intent addVehIntent = new Intent(this, AddVehicleActivity.class);
        startActivity(addVehIntent);
    }

    @Override
    protected void onStart() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            sentToLogin();
        }
        super.onStart();
    }

    public void generateQRCode(String QRString) {
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(QRString, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            image.setImageBitmap(bitmap);
        }
        catch (WriterException e){
            e.printStackTrace();
        }
    }

    public static String encrypt(String key, String initVector, String value) {
        try {
            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");

            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, iv);

            byte[] encrypted = cipher.doFinal(value.getBytes());
            System.out.println("encrypted string: "
                    + Base64.encode(encrypted, Base64.DEFAULT));

            return new String(Base64.encode(encrypted, Base64.DEFAULT));
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return null;
    }

    private void sentToLogin() {
        Intent loginIntent = new Intent(this, LoginActivity.class);
        startActivity(loginIntent);
        finish();
    }

    private void sendToInfo() {
        Intent infoIntent = new Intent(this, InfoActivity.class);
        startActivity(infoIntent);
    }

    private void sendToMap() {
        Intent mapIntent = new Intent(this, MapsActivity.class);
        startActivity(mapIntent);
    }

//    public static String decrypt(String key, String initVector, String encrypted) {
//        try {
//            IvParameterSpec iv = new IvParameterSpec(initVector.getBytes("UTF-8"));
//            SecretKeySpec skeySpec = new SecretKeySpec(key.getBytes("UTF-8"), "AES");
//
//            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING");
//            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv);
//
//            byte[] original = cipher.doFinal(Base64.decode(encrypted, Base64.DEFAULT));
//
//            return new String(original);
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        return null;
//    }



}
