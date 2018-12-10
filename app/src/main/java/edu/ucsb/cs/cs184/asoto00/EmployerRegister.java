package edu.ucsb.cs.cs184.asoto00;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EmployerRegister extends AppCompatActivity implements View.OnClickListener {

    private EditText EmployerName;
    private EditText EmployerCompany;
    private EditText EmployerPhone;
    private EditText EmployerEmail;
    private EditText EmployerPassword;
    private Button EmployerRegButton;
    private TextView StudentReg;
    private TextView SignIn;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_register);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null){
            FirebaseUser user  = firebaseAuth.getCurrentUser();
            String UID = user.getUid();

            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(UID);


            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    StudentUser studentUser = dataSnapshot.getValue(StudentUser.class);
                    if(studentUser.Type.equals("student")){
                        startActivity(new Intent(EmployerRegister.this, StudentProfile.class));
                        finish();
                    }else if(studentUser.Type.equals("employer")){
                        startActivity(new Intent(EmployerRegister.this, EmployerProfile.class));
                        finish();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }else{
            databaseReference = FirebaseDatabase.getInstance().getReference();
        }




        progressDialog = new ProgressDialog(this);


        EmployerName = (EditText) findViewById(R.id.EName);
        EmployerCompany = (EditText) findViewById(R.id.Company);
        EmployerPhone = (EditText) findViewById(R.id.EPhone);
        EmployerEmail = (EditText) findViewById(R.id.employerEmail);
        EmployerPassword = (EditText) findViewById(R.id.employerPassword);

        EmployerRegButton = (Button) findViewById(R.id.EReg);

        StudentReg = (TextView) findViewById(R.id.Student);
        SignIn = (TextView) findViewById(R.id.SignIn2);

        EmployerRegButton.setOnClickListener(this);
        StudentReg.setOnClickListener(this);
        SignIn.setOnClickListener(this);

    }

    public void registerEmployer(){
        String email = EmployerEmail.getText().toString().trim();
        String password = EmployerPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please Enter Email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this, "Please Enter Password",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering User");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    onEmployerAuthSuccess(task.getResult().getUser());
                }else{
                    Toast.makeText(EmployerRegister.this, "Register Failed... Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    private void onEmployerAuthSuccess(FirebaseUser user){
        String n = EmployerName.getText().toString().trim();
        String c = EmployerCompany.getText().toString().trim();
        String p = EmployerPhone.getText().toString().trim();

        addEmployerUser(user.getUid(),n,c,p,user.getEmail());

        startActivity(new Intent(this, Sign_In.class));
        finish();

    }


    public void addEmployerUser(String userId, String name, String company, String phoneNumber, String email){
        EmployerUser employerUser = new EmployerUser(name,company,phoneNumber,email);

        databaseReference.child("users").child(userId).setValue(employerUser);

    }


    @Override
    public void onClick(View v) {
        if(v == EmployerRegButton){
            registerEmployer();
        }

        if(v == StudentReg){
            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }

        if(v == SignIn){
            startActivity(new Intent(this, Sign_In.class));
            finish();
        }

    }
}
