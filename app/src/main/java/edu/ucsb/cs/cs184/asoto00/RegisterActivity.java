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
import android.widget.ProgressBar;
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

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private Button StudentRegister;
    private EditText StudentName;
    private EditText StudentPhone;
    private EditText StudentMajor;
    private EditText GraduationYear;
    private EditText StudentGPA;
    private EditText Email;
    private EditText Password;
    private TextView EmployerReg;
    private TextView SignIn;

    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
                        startActivity(new Intent(RegisterActivity.this, StudentProfile.class));
                        finish();
                    }else if(studentUser.Type.equals("employer")){
                        startActivity(new Intent(RegisterActivity.this, EmployerProfile.class));
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

        StudentRegister = (Button) findViewById(R.id.reg);
        StudentName = (EditText) findViewById(R.id.Name);
        StudentPhone = (EditText) findViewById(R.id.Phone);
        StudentMajor = (EditText) findViewById(R.id.Major);
        GraduationYear = (EditText) findViewById(R.id.GradYear);
        StudentGPA = (EditText) findViewById(R.id.GPA);
        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);
        EmployerReg = (TextView) findViewById(R.id.Employer);
        SignIn = (TextView) findViewById(R.id.SignIn);

        StudentRegister.setOnClickListener(this);
        EmployerReg.setOnClickListener(this);
        SignIn.setOnClickListener(this);

    }

    private void registerStudent(){
        String email = Email.getText().toString().trim();
        String password = Password.getText().toString().trim();

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
                    onAuthSuccess(task.getResult().getUser());
                }else{
                    Toast.makeText(RegisterActivity.this, "Register Failed... Try Again", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void onAuthSuccess(FirebaseUser user){
        String n = StudentName.getText().toString().trim();
        String p = StudentPhone.getText().toString().trim();
        String m = StudentMajor.getText().toString().trim();
        int gy = Integer.parseInt(GraduationYear.getText().toString());
        Float gpa = Float.parseFloat(StudentGPA.getText().toString());

        writeNewUser(user.getUid(),n,p,m,gy,gpa,user.getEmail());

        startActivity(new Intent(this, Sign_In.class));
        finish();

    }

    private void writeNewUser(String userId, String name, String phone, String major,int gradYear, Float gpa, String email) {
         StudentUser user = new StudentUser(name,phone,major,gradYear,gpa,email,userId);

        databaseReference.child("users").child(userId).setValue(user);
    }


    @Override
    public void onClick(View v) {
        if(v == StudentRegister){
            registerStudent();
        }

        if(v == EmployerReg){
            startActivity(new Intent(this, EmployerRegister.class));
            finish();
        }

        if(v == SignIn){
            startActivity(new Intent(this, Sign_In.class));
            finish();

        }
    }
}
