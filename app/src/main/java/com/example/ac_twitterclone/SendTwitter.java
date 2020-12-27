package com.example.ac_twitterclone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SendTwitter extends AppCompatActivity implements View.OnClickListener {
    private EditText edtTweet;
    private Button bntSendTweet, btnViewOtherUsersTweets;
    private ParseUser currentUser;
    private ListView otherTweetsListView;
    private ArrayList<HashMap<String,String>> arrayOtherUsersTweets;
    private SimpleAdapter simpleAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_twitter);

        setTitle("Tweets");

        edtTweet = findViewById(R.id.edtTweet);
        bntSendTweet = findViewById(R.id.bntSendTweet);
        btnViewOtherUsersTweets = findViewById(R.id.btnViewOtherUsersTweets);
        otherTweetsListView = findViewById(R.id.otherTweetsListView);

        currentUser = ParseUser.getCurrentUser();

        if (currentUser == null) {
            FancyToast.makeText(this,"Please login...",
                    Toast.LENGTH_SHORT,FancyToast.ERROR,false).show();
            finish();
        }

        btnViewOtherUsersTweets.setOnClickListener(this);

        bntSendTweet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bntSendTweet:
                if(edtTweet.getText().toString().equals("")) {
                    FancyToast.makeText(getApplicationContext(),"Please enter your tweet...",
                            Toast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                }
                ParseObject myTweet = new ParseObject("MyTweet");
                myTweet.put("tweet", edtTweet.getText().toString());
                myTweet.put("user", currentUser.getUsername());

                ProgressDialog sendingDialog = new ProgressDialog(SendTwitter.this);
                sendingDialog.setMessage("Sending tweet out... " );
                sendingDialog.show();

                myTweet.saveEventually(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        sendingDialog.dismiss();
                        if(e == null) {
                            FancyToast.makeText(getApplicationContext(),currentUser.getUsername() + "'s tweet is saved...",
                                    Toast.LENGTH_SHORT,FancyToast.SUCCESS,true).show();
                            edtTweet.setText("");
                        } else {
                            FancyToast.makeText(getApplicationContext(),"Unknown Error: " + e.getMessage(),
                                    Toast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                        }
                    }
                });

                break;
            case R.id.btnViewOtherUsersTweets:
                try {
                    arrayOtherUsersTweets = new ArrayList<>();
                    simpleAdapter = new SimpleAdapter(SendTwitter.this, arrayOtherUsersTweets,
                            android.R.layout.simple_list_item_2,
                            new String[]{"tweetUserName","tweetValue"},
                            new int[]{android.R.id.text1,android.R.id.text2});
                    ParseQuery<ParseObject> otherUsersTweets = new ParseQuery<ParseObject>("MyTweet");
                    otherUsersTweets.whereContainedIn("user", currentUser.getList("fanOf"));
                    ProgressDialog progressDialog = new ProgressDialog(SendTwitter.this);
                    progressDialog.setMessage("Fetching other users's tweets from the server...");
                    progressDialog.show();

                    otherUsersTweets.findInBackground(new FindCallback<ParseObject>() {
                        @Override
                        public void done(List<ParseObject> tweets, ParseException e) {
                            if(e == null) {
                                for (ParseObject tweet : tweets) {
                                    HashMap<String, String> hashMapTweet = new HashMap<>();
                                    hashMapTweet.put("tweetUserName",tweet.getString("user"));
                                    hashMapTweet.put("tweetValue", tweet.getString("tweet"));
                                    arrayOtherUsersTweets.add(hashMapTweet);
                                }
                                otherTweetsListView.setAdapter(simpleAdapter);
                            } else {
                                FancyToast.makeText(getApplicationContext(),"e != null \n Unknown Error: \n" + e.getMessage(),
                                        Toast.LENGTH_LONG,FancyToast.ERROR,true).show();
                            }
                            progressDialog.dismiss();
                        }
                    });
                } catch (Exception e) {
                    FancyToast.makeText(getApplicationContext(),"Unknown Error: " + e.getMessage(),
                            Toast.LENGTH_LONG,FancyToast.ERROR,true).show();
                }
                break;
        }

    }

    public void rootLayoutTapped(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }
}