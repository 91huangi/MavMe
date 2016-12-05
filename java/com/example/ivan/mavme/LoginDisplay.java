package com.example.ivan.mavme;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;



import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;


/******************************************************************************************
 * LoginDisplay is the app starting GUI. It handles login and authentication methods
 * @author Ivan Huang
 * ****************************************************************************************/


public class LoginDisplay extends AppCompatActivity {


    private Context context;

    private EditText inputEmail, inputPassword;
    private Button btnSignup, btnLogin, btnReset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_display);
    }

    @Override
    public void onResume() {
        super.onResume();

        context = this;
        DBMgr.initiateDB();
        DBMgr.updateAll();
        addListenerOnButtons();


    }

    /**
     * adding listener on buttons
     */
    public void addListenerOnButtons() {

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent("com.example.ivan.mavme.RegistrationDisplay");
                startActivity(i);
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sysEmail=DBMgr.getUserByID("+000000").getEmail();
                Utils.alertDialog(context, "Please contact "+sysEmail+" to request a password reset.",
                        new Utils.dialogHandler() {
                            @Override
                            public void onButtonClick(boolean click) {
                            }
                        });
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                // making sure both email and password are filled
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Utils.alertDialog(context, "Please enter an email and password.",
                            new Utils.dialogHandler() {
                                @Override
                                public void onButtonClick(boolean click) {
                                }
                            });
                    return;
                }

                int uID=authenticate(email, password);

                if(uID>=0) {
                    HomeDisplay.userID = DBMgr.indexToID(uID);
                    Intent i = new Intent("com.example.ivan.mavme.HomeDisplay");
                    startActivity(i);
                } else {
                    if(uID==-1)
                        Utils.alertDialog(context, "Invalid Login Credentials. Please verify email or password and try again.",
                                new Utils.dialogHandler() {
                                    @Override
                                    public void onButtonClick(boolean click) { }
                                });
                    if(uID==-2) {
                        String sysEmail=DBMgr.getUserByID(Utils.sysID).getEmail();
                        Utils.alertDialog(context, "This account has been deactivated. Please contact "+sysEmail+" to re-activate.",
                                new Utils.dialogHandler() {
                                    @Override
                                    public void onButtonClick(boolean click) {
                                    }
                                });
                    }
                }
            }
        });
    }

    /**
     * Authenticate returns a state depending on the input email and password combination
     * @param email
     * @param password
     * @return int: -2 if user is blocked, -1 if invalid credentials, or the user ID as a number if successful
     */
    public int authenticate(String email, String password) {

        User u = DBMgr.getUserByEmail(email);
        if(u!=null) {
            if (u.getIsBlocked()) return -2;
            if (u.getPassword().equals(password)) return Integer.parseInt(u.getUserID());
        }

        return -1;
    }
}