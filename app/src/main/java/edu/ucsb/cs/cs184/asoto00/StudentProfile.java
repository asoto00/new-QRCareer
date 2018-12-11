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
    private EditText Link;
    private Button SignOutButton;
    private Button genBtn;
    private ImageView qrImage;
    private Button editInfo;
    private Button saveInfoBtn;
    private Button AddLinkBtn;
    private Button addResumeBtn;
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
        Link = (EditText) findViewById(R.id.Link);
        addResumeBtn=(Button)findViewById(R.id.addResume);


        SignOutButton = (Button) findViewById(R.id.SignOutButton);

        SignOutButton.setOnClickListener(this);
        genBtn.setOnClickListener(this);
        editInfo.setOnClickListener(this);
        saveInfoBtn.setOnClickListener(this);
        AddLinkBtn.setOnClickListener(this);
        addResumeBtn.setOnClickListener(this);

    }


    public void updateInfo(){
        SName.setText(studentUser.Name);
        SPhone.setText(studentUser.Phone.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3"));
        SMajor.setText(studentUser.Major);
        SGYear.setText(studentUser.GradYear + "");
        SGPA.setText(studentUser.GPA.toString());

    }
public void editInfo(){
    SName.setFocusableInTouchMode(true);
    SName.setFocusable (true);
    SName.requestFocus();
    //SName.setEnabled(true);
    SPhone.setFocusableInTouchMode(true);
    SPhone.setFocusable(true);
    SPhone.requestFocus();
    //SPhone.setEnabled(true);
    SMajor.setFocusableInTouchMode(true);
    SMajor.setFocusable(true);
    SMajor.requestFocus();
    //SMajor.setEnabled(true);
    SGYear.setFocusableInTouchMode(true);
    SGYear.setFocusable(true);
    SGYear.requestFocus();
    //SGYear.setEnabled(true);
    SGPA.setFocusableInTouchMode(true);
    SGPA.setFocusable(true);
    SGPA.requestFocus();
    //SGPA.setEnabled(true);
    Toast.makeText(this,"Test1",Toast.LENGTH_SHORT).show();
}
    public void saveInfo(){
        studentUser.Name = SName.getText().toString();

        studentUser.Phone = SPhone.getText().toString();
        studentUser.Major = SMajor.getText().toString();
        studentUser.GradYear = Integer.parseInt( SGYear.getText().toString());
        studentUser.GPA= Float.parseFloat(SGPA.getText().toString());
        Toast.makeText(this, Float.toString(studentUser.GPA), Toast.LENGTH_SHORT);

        DatabaseReference newDatabaseReference = FirebaseDatabase.getInstance().getReference();
        newDatabaseReference.child("users").child(user.getUid()).setValue(studentUser);


        SName.setFocusable (false);
       // SName.setEnabled(false);
        SPhone.setFocusable(false);
       // SPhone.setEnabled(false);
        SMajor.setFocusable(false);
       // SMajor.setEnabled(false);
        SGYear.setFocusable(false);
       // SGYear.setEnabled(false);
        SGPA.setFocusable(false);
        //SGPA.setEnabled(false);

        Toast.makeText(this,"Information Saved Successfully",Toast.LENGTH_SHORT).show();
    }

    public void addLink(){

    }


    @Override
    public void onClick(View v) {
        if(v == SignOutButton){
            firebaseAuth.signOut();

            startActivity(new Intent(this, Sign_In.class));
            finish();
        }
        else if (v==editInfo){
            //make all shits editable
           editInfo();
        }
        else if(v==saveInfoBtn){
            saveInfo();
            //save edited shit
        }
        else if(v==AddLinkBtn){
            //add link to linkedIn
            if(Link.getText() != null && !Link.getText().toString().equals("")){
                studentUser.LinkedInLink = Link.getText().toString().trim();
                DatabaseReference addlinkReference = FirebaseDatabase.getInstance().getReference();
                addlinkReference.child("users").child(user.getUid()).setValue(studentUser);

            }else{
                Toast.makeText(this, "Please Enter a Valid Link", Toast.LENGTH_SHORT);
            }


        }

        else if(v==addResumeBtn){
            //add link to linkedIn

            startActivity(new Intent(this, ResumeActivity.class));
            finish();
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
