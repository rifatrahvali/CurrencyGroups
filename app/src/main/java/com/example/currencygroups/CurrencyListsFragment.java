package com.example.currencygroups;


import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrencyListsFragment extends Fragment {

    private TextView addCurrencyGroup,settings,exit , fragment_currency_converter;
    private View view;

    //Firebase
    private FirebaseUser availableUser;
    private FirebaseAuth mYetki;
    //VT'nin içerisinde kök dizinin kullanıcılar yolu - gruplar için
    //          (   "Kullanicilar"   )
    private DatabaseReference usersReferance;


    public CurrencyListsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_currency_lists, container, false);



        addCurrencyGroup = view.findViewById(R.id.fragment_add_currency_group);
        settings = view.findViewById(R.id.fragment_settings);
        exit = view.findViewById(R.id.fragment_exit);
        fragment_currency_converter = view.findViewById(R.id.fragment_currency_converter);

        //Firebase
        mYetki=FirebaseAuth.getInstance();
        availableUser = mYetki.getCurrentUser();
        usersReferance = FirebaseDatabase.getInstance().getReference();


        addCurrencyGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newGroupRequest();
            }
        });


        fragment_currency_converter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToCurrencyConverter = new Intent(getContext(),CurrencyConverterActivity.class);
                startActivity(goToCurrencyConverter);
            }
        });



        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent goToSettings = new Intent(getContext(),SettingsActivity.class);
                startActivity(goToSettings);
            }
        });

        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mYetki.signOut();

                Intent goToSignInActivity = new Intent(getContext(),SignInActivity.class);
                startActivity(goToSignInActivity);
                goToSignInActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

            }
        });



        // Inflate the layout for this fragment
        return view;
    }

    private void newGroupRequest() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext(),R.style.AlertDialog);
        builder.setTitle("Enter group name.");
        final EditText groupNameArea = new EditText(getContext());

        groupNameArea.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(3), //maximum 3 karakter
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                        String blockCharacterSet = "~#^|$%&+*!@/()-'\":;,?{}=!$^';,?×÷<>{}€£¥₩%~`¤♡♥_|《》¡¿°•○●□■◇◆♧♣▲▼▶◀↑↓←→☆★▪:-);-):-D:-(:'(:O 1234567890";
                        if (source != null && blockCharacterSet.contains(("" + source))) {
                            return "";
                        }
                        return null;
                    }
                },
                new InputFilter.AllCaps(), //girilen karakterleri büyük harfe çevir
        });

        groupNameArea.setHint("Example : USD ");

        builder.setView(groupNameArea);

        builder.setPositiveButton("Create", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String groupName = groupNameArea.getText().toString();

                if (TextUtils.isEmpty(groupName)){
                    Toast.makeText(getContext(), "Group name cannot be left blank.", Toast.LENGTH_LONG).show();
                }else{
                    //YENİ GRUP OLUŞTUR-GRUP' U OLUŞTURUP FİREBASE'E GÖNDERİR.
                    CreateNewGroup(groupName);
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    private void CreateNewGroup(final String groupName) {
        usersReferance.child("Gruplar").child(groupName).setValue("")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //grup oluşturma görevimiz

                        if (task.isSuccessful()){
                            Toast.makeText(getContext(), "The group named "+ groupName +" was created.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

}
