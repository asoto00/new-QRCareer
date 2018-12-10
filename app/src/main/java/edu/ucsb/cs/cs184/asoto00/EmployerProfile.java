package edu.ucsb.cs.cs184.asoto00;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;

public class EmployerProfile extends AppCompatActivity implements View.OnClickListener {

    private TextView CEName;
    private TextView CECompany;
    private TextView CEPhone;
    private Button SignOutButton2;

    protected RecyclerView recyclerView;
    static StudentListAdapter mAdapter;
    static ArrayList<StudentUser> allStudents;
    static ArrayList<String> StudentIds;
    protected RecyclerView.LayoutManager mLayoutManger;
    protected FloatingActionButton addStudentButton;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;
    private EmployerUser employerUser;
    static FirebaseUser user;
    public static final String FRAGMENT_PDF_RENDERER= "pdf_renderer";
    final Activity activity = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employer_profile);
        //initDataSet();
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        allStudents = new ArrayList<>();


        if(user == null){
            startActivity( new Intent(this, Sign_In.class));
            finish();
        }else{
            databaseReference = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid());

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    employerUser = dataSnapshot.getValue(EmployerUser.class);
                    updateEmployerInfo();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        recyclerView = findViewById(R.id.recyclerView);

        mAdapter = new StudentListAdapter(allStudents);
        mLayoutManger = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(mLayoutManger);
        recyclerView.setAdapter(mAdapter);
        addStudentButton = findViewById(R.id.addStudentButton);
        addStudentButton.setOnClickListener(this);
        CEName = (TextView) findViewById(R.id.CEName);
        CECompany = (TextView) findViewById(R.id.CECompany);
        CEPhone = (TextView) findViewById(R.id.CEPhone);

        SignOutButton2 = (Button) findViewById(R.id.SignOutButton2);

        SignOutButton2.setOnClickListener(this);


    }

    /*private void initDataSet() {
        // This needs to be ArrayList<StudentUsers>
        allStudents = new ArrayList<>();
        allStudents.add("Yessenia Valencia");
        allStudents.add("Alex Soto");
        allStudents.add("Arturo Something");

    }*/

    public void updateEmployerInfo(){
        CEName.setText(employerUser.Name);
        CECompany.setText(employerUser.Company);
        CEPhone.setText(employerUser.PhoneNumber.replaceFirst("(\\d{3})(\\d{3})(\\d+)", "($1) $2-$3"));

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        //Intent intent = new Intent(this, InfoList.class);
        if (result != null) {
            if (result.getContents() == null) {
                Toast.makeText(this, "You Cancelled the scanning", Toast.LENGTH_LONG).show();

            } else {
                String uid = result.getContents();

                DatabaseReference studentReference = FirebaseDatabase.getInstance().getReference().child(uid);
                studentReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        StudentUser tempStudent = dataSnapshot.getValue(StudentUser.class);
                        int itemPosition;
                        if(!StudentIds.contains(tempStudent.UserID)){
                            StudentIds.add(tempStudent.UserID);
                            allStudents.add(tempStudent);
                            itemPosition = allStudents.indexOf(tempStudent);
                            mAdapter.notifyItemInserted(allStudents.indexOf(tempStudent));
                            databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("users").child(user.getUid()).child("Students").child(tempStudent.UserID).setValue(tempStudent);


                        }else{
                            int index = StudentIds.indexOf(tempStudent.UserID);
                            allStudents.set(index, tempStudent);
                            itemPosition = index;
                            mAdapter.notifyItemChanged(index);
                            databaseReference = FirebaseDatabase.getInstance().getReference();
                            databaseReference.child("users").child(user.getUid()).child("Students").child(tempStudent.UserID).setValue(tempStudent);


                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



              //  startActivity(new Intent(this, StudentListAdapter.class));

                //startActivity( new Intent(getApplicationContext(), InfoList.class ));
                //Toast.makeText(this, uid, Toast.LENGTH_LONG).show();




               // finish();
            }
        }

        else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void onItemlick(View view, int position) {

    }

    public static void removeStudent(int position){
        StudentUser removedStudent = allStudents.get(position);
        String StudentId = removedStudent.UserID;
        DatabaseReference removedStudentReference = FirebaseDatabase.getInstance().getReference();
        removedStudentReference.child("users").child(user.getUid()).child("Students").child(StudentId).removeValue();
        allStudents.remove(position);
        StudentIds.remove(position);
        mAdapter.notifyItemRemoved(position);





    }


    @Override
    public void onClick(View v) {
        if( v == SignOutButton2){
            firebaseAuth.signOut();

            startActivity(new Intent(this, Sign_In.class));
            finish();
        }
        if(v == addStudentButton) {
            // Testing PDFFragment
            // getSupportFragmentManager().beginTransaction().add(R.id.employer_profile_id, new PdfFragment(), FRAGMENT_PDF_RENDERER).commit();
            IntentIntegrator integrator = new IntentIntegrator(activity);
            integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES);
            integrator.setPrompt("Scan");
            integrator.setCameraId(0);
            integrator.setBeepEnabled(true);
            integrator.setBarcodeImageEnabled(false);
            integrator.initiateScan();

//            startActivity(new Intent(this, ScannerActivity.class));
//            finish();
        }
    }
}
