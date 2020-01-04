package com.example.currencygroups;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private Toolbar mainToolbar;
    private ViewPager mainViewPager;
    private TabLayout mainTabLayout; //fragmentleri oluştur

    private TabAccessAdapter tabAccessAdapter;

    //Firebase
    private FirebaseUser availableUser;
    private FirebaseAuth mYetki;
    //VT'nin içerisinde kök dizinin kullanıcılar yolu - gruplar için
    //          (   "Kullanicilar"   )
    private DatabaseReference usersReferance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainToolbar = findViewById(R.id.mainactivity_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setLogo(R.drawable.ic_currency_logo);
        getSupportActionBar().setTitle("Currency With Chat");

        mainViewPager = findViewById(R.id.main_tabs_pager);
        tabAccessAdapter = new TabAccessAdapter(getSupportFragmentManager());
        mainViewPager.setAdapter(tabAccessAdapter);

        mainTabLayout = findViewById(R.id.main_tabs);

        /*
        mainTabLayout.addTab(mainTabLayout.newTab().setIcon(R.drawable.ic_group));
        mainTabLayout.addTab(mainTabLayout.newTab().setText("Gruplar"));
        mainTabLayout.addTab(mainTabLayout.newTab().setIcon(R.drawable.ic_menu));
        mainTabLayout.addTab(mainTabLayout.newTab().setText("Menüler"));
        mainTabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
         */
        mainTabLayout.setupWithViewPager(mainViewPager);

        //İSTERSEK LOGO EKLEDİK.
        mainTabLayout.getTabAt(0).setIcon(R.drawable.ic_group);
        mainTabLayout.getTabAt(1).setIcon(R.drawable.ic_menu);
        mainTabLayout.getTabAt(2).setIcon(R.drawable.ic_sync);

        //Firebase
        mYetki=FirebaseAuth.getInstance();
        availableUser = mYetki.getCurrentUser();
        usersReferance = FirebaseDatabase.getInstance().getReference();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (availableUser == null){
            SendUserToLoginActivity();
        }else{
            //Kullanıcının veritabanında varlığını doğrulayalım.
            VerifyUserPresence();
        }
    }

    private void VerifyUserPresence() {

        //VERITABANINDA IDLERI VAR MI ? - veriyolunu kullanıcılar'dan alacak.
        String availableUserID = mYetki.getCurrentUser().getUid();
        usersReferance.child("Kullanicilar").child(availableUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //kök kullanicilar içerisindeki uidlerin içerisindeki çocukların AD özelliği ekleme ayarları.


                //datasnapshot vt'deki verileri temsil eder
                //HER BİRİNİN ALTINDA AD DURUM RESİM
                //DATASNAPSHOT VT'DEKİ VERİLERİ TEMSİL EDER.

                //ad diye alan varsa
                if ((dataSnapshot.child("ad").exists())){
                    //Toast.makeText(MainActivity.this, "Welcome", Toast.LENGTH_LONG).show();
                }else{
                    //AD YOKSA - ayarlardan adını tanımlamak için kullanıcıyı ayarlara göndersin
                    /*
                    Intent goToSettings = new Intent(MainActivity.this,SettingsActivity.class);
                    goToSettings.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(goToSettings);
                    finish();
                       */
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SendUserToLoginActivity() {

        Intent signinIntent = new Intent(MainActivity.this,SignInActivity.class);
        startActivity(signinIntent);
        signinIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        finish();

    }
}
