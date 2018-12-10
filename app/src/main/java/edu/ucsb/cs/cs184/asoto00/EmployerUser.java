package edu.ucsb.cs.cs184.asoto00;

public class EmployerUser {
    public String Name;
    public String Company;
    public String PhoneNumber;
    public String Email;
    public String Type = "employer";

    public EmployerUser(){

    }

    public EmployerUser(String name, String company, String phoneNumber, String email){
        this.Name = name;
        this.Company = company;
        this.PhoneNumber = phoneNumber;
        this.Email = email;
    }



}
