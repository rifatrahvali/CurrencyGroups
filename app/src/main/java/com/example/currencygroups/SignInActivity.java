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

public class SignInActivity extends AppCompatActivity {

    private Button signinbutton ;
    private EditText signinmail , signinpassword;
    private TextView signinnewaccount , signinforgetpasswordconnection;

    //Firebase
    private FirebaseAuth mYetki;

    //Progress Dialog
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        //Progress Dialog Controls
        loadingBar = new ProgressDialog(this);

        //Firebase Controls
        mYetki = FirebaseAuth.getInstance();

        //Controls
        signinbutton = findViewById(R.id.signin_button);

        signinmail = findViewById(R.id.signin_email);
        signinpassword = findViewById(R.id.signin_password);

        signinnewaccount = findViewById(R.id.signin_createnewaccount);
        signinforgetpasswordconnection = findViewById(R.id.signin_password_forget_connection);

        signinnewaccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent signupIntent = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(signupIntent);
                finish();
            }
        });

        signinbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SuccessfullyLogin();
            }
        });
    }

    private void SuccessfullyLogin() {

        String email = signinmail.getText().toString();
        String password = signinpassword.getText().toString();

        if (TextUtils.isEmpty(email)){
            Toast.makeText(this,"E-mail cannot be left blank.",Toast.LENGTH_LONG).show();
        }
        else if (TextUtils.isEmpty(password)){
            Toast.makeText(this,"Password cannot be left blank.",Toast.LENGTH_LONG).show();
        }else{

            //Progress Dialog
            loadingBar.setTitle("Logging in");
            loadingBar.setMessage("Please wait.");
            loadingBar.setCanceledOnTouchOutside(true);
            loadingBar.show();

            //Giriş yaptıralım.
            mYetki.signInWithEmailAndPassword(email,password)
                    .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){

                                //Giriş görevimiz başarılıysa mainActivity' e gönderelim.
                                Intent goToMainActivity = new Intent(SignInActivity.this,MainActivity.class);
                                goToMainActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(goToMainActivity);
                                finish();

                                Toast.makeText(SignInActivity.this, "Login is successful.", Toast.LENGTH_SHORT).show();
                                //loadingBar' ı kapatalım.
                                loadingBar.dismiss();
                            }else {

                                String errorMessage = task.getException().toString();
                                Toast.makeText(SignInActivity.this,"Error : " + errorMessage+ " " +
                                        "Check your information.",Toast.LENGTH_LONG).show();
                                loadingBar.dismiss();
                            }
                        }
                    });

        }
    }
}
