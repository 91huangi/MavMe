package com.example.ivan.mavme;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.View;


import android.widget.EditText;
import android.widget.TextView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView;

/******************************************************************************************
 * LoginDisplay is the app starting GUI. It handles login and authentication methods
 * @author Ivan Huang
 * ****************************************************************************************/
public class SearchDisplay extends BaseActivity {

    private ArrayList<String> userIDs = new ArrayList<String>();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_display);
        onResume();
    }

    @Override
    public void onResume() {
        super.onResume();
        loadMenu(2);

        DBMgr.updateAll();
        loadSearchResults();
        addListenerOnButtons();
        addListenerOnListView(this);
    }


    /**
     * Loading search results
     */
    public void loadSearchResults() {

        ListView searchResults = (ListView) findViewById(R.id.searchResults);
        TextView noResults = (TextView) findViewById(R.id.noSearchResults);
        TextView background = (TextView) findViewById(R.id.background1);
        TextView shadow = (TextView) findViewById(R.id.shadow1);

        if(userIDs.isEmpty()) {
            searchResults.setVisibility(View.GONE);
            background.setVisibility(View.GONE);
            shadow.setVisibility(View.GONE);
            noResults.setVisibility(View.VISIBLE);
        } else {

            UserAdapter userAdapter = new UserAdapter(this, R.layout.user_list, userIDs);
            searchResults.setAdapter(userAdapter);

            searchResults.setVisibility(View.VISIBLE);
            background.setVisibility(View.VISIBLE);
            shadow.setVisibility(View.VISIBLE);
            noResults.setVisibility(View.GONE);
        }
    }

    /**
     * Adding button functionality
     */
    public void addListenerOnButtons() {

        final EditText query = (EditText) findViewById(R.id.searchQuery);
        final TextView search = (TextView) findViewById(R.id.search);


        search.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                userIDs = new SearchQuery(query.getText().toString()).search();
                loadSearchResults();
            }
        });


    }

    /**
     * On click of list of users, direct to user's profile
     * @param context
     */
    public void addListenerOnListView(final Context context) {
        ListView searchResults = (ListView) findViewById(R.id.searchResults);
        searchResults.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Utils.goToURL(context, "U"+userIDs.get(position));
            }
        });

    }
}
