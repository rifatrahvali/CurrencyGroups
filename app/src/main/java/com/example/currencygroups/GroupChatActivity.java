package com.example.currencygroups;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;

public class GroupChatActivity extends AppCompatActivity {

    private Toolbar mainToolbar;//Vidget
    private TextView showGroupChatText , group_chat_currency_info;
    private ImageButton sendMessageButton;
    private EditText userMessageEntry;
    private ScrollView mainScrollView;

    //Intent değişkeni
    private String currentGroupName , activeUserID , activeUsername , activeDate , activeTime;

    //Firebase

    private FirebaseAuth mYetki;
    private DatabaseReference userPath , groupNamePath , groupMessageKeyPath;// YOLLAR

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_chat);

        //Get Intent
        currentGroupName = getIntent().getExtras().get("grupAdi").toString();

        //Firebase definitions
        mYetki = FirebaseAuth.getInstance();
        activeUserID = mYetki.getCurrentUser().getUid();
        userPath = FirebaseDatabase.getInstance().getReference().child("Kullanicilar");
        groupNamePath = FirebaseDatabase.getInstance().getReference().child("Gruplar").child(currentGroupName);


        //Control definitions
        mainToolbar = findViewById(R.id.group_chat_bar_layout);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle(currentGroupName);

        group_chat_currency_info = findViewById(R.id.group_chat_currency_info);

        sendMessageButton = findViewById(R.id.group_chat_send_message_button);
        userMessageEntry = findViewById(R.id.group_chat_user_message_entry);

        showGroupChatText = findViewById(R.id.group_chat_show_text);

        mainScrollView = findViewById(R.id.group_chat_scroll_view);

        //KULLANICI BİLGİSİ ALALIM
        getUserInfo();



        sendMessageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveMessageToDatabase();

                //ilgili edittext'i boşaltalım.
                userMessageEntry.setText("");

                mainScrollView.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
        //mesajları göster
        groupNamePath.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()){
                    //mesajları göster
                    showMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                if (dataSnapshot.exists()){
                    //mesajları göster
                    showMessages(dataSnapshot);
                }

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DownloadData downloadData = new DownloadData();
        try {
            String url = "http://data.fixer.io/api/latest?access_key=fd7a716ab35850970f50f178844062fc";
            downloadData.execute(url);

        }catch (Exception e){
        }
    }

    private class DownloadData extends AsyncTask<String,Void,String> {

        @Override
        protected String doInBackground(String... strings) {

            String result = "";
            URL url; //web adresslerini url olarak alacağız
            HttpURLConnection httpURLConnection;

            //App'imiz çökmemesi için.
            try {

                //URL' imizi nereden alacağız ?
                url = new URL(strings[0]);
                httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);

                int data = inputStreamReader.read();
                while (data>0){

                    //alınan datayı karakter karakter alıp result'a atayalım.
                    char character = (char) data;
                    result += character; //her gelecek karakteri result'a tek tek ekle.

                    data = inputStreamReader.read(); //Bir sonraki karaktere geçelim.

                }
                return result;
            }catch (Exception e){
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {


                JSONObject jsonObject = new JSONObject(s);
                String base = jsonObject.getString("base");
                String rates = jsonObject.getString("rates");

                JSONObject jsonObject1 = new JSONObject(rates);
                String currencyInfoInGroup = jsonObject1.getString(currentGroupName);
                group_chat_currency_info.setText(currentGroupName+ " : " + currencyInfoInGroup);
            }catch (Exception e){

            }
        }
    }

    private void showMessages(DataSnapshot dataSnapshot) {

        Iterator iterator = dataSnapshot.getChildren().iterator(); //SATIR SATIR YINELEYEREK İŞLEM YAPACAĞIZ.
        while (iterator.hasNext()){
            String chatName = (String)((DataSnapshot)iterator.next()).getValue();
            String chatMessage = (String)((DataSnapshot)iterator.next()).getValue();
            String chatDate = (String)((DataSnapshot)iterator.next()).getValue();
            String chatTime = (String)((DataSnapshot)iterator.next()).getValue();

            showGroupChatText.append(chatName + "\n" + chatMessage + "\n" + chatDate + " / " + chatTime + "\n\n");

            mainScrollView.fullScroll(ScrollView.FOCUS_DOWN);
        }

    }

    private void saveMessageToDatabase() {

        String message = userMessageEntry.getText().toString();
        String messageKey = groupNamePath.push().getKey();

        if (TextUtils.isEmpty(message)){
            Toast.makeText(this, "Message area cannot be left blank.", Toast.LENGTH_LONG).show();
        }else {

            //MESAJIN TARİHİNİ KAYDEDELİM.
            Calendar calendarForDate = Calendar.getInstance();
            SimpleDateFormat activeDateFormat = new SimpleDateFormat("MMM dd, yyyy");
            //
            activeDate = activeDateFormat.format(calendarForDate.getTime());

            Calendar timeForDate = Calendar.getInstance();
            SimpleDateFormat activeTimeFormat = new SimpleDateFormat("hh:mm:ss a");
            activeTime = activeTimeFormat.format(timeForDate.getTime());

            //VERİTABANINA GÖNDERELİM
            HashMap<String,Object> groupMessageKey = new HashMap<>();
            groupNamePath.updateChildren(groupMessageKey);

            groupMessageKeyPath = groupNamePath.child(messageKey); //Grup -> Mevcut gruptaki -> hangi grubuna altına gideceğini belirler.
            HashMap<String,Object> messageInfoMap = new HashMap<>();

            messageInfoMap.put("ad",activeUsername);
            messageInfoMap.put("mesaj",message);
            messageInfoMap.put("tarih",activeDate);
            messageInfoMap.put("zaman",activeTime);

            groupMessageKeyPath.updateChildren(messageInfoMap);

        }

    }

    private void getUserInfo() {

        //Uid'e gidecek adı varmı varsa al.-burası fotograf benzerleri ile geliştirilebilir.
        userPath.child(activeUserID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    activeUsername = dataSnapshot.child("ad").getValue().toString();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
