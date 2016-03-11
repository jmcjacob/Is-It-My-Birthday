package com.jacob.authentication;

import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicBoolean;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import com.google.common.util.concurrent.FutureCallback;
import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceAuthenticationProvider;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;


public class MainActivity extends AppCompatActivity {

    private MobileServiceClient mClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
            mClient = new MobileServiceClient(
                    "https://mobilecomputingauthentication.azurewebsites.net",
                    this
            );
            authenticate();
        }
        catch (Exception e) {}
    }

    private void authenticate() {

        ListenableFuture<MobileServiceUser> mLogin = mClient.login(MobileServiceAuthenticationProvider.Facebook);

        Futures.addCallback(mLogin, new FutureCallback<MobileServiceUser>() {
            @Override
            public void onFailure(Throwable exc) {
                createAndShowDialog((exc.toString()), "Error");
            }

            @Override
            public void onSuccess(MobileServiceUser user) {
                createAndShowDialog(String.format(
                        "You are now logged in - %1$2s",
                        user.getUserId()), "Success");

                // call below method to show table data upon successful Twitter authentication
                //showTableData();
            }
        });
    }

    private void createAndShowDialog(String message, String title) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(message);
        builder.setTitle(title);
        builder.create().show();
    }
}
