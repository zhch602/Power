package tech.zhangchi.power;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Calendar;

public class Tab1 extends Fragment {

    private int ymdhi;
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int arrIndex = 0;
    private int minute_;
    private int hour_;
    private int day_;
    private int month_;
    private int year_;
    private int temp;
    private Button button_on;
    private Button button_off;
    private RadioGroup rg1;
    private RadioButton power1_1;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout1, null);

        Calendar c = Calendar.getInstance();
        year = c.get(Calendar.YEAR);
        month = c.get(Calendar.MONTH);
        day = c.get(Calendar.DAY_OF_MONTH);
        hour = c.get(Calendar.HOUR_OF_DAY);
        minute = c.get(Calendar.MINUTE);
        ymdhi = (year % 100) * 100000000 + (month + 1) * 1000000 + day * 10000 + hour * 100 + minute;

        view.findViewById(R.id.button_time).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), TimeActivity.class);
                startActivityForResult(intent, 0);
            }
        });
//        view.findViewById(R.id.button_choose1).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                powerChoice(v);
//            }
//        });
        button_on = (Button) view.findViewById(R.id.button_on);
        button_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_post("http://222.210.113.185/Power/switch.php", "1", String.valueOf(ymdhi), String.valueOf(arrIndex), v);
            }
        });
        button_off = (Button) view.findViewById(R.id.button_off);
        button_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch_post("http://222.210.113.185/Power/switch.php", "0", String.valueOf(ymdhi), String.valueOf(arrIndex), v);
            }
        });
        power1_1 = (RadioButton) view.findViewById(R.id.power1_1);
        rg1 = (RadioGroup) view.findViewById(R.id.rg1);
        rg1.check(power1_1.getId());
        rg1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == power1_1.getId()){
                    arrIndex = 0;
                }else
                    arrIndex = 1;
            }
        });
        Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                // handler接收到消息后就会执行此方法
                if (msg.what == 0){
                    button_off.setEnabled(false);
                    button_off.setBackgroundResource(R.drawable.Pink);
                    button_on.setEnabled(true);
                    button_on.setBackgroundResource(R.drawable.lightgreen);
                }
                else {
                    button_off.setEnabled(true);
                    button_off.setBackgroundResource(R.drawable.lightgreen);
                    button_on.setEnabled(false);
                    button_on.setBackgroundResource(R.drawable.Pink);
                }
            }
        };
        getStatus("http://222.210.113.185/Power/status.php", handler);

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0 && resultCode == 0) {
            Bundle da = data.getExtras();
            ymdhi = da.getInt("ymdhi");
            minute_ = ymdhi%100;
            hour_ = (ymdhi/100)%100;
            day_ = (ymdhi/10000)%100;
            month_ = (ymdhi/1000000)%100;
            year_ = (ymdhi/100000000)%100;
            Snackbar.make(getView(), "20" + year_ + "年" + month_ + "月" + day_ + "日" +hour_ + "分" + minute_ + "秒", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
        }
    }

    public void switch_post(String str_url, String str_switch, String str_ymdhi, String arr_index, final View view) {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                try {
                    URL url = new URL(params[0]);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setDoOutput(true);
                    urlConnection.setDoInput(true);
                    urlConnection.setRequestMethod("POST");
                    OutputStreamWriter osw = new OutputStreamWriter(urlConnection.getOutputStream(), "utf-8");
                    BufferedWriter bw = new BufferedWriter(osw);
                    String data = "switch=" + URLEncoder.encode(params[1], "UTF-8")
                            + "&ymdhi=" + URLEncoder.encode(params[2], "UTF-8")
                            + "&p_index=" + URLEncoder.encode(params[3], "UTF-8");
                    bw.write(data);
                    bw.flush();
                    InputStream is = urlConnection.getInputStream();
                    InputStreamReader isr = new InputStreamReader(is, "utf-8");
                    BufferedReader br = new BufferedReader(isr);
                    String line;
                    String result = "";
                    while ((line = br.readLine()) != null) {
                        result += line;
                    }
                    br.close();
                    isr.close();
                    is.close();
                    return result;
                } catch (Exception e) {
                    e.printStackTrace();
                    return "-1";
                }
            }

            @Override
            protected void onPostExecute(String s) {
                if (s.equals("0")) {
                    Snackbar.make(view, "成功", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, s, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        }.execute(str_url, str_switch, str_ymdhi, arr_index);

    }

//    public void powerChoice(final View v) {
//        String[] arr = {"电源一", "电源二"};
//        AlertDialog builder = new AlertDialog.Builder(getContext())
//                .setTitle("选择")
//                .setSingleChoiceItems(arr, arrIndex, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        temp = which;
//                    }
//                })
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        arrIndex = temp;
//                        Snackbar.make(v, "电源" + (arrIndex+1), Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
//                    }
//                })
//                .create();
//        builder.show();
//    }

    public void getStatus(final String string_url,final Handler handler){
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true){
                    try {
                        URL url = new URL(string_url);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setDoOutput(true);
                        urlConnection.setDoInput(true);
                        urlConnection.setRequestMethod("POST");
                        OutputStreamWriter osw = new OutputStreamWriter(urlConnection.getOutputStream(), "utf-8");
                        BufferedWriter bw = new BufferedWriter(osw);
                        String data = "p_index=" + URLEncoder.encode(String.valueOf(arrIndex), "UTF-8");
                        bw.write(data);
                        bw.flush();
                        InputStream is = urlConnection.getInputStream();
                        InputStreamReader isr = new InputStreamReader(is, "utf-8");
                        BufferedReader br = new BufferedReader(isr);
                        String line;
                        String result = "";
                        while ((line = br.readLine()) != null) {
                            result += line;
                        }
                        System.out.println("result: "+result);
                        System.out.println("index: "+arrIndex);
                        if(result!="") {
                            handler.sendEmptyMessage(Integer.valueOf(result));
                        }
                        br.close();
                        isr.close();
                        is.close();
                        Thread.sleep(1000);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

}
