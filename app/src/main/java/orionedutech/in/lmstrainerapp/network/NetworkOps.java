package orionedutech.in.lmstrainerapp.network;

import android.content.Context;
import android.view.View;
import android.webkit.MimeTypeMap;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import io.github.lizhangqu.coreprogress.ProgressHelper;
import io.github.lizhangqu.coreprogress.ProgressListener;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import orionedutech.in.lmstrainerapp.mLog;
import static orionedutech.in.lmstrainerapp.mLog.TAG;
import static orionedutech.in.lmstrainerapp.MUtilsKt.noInternetSnackBar;
import static orionedutech.in.lmstrainerapp.MUtilsKt.showToast;
import static orionedutech.in.lmstrainerapp.MUtilsKt.isConnected;

public class NetworkOps {
    public static void get(String url, final response resp, final Context context, final View view) {
        if(isConnected(context)){
        OkHttpClient client = new OkHttpClient();
          Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                resp.onfailure();
                showToast(context,"server error");
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if(response.isSuccessful()) {
                    resp.onrespose(Objects.requireNonNull(response.body()).string());
                }else{
                    showToast(context,"server error : " + response.code());
                }
            }
        });
    }else{

         resp.onInternetfailure();
        }
    }
    public static void post1(String url, String json, String mediaType, final Context context,View view, final response myres) {
        mLog.i(TAG, "network call url: " + url);
        mLog.i(TAG, "network call json: " + json);
        mLog.i(TAG, "network media-type: " + mediaType);
        if(isConnected(context)){
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(json,MediaType.get(mediaType));
            RequestBody responseBody = ProgressHelper.withProgress(body, new ProgressListener() {
                @Override
                public void onProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                    double per = Math.round((double) numBytes / totalBytes * 100);
                    mLog.i(TAG, "onProgressChanged: "+per);
                }
            });

            Request request = new Request.Builder()
                    .url(url)
                    .post(responseBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    myres.onfailure();
                    mLog.i(TAG, "network call onFailure: " + e.getMessage() + "\n" + e.getCause());
                    showToast(context,"server error");
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    mLog.i(TAG, "response response code: " + response.code());
                    if (response.isSuccessful()) {
                        try {
                            String s = Objects.requireNonNull(response.body()).string();
                            myres.onrespose(s);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mLog.i(TAG, "response: success");
                    } else {
                        myres.onfailure();
                        showToast(context,"server error : "+response.code());
                        mLog.i(TAG, "response:  server side error");
                    }
                }
            });
        }else{

          myres.onInternetfailure();
        }


    }
   public static void post(String url, String json, final Context context, final response onRes,@Nullable final progress prog){
       if(isConnected(context)) {
           OkHttpClient okHttpClient;
           OkHttpClient.Builder build = new OkHttpClient.Builder();
           build.connectTimeout(30, TimeUnit.SECONDS);
           build.readTimeout(30, TimeUnit.SECONDS);
           build.writeTimeout(30, TimeUnit.SECONDS);
           Request.Builder builder = new Request.Builder();
           builder.url(url);
           mLog.i(TAG, "postMultipart url : " + url);
           mLog.i(TAG,"json : "+json);
           MultipartBody.Builder multipart = new MultipartBody.Builder();
           multipart.setType(MultipartBody.FORM);
           multipart.addFormDataPart("data_json",json);
           final long startTime = System.nanoTime();
           MultipartBody body = multipart.build();
           RequestBody requestBody = ProgressHelper.withProgress(body, new ProgressListener() {
               @Override
               public void onProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                   double per = Math.round((double) numBytes / totalBytes * 100);
                   mLog.i(TAG, "onProgressChanged: " + per);
                   Long elapsedTime = System.nanoTime() - startTime;
                   Long allTimeForDownloading = (elapsedTime * totalBytes / numBytes);
                   long remainingTime = allTimeForDownloading - elapsedTime;
                   prog.progress(per, speed,remainingTime);
               }
           });
           builder.post(requestBody);
           okHttpClient = build.build();
           Call call = okHttpClient.newCall(builder.build());
           call.enqueue(new Callback() {
               @Override
               public void onFailure(@NotNull Call call, @NotNull IOException e) {
                   onRes.onfailure();
               }

               @Override
               public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                  if(response.body()!=null) {
                      onRes.onrespose(Objects.requireNonNull(response.body()).string());
                  }else{
                      onRes.onfailure();
                  }
               }
           });
       }else{

          onRes.onInternetfailure();
       }
   }
    public static void postMultipart(String UPLOAD_URL, HashMap<String, String> data, Context context, final response onRes, final progress prog) {

        if(isConnected(context)) {
            OkHttpClient okHttpClient;
            OkHttpClient.Builder build = new OkHttpClient.Builder();
            build.connectTimeout(30, TimeUnit.SECONDS);
            build.readTimeout(30, TimeUnit.SECONDS);
            build.writeTimeout(30, TimeUnit.SECONDS);
            Request.Builder builder = new Request.Builder();
            builder.url(UPLOAD_URL);
            mLog.i(TAG, "postMultipart url : " + UPLOAD_URL);
            MultipartBody.Builder multipart = new MultipartBody.Builder();
            multipart.setType(MultipartBody.FORM);
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                if (key.equals("file_path")) {
                    mLog.i(TAG, "postMultipart: adding file");
                    String filename = value.substring(value.lastIndexOf("/") + 1);
                    String type = getMimetype(value);
                    mLog.i(TAG, "postMultipart file name: " + filename);
                    mLog.i(TAG, "postMultipart: file type " + type);
                    mLog.i(TAG, "postMultipart: file path" + value);
                    File file = new File(value);
                    multipart.addFormDataPart("file_path", filename,
                            RequestBody.create(file,MediaType.parse(type)));
                } else {
                    mLog.i(TAG, "postMultipart: " + key + "  " + value);
                    multipart.addFormDataPart(key, value);
                }

                mLog.i(TAG, "postMultipart: " + multipart.getClass().toString());
            }
            final long startTime = System.nanoTime();
            MultipartBody body = multipart.build();
            RequestBody requestBody = ProgressHelper.withProgress(body, new ProgressListener() {
                @Override
                public void onProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                    double per = Math.round((double) numBytes / totalBytes * 100);
                    mLog.i(TAG, "onProgressChanged: " + per);
                    Long elapsedTime = System.nanoTime() - startTime;
                    Long allTimeForDownloading = (elapsedTime * totalBytes / numBytes);
                    long remainingTime = allTimeForDownloading - elapsedTime;
                    prog.progress(per, speed,remainingTime);
                }
            });
            builder.post(requestBody);
            okHttpClient = build.build();
            Call call = okHttpClient.newCall(builder.build());
            call.enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, @NotNull IOException e) {
                    onRes.onfailure();
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                    if(response.body()!=null) {
                        onRes.onrespose(Objects.requireNonNull(response.body()).string());
                    }else{
                        onRes.onfailure();
                    }
                }
            });
        }else{

           onRes.onInternetfailure();
        }
    }
    private static String getMimetype(String filepath) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(filepath);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    public static void postFromService(String url, String json, String mediaType, final response myres) {
        mLog.i(TAG, "network call url: " + url);
        mLog.i(TAG, "network call json: " + json);
        mLog.i(TAG, "network media-type: " + mediaType);
            OkHttpClient client = new OkHttpClient();
            RequestBody body = RequestBody.create(MediaType.get(mediaType),json);
            RequestBody responseBody = ProgressHelper.withProgress(body, new ProgressListener() {
                @Override
                public void onProgressChanged(long numBytes, long totalBytes, float percent, float speed) {
                    double per = Math.round((double) numBytes / totalBytes * 100);
                    mLog.i(TAG, "onProgressChanged: "+per);
                }
            });

            Request request = new Request.Builder()
                    .url(url)
                    .post(responseBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(@NotNull Call call, IOException e) {
                    myres.onfailure();
                    mLog.i(TAG, "network call onFailure: " + e.getMessage() + "\n" + e.getCause() + "\n" + e.getStackTrace());
                }

                @Override
                public void onResponse(@NotNull Call call, @NotNull Response response) {
                    mLog.i(TAG, "response response code: " + response.code());
                    if (response.isSuccessful()) {
                        try {
                            String s = Objects.requireNonNull(response.body()).string();
                            myres.onrespose(s);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        mLog.i(TAG, "response: success");
                    } else {
                        myres.onfailure();

                        mLog.i(TAG, "response:  server side error");

                    }
                }
            });



    }
}
