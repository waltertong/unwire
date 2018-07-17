package com.songlee.htapp;

/**
 * Created by walter_tong on 7/17/17.
 */

import android.app.ActionBar;
import android.app.Activity;
//import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.*;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public abstract class AbstractFragment extends Fragment {
    FragmentActivity activity;
    public Context thisContext;
    private ProgressDialog progressDialog = null;
    private int numberOfProgressDialog;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        thisContext = getActivity();
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        numberOfProgressDialog = 0;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return container;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        activity = (FragmentActivity) getActivity();

    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    @Override
    public void onDetach() {
        super.onDetach();

    }

    protected void showProgressDialog(String title, String msg) {
        if (progressDialog == null) {
//            progressDialog = new ProgressDialog(thisContext, R.style.progress_bar_style);
        }

        progressDialog.setCancelable(false);
        if (!progressDialog.isShowing()) {
            progressDialog.show();
//            progressDialog.setContentView(getProgressView());
        }
        numberOfProgressDialog++;
    }

    protected void dismissProgressDialog() {
        numberOfProgressDialog--;
        if (numberOfProgressDialog < 1) {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }

    protected void setActionBarTitle(String title) {
        ActionBar actionBar = getActivity().getActionBar();
        if (actionBar != null) {
            actionBar.setTitle(title);
        }
    }


}
