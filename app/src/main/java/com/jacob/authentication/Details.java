package com.jacob.authentication;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.microsoft.windowsazure.mobileservices.MobileServiceClient;
import com.microsoft.windowsazure.mobileservices.authentication.MobileServiceUser;
import com.securepreferences.SecurePreferences;

public class Details extends AppCompatActivity {
    SharedPreferences prefs;
    public static final String USERIDPREF = "uid";
    public static final String TOKENPREF = "tkn";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        prefs = new SecurePreferences(this, "01827711125", "token");

    }

    //exports.get = function (request, response) {
    //   request.user.getIdentities({
    //            success: function (identities) {
    //        var http = require('request');
    //        console.log('Identities: ', identities);
    //        var url = 'https://graph.facebook.com/me?fields=id,name,birthday,hometown,email&access_token=' +
    //                identities.facebook.accessToken;
    //
    //        var reqParams = { uri: url, headers: { Accept: 'application/json' } };
    //        http.get(reqParams, function (err, resp, body) {
    //            var userData = JSON.parse(body);
    //            response.send(200, userData);
    //        });
    //    }
    //    });
    //};

    private boolean loadUserTokenCache(MobileServiceClient client) {
        String userId = prefs.getString(USERIDPREF, "undefined");
        if (userId == "undefined")
            return false;
        String token = prefs.getString(TOKENPREF, "undefined");
        if (token == "undefined")
            return false;

        MobileServiceUser user = new MobileServiceUser(userId);
        user.setAuthenticationToken(token);
        client.setCurrentUser(user);

        return true;
    }
}
