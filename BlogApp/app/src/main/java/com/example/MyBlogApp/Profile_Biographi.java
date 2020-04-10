package com.example.MyBlogApp;

public class Profile_Biographi {
    private String About,Bİographi,Phone;

    public Profile_Biographi(String about, String Bİographi, String phone) {
        About = about;
        this.Bİographi = Bİographi;
        Phone = phone;
    }

    public String getAbout() {
        return About;
    }

    public void setAbout(String about) {
        About = about;
    }

    public String getBİographi() {
        return Bİographi;
    }

    public void setBİographi(String Bİographi) {
        this.Bİographi = Bİographi;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }

    public Profile_Biographi() {
    }
}
