package com.health_e;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Exclude;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText inputEmail, inputPassword;
    private Button btnSignIn, btnSignUp, btnResetPassword;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private DatabaseReference mDatabase;
    Model appData;

    private void writeNewProfile(String userId, String email, String name, Profile.UserType userType, String age,
                                 String emerg_name, String emerg_num, String physicianId, String familyId,
                                 String patientId) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
//        String key = mDatabase.child("Profile").push().getKey();
        Profile profile = new Profile(userId, email, name, userType, age,
                emerg_name, emerg_num,physicianId,familyId,patientId, " ");
        Map<String, Object> profileValues = profile.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
//        childUpdates.put("/profiles/" + key, profileValues);
        childUpdates.put("/users/" + userId , profileValues);
        mDatabase.updateChildren(childUpdates);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Get Firebase auth instance
        auth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        // ask for user info
        appData = Model.getInstance(getApplicationContext());

        final EditText nameInput = new EditText(this);
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);

        final EditText userTypeInput = new EditText(this);
        userTypeInput.setInputType(InputType.TYPE_CLASS_TEXT);

        final EditText ageInput = new EditText(this);
        ageInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        ageInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});

        final EditText contactInput = new EditText(this);
        contactInput.setInputType(InputType.TYPE_CLASS_TEXT);

        final EditText contactNumInput = new EditText(this);
        contactNumInput.setInputType(InputType.TYPE_CLASS_NUMBER);
        contactNumInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(11)});

//        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(NewCase.this, R.style.AlertDialogCustom));

        final AlertDialog warn = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AlertDialogCustom))
                .setTitle("WARNING")
                .setMessage("The field is empty!")
                .setPositiveButton("Okay", null)
                .setCancelable(false)
                .create();

        final AlertDialog name = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AlertDialogCustom))
                .setMessage("What is your name?")
                .setTitle("Welcome!")
                .setPositiveButton("Next", null)
                .setNegativeButton("Cancel",null)
                .setView(nameInput)
                .setCancelable(false)
                .create();

        final AlertDialog userType = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AlertDialogCustom))
                .setMessage("Please select user type:")
                .setTitle("Welcome!")
                .setPositiveButton("Patient",null)
                .setNegativeButton("Family",null)
//                .setView(userTypeInput)
                .setCancelable(false)
                .create();

        final AlertDialog age = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AlertDialogCustom))
                .setMessage("How old are you?")
                .setTitle("Welcome!")
                .setPositiveButton("Next", null)
                .setView(ageInput)
                .setCancelable(false)
                .create();

        final AlertDialog contact = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AlertDialogCustom))
                .setMessage("Emergency Contact Name")
                .setTitle("Welcome!")
                .setPositiveButton("Next", null)
                .setView(contactInput)
                .setCancelable(false)
                .create();

        final AlertDialog contactNum = new AlertDialog.Builder(new ContextThemeWrapper(this,R.style.AlertDialogCustom))
                .setMessage("Emergency Contact Number")
                .setTitle("Welcome!")
                .setPositiveButton("Next", null)
                .setView(contactNumInput)
                .setCancelable(false)
                .create();

        contactNum.show();
        contact.show();
        age.show();
        userType.show();
        name.show();

        name.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = nameInput.getText().toString();
                if (s.length() > 0) {
                    appData.setName(s);
                    name.dismiss();
                } else {
                    warn.show();
                }
            }
        });

        name.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name.dismiss();
                userType.dismiss();
                contactNum.dismiss();
                contact.dismiss();
                age.dismiss();
            }
        });

        userType.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appData.setUserType(Profile.UserType.PATIENT);
                userType.dismiss();
            }
        });

        userType.getButton(AlertDialog.BUTTON_NEGATIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                appData.setUserType(Profile.UserType.FAMILY);
                userType.dismiss();
                contactNum.dismiss();
                contact.dismiss();
                age.dismiss();
                appData.setAge("");
                appData.setEmerName("");
                appData.setEmerNum("");
            }
        });

        age.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = ageInput.getText().toString();
                if (s.length() > 0) {
                    appData.setAge(s);
                    age.dismiss();
                } else {
                    warn.show();
                }
            }
        });


        contact.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = contactInput.getText().toString();
                if (s.length() > 0) {
                    appData.setEmerName(s);
                    contact.dismiss();
                } else {
                    warn.show();
                }
            }
        });

        contactNum.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = contactNumInput.getText().toString();
                if (s.length() > 0) {
                    appData.setEmerNum(s);
                    contactNum.dismiss();
                } else {
                    warn.show();
                }
            }
        });

        btnSignIn = (Button) findViewById(R.id.sign_in_button);
        btnSignUp = (Button) findViewById(R.id.sign_up_button);
        inputEmail = (EditText) findViewById(R.id.email);
        inputPassword = (EditText) findViewById(R.id.password);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnResetPassword = (Button) findViewById(R.id.btn_reset_password);

        btnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignUpActivity.this, ResetPasswordActivity.class));
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String email = inputEmail.getText().toString().trim();
                String password = inputPassword.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                progressBar.setVisibility(View.VISIBLE);
                //create user
                auth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                Toast.makeText(SignUpActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                progressBar.setVisibility(View.GONE);
                                // If sign in fails, display a message to the user. If sign in succeeds
                                // the auth state listener will be notified and logic to handle the
                                // signed in user can be handled in the listener.
                                if (!task.isSuccessful()) {
                                    Toast.makeText(SignUpActivity.this, "Authentication failed." + task.getException(),
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    writeNewProfile(auth.getCurrentUser().getUid(),auth.getCurrentUser().getEmail(),
                                            appData.getName(),appData.getUserType(),appData.getAge(),appData.getEmerName(),
                                            appData.getEmerNum(),"TBD", "TBD","TBD");
                                    startActivity(new Intent(SignUpActivity.this, HomeScreen.class)); // MainActivity
                                    finish();
                                }
                            }
                        });
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        progressBar.setVisibility(View.GONE);
    }
}

