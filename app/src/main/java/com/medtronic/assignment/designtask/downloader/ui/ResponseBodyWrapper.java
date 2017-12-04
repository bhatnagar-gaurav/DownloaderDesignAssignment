package com.medtronic.assignment.designtask.downloader.ui;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;
import okio.Source;

// inspired by https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java

/**
 *
 *  The abstract base class used to show the progress to the end-user
 *  for the Download process happening in the background.
 *  Developer Name : Gaurav Bhatnagar
 *  Created Date : Dec 03,2017
 */
abstract class ResponseBodyWrapper extends ResponseBody {
  abstract Source wrapSource(Source original);

  private final ResponseBody wrapped;
  private BufferedSource buffer;

  ResponseBodyWrapper(ResponseBody wrapped) {
    this.wrapped=wrapped;
  }

  @Override
  public MediaType contentType() {
    return(wrapped.contentType());
  }

  @Override
  public long contentLength() {
    return(wrapped.contentLength());
  }

  @Override
  public BufferedSource source() {
    if (buffer==null) {
      buffer=Okio.buffer(wrapSource(wrapped.source()));
    }

    return(buffer);
  }
}
