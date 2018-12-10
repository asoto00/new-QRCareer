package edu.ucsb.cs.cs184.asoto00;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

    private TextView SName;
    private TextView SPhone;
    private TextView SMajor;
    private TextView SGYear;
    private TextView SGPA;
    private Button SignOutButton;
    private Button genBtn;
    private ImageView qrImage;
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


        SName = (TextView) findViewById(R.id.SName);
        SPhone = (TextView) findViewById(R.id.SPhone);
        SMajor = (TextView) findViewById(R.id.SMajor);
        SGYear = (TextView) findViewById(R.id.SGYear);
        SGPA = (TextView) findViewById(R.id.SGPA);
        genBtn= (Button) findViewById(R.id.generatorButton);
        qrImage= (ImageView) findViewById(R.id.qrImage);

        SignOutButton = (Button) findViewById(R.id.SignOutButton);

        SignOutButton.setOnClickListener(this);
        genBtn.setOnClickListener(this);

    }

    public void updateInfo(){
        SName.setText(studentUser.Name);
        SPhone.setText(studentUser.Phone.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3"));
        SMajor.setText(studentUser.Major);
        SGYear.setText(studentUser.GradYear + "");
        SGPA.setText(studentUser.GPA.toString());

    }

    @Override
    public void onClick(View v) {
        if(v == SignOutButton){
            firebaseAuth.signOut();

            startActivity(new Intent(this, Sign_In.class));
            finish();
        }
        if (v == genBtn) {
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
    }
}
