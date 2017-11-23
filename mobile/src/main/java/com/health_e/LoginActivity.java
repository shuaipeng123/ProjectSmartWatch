package com.health_e;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

import static android.Manifest.permission.READ_CONTACTS;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private FirebaseAuth auth;
    private ProgressBar progressBar;
    private Button btnSignup, btnLogin, btnReset;

    private DatabaseReference mDatabase;
    private Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        // TODO always need sign in for debug
        if (auth.getCurrentUser() != null) {
            Toast.makeText(getApplicationContext(), "already logged in!", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this, HomeScreen.class));
            finish();
        }

        // set the view now
        setContentView(R.layout.activity_login);

        // AliN failed Action bar to start
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnSignup = (Button) findViewById(R.id.btn_signup);
        btnLogin = (Button) findViewById(R.id.btn_login);
        btnReset = (Button) findViewById(R.id.btn_reset_password);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        btnReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = inputEmail.getText().toString();
                final String password = inputPassword.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);

                //authenticate user
                auth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                progressBar.setVisibility(View.GONE);
                                if (!task.isSuccessful()) {
                                    // there was an error
                                    if (password.length() < 6) {
                                        inputPassword.setError(getString(R.string.minimum_password));
                                    } else {
                                        Toast.makeText(LoginActivity.this, getString(R.string.auth_failed), Toast.LENGTH_LONG).show();
                                    }
                                } else {
                                    Toast.makeText(getApplicationContext(), auth.getCurrentUser().getEmail() + " Signed in",
                                            Toast.LENGTH_SHORT).show();
//                                    userScreenSelection();
                                    Intent intent = new Intent(LoginActivity.this, HomeScreen.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        });
            }
        });
    }

//    public void userScreenSelection(){
//        ValueEventListener profileListener = new ValueEventListener() {
//            @Override
//            public void onDataChange(DataSnapshot dataSnapshot) {
//                try{
//                    mDatabase = FirebaseDatabase.getInstance().getReference();
//                    profile = dataSnapshot.child("users").child(auth.getCurrentUser().getUid()).getValue(Profile.class);
////                    Toast.makeText(getApplicationContext(),"Database read:" + profile.email +
////                            " Physician:" + profile.physicianId,Toast.LENGTH_SHORT).show();
//                    if(profile.userType.equals(Profile.UserType.FAMILY))
//                    {
//                        startActivity(new Intent(LoginActivity.this, FamilyHomeScreen.class)); // Switch to Family member view
//                        finish();
//                    } else if (profile.userType.equals(Profile.UserType.PATIENT)){
//                        startActivity(new Intent(LoginActivity.this, HomeScreen.class));
//                        finish();
//                    } else{
//                        Toast.makeText(getApplicationContext(),"UserType query failed",Toast.LENGTH_LONG).show();
//                        finish();
//                    }
//                }catch(Exception e)
//                {
//                    Toast.makeText(getApplicationContext(),"Profile query failed",Toast.LENGTH_SHORT).show();
//                    finish();
//                }
//            }
//            @Override
//            public void onCancelled(DatabaseError databaseError) {
//                Log.w("loadPost:onCancelled", databaseError.toException());
//            }
//        };
//        mDatabase.addValueEventListener(profileListener);
//    }

    // this listener will be called when there is change in firebase user session
    FirebaseAuth.AuthStateListener authListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // user auth state is changed - user is null
                // launch login activity
                startActivity(new Intent(LoginActivity.this, LoginActivity.class));
                finish();
            }
        }
    };

    //TODO    Change Password
//    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//    user.updatePassword(newPassword.getText().toString().trim())
//            .addOnCompleteListener(new OnCompleteListener<Void>() {
//        @Override
//
//public void onComplete(@NonNull Task<Void> task) {
//        if (task.isSuccessful()) {
//        Toast.makeText(MainActivity.this, "Password is updated!", Toast.LENGTH_SHORT).show();
//        } else {
//        Toast.makeText(MainActivity.this, "Failed to update password!", Toast.LENGTH_SHORT).show();
//        progressBar.setVisibility(View.GONE);
//        }
//        }
//        });

//    Change Email
//    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//    user.updateEmail(newEmail.getText().toString().trim())
//            .addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//        if (task.isSuccessful()) {
//        Toast.makeText(MainActivity.this, "Email address is updated.", Toast.LENGTH_LONG).show();
//        } else {
//        Toast.makeText(MainActivity.this, "Failed to update email!", Toast.LENGTH_LONG).show();
//        }
//        }
//        });

//    Deleting Account / User
//    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//if (user != null) {
//        user.delete()
//        .addOnCompleteListener(new OnCompleteListener<Void>() {
//@Override
//public void onComplete(@NonNull Task<Void> task) {
//        if (task.isSuccessful()) {
//        Toast.makeText(MainActivity.this, "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();
//        } else {
//        Toast.makeText(MainActivity.this, "Failed to delete your account!", Toast.LENGTH_SHORT).show();
//        }
//        }
//        });
//        }

}
