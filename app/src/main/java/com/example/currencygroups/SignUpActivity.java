package com.example.currencygroups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private Button signupbutton;
    private EditText signupmail , signuppassword;
    private TextView signupalreadyhaveaccount;

    //Firebase
    private FirebaseAuth mYetki;
    //Firebase-Database' de ağaç yapısı vardır. //veritabanı tanımlayalım
    //KÖK yapısı, hangi köke kayıt edeceğiz.
    private DatabaseReference kokReferance;

    //Progress Dialog
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        //Progress Dialog Controls
        loadingBar = new ProgressDialog(this);

        //Firebase Controls
        mYetki = FirebaseAuth.getInstance();
        kokReferance = FirebaseDatabase.getInstance().getReference();

        //Controls
        signupmail =  findViewById(R.id.signup_email);
        signuppassword =  findViewById(R.id.signup_password);

        signupbutton = findViewById(R.id.signup_button);

        signupalreadyhaveaccount = findViewById(R.id.signup_alreadyhaveaccount);

        signupalreadyhaveaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(SignUpActivity.this,SignInActivity.class);
                startActivity(signupIntent);
                finish();
            }
        });

        signupbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CreateNewAccount();
            }
        });

    }

    private void CreateNewAccount() {

        String email = signupmail.getText().toString();
        String password = signuppassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"E-mail cannot be left blank.",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Password cannot be left blank.",Toast.LENGTH_LONG).show();
        }else {

            //Progress Dialog
            loadingBar.setTitle("Creating new account");
            loadingBar.setMessage("Please wait.");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            //Kayıt yaptıralım.
            mYetki.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            //Kayıt yaptırma görevimiz.
                            if (task.isSuccessful()){

                                //Vtde kullanicilar altında mevcutkulaniciuid altında oluşturalım.
                                //UID : Firebase ' deki her kullanıcının benzersiz id'sidir.

                                String availableUserID = mYetki.getCurrentUser().getUid();
                                kokReferance.child("Kullanicilar").child(availableUserID).setValue("");

                                //Kayıt görevimiz başarılıysa mainActivity' e gönderelim.
                                Intent goToMainActivity = new Intent(SignUpActivity.this,MainActivity.class);
                                goToMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(goToMainActivity);
                                finish();

                                Toast.makeText(SignUpActivity.this, "New account created successfully.", Toast.LENGTH_SHORT).show();
                                //loadingBar' ı kapatalım.
                                loadingBar.dismiss();

                            }else {
                                String errorMessage = task.getException().toString();
                                Toast.makeText(SignUpActivity.this, "Error : " + errorMessage + " "+
                                        "There was a problem creating the account.", Toast.LENGTH_SHORT).show();

                                //loadingBar' ı kapatalım.
                                loadingBar.dismiss();
                            }

                        }
                    });

        }

    }
}
