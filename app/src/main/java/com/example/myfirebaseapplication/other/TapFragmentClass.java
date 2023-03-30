package com.example.myfirebaseapplication.other;

import androidx.fragment.app.Fragment;

public class TapFragmentClass {
   private String tapName;
   private Fragment fragment;

    public TapFragmentClass() {
    }

    public TapFragmentClass(String tapName, Fragment fragment) {
        this.tapName = tapName;
        this.fragment = fragment;
    }

    public String getTapName() {
        return tapName;
    }

    public void setTapName(String tapName) {
        this.tapName = tapName;
    }

    public Fragment getFragment() {
        return fragment;
    }

    public void setFragment(Fragment fragment) {
        this.fragment = fragment;
    }
}
