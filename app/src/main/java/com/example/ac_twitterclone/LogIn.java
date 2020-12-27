package com.example.ac_twitterclone;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.shashank.sony.fancytoastlib.FancyToast;

public class LogIn extends AppCompatActivity implements View.OnClickListener {
    private EditText edtUserEmail, edtPasswordLogin;
    private Button btnLogin, btnSignUpLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);

        setTitle(getString(R.string.login_title));

        edtUserEmail = findViewById(R.id.edtUserNameLogin);
        edtPasswordLogin = findViewById(R.id.edtPasswordLogin);

        btnLogin = findViewById(R.id.btnLogin);
        btnSignUpLogin = findViewById(R.id.btnSignUpLogin);

        if (ParseUser.getCurrentUser() != null) {
            ParseUser.logOut();
        }

        btnLogin.setOnClickListener(this);
        btnSignUpLogin.setOnClickListener(this);
        edtPasswordLogin.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN){
                    onClick(btnLogin);
                }
                return false;
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnLogin:
                rootLayoutTapped(v);
                String userName = edtUserEmail.getText().toString();
                String passWord = edtPasswordLogin.getText().toString();
                ProgressDialog progressDialog = new ProgressDialog(LogIn.this);
                progressDialog.setMessage("Login " + userName + "...");
                progressDialog.show();
                ParseUser.logInInBackground(userName, passWord, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user != null && e == null) {
                            FancyToast.makeText(getApplicationContext(), "Logged in " + user.getUsername(),
                                    FancyToast.LENGTH_SHORT, FancyToast.SUCCESS, false).show();
                            switchToTwitterUsers();
                        } else if (user == null && e == null) {
                            FancyToast.makeText(getApplicationContext(), "Could login, please check username and password.",
                                    FancyToast.LENGTH_SHORT, FancyToast.WARNING, false).show();
                        } else {
                            FancyToast.makeText(getApplicationContext(), "Unknown error: " + e.getMessage(),
                                    FancyToast.LENGTH_SHORT, FancyToast.ERROR, false).show();
                        }
                        progressDialog.dismiss();
                    }
                });
                break;
            case R.id.btnSignUpLogin:
                Intent signUp = new Intent(LogIn.this, MainActivity.class);
                finish();
                startActivity(signUp);
        }
    }
    //Switch to Twitter users
    private void switchToTwitterUsers() {
        Intent intent = new Intent(LogIn.this,TwitterUsers.class);
        startActivity(intent);
    }

    //Hide SoftKeyBoard
    public void rootLayoutTapped(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (inputMethodManager.isAcceptingText()) {
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }

    //Show or hide password
    public void showHidePassword(View view) {
        if (edtPasswordLogin.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            ((ImageView) (view)).setImageResource(R.drawable.eye_hide);
            //Show Password
            edtPasswordLogin.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            ((ImageView) (view)).setImageResource(R.drawable.eye_show);
            //Hide Password
            edtPasswordLogin.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
}