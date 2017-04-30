package com.example.criminalintent.activity;

import android.support.v4.app.Fragment;

import com.example.criminalintent.fragment.CrimeListFragment;

public class CrimeListActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return new CrimeListFragment();
    }
}
