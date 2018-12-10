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

public class Sign_In extends AppCompatActivity implements View.OnClickListener {

    private EditText SignIn_Email;
    private EditText SignIn_Password;
    private Button SignIn_Button;
    private TextView StudentRegister;
    private TextView EmployerRegisterTextView;

    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign__in);

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
                        startActivity(new Intent(Sign_In.this, StudentProfile.class));
                        finish();
                    }else if(studentUser.Type.equals("employer")){
                        startActivity(new Intent(Sign_In.this, EmployerProfile.class));
                        finish();
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        SignIn_Email = (EditText) findViewById(R.id.SIemail);
        SignIn_Password = (EditText) findViewById(R.id.SIPassword);
        SignIn_Button = (Button) findViewById(R.id.LogIn);
        StudentRegister = (TextView) findViewById(R.id.StudentRegister);
        EmployerRegisterTextView = (TextView) findViewById(R.id.EmployerRegister);

        progressDialog = new ProgressDialog(this);

        SignIn_Button.setOnClickListener(this);
        StudentRegister.setOnClickListener(this);
        EmployerRegisterTextView.setOnClickListener(this);

    }

    public void userSignIn(){
        String email = SignIn_Email.getText().toString().trim();
        String password = SignIn_Password.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            Toast.makeText(this, "Please Enter Email",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Signing In");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();
                if(task.isSuccessful()){
                    FirebaseUser currentUser = task.getResult().getUser();
                    String UserId = currentUser.getUid();

                    databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(UserId);


                    databaseReference.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            StudentUser studentUser = dataSnapshot.getValue(StudentUser.class);

                            if(studentUser.Type.equals("student")){

                                startActivity(new Intent(Sign_In.this, StudentProfile.class));
                                finish();
                            }else if(studentUser.Type.equals("employer")){
                                startActivity(new Intent(Sign_In.this, EmployerProfile.class));
                                finish();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });




                }else{
                    Toast.makeText(Sign_In.this, "Sign In Failed", Toast.LENGTH_SHORT).show();
                }

            }
        });



    }

    @Override
    public void onClick(View v) {
        if(v == SignIn_Button){
            userSignIn();
        }
        if(v == StudentRegister){

            startActivity(new Intent(this, RegisterActivity.class));
            finish();
        }

        if(v == EmployerRegisterTextView){
            startActivity(new Intent(this, EmployerRegister.class));
            finish();
        }
    }
}
