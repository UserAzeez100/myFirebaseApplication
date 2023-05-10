package com.example.myfirebaseapplication.other;

import java.util.ArrayList;

public class UserData {
    private String id,name,country,image,phone,email,imageName;
    private ArrayList<ProdectClass> userArrayListProducts =new ArrayList<>();

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public UserData(String id, String name, String country, String phone , String email) {
        this.id = id;
        this.name = name;
        this.country = country;
        this.phone = phone;
        this.email = email;

    }

    public UserData() {
    }

    public ArrayList<ProdectClass> getUserArrayListProducts() {
        return userArrayListProducts;
    }

    public void setUserArrayListProducts(ArrayList<ProdectClass> userArrayListProducts) {
        this.userArrayListProducts = userArrayListProducts;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
