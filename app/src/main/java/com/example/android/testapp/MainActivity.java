package com.example.android.testapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity {

            String host = "192.168.1.46";
            String user = "idiota";
            String password = "peroidiota";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Encuentra el EditText.
        EditText field = (EditText) findViewById(R.id.text);
        Editable editable = field.getText();
        final String text = editable.toString();

        // Encuentra la textview que manda el comando.
        Button comando = (Button) findViewById(R.id.mandar_comando);

        // Se coloca un onClickListener en ella.
        comando.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Se conecta al servidor SSH.

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
                    ((ChannelExec)channel).setCommand(text);
                    channel.setInputStream(null);
                    ((ChannelExec)channel).setErrStream(System.err);

                    InputStream in = channel.getInputStream();

                        StringBuilder output = new StringBuilder();
                        if (in != null) {
                            InputStreamReader inputStreamReader = new InputStreamReader(in, Charset.forName("UTF-8"));
                            BufferedReader reader = new BufferedReader(inputStreamReader);
                            String line = reader.readLine();
                            while (line != null){
                                output.append(line);
                                line = reader.readLine();
                            }
                        }
                        output.toString();


                   /* channel.connect();
                    byte[] tmp = new byte[1024];
                    while (true){
                        while (in.available()>0){
                            int i = in.read(tmp, 0, 1024);
                            if (i<0)break;
                            System.out.print(new String(tmp, 0, 1));
                        }
                        if (channel.isClosed()){
                            System.out.println("exit status: " + channel.getExitStatus());
                            break;
                        }
                        try {Thread.sleep(1000);}catch (Exception ee){}
                    }*/
                    channel.disconnect();
                    session.disconnect();
                    System.out.println("DONE");

                    // Busca la TextView que mostrará la respuesta del servidor.
                    TextView responseTextView = (TextView) findViewById(R.id.recibir_comando);

                    // Le asigna el texto recibido del server.
                    responseTextView.setText(output);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
    }
}
