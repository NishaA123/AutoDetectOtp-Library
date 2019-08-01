package com.autodetectotp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.autodetectotplibrary.GetOtpMessage;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private int PERMISSION_REQUEST_CODE = 1;
    private String[] PERMISSIONS = {
            android.Manifest.permission.RECEIVE_SMS,
            android.Manifest.permission.SEND_SMS,
            android.Manifest.permission.READ_SMS,
    };
    private GetOtpMessage getOtpMessage;
    private WebView webView;
    private ArrayList<String> bankOtpList = new ArrayList<>();
    private boolean flag;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (!hasPermissions(this, PERMISSIONS))
            ActivityCompat.requestPermissions(MainActivity.this,
                    PERMISSIONS,
                    PERMISSION_REQUEST_CODE);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<String>> call = apiService.getBankOtpList();
        call.enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<String>> call, @NonNull Response<ArrayList<String>> response) {
                if (response.body() != null && !response.body().isEmpty()) {
                    bankOtpList.addAll(response.body());
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<String>> call, @NonNull Throwable t) {
                Log.e("API Error", t.getMessage());
            }
        });

        webView = findViewById(R.id.web_view);
        webView.getSettings().setJavaScriptEnabled(true);
        setOtpReceived();
        webView.loadUrl("https://www.tecprocesssolution.com/proto/test/checkout.html");
    }

    public void setOtpReceived() {
        getOtpMessage = new GetOtpMessage(this, new GetOtpMessage.OnOtpReceivedListener() {
            @Override
            public void onSuccess(final String otp) {

                for (String otpCode : bankOtpList) {
                    String splitedValue = otpCode.split("~")[1];
                    if (!flag) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            System.out.println("otpReceived " + otp);
                            webView.evaluateJavascript("javascript:(function otp() {\n" +
                                            "  var flag = false;\n" +
                                            "  var all = document.getElementsByTagName(\"input\");\n" +
                                            "  for (i = 0; i < all.length; i++) {\n" +
                                            "    if (all[i].hasAttribute('name') || all[i].hasAttribute('class') || all[i].hasAttribute('id')) {\n" +
                                            "      if (\n" +
                                            "        '" + splitedValue + "'.includes(document.getElementsByTagName('input')[i].getAttribute('name'))) {\n" +
                                            "        document.querySelector('" + splitedValue + "').value = '" + otp + "'\n" +
                                            "        flag = true;\n" +
                                            "        break;\n" +
                                            "      }\n" +
                                            "    }\n" +
                                            "  }\n" +
                                            "  return flag;\n" +
                                            "})()",
                                    new ValueCallback<String>() {
                                        @Override
                                        public void onReceiveValue(String s) {
                                            flag = Boolean.parseBoolean(s);
                                        }
                                    });
                        }
                    } else {
                        break;
                    }
                }
            }

            @Override
            public void onFailure(String message) {
                Log.e("Failure", message);
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(MainActivity.this, "Permission denied"
                        , Toast.LENGTH_SHORT).show();
            }
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onResume() {
        getOtpMessage.start();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        getOtpMessage.stop();
    }
}
