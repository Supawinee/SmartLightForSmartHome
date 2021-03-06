package com.example.supawinee.smartlightforsmarthome;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.widget.SeekBar.OnSeekBarChangeListener;


import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.OpacityBar;
import com.larswerkman.holocolorpicker.SVBar;

import io.netpie.microgear.Microgear;
import io.netpie.microgear.MicrogearEventListener;

public class NormalPage extends AppCompatActivity implements View.OnClickListener , ColorPicker.OnColorChangedListener{
    /////////////////// NETPIE /////////////////////////////////////////
    private Microgear microgear = new Microgear(this);
    private String alias = "MobileApp";


    private Button submit;

    private ColorPicker picker;
    private SVBar svBar;



    //  Shared Preferences
    SharedPreferences sp;




    ///////////////////////////NETPIE///////////////////////
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Bundle bundle = msg.getData();
            String string = bundle.getString("myKey");
            TextView myTextView =
                    (TextView)findViewById(R.id.textView);
            myTextView.setText(string);
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_normal_page);


        //  Shared Preferences
        sp = getSharedPreferences("App_Setting", Context.MODE_PRIVATE);
        String APPID_SP = sp.getString("AppID", "");
        String KEY_SP = sp.getString("key", "");
        String SECRET_SP = sp.getString("Secret", "");



        submit = (Button)findViewById(R.id.btn_submit);
        submit.setOnClickListener(this);

        ///////////////////////////NETPIE///////////////////////
        MicrogearCallBack callback = new MicrogearCallBack();
        microgear.resettoken();
        microgear.connect(APPID_SP,KEY_SP,SECRET_SP,alias);
        microgear.setCallback(callback);
        microgear.subscribe("Topictest");
        microgear.subscribe("/chat");


        ////////////////////////// COLOR PICKER /////////////////
        picker = (ColorPicker) findViewById(R.id.picker);
        svBar = (SVBar) findViewById(R.id.svbar);



        picker.addSVBar(svBar);
        picker.setOnColorChangedListener(this);


    }


    //////////////////////// WTF-NETPIE ////////////////////////////////////

    protected void onDestroy() {
        super.onDestroy();
        microgear.disconnect();
    }

    protected void onResume() {
        super.onResume();
        microgear.bindServiceResume();
    }
    //////////////////////// WTF-NETPIE ////////////////////////////////////


    /////////////////////////// SUBMIT - BUTTON /////////////////////////
    @Override
    public void onClick(View v) {
        //text.setTextColor(picker.getColor());
        int red = Color.red(picker.getColor());
        int green = Color.green(picker.getColor());
        int blue = Color.blue(picker.getColor());
        String color_picker = red + ":" + green +  ":" + blue;
        //text.setText(color_picker);
        picker.setOldCenterColor(picker.getColor());

        //  Shared Preferences
        String Alias_SP = sp.getString("Alias", "");

        microgear.chat(Alias_SP, "cc:" + color_picker);
        Log.i("Color is ", color_picker);




    }

    @Override
    public void onColorChanged(int color) {
        /*

        ///////// FOR REAL TIME COLOR CHANGE ///////

        picker.setOldCenterColor(picker.getColor());
        int red = Color.red(picker.getColor());
        int green = Color.green(picker.getColor());
        int blue = Color.blue(picker.getColor());
        String color_picker = red + ":" + green +  ":" + blue;
        microgear.chat("switch","cc:" + color_picker);
        Log.i("Color is ", color_picker);                   */

    }

    class MicrogearCallBack implements MicrogearEventListener{
        @Override
        public void onConnect() {
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("myKey", "Connected");
            msg.setData(bundle);
            handler.sendMessage(msg);
            Log.i("Connected","Now I'm connected with netpie");
            //textStatus.setText("ONLINE!");
            //textStatus.setTextColor(Color.parseColor("#7CFC00"));
        }

        @Override
        public void onMessage(String topic, String message) {
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("myKey", topic+" : "+message);
            msg.setData(bundle);
            handler.sendMessage(msg);
            Log.i("Message",topic+" : "+message);
        }

        @Override
        public void onPresent(String token) {
            Log.i("present","New friend Connect :"+token);
        }

        @Override
        public void onAbsent(String token) {
            Log.i("absent","Friend lost :"+token);
        }

        @Override
        public void onDisconnect() {
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("myKey", "Disconnected");
            msg.setData(bundle);
            handler.sendMessage(msg);
            Log.i("disconnect","Disconnected");
            //textStatus.setText("OFFLINE!");
            //textStatus.setTextColor(Color.parseColor("#B22222"));
        }

        @Override
        public void onError(String error) {
            Message msg = handler.obtainMessage();
            Bundle bundle = new Bundle();
            bundle.putString("myKey", "Exception : "+error);
            msg.setData(bundle);
            handler.sendMessage(msg);
            Log.i("exception","Exception:"+error);
            //textStatus.setText("OFFLINE!");
            //textStatus.setTextColor(Color.parseColor("#B22222"));
        }
    }
}

