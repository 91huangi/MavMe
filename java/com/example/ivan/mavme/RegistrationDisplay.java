package com.example.ivan.mavme;


import android.content.Intent;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;


/******************************************************************************************
 * RegistrationDisplay manages the GUI for the registration form
 * @author Ivan Huang
 * ****************************************************************************************/

public class RegistrationDisplay extends AppCompatActivity {

    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.registration_display);
        onResume();
    }

    public void onResume() {
        super.onResume();

        context=this;
        DBMgr.updateAll();

        loadSpinners();
        addListenerOnButtons();
    }

    /**
     * Populating spinners
     */
    public void loadSpinners() {

        Spinner gender = (Spinner) findViewById(R.id.spinnerGender);
        Spinner major = (Spinner) findViewById(R.id.spinnerMajor);
        Spinner degree = (Spinner) findViewById(R.id.spinnerDegree);
        Spinner residence = (Spinner) findViewById(R.id.spinnerResidence);


        // populating spinners
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_list, Utils.genders);
        gender.setAdapter(arrayAdapter);

        arrayAdapter = new ArrayAdapter(this, R.layout.spinner_list, Utils.majors);
        major.setAdapter(arrayAdapter);

        arrayAdapter = new ArrayAdapter(this, R.layout.spinner_list, Utils.degrees);
        degree.setAdapter(arrayAdapter);


        arrayAdapter = new ArrayAdapter(this, R.layout.spinner_list, Utils.residences);
        residence.setAdapter(arrayAdapter);
    }

    /**
     * Adding functionality on buttons
     */
    public void addListenerOnButtons() {

        final Spinner gender = (Spinner) findViewById(R.id.spinnerGender);
        final Spinner major = (Spinner) findViewById(R.id.spinnerMajor);
        final Spinner degree = (Spinner) findViewById(R.id.spinnerDegree);
        final EditText gradYear = (EditText) findViewById(R.id.editGradYear);
        final Spinner residence = (Spinner) findViewById(R.id.spinnerResidence);
        final EditText editName = (EditText) findViewById(R.id.editName);
        final EditText editDOB = (EditText) findViewById(R.id.editDOB);
        final EditText editEmail = (EditText) findViewById(R.id.editEmail);
        final EditText editPhone = (EditText) findViewById(R.id.editPhone);
        final CheckBox privacy = (CheckBox) findViewById(R.id.privacy);

        final EditText editPassword1 = (EditText) findViewById(R.id.editPassword1);
        final EditText editPassword2 = (EditText) findViewById(R.id.editPassword2);
        final TextView register = (TextView) findViewById(R.id.register);
        final TextView exit = (TextView) findViewById(R.id.exit);


        register.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                String errorMessage = "";
                String dateString = editDOB.getText().toString();
                Date dob = new Date();


                // testing format of birth date
                if(dateString.length()!=10) errorMessage="Error: Please enter a valid date of birth (mm/dd/yyyy)";
                DateFormat df = new SimpleDateFormat("MM/dd/yyyy");
                try {
                    dob = df.parse(dateString);
                } catch (ParseException e) {
                    errorMessage="Error: Please enter a valid date of birth (mm/dd/yyyy)";
                }

                User u = DBMgr.getUserByEmail(editEmail.getText().toString());
                // Checking if...
                        // Email already exists
                        // Password is too short
                        // Passwords do not match
                        // Email is not a UTA email
                        // Name field is empty
                if(u!=null) {
                    errorMessage="Error: Email is already taken.";
                } else if (editPassword1.getText().toString().length()<8 ) {
                    errorMessage="Error: New password length must be at least 8 characters";
                } else if (!editPassword1.getText().toString().equals(editPassword2.getText().toString())) {
                    errorMessage = "Error: Passwords do not match";
                } else if (!editEmail.getText().toString().substring(editEmail.getText().toString().length()-7).equals("uta.edu")) {
                    errorMessage="Error: Email must end in 'uta.edu'";
                } else if (editName.getText().toString().length()==0) {
                    errorMessage="Error: Name cannot be blank";
                }

                // if no errors, register and login!
                if(errorMessage.length()==0) {
                    u = new User();
                    u.setName(editName.getText().toString());
                    u.setIsMale(gender.getSelectedItem().toString().equals("Male"));
                    u.setMajor(major.getSelectedItem().toString());
                    u.setDegree(degree.getSelectedItem().toString());
                    u.setGradYear(gradYear.getText().toString());
                    u.setOnCampus(residence.getSelectedItem().toString().equals("On Campus"));
                    u.setDob(dateString.replace("/",""));
                    u.setEmail(editEmail.getText().toString());
                    u.setPhoneNumber(editPhone.getText().toString());
                    u.setPassword(editPassword1.getText().toString());
                    u.setIsPrivate(privacy.isChecked());

                    u=User.newUser(u.getName(), u.getPassword(), u.getEmail(), u.getIsMale(), u.getDob(), u.getMajor(),
                            u.getDegree(), u.getGradYear(), u.getPhoneNumber(), u.getOnCampus(), u.getIsPrivate());
                    HomeDisplay.userID=u.getUserID();

                    Utils.alertDialog(context, "Registration complete!", new Utils.dialogHandler() {
                        @Override
                        public void onButtonClick(boolean click) {
                            Intent i = new Intent("com.example.ivan.mavme.HomeDisplay");
                            startActivity(i);
                        }
                    });


                } else {
                    Utils.alertDialog(context, errorMessage, new Utils.dialogHandler() {
                        public void onButtonClick(boolean click) {}
                    });
                }
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Utils.confirmationDialog(context, "Exit without registering?",
                        new Utils.dialogHandler() {
                            @Override
                            public void onButtonClick(boolean click) {
                                if(click) finish();
                            }
                        });
            }
        });

    }


}
