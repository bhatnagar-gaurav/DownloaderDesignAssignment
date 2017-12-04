package com.medtronic.assignment.designtask.downloader.ui;

import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v13.app.FragmentCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.medtronic.assignment.designtask.downloader.R;
import com.medtronic.assignment.designtask.downloader.backend.Downloader;
import com.medtronic.assignment.designtask.downloader.utils.Constants;

import java.net.HttpURLConnection;


import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static com.medtronic.assignment.designtask.downloader.utils.Constants.REQUEST_STORAGE_PERMISSION;
/**
 *
 *  The main screen to be launched by the activity where
 *  the URL will be entered and download process will be triggered.
 *  Developer Name : Gaurav Bhatnagar
 *  Created Date : Dec 03,2017
 */
public class DownloadFragment extends Fragment implements
        FragmentCompat.OnRequestPermissionsResultCallback {


    public DownloadFragment() {
        super();
    }
    // UI Controls
      @BindView(R.id.edit_text_url)
      EditText editTextURL;
      @BindView(R.id.buttonDownload)
      Button buttonDownload;
    // UI Controls for showing that UI is unaffected while download is going on in background.
      @BindView(R.id.buttonDoSomethingCool)
      Button buttonDoCool;
      @BindView(R.id.textViewFun)
      TextView textViewFun;

      boolean isGoodWork=false;

    // ---------------------------------------------------------------------------------------------
    // Fragment lifecycle
    // ---------------------------------------------------------------------------------------------
    @Override
      public View onCreateView(LayoutInflater inflater, ViewGroup parent,
                               Bundle savedInstanceState) {
        View result=inflater.inflate(R.layout.main, parent, false);
        ButterKnife.bind(this, result);
        return(result);
      }

      // Function to be executed when the download button is clicked.
      @OnClick(R.id.buttonDownload)
      public void downloadFile() {
          if (hasPermission(WRITE_EXTERNAL_STORAGE)) {
              if (areFieldsValid()){
                  downloadFileMethod();
              }
          }
          else {
              FragmentCompat.requestPermissions(this,
                      new String[] { WRITE_EXTERNAL_STORAGE }, REQUEST_STORAGE_PERMISSION);
          }
      }



    // Validation Function for the input fields
    private boolean areFieldsValid() {

            boolean allValid = true;

            if (editTextURL.getText().toString().equalsIgnoreCase(Constants.EMPTY_STRING)) {
                editTextURL.setHintTextColor(ContextCompat.getColor(getActivity(),R.color.red));
                editTextURL.setHint(R.string.empty_field_error);
                editTextURL.setError(getString(R.string.empty_field_error));
                allValid = false;
            } else if (!isURLValid(editTextURL.getText().toString())) {
                editTextURL.setHintTextColor(ContextCompat.getColor(getActivity(), R.color.red));
                editTextURL.setError(getString(R.string.invalid_url_error));
                allValid = false;
            }

            return allValid;
        }

    // Utility Method for Other UI Controls available on the screen to set/reset the Label.
        @OnClick(R.id.buttonDoSomethingCool)
        public void doSomethingCool(){
            if (isGoodWork){
                textViewFun.setText(getString(R.string.good_work));
                isGoodWork = false;
            }
            else{
                textViewFun.setText(getString(R.string.what_else));
                isGoodWork = true;
            }

        }

    // Validation Function for the input fields
    private boolean isURLValid(CharSequence target) {
            if (TextUtils.isEmpty(target.toString())) {
                return false;
            } else {
                return Patterns.WEB_URL.matcher(
                        target.toString().toLowerCase().trim()).matches();
            }
        }

        // Utility Function to request the storage permission from end-user.
        private boolean hasPermission(String perm) {
            return(ContextCompat.checkSelfPermission(getActivity(), perm)==
                    PackageManager.PERMISSION_GRANTED);
        }

    // Interface Callback method for requesting permission to the end user when he denies the first time.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        if (hasPermission(WRITE_EXTERNAL_STORAGE)) {
            if (areFieldsValid()){
                downloadFileMethod();
            }
        }
        else{
            Toast.makeText(getActivity(), R.string.permission_rationale,
                    Toast.LENGTH_LONG).show();
        }
    }

    // Utility method for initiating the file download.
    private void downloadFileMethod(){
        Uri downloadFileUri = Uri.parse(editTextURL.getText().toString().toLowerCase().trim());
        String contentType = HttpURLConnection.guessContentTypeFromName(editTextURL.getText().toString().toLowerCase().trim());
        Intent intent=new Intent(getActivity(), Downloader.class);
        if (TextUtils.isEmpty(contentType)){
            intent.setData(downloadFileUri);
        }
        else{
            intent.setDataAndType(downloadFileUri,contentType);
        }

        getActivity().startService(intent);
        buttonDownload.setEnabled(false);
    }

    // ---------------------------------------------------------------------------------------------
    // Fragment lifecycle method for creating intent-filter and registering receiver of Local Broadcast Manager.
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onStart() {
        super.onStart();

        IntentFilter f=new IntentFilter(Downloader.ACTION_COMPLETE);

        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(onEvent, f);
    }

    // ---------------------------------------------------------------------------------------------
    // Fragment lifecycle method for un-registering receiver of Local Broadcast Manager.
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onStop() {
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(onEvent);

        super.onStop();
    }

    // Utility Method for Creating the Broadcast receiver for capturing the finished download event.
    private BroadcastReceiver onEvent=new BroadcastReceiver() {
        public void onReceive(Context ctxt, Intent i) {
            buttonDownload.setEnabled(true);

            Toast.makeText(getActivity(), R.string.download_complete,
                    Toast.LENGTH_LONG).show();
        }
    };
}
