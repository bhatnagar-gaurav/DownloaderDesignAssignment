# Design Assignment

In this repositiory I have provided the solution for the second assignment.We are using here 
OKHttp3 library for downloading a file through an intent service. The Interceptor API is utilized to show the download progress through notification.
Please go through the project :

**1)  Downloader Service:**
    This is the intent service which is used to download the file in background using OkHttp3 library.Here we call the startForeground() method
    for elevating our process priority while the download is happening and show a notification for the same.
    
**2)  Dowloader Fragment Screen:**
     This is the main screen shown to the user from where the download process can be triggered after entering valid URL. 
    
**3)  ProgressResponseBody:**
      This class is a subclass of ResponseBodyWrapper which is a ResponseBody that forwards everything onto a wrapper of the same.
      This needs to implement wrapSource() method to wrap the Source of the Response which in turn tracks the number of bytes
      that have been read and passes on the status to notification/progress bar.
