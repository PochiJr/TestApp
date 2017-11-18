package com.example.android.testapp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

            String host = "192.168.1.35";
            String user = "idiota";
            String password = "peroidiota";

    private class connectAsyncTask extends AsyncTask<String, Void, String>{
        @Override
        protected String doInBackground (String... cmd){
            // Se conecta al servidor SSH.
            StringBuilder output = new StringBuilder();
            try {
                java.util.Properties config = new java.util.Properties();
                config.put("StrictHostKeyChecking", "no");
                JSch jSch = new JSch();
                Session session = jSch.getSession(user, host, 22);
                session.setPassword(password);
                session.setConfig(config);
                session.connect();
                System.out.println("Connected");

                Channel channel = session.openChannel("exec");
                ((ChannelExec)channel).setCommand(cmd[0]);
                channel.setInputStream(null);
                ((ChannelExec)channel).setErrStream(System.err);

                InputStream in = channel.getInputStream();
                channel.connect();

                if (in != null) {
                    InputStreamReader inputStreamReader = new InputStreamReader(in, Charset.forName("UTF-8"));
                    BufferedReader reader = new BufferedReader(inputStreamReader);
                    String line = reader.readLine();
                    while (line != null){
                        output.append(line);
                        line = reader.readLine();
                    }
                }
                channel.disconnect();
                session.disconnect();
                System.out.println("DONE");

            } catch (Exception e){
                e.printStackTrace();
            }
            return output.toString();
        }

        @Override
        protected void onPostExecute (String response){
            TextView responseTextView = (TextView) findViewById(R.id.recibir_comando);
            responseTextView.setText(response);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        // Encuentra la textview que manda el comando.
        ImageView comando = (ImageView) findViewById(R.id.mandar_comando);

        // Detalle de que se remarcque en oscuro al presionarse
        comando.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        ImageView view = (ImageView) v;
                        //overlay is black with transparency of 0x77 (119)
                        view.getDrawable().setColorFilter(0x77000000, PorterDuff.Mode.SRC_ATOP);
                        view.invalidate();
                        break;
                    }
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL: {
                        ImageView view = (ImageView) v;
                        //clear the overlay
                        view.getDrawable().clearColorFilter();
                        view.invalidate();
                        break;
                    }
                }
                return false;
            }
        });
        // Se coloca un onClickListener en ella.
        comando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Encuentra el EditText.
                EditText field = (EditText) findViewById(R.id.text);
                Editable editable = field.getText();
                String text = editable.toString();

                connectAsyncTask task = new connectAsyncTask();
                task.execute(text);
            }
        });
    }


    @SuppressLint("SetTextI18n")
    @Override
    protected void onStart() {
        super.onStart();
        if (!isPass()) {
            Intent intent = new Intent(this, LockActivity.class);
            startActivity(intent);
        } else {
            Log.v("MainActivity", "Funciona, has metido la contraseña correcta");
        }
    }

    // Definimos "Is_pass"
    private boolean isPass() {
        SharedPreferences prefs = getSharedPreferences("PASS_CODE", MODE_PRIVATE);
        return prefs.getBoolean("is_pass", false);
    }

   // Al cerrarse la App la key "is_pass" se convierte en negativa para que al salir de la App y
   // entrar de nuevo te pidan la contraseña.
    @Override
    protected void onStop() {
        super.onStop();

        SharedPreferences.Editor editor = getSharedPreferences("PASS_CODE", MODE_PRIVATE).edit();
        editor.putBoolean("is_pass", false);
        editor.apply();
    }
}
