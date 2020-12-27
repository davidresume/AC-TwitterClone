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

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.parse.SignUpCallback;
import com.shashank.sony.fancytoastlib.FancyToast;

public class MainActivity extends AppCompatActivity  implements View.OnClickListener {
    private EditText edtEmail, edtUserName, edtPassword;
    private Button btnSignUp, btnLoginSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setTitle(getString(R.string.sign_up));

        edtEmail = findViewById(R.id.edtEmail);
        edtUserName = findViewById(R.id.edtName);
        edtPassword = findViewById(R.id.edtPassword);

        btnSignUp = findViewById(R.id.btnSignUp);
        btnLoginSignUp = findViewById(R.id.btnLoginSignUp);

        if(ParseUser.getCurrentUser() != null) {
            ParseUser.logOut();
        }

        btnSignUp.setOnClickListener(this);
        btnLoginSignUp.setOnClickListener(this);
        edtPassword.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() ==KeyEvent.ACTION_DOWN) {
                    onClick(btnSignUp);
                }
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btnSignUp:
                try {
                    rootLayoutTapped(v);
                    String email = edtEmail.getText().toString();
                    String userName = edtUserName.getText().toString();
                    String password = edtPassword.getText().toString();
                    if(email.equals("") || password == "" || userName == "") {
                        FancyToast.makeText(this,"Email, Username and Password is required!",
                                FancyToast.LENGTH_SHORT,FancyToast.ERROR,false).show();
                    } else {
                        final ParseUser user = new ParseUser();
                        user.setUsername(userName);
                        user.setEmail(email);
                        user.setPassword(password);
                        ProgressDialog signUpDialog = new ProgressDialog(this);
                        signUpDialog.setMessage("Sign up " + userName);
                        signUpDialog.show();

                        user.signUpInBackground(new SignUpCallback() {
                            @Override
                            public void done(ParseException e) {
                                signUpDialog.dismiss();
                                if(e == null) {
                                    FancyToast.makeText(getApplicationContext(),user.getUsername() + " signed up successfully.",
                                            FancyToast.LENGTH_LONG,FancyToast.SUCCESS,false).show();
                                    switchToTwitterUsers();
                                } else {
                                    FancyToast.makeText(getApplicationContext(),"Unknown error: " + e.getMessage(),
                                            FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    FancyToast.makeText(this,"Unknown error:" + e.getMessage(),
                            FancyToast.LENGTH_LONG,FancyToast.ERROR,false).show();
                }

                break;
            case R.id.btnLoginSignUp:
                Intent login = new Intent(MainActivity.this,LogIn.class);
                startActivity(login);
                break;
            default:
        }
    }

    public void rootLayoutTapped(View v) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if(inputMethodManager.isAcceptingText()){
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),0);
        }
    }
    //Show or hide password
    public void showHidePassword(View view) {
        if (edtPassword.getTransformationMethod().equals(PasswordTransformationMethod.getInstance())) {
            ((ImageView) (view)).setImageResource(R.drawable.eye_hide);
            //Show Password
            edtPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            ((ImageView) (view)).setImageResource(R.drawable.eye_show);
            //Hide Password
            edtPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
    }
    //Switch to Twitter users
    private void switchToTwitterUsers() {
        Intent intent = new Intent(this,TwitterUsers.class);
        startActivity(intent);
    }
}