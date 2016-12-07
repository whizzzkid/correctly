package com.example.lenovo.correctly.utils;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;

import com.example.lenovo.correctly.R;

public class FragmentLoader {
    private FragmentManager fragmentManager;
    private Fragment newFragment;

    public FragmentLoader(FragmentManager fragmentManager, Bundle arguments,
                           Fragment newFragment) {
        this.fragmentManager = fragmentManager;
        this.newFragment = newFragment;
        this.newFragment.setArguments(arguments);
    }

    public void Load () {
        FragmentTransaction animatedReplace = fragmentManager
                .beginTransaction().setCustomAnimations(
                        R.animator.slide_in_left,
                        R.animator.slide_out_right,
                        R.animator.slide_in_left,
                        R.animator.slide_out_right).replace(R.id.fragment_container,
                        newFragment);
        if (fragmentManager.findFragmentById(R.id.fragment_container) != null) {
            animatedReplace.addToBackStack(null);
        }
        animatedReplace.commit();
    }
}
