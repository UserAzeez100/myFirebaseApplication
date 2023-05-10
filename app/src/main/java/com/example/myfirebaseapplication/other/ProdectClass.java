package com.example.myfirebaseapplication.other;

public class ProdectClass {

    private String prodectName , prodectDescription , imageString ;
    private  boolean favoriteBool=false;
    private  String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public ProdectClass() {

    }

    public ProdectClass(String prodectName, String prodectDescription) {
        this.prodectName = prodectName;
        this.prodectDescription = prodectDescription;

    }

    public boolean isFavoriteBool() {
        return favoriteBool;
    }

    public void setFavoriteBool(boolean favoriteBool) {
        this.favoriteBool = favoriteBool;
    }

    public String getProdectName() {
        return prodectName;
    }

    public void setProdectName(String prodectName) {
        this.prodectName = prodectName;
    }

    public String getProdectDescription() {
        return prodectDescription;
    }

    public void setProdectDescription(String prodectDescription) {
        this.prodectDescription = prodectDescription;
    }

    public String getImageString() {
        return imageString;
    }

    public void setImageString(String imageString) {
        this.imageString = imageString;
    }
}
