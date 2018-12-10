package edu.ucsb.cs.cs184.asoto00;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.Arrays;

public class StudentProfile extends AppCompatActivity implements View.OnClickListener {

    private EditText SName;
    private EditText SPhone;
    private EditText SMajor;
    private EditText SGYear;
    private EditText SGPA;
    private Button SignOutButton;
    private Button genBtn;
    private ImageView qrImage;
    private Button editInfo;
    private Button saveInfoBtn;
    private Button AddLinkBtn;
    //private ImageView QRCode;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private StudentUser studentUser;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);

        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();


        if(user == null){
            startActivity( new Intent(this, Sign_In.class));
            finish();
        }else{
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    studentUser = dataSnapshot.getValue(StudentUser.class);
                    updateInfo();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }


        SName = (EditText) findViewById(R.id.SName);
        SPhone = (EditText) findViewById(R.id.SPhone);
        SMajor = (EditText) findViewById(R.id.SMajor);
        SGYear = (EditText) findViewById(R.id.SGYear);
        SGPA = (EditText) findViewById(R.id.SGPA);
        genBtn= (Button) findViewById(R.id.generatorButton);
        qrImage= (ImageView) findViewById(R.id.qrImage);
        editInfo= (Button)findViewById(R.id.editInfo);
        saveInfoBtn=(Button)findViewById(R.id.saveInfo);
        AddLinkBtn=(Button)findViewById(R.id.addLink);


        SignOutButton = (Button) findViewById(R.id.SignOutButton);

        SignOutButton.setOnClickListener(this);
        genBtn.setOnClickListener(this);
        editInfo.setOnClickListener(this);
        saveInfoBtn.setOnClickListener(this);
        AddLinkBtn.setOnClickListener(this);

    }


    public void updateInfo(){
        SName.setText(studentUser.Name);
        SPhone.setText(studentUser.Phone.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3"));
        SMajor.setText(studentUser.Major);
        SGYear.setText(studentUser.GradYear + "");
        SGPA.setText(studentUser.GPA.toString());

    }

    public void saveInfo(){
        studentUser.Name = SName.getText().toString();

        studentUser.Phone = SPhone.getText().toString();
        studentUser.Major = SMajor.getText().toString();
        studentUser.GradYear = Integer.parseInt( SGYear.getText().toString());
        studentUser.GPA= Float.parseFloat(SGPA.getText().toString());
        Toast.makeText(this, Float.toString(studentUser.GPA), Toast.LENGTH_SHORT);
        //user.updatePhoneNumber(studentUser.Phone);
        //databaseReference.child("users").child(user.getUid()).;
        String n = SName.getText().toString().trim();
        String p = SPhone.getText().toString().trim();
        String m = SMajor.getText().toString().trim();
        int gy = Integer.parseInt(SGYear.getText().toString());
        Float gpa = Float.parseFloat(SGPA.getText().toString());

       // writeNewUser(user.getUid(),n,p,m,gy,gpa,user.getEmail());


        SName.setFocusable (false);
        SName.setEnabled(false);
        SPhone.setFocusable(false);
        SPhone.setEnabled(false);
        SMajor.setFocusable(false);
        SMajor.setEnabled(false);
        SGYear.setFocusable(false);
        SGYear.setEnabled(false);
        SGPA.setFocusable(false);
        SGPA.setEnabled(false);

        Toast.makeText(this,"Test2",Toast.LENGTH_SHORT).show();
    }


//    private void writeNewUser(String userId, String name, String phone, String major,int gradYear, Float gpa, String email) {
//        StudentUser user = new StudentUser(name,phone,major,gradYear,gpa,email);
//
//        databaseReference.child("users").child(userId).setValue(user);
//    }

    @Override
    public void onClick(View v) {
        if(v == SignOutButton){
            firebaseAuth.signOut();

            startActivity(new Intent(this, Sign_In.class));
            finish();
        }
        else if (v==editInfo){
            //make all shits editable
            SName.setFocusable (true);
            SName.setEnabled(true);
            SPhone.setFocusable(true);
            SPhone.setEnabled(true);
            SMajor.setFocusable(true);
            SMajor.setEnabled(true);
            SGYear.setFocusable(true);
            SGYear.setEnabled(true);
            SGPA.setFocusable(true);
            SGPA.setEnabled(true);
            Toast.makeText(this,"Test1",Toast.LENGTH_SHORT).show();
        }
        else if(v==saveInfoBtn){
            saveInfo();
            //save edited shit
        }
        else if(v==AddLinkBtn){
            //add link to linkedIn
        }

        else if (v == genBtn) {
//            for(int i=0; i<text2Qr.length ; i++){
//                r[i] = text2Qr[i].getText().toString().trim();
//            }
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            try {
                // String str = Arrays.toString(r);
                // String s = user.
                BitMatrix bitMatrix = multiFormatWriter.encode(user.getUid(), BarcodeFormat.QR_CODE, 200, 200);
                BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                qrImage.setImageBitmap(bitmap);
            } catch (WriterException e) {
                e.printStackTrace();
            }
        }
        else{
            Toast.makeText(this, "WTF",Toast.LENGTH_SHORT).show();
        }
    }
}
