package com.example.ac_twitterclone;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class TwitterUsers extends AppCompatActivity implements AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener {
    private ListView listViewUsers;
    private ArrayList<String> usersArray;
    private ArrayAdapter usersArrayAdapter;
    private String followingUsers = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitter_users);

        listViewUsers = findViewById(R.id.listViewUsers);
        listViewUsers.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        usersArray = new ArrayList<>();
        //usersArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,usersArray);
        //usersArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_multiple_choice,usersArray);
        usersArrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_checked,usersArray);
        FancyToast.makeText(getApplicationContext(),"Welcome " + ParseUser.getCurrentUser().getUsername(),
                Toast.LENGTH_LONG,FancyToast.SUCCESS,true).show();
        try {
            ParseQuery<ParseUser> parseQuery = ParseUser.getQuery();
            parseQuery.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());

            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Fetching users list from the server...");
            progressDialog.show();

            parseQuery.findInBackground(new FindCallback<ParseUser>() {
                @Override
                public void done(List<ParseUser> users, ParseException e) {
                    progressDialog.dismiss();
                    if (users.size() > 0 && e == null) {
                        for (ParseUser user : users) {
                            usersArray.add(user.getUsername());
                        }
                        listViewUsers.setAdapter(usersArrayAdapter);
                        for (String user : usersArray) {
                            if(ParseUser.getCurrentUser().getList("fanOf") != null) {
                                if (ParseUser.getCurrentUser().getList("fanOf").contains(user)) {
                                    followingUsers += user + "\n";
                                    listViewUsers.setItemChecked(usersArray.indexOf(user), true);
                                }
                            }
                        }
                        FancyToast.makeText(getApplicationContext(), ParseUser.getCurrentUser().getUsername() + " is following " + followingUsers,
                                Toast.LENGTH_LONG, FancyToast.INFO, false).show();

                    } else if (e != null) {
                        FancyToast.makeText(getApplicationContext(), "Unknown error:" + e.getMessage(),
                                Toast.LENGTH_LONG, FancyToast.ERROR, false).show();
                    } else {
                        FancyToast.makeText(getApplicationContext(), "No other users...",
                                Toast.LENGTH_LONG, FancyToast.WARNING, false).show();
                    }
                }
            });
        } catch (Exception e) {
            FancyToast.makeText(getApplicationContext(), "Unknown error:" + e.getMessage(),
                    Toast.LENGTH_LONG, FancyToast.ERROR, false).show();
        }
        listViewUsers.setOnItemLongClickListener(this);
        listViewUsers.setOnItemClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.my_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.logOut:
                ParseUser.getCurrentUser().logOut();
                Intent intent = new Intent(TwitterUsers.this,MainActivity.class);
                startActivity(intent);
                finish();
            case R.id.send:
                Intent intent1 = new Intent(this,SendTwitter.class);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        FancyToast.makeText(getApplicationContext(), usersArray.get(position) + " is long press on",
                Toast.LENGTH_LONG,FancyToast.INFO,false).show();
        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        CheckedTextView checkedTextView = (CheckedTextView) view;
        if(checkedTextView.isChecked()) {
            FancyToast.makeText(getApplicationContext(), usersArray.get(position) + " is followed",
                    Toast.LENGTH_SHORT,FancyToast.INFO,true).show();
            ParseUser.getCurrentUser().add("fanOf",usersArray.get(position));
        } else {
            FancyToast.makeText(getApplicationContext(), usersArray.get(position) + " is not followed",
                    Toast.LENGTH_SHORT,FancyToast.INFO,true).show();
            ParseUser.getCurrentUser().getList("fanOf").remove(usersArray.get(position));
            List currentUserFanOfList = ParseUser.getCurrentUser().getList("fanOf");
            ParseUser.getCurrentUser().remove("fanOf");
            ParseUser.getCurrentUser().put("fanOf", currentUserFanOfList);
        }
        ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e == null ) {
                    FancyToast.makeText(getApplicationContext(), "Saved",
                            Toast.LENGTH_LONG,FancyToast.SUCCESS,true).show();
                } else {
                    FancyToast.makeText(getApplicationContext(), "Unknown error: " + e.getMessage(),
                            Toast.LENGTH_LONG,FancyToast.ERROR,true).show();
                }
            }
        });
    }
}