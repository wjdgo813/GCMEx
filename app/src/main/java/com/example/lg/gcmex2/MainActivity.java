package com.example.lg.gcmex2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.MulticastResult;
import com.google.android.gcm.server.Sender;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    EditText messageInput;
    TextView messageOutput;

    Sender sender;

    Handler handler = new Handler();

    private Random random ;

    private int TTLTime = 60;

    private	int RETRY = 3;
    ArrayList<String> idList = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sender = new Sender(GCMInfo.GOOGLE_API_KEY);

        messageInput = (EditText) findViewById(R.id.messageEdit);

        messageOutput = (TextView) findViewById(R.id.textView);

        Button registerButton = (Button) findViewById(R.id.registerBtn);
        registerButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    // �⑤쭚 �깅줉�섍퀬 �깅줉 ID 諛쏄린
                    registerDevice();

                } catch(Exception ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button sendButton = (Button) findViewById(R.id.sendBtn);
        sendButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String data = messageInput.getText().toString();

                sendToDevice(data);
            }
        });

        // �명뀗�몃� �꾨떖諛쏅뒗 寃쎌슦
        Intent intent = getIntent();
        if (intent != null) {
            processIntent(intent);
        }

    }
    private void registerDevice() {

        RegisterThread registerObj = new RegisterThread();
        registerObj.start();

    }

    private void sendToDevice(String data) {

        SendThread thread = new SendThread(data);
        thread.start();

    }

    class RegisterThread extends Thread {
        public void run() {

            try {
                GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(getApplicationContext());
                String regId = gcm.register(GCMInfo.SENDER_ID);
                println("�몄떆 �쒕퉬�ㅻ� �꾪빐 �⑤쭚�� �깅줉�덉뒿�덈떎.");
                println("�깅줉 ID : " + regId);

                // �깅줉 ID 由ъ뒪�몄뿉 異붽� (�꾩옱�� 1媛쒕쭔)
                idList.clear();
                idList.add(regId);

            } catch(Exception ex) {
                ex.printStackTrace();
            }

        }
    }

    private void println(String msg) {
        final String output = msg;
        handler.post(new Runnable() {
            public void run() {
                Log.d("MainActivity", output);
                Toast.makeText(getApplicationContext(), output, Toast.LENGTH_LONG).show();
            }
        });
    }


    class SendThread extends Thread {
        String data;

        public SendThread(String inData) {
            data = inData;
        }

        public void run() {

            try {
                sendText(data);
            } catch(Exception ex) {
                ex.printStackTrace();
            }

        }

        public void sendText(String msg)
                throws Exception
        {

            if( random == null){
                random = new Random(System.currentTimeMillis());
            }

            String messageCollapseKey = String.valueOf(Math.abs(random.nextInt()));

            try {
                // �몄떆 硫붿떆吏� �꾩넚�� �꾪븳 硫붿떆吏� 媛앹껜 �앹꽦 諛� �섍꼍 �ㅼ젙
                Message.Builder gcmMessageBuilder = new Message.Builder();
                gcmMessageBuilder.collapseKey(messageCollapseKey).delayWhileIdle(true).timeToLive(TTLTime);
                gcmMessageBuilder.addData("type","text");
                gcmMessageBuilder.addData("command", "show");
                gcmMessageBuilder.addData("data", URLEncoder.encode(data, "UTF-8"));

                Message gcmMessage = gcmMessageBuilder.build();

                // �щ윭 �⑤쭚�� 硫붿떆吏� �꾩넚 �� 寃곌낵 �뺤씤
                MulticastResult resultMessage = sender.send(gcmMessage, idList, RETRY);
                String output = "GCM �꾩넚 硫붿떆吏� 寃곌낵 => " + resultMessage.getMulticastId()
                        + "," + resultMessage.getRetryMulticastIds() + "," + resultMessage.getSuccess();

                println(output);

            } catch(Exception ex) {
                ex.printStackTrace();

                String output = "GCM 硫붿떆吏� �꾩넚 怨쇱젙�먯꽌 �먮윭 諛쒖깮 : " + ex.toString();
                println(output);

            }

        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d("MainActivity", "onNewIntent() called.");

        processIntent(intent);

        super.onNewIntent(intent);
    }

    /**
     * �섏떊�먮줈遺��� �꾨떖諛쏆� Intent 泥섎━
     *
     * @param intent
     */
    private void processIntent(Intent intent) {
        String from = intent.getStringExtra("from");
        if (from == null) {
            Log.d("MainActivity", "from is null.");
            return;
        }

        String command = intent.getStringExtra("command");
        String type = intent.getStringExtra("type");
        String data = intent.getStringExtra("data");

        println("DATA : " + command + ", " + type + ", " + data);
        messageOutput.setText("[" + from + "]濡쒕��� �섏떊�� �곗씠�� : " + data);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}


