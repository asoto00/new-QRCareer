package edu.ucsb.cs.cs184.asoto00;


import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 */
public class StudentDialogFragment extends DialogFragment {
    private TextView studentName;
    private TextView studentEmail;
    private TextView studentPhone;
    private TextView studentGradYear;
    private TextView studentMajor;
    private TextView studentGPA;
    private Button pdfButton;
    private static StudentUser currentStudent;

    String studentNameString;
    String studentMajorString;
    String studentEmailString;
    String studentPhoneString;
    int studentGradInt;
    float studentGPAString;



    public StudentDialogFragment() {
        // Required empty public constructor
    }

    public static StudentDialogFragment newInstance(StudentUser studentUser){
        StudentDialogFragment frag = new StudentDialogFragment();
        Bundle args = new Bundle();
        args.putString("studentEmail", studentUser.Email);
        args.putString("studentName" , studentUser.Name);
        args.putString("studentPhone", studentUser.Phone);
        args.putFloat("studentGPA", studentUser.GPA);
        args.putInt("studentGradYear", studentUser.GradYear);
        args.putString("studentMajor", studentUser.Major);
        currentStudent = studentUser;

        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_student_dialog, container, false);
        studentName = view.findViewById(R.id.student_name_dialog);
        studentEmail = view.findViewById(R.id.student_email_dialog);
        studentPhone = view.findViewById(R.id.student_phone_dialog);
        studentGradYear = view.findViewById(R.id.student_graduation_dialog);
        studentMajor = view.findViewById(R.id.student_major_dialog);
        studentGPA = view.findViewById(R.id.student_gpa_dialog);
        pdfButton = view.findViewById(R.id.viewPDF);
        pdfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //startActivity(new Intent(getContext(), PdfFragment.class));
            }
        });
        getDialogInformation();

        setDialogInformation();
        setWindowDimensions();

        return view;
    }
    public void setDialogInformation() {
        studentEmail.setText(studentEmailString);
        studentName.setText(studentNameString);
        studentMajor.setText(studentMajorString);
        studentGPA.setText(Float.toString(studentGPAString));
        studentGradYear.setText(Integer.toString(studentGradInt));
        studentPhone.setText(studentPhoneString);

    }
    public void getDialogInformation() {
        studentEmailString = currentStudent.Email;//getArguments().getString("studentEmail");
        studentNameString = currentStudent.Name;//getArguments().getString("studentName");
        studentPhoneString = currentStudent.Phone;// getArguments().getString("studentPhone");
        studentGPAString = currentStudent.GPA;//getArguments().getFloat("studentGPA");
        studentGradInt = currentStudent.GradYear;//getArguments().getInt("studentGradYear");
        studentMajorString = currentStudent.Major;//getArguments().getString("studentMajor");
    }

    public void setWindowDimensions() {
        Point dim = new Point();
        Window window = getDialog().getWindow();
        window.setGravity(Gravity.CENTER);
        window.getWindowManager().getDefaultDisplay().getSize(dim);
        int x = (int) (dim.x*.7);
        int y = (int) (dim.y*.7);
        window.setLayout(x, y);
    }
}
