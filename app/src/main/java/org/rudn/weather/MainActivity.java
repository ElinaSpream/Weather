package org.rudn.weather;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button btn1;
    private TextView res;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.editText);
        btn1 = findViewById(R.id.btn1);
        res = findViewById(R.id.res);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().trim().equals(""))
                    Toast.makeText(MainActivity.this, R.string.hint_text, Toast.LENGTH_LONG).show();
                else {
                    String city = editText.getText().toString();
                    String key = "c6d2b9891a65f6411d117147b4c1446a";
                    String url = "https://api.openweathermap.org/data/2.5/weather?q=" + city + "&appid=" + key + "&units=metric&lang=ru";

                    new GetData().execute(url);
                    //http://api.openweathermap.org/data/2.5/weather?q=Moscow&appid=c6d2b9891a65f6411d117147b4c1446a&units=metric&lang=ru
                }
            }
        });
    }

    private class GetData extends AsyncTask<String, String, String> {

        protected void onPreExecute(String s) {
            super.onPreExecute();
            res.setText("Please wait for a while...");
        }

        @Override
        protected String doInBackground(String... strings) {
            HttpURLConnection connection = null;
            BufferedReader reader = null;

            try {
                URL url = new URL(strings[0]);
                connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream stream = connection.getInputStream();
                reader = new BufferedReader(new InputStreamReader(stream));

                StringBuffer buffer = new StringBuffer();
                String line = "";

                while ((line = reader.readLine()) != null)
                    buffer.append(line).append("\n");

                return buffer.toString();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if(connection != null)
                    connection.disconnect();

                if(reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

            }
            return null;
        }

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            try {
                JSONObject object = new JSONObject(result);
                res.setText("Температура: " + object.getJSONObject("main").getDouble("temp") + "\n" + "Ощущается как: " + object.getJSONObject("main").getDouble("feels_like"));

            } catch (JSONException e) {
                e.printStackTrace();
            }



        }
    }
}