package edu.ucsb.cs.cs184.asoto00;

public class StudentUser {
    public String Name;
    public String Phone;
    public String Major;
    public int GradYear;
    public Float GPA;
    public String Type = "student";
    public String Email;
    public String UserID;

    public StudentUser(){

    }

    public StudentUser(String name, String phone, String major,int gradYear, Float gpa, String email, String userID){
        this.Name = name;
        this.Phone = phone;
        this.Major = major;
        this.GradYear = gradYear;
        this.GPA = gpa;
        this.Email = email;
        this.UserID = userID;

    }


}
