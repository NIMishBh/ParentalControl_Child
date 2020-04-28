package com.example.parentalcontrol_child.models;

public class parents {
    public String name, email, childEmail, phno, ownimg;

    public parents() {

    }

    public parents(String name, String email, String childEmail, String phno, String ownimg) {
        this.name = name;
        this.email = email;
        this.childEmail = childEmail;
        this.phno = phno;
        this.ownimg = ownimg;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getChildEmail() {
        return childEmail;
    }

    public void setChildEmail(String childEmail) {
        this.childEmail = childEmail;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }

    public String getOwnimg() {
        return ownimg;
    }

    public void setOwnimg(String ownimg) {
        this.ownimg = ownimg;
    }
}
