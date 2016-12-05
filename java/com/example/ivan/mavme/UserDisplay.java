package com.example.ivan.mavme;

import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.provider.ContactsContract;
import android.widget.TextView;
import android.widget.GridLayout;
import android.widget.RelativeLayout;
import android.widget.ImageButton;

import android.content.Intent;
import android.view.View;
import android.os.Bundle;
import android.widget.Toast;

/******************************************************************************************
 * UserDisplay is the GUI for viewing the information of another MavMe user. It contains actions
 * such as poke, download to contacts, and block if an admin.
 * @author Ivan Huang
 * ****************************************************************************************/
public class UserDisplay extends BaseActivity {

    private User activeUser;
    private User u;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_display);

        onResume();
    }

    @Override
    public void onResume() {
        super.onResume();
        Intent i = getIntent();
        String userID = i.getStringExtra("userID");
        activeUser = DBMgr.getUserByID(HomeDisplay.userID);
        u = DBMgr.getUserByID(userID);

        DBMgr.updateAll();

        loadMenu(-1);
        loadUserDisplay();
    }

    /**
     * LoadUserDisplay populates the Views based on the User's information. If it is private, nothing except
     * the user's name is shown.
     */
    private void loadUserDisplay() {

        GridLayout profileInfo = (GridLayout) findViewById(R.id.profileInfo);
        RelativeLayout profileActions = (RelativeLayout) findViewById(R.id.profileActions);
        TextView privateDisplay = (TextView) findViewById(R.id.privateDisplay);
        TextView name = (TextView) findViewById(R.id.userName);
        TextView adminLabel = (TextView) findViewById(R.id.adminLabel);

        name.setText(u.getName());
        if(u.getIsAdmin()) {
            adminLabel.setVisibility(View.VISIBLE);
        } else {
            adminLabel.setVisibility(View.GONE);
        }

        // if profile is not private
        if(u.getIsPrivate() && !activeUser.getIsAdmin()) {
            profileInfo.setVisibility(View.GONE);
            profileActions.setVisibility(View.GONE);
            privateDisplay.setVisibility(View.VISIBLE);
        } else {
            profileInfo.setVisibility(View.VISIBLE);
            profileActions.setVisibility(View.VISIBLE);
            privateDisplay.setVisibility(View.GONE);

            // loading profile information
            TextView gender = (TextView) findViewById(R.id.gender);
            TextView dob = (TextView) findViewById(R.id.dob);
            TextView major = (TextView) findViewById(R.id.major);
            TextView degree = (TextView) findViewById(R.id.degree);
            TextView gradYear = (TextView) findViewById(R.id.graduation);
            TextView email = (TextView) findViewById(R.id.email);
            TextView phone = (TextView) findViewById(R.id.phone);
            TextView residence = (TextView) findViewById(R.id.residence);

            gender.setText(u.returnGender());
            dob.setText(u.returnDOB());
            major.setText(u.getMajor());
            degree.setText(u.getDegree());
            gradYear.setText(""+u.getGradYear());
            email.setText(u.getEmail());
            phone.setText(u.returnPhoneNumber());
            residence.setText(u.returnResidence());


            // resetting buttons
            ImageButton poke = (ImageButton) findViewById(R.id.poke);
            TextView pokeLabel = (TextView) findViewById(R.id.pokeLabel);
            poke.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#aaaaaa"), PorterDuff.Mode.MULTIPLY));
            pokeLabel.setTextColor(Color.parseColor("#aaaaaa"));

            ImageButton addContact = (ImageButton) findViewById(R.id.addContact);
            TextView addContactLabel = (TextView) findViewById(R.id.addContactLabel);
            addContact.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#aaaaaa"), PorterDuff.Mode.MULTIPLY));
            addContactLabel.setTextColor(Color.parseColor("#aaaaaa"));


            // adding blocking functionality for admin to end-user
            ImageButton block = (ImageButton) findViewById(R.id.block);
            TextView blockLabel = (TextView) findViewById(R.id.blockLabel);
            if(activeUser.getIsAdmin() && !u.getIsAdmin()) {
                block.setVisibility(View.VISIBLE);
                blockLabel.setVisibility(View.VISIBLE);
            } else {
                block.setVisibility(View.GONE);
                blockLabel.setVisibility(View.GONE);
            }


            configureButtons();
            addListenerOnButtons();
        }

    }


    /**
     * Configures the action buttons
     */
    public void configureButtons() {
        ImageButton block = (ImageButton) findViewById(R.id.block);
        TextView blockLabel = (TextView) findViewById(R.id.blockLabel);
        if(u.getIsBlocked()) {
            block.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#ffa500"), PorterDuff.Mode.MULTIPLY));
            blockLabel.setTextColor(Color.parseColor("#ffa500"));
        } else {
            block.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#aaaaaa"), PorterDuff.Mode.MULTIPLY));
            blockLabel.setTextColor(Color.parseColor("#aaaaaa"));
        }
    }

    /**
     * Adds functionality to the action buttons
     */
    public void addListenerOnButtons() {

        final ImageButton addContact = (ImageButton) findViewById(R.id.addContact);
        final TextView addContactLabel = (TextView) findViewById(R.id.addContactLabel);
        final ImageButton poke = (ImageButton) findViewById(R.id.poke);
        final TextView pokeLabel = (TextView) findViewById(R.id.pokeLabel);
        final ImageButton block = (ImageButton) findViewById(R.id.block);

        addContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(addContactLabel.getCurrentTextColor()!=Color.parseColor("#ffa500")) {
                    addContact.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#ffa500"), PorterDuff.Mode.MULTIPLY));
                    addContactLabel.setTextColor(Color.parseColor("#ffa500"));
                    Intent contactIntent = new Intent(ContactsContract.Intents.Insert.ACTION);
                    contactIntent.setType(ContactsContract.RawContacts.CONTENT_TYPE);

                    contactIntent
                            .putExtra(ContactsContract.Intents.Insert.NAME, u.getName())
                            .putExtra(ContactsContract.Intents.Insert.PHONE, u.getPhoneNumber())
                            .putExtra(ContactsContract.Intents.Insert.EMAIL, u.getEmail())
                    ;

                    startActivityForResult(contactIntent, 1);
                }
            }
        });


        poke.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(pokeLabel.getCurrentTextColor()!=Color.parseColor("#ffa500")) {
                    poke.getBackground().setColorFilter(new PorterDuffColorFilter(Color.parseColor("#ffa500"), PorterDuff.Mode.MULTIPLY));
                    pokeLabel.setTextColor(Color.parseColor("#ffa500"));
                    Notification n = Notification.newNotification(activeUser.getUserID(), activeUser.getName()+" has poked you!", "U"+activeUser.getUserID());
                    n.sendNotificationTo(new ArrayList<String>(Arrays.asList(u.getUserID())));
                    Toast.makeText(getApplicationContext(), "You have poked "+u.getName(), Toast.LENGTH_SHORT).show();
                }
            }
        });


        block.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                u.toggleIsBlocked();
                configureButtons();
            }
        });
    }
}
