package com.example.currencygroups;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class SettingsActivity extends AppCompatActivity {

    private Button settingsUpdateButton;
    private EditText settingsUsername , settingsUserStatus;
    private CircleImageView settingsUserProfilePhoto;
    private Toolbar settingsToolbar;
    //FIREBASE
    private FirebaseAuth mYetki;
    private DatabaseReference dataPath;
    //FIREBASE -> STORAGE -> Profil Fotograflari KLASÖRÜ oluşturmak için tanımladığımız değişken adıdır.
    private StorageReference userProfilePhotoPath;

    private StorageTask uploadQuest; //yükleme görevi

    private String currentUserID;

    //FOTOGRAF SEÇMEK
    private static final int GallerySelectCode = 1;

    private ProgressDialog loadingBar;

    //Uri
    Uri photoUri;
    String myUri = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);


        settingsToolbar = findViewById(R.id.settingsactivity_toolbar);
        setSupportActionBar(settingsToolbar);
        getSupportActionBar().setLogo(R.drawable.ic_currency_logo);
        getSupportActionBar().setTitle("Settings");

        //progress bar
        loadingBar = new ProgressDialog(this);

        //Firebase Controls
        mYetki = FirebaseAuth.getInstance();
        currentUserID = mYetki.getCurrentUser().getUid();
        dataPath = FirebaseDatabase.getInstance().getReference(); //VERİ YOLU

        //FİREBASE -> STORAGE -> Profil Fotograflari KLASÖRÜ OLUŞTACAKTIR.
        userProfilePhotoPath = FirebaseStorage.getInstance().getReference().child("Profil Fotograflari");

        //Controls
        settingsUpdateButton = findViewById(R.id.settings_update_button);
        settingsUsername = findViewById(R.id.settings_username);
        settingsUserStatus = findViewById(R.id.settings_profile_status);
        settingsUserProfilePhoto = findViewById(R.id.settings_profile_photo);

        // GÜNCELLE BUTONUNA TIKLANDIĞINDA.
        settingsUpdateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpdateSettings();
            }
        });

        // KULLANICI BİLGİLERİNİ GETİRECEK FONKSİYONDUR.
        getUserInfo();

        // PROFİL FOTOGRAFINA TIKLANDIĞINDA - BAŞLANGIÇ
        settingsUserProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //DİREKT CROP ACTİVİTY AÇACAKTIR.
                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1,1)
                        .start(SettingsActivity.this);
            }
        });
        // PROFİL FOTOGRAFINA TIKLANDIĞINDA - BİTİŞ

    }

    // SEÇİLEN DOSYANIN UZANTISINI ALALIM - BAŞLANGIÇ
    private String getTheFileExtension(Uri uri){

        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();

        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    // SEÇİLEN DOSYANIN UZANTISINI ALALIM - BİTİŞ

    //FOTOGRAF SEÇME İŞLEMİNİ YAPACAK KOD - BAŞLANGIÇ
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK){

            //FOTOGRAF SEÇİLİYORSA - foto'yu seçebildiysek , alsın -> result değişkenine atasın.
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            photoUri = result.getUri();
            //kırptığını alıp yuvarlağın içerisine aktaracaktır.
            settingsUserProfilePhoto.setImageURI(photoUri);
        }
        else{
            //FOTOGRAF SEÇİLEMİYORSA
            Toast.makeText(this, "Photo not selected.", Toast.LENGTH_SHORT).show();
        }

    }
    //FOTOGRAF SEÇME İŞLEMİNİ YAPACAK KOD - BİTİŞ

    //KULLANICI VERİLERİNİ GETİR - BAŞLANGIÇ
    private void getUserInfo() {

        dataPath.child("Kullanicilar").child(currentUserID)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        //DataSnapshot firebase içerisindeki veritabanındaki verileri temsil ediyor.
                        //vt'de kullanıcı bilgisi yoksa profilini güncellemesine zorlayalım.
                        if (    (dataSnapshot.exists()) &&
                                (dataSnapshot.hasChild("ad") && (dataSnapshot.hasChild("resim")))){

                            String getUsername = dataSnapshot.child("ad").getValue().toString();
                            String getUserStatus = dataSnapshot.child("durum").getValue().toString();
                            String getProfilePhoto = dataSnapshot.child("resim").getValue().toString();

                            settingsUsername.setText(getUsername);
                            settingsUserStatus.setText(getUserStatus);
                            //Picasso.get().load(getProfilePhoto).into(settingsUserProfilePhoto);
                        }
                        else if ((dataSnapshot.exists())     &&      (dataSnapshot.hasChild("ad"))){

                            String getUsername = dataSnapshot.child("ad").getValue().toString();
                            String getUserStatus = dataSnapshot.child("durum").getValue().toString();

                            settingsUsername.setText(getUsername);
                            settingsUserStatus.setText(getUserStatus);
                        }
                        else{
                            Toast.makeText(SettingsActivity.this, "Please , Update profile settings", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

    }
    //KULLANICI VERİLERİNİ GETİR - BİTİŞ


    //AYARLARI GÜNCELLE - BAŞLANGIÇ
    private void UpdateSettings() {
        /*

        String getUsername = settingsUsername.getText().toString();
        String getUserStatus = settingsUserStatus.getText().toString();

        if (TextUtils.isEmpty(getUsername)){
            Toast.makeText(this, "Lütfen isminizi giriniz.", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(getUserStatus)){
            Toast.makeText(this, "Lütfen kullanıcı durumunuzu giriniz.", Toast.LENGTH_SHORT).show();
        }
        else{
            UploadPhoto();
        }

        */

        String getUsername = settingsUsername.getText().toString();
        String getUserStatus = settingsUserStatus.getText().toString();

        if (TextUtils.isEmpty(getUsername)){
            Toast.makeText(this, "Lütfen isminizi giriniz.", Toast.LENGTH_SHORT).show();
        }
        if (TextUtils.isEmpty(getUserStatus)){
            Toast.makeText(this, "Lütfen kullanıcı durumunuzu giriniz.", Toast.LENGTH_SHORT).show();
        }
        else{
            HashMap<String,String> profileMap = new HashMap<>();
            profileMap.put("uid",currentUserID);
            profileMap.put("ad",getUsername);
            profileMap.put("durum",getUserStatus);

            dataPath.child("Kullanicilar").child(currentUserID).setValue(profileMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {

                            //Aktarım tamamlandığında yapılacak

                            if (task.isSuccessful()){
                                Toast.makeText(SettingsActivity.this, "Profiliniz başarıyla güncellendi.", Toast.LENGTH_SHORT).show();

                                Intent gotoMain = new Intent(SettingsActivity.this,MainActivity.class);
                                gotoMain.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(gotoMain);
                                finish();

                            }else{
                                String errorMessage = task.getException().toString();
                                Toast.makeText(SettingsActivity.this, "Hata : " + errorMessage, Toast.LENGTH_SHORT).show();
                            }

                        }
                    });

        }




    }
    //AYARLARI GÜNCELLE - BİTİŞ


    // FOTOGRAF YÜKLEME METODU - BAŞLANGIÇ
    private void UploadPhoto() {


        ///progress bar başlangıç
        loadingBar.setTitle("Uploading");
        loadingBar.setMessage("Please Wait");
        loadingBar.setCanceledOnTouchOutside(false);
        loadingBar.show();
        //progress bar bitiş

        //VT'ye göndereceğiz.
        final StorageReference photoPath = userProfilePhotoPath.child(currentUserID + "."+ getTheFileExtension(photoUri));
        //getTheFileExtension -> fonksiyonundaki uriyi çağıralım.
        //yani -> seçilen dosyanın uzantısı.

        //Resmi yükleme görevi.
        uploadQuest = photoPath.putFile(photoUri);
        uploadQuest.continueWithTask(new Continuation() {
            @Override
            public Object then(@NonNull Task task) throws Exception {

                if (!task.isSuccessful()){
                    throw task.getException();
                }
                return photoPath.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() { // <Uri> biz ekledik
            @Override
            public void onComplete(@NonNull Task<Uri> task) { // <Uri> biz ekledik

                //Yükleme işi tamamlandığında.
                if (task.isSuccessful()){

                    //Başarılıysa
                    Uri downloadUri = task.getResult();
                    myUri = downloadUri.toString();

                    DatabaseReference dataPath = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
                    //GönderiID
                    String shipmentID = dataPath.push().getKey();

                    String getUsername = settingsUsername.getText().toString();
                    String getUserStatus = settingsUserStatus.getText().toString();

                    HashMap<String,String> profileMap = new HashMap<>();
                    profileMap.put("uid",shipmentID);
                    profileMap.put("ad",getUsername);
                    profileMap.put("durum",getUserStatus);
                    profileMap.put("resim",myUri);

                    //gönderelim
                    dataPath.child(currentUserID).setValue(profileMap);

                    loadingBar.dismiss();

                }else{
                    //Başarısızsa
                    String error = task.getException().toString();
                    Toast.makeText(SettingsActivity.this, "Error : " + error, Toast.LENGTH_SHORT).show();

                    loadingBar.dismiss();
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(SettingsActivity.this,"Error : " + e.getMessage(), Toast.LENGTH_SHORT).show();
                loadingBar.dismiss();
            }
        });

    }
    // FOTOGRAF YÜKLEME METODU - BİTİŞ
}
