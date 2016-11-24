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
        FragmentTransaction replacement = this.fragmentManager
                .beginTransaction().replace(R.id.fragment_container,
                this.newFragment);
        if (this.fragmentManager.findFragmentById(R.id
                .fragment_container) != null) {
            replacement.addToBackStack(null);
        }
        replacement.commit();
    }
}
