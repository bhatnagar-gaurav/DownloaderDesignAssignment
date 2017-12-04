package com.medtronic.assignment.designtask.downloader.ui;

import android.app.Activity;
import android.os.Bundle;

import com.medtronic.assignment.designtask.downloader.R;

import static com.medtronic.assignment.designtask.downloader.utils.Constants.DOWNLOAD_FRAGMENT_TAG;

// UI Module for the application
// The main activity for the application.
/**
 *
 *  The main activity powers up the Download Fragment/Screen
 *  to be shown to the end user.
 *  Developer Name : Gaurav Bhatnagar
 *  Created Date : Dec 03,2017
 */


public class DownloaderDemo extends Activity {
    // ---------------------------------------------------------------------------------------------
    // Activity lifecycle methods
    // ---------------------------------------------------------------------------------------------
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_downloader);
        if (getFragmentManager().findFragmentByTag(DOWNLOAD_FRAGMENT_TAG) == null) {
          getFragmentManager().beginTransaction()
                              .replace(R.id.fragmentContainer,
                                   new DownloadFragment()).commit();
        }
  }
}
