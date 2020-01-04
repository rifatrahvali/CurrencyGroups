package com.example.currencygroups;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class CurrencyGroupsFragment extends Fragment {


    //NOT : View' LAR İÇİN Adapter' LAR KULLANILIR.
    //NOT : //VERİ YAZARKEN ONCOMPLATELİSTENER KULLANILIR
    //        //VERİ ÇEKERKEN ADDVALUEEVENTLİSTENER KULLANILIR.

    private View groupFragmentView ;
    private ListView currencyGroupListView;
    private ArrayAdapter<String> arrayAdapter; //Listview'deki verileri bağlamak için
    private ArrayList<String> groupLists = new ArrayList<>(); //String olarak FİREBASE'den alıp listview'a atayacağız.

    //Firebase Definitions
    private DatabaseReference groupPath;

    public CurrencyGroupsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        groupFragmentView = inflater.inflate(R.layout.fragment_currency_groups, container, false);

        //Firebase Definitions
        groupPath = FirebaseDatabase.getInstance().getReference().child("Gruplar");

        //Control Definitions
        currencyGroupListView=groupFragmentView.findViewById(R.id.currency_group_listview);
        arrayAdapter = new ArrayAdapter<String>(getContext(),android.R.layout.simple_list_item_1,groupLists);
        currencyGroupListView.setAdapter(arrayAdapter);

        getGroupsAndShow();

        currencyGroupListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String currentGroupName = parent.getItemAtPosition(position).toString();

                Intent goToGroupChatActivity = new Intent(getContext(),GroupChatActivity.class);
                goToGroupChatActivity.putExtra("grupAdi",currentGroupName);
                startActivity(goToGroupChatActivity);

            }
        });

        return groupFragmentView;
    }

    private void getGroupsAndShow() {
        groupPath.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                //VERİYİ GÖNDERİRKEN HASHMAP-----BURADA HASHSET KULLANACAĞIZ
                Set<String> set = new HashSet<>();

                //VERİLERİ ÇEK
                Iterator iterator = dataSnapshot.getChildren().iterator(); //satır satır yineleyerek verileri çeker.
                while (iterator.hasNext()){
                    //ANAHTARI AL
                    //TEK TEK GRUP ADLARINI.
                    set.add(((DataSnapshot)iterator.next()).getKey());
                }
                groupLists.clear();
                groupLists.addAll(set);
                arrayAdapter.notifyDataSetChanged();//Eş zamanlı yenileme

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

}
