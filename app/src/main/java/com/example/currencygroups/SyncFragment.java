package com.example.currencygroups;


import android.os.AsyncTask;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;


/**
 * A simple {@link Fragment} subclass.
 */
public class SyncFragment extends Fragment {

    private View view;
    private TextView tv_chf,tv_cad,tv_usd,tv_jpy,tv_try;
    private Button buttonGetRates;




    public SyncFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_sync, container, false);

        tv_chf = view.findViewById(R.id.tv_chf);
        tv_cad = view.findViewById(R.id.tv_cad);
        tv_usd = view.findViewById(R.id.tv_usd);
        tv_jpy = view.findViewById(R.id.tv_jpy);
        tv_try = view.findViewById(R.id.tv_try);


        buttonGetRates = view.findViewById(R.id.btn_getRates);
        buttonGetRates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getRates(view);
            }
        });

        return view;
    }

    public void getRates(View view){

        DownloadData downloadData = new DownloadData();
        try {

            String url = "http://data.fixer.io/api/latest?access_key=fd7a716ab35850970f50f178844062fc";
            downloadData.execute(url);

        }catch (Exception e){

        }

    }
    private class DownloadData extends AsyncTask<String,Void,String>{

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

            //System.out.println("Alınan Data" + s);

            try {

                JSONObject jsonObject = new JSONObject(s);
                String base = jsonObject.getString("base");
                //System.out.println("base : " + base);
                String rates = jsonObject.getString("rates");
                //System.out.println("rates : " + rates);

                JSONObject jsonObject1 = new JSONObject(rates);
                String turkishlira = jsonObject1.getString("TRY");
                //System.out.println("TRY : " + turkishlira);
                tv_try.setText("TRY : " + turkishlira);

                String cad = jsonObject1.getString("CAD");
                tv_cad.setText("CAD : " + cad);

                String usd = jsonObject1.getString("USD");
                tv_usd.setText("USD : " + usd);

                String jpy = jsonObject1.getString("JPY");
                tv_jpy.setText("JPY : " + jpy);

                String chf = jsonObject1.getString("CHF");
                tv_chf.setText("CHF : " + chf);

            }catch (Exception e){

            }
        }
    }

}
