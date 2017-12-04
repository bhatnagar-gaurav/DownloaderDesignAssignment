package com.medtronic.assignment.designtask.downloader.ui;

import java.io.IOException;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.ForwardingSource;
import okio.Source;

// inspired by https://github.com/square/okhttp/blob/master/samples/guide/src/main/java/okhttp3/recipes/Progress.java

/**
 *
 *  The utility class for showing progress for the File Download
 *  happening in the background.
 *  Developer Name : Gaurav Bhatnagar
 *  Created Date : Dec 03,2017
 */

public class ProgressResponseBody extends ResponseBodyWrapper {
  private final Listener listener;

  public ProgressResponseBody(ResponseBody wrapped, Listener listener) {
    super(wrapped);

    this.listener=listener;
  }

  @Override
  Source wrapSource(Source original) {
    return(new ProgressSource(original, listener));
  }

  class ProgressSource extends ForwardingSource {
    private final Listener listener;
    private long totalRead=0L;

    public ProgressSource(Source delegate, Listener listener) {
      super(delegate);

      this.listener=listener;
    }

    @Override
    public long read(Buffer sink, long byteCount)
      throws IOException {
      long bytesRead=super.read(sink, byteCount);
      boolean done=(bytesRead==-1);

      if (!done) {
        totalRead+=bytesRead;
      }

      listener.onProgressChange(totalRead,
        ProgressResponseBody.this.contentLength(), done);

      return(bytesRead);
    }
  }

  public interface Listener {
    void onProgressChange(long bytesRead, long contentLength,
                          boolean done);
  }
}
