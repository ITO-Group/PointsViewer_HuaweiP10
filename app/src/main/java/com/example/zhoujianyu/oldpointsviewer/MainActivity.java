package com.example.zhoujianyu.oldpointsviewer;

import android.app.DownloadManager;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class MainActivity extends AppCompatActivity {
    public MyView mview;
    public RequestQueue myQueue;
    public Button switch_button;
    String button_text;
    static final int ROW_NUM = 28;
    static final int COL_NUM = 16;
    static final int window_size=2;
    static final int THR = 50;
    TextView tv;
    int capaData[][][] = new int[ROW_NUM][COL_NUM][window_size];
    String last="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myQueue = Volley.newRequestQueue(this);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.sample_my_view);
        mview = findViewById(R.id.my_view);
        tv=findViewById(R.id.textView);
        switch_button = findViewById(R.id.button);
        button_text = switch_button.getText().toString();
        switch_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(button_text.equals("start")){button_text = "end";switch_button.setText(button_text);}
                else{button_text ="start";switch_button.setText(button_text);}
            }
        });

        // get screen size
        Point size = new Point();
        getWindowManager().getDefaultDisplay().getRealSize(size);
        int screenWidth = size.x;
        int screenHeight = size.y;
        mview.screenWidth = screenWidth;
        mview.screenHeight = screenHeight;
        mview.init();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        refresh();
                        Log.e("bug","here");
                    }catch (Exception e){

                    }
                }
            }
        }).start();
    }

    public void sendCloud(final String data_str,String server_ip,String port){
        /**
         * send the str to a remote server
         */
        String url = "http://"+server_ip+":"+"5000"+"/";
        StringRequest req = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //nothing here
                final String res = response;
                if(!response.equals("[]")){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Log.e("bug",res);
                            tv.setText(res);
                        }
                    });
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Log.e("bug",error.toString());
            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap data = new HashMap<String,String>();
                data.put("data",data_str);
                return data;
            }
        };
//        req.setRetryPolicy(new DefaultRetryPolicy(1000, 0, 1.0f));
        myQueue.add(req);
    }

    public ArrayList<String> getRawCapacityData()throws Exception{
        String line = "";
        ArrayList<String> rawData = new ArrayList<>();
        String command[] = {"aptouch_daemon_debug", "diffdata"};
        Process process = new ProcessBuilder(new String[] {"aptouch_daemon_debug", "diffdata"}).start();
        InputStream procInputStream = process.getInputStream();
        InputStreamReader reader = new InputStreamReader(procInputStream);
        BufferedReader bufferedreader = new BufferedReader(reader);
        while ((line = bufferedreader.readLine()) != null) {
            rawData.add(line);
        }
        return rawData;
    }

    public void refresh()throws Exception{
        ArrayList<String> rawData = getRawCapacityData();
        short data[] = new short[28*16];
        short tmp[][] = new short[16][28];
        for(int i = 0;i<rawData.size();i++){
            StringTokenizer t = new StringTokenizer(rawData.get(i));
            int j = 0;
            while(t.hasMoreTokens()){
                tmp[i][j++] = Short.parseShort(t.nextToken());
            }
        }
        int k = 0;

        for(int i = 0;i<28;i++){
            for(int j = 0;j<16;j++){
                capaData[i][j][0] = capaData[i][j][1];
            }
        }
        boolean touched = false;
        for(int j = 0;j<28;j++){
            for(int i = 0;i<16;i++){
                capaData[j][i][1] = tmp[i][j];
                if(capaData[j][i][1]-capaData[j][i][0]>THR) {
//                    Log.e("bug",Integer.toString(j)+","+Integer.toString(i));
                    touched=true;
                }
                data[k++] = tmp[i][j];
            }
        }

        String capaString = "";
        for(int i = 0;i<data.length;i++){
            capaString+=(" "+Short.toString(data[i]));
        }
        if(button_text.equals("end")){
            String timeStamp = new SimpleDateFormat("yyyy/MM/dd-HH:mm:ss.SSS").format(new Date());
            String tag=touched?"1":"0";
            String mip="10.19.11.170";
            String hip = "10.19.35.196";
            //sendCloud(timeStamp+" "+tag+capaString+"\n",hip,"5000");
        }
        mview.updateData(data);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mview.invalidate();
            }
        });
    }
}
