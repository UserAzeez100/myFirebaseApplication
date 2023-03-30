package com.example.myfirebaseapplication.other;

public class ProdectClass {

    private String prodectName, prodectDescription,imageString;


    public ProdectClass(String prodectName, String prodectDescription) {
        this.prodectName = prodectName;
        this.prodectDescription = prodectDescription;

    }

    public String getProdectName() {
        return prodectName;
    }

    public void setProdectName(String prodectName) {
        this.prodectName = prodectName;
    }

    public String getServiceType() {
        return prodectDescription;
    }

    public void setServiceType(String serviceType) {
        this.prodectDescription = serviceType;
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
