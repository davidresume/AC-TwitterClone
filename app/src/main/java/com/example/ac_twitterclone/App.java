package com.example.ac_twitterclone;

import android.app.Application;

import com.parse.Parse;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("QhzSAuJ3c7LDZiT2U3Ee9uWURC1L39odjtlEaucq")
                // if defined
                .clientKey("FF0cXgEFy3NE9c0d5wJK9CD6NkvkSTnZ7eohSYf7")
                .server("https://parseapi.back4app.com/")
                .build()
        );
    }
}
