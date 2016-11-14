package tech.zhangchi.power;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

/**
 * A simple {@link Fragment} subclass.
 */
public class Tab2 extends Fragment {

    private Spinner spinner;
    private Button button_bind;
    private int index = 0;
    private int arrIndex = 0;
    private int temp = 0;
    private RadioGroup rg2;
    private RadioButton power1_2;

    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout2, null);

        spinner = (Spinner) view.findViewById(R.id.spinner_bind);

        final String[] arr = {"请选择", "电暖器", "灯", "加湿器"};
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_activated_1, arr);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                index = position;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        power1_2 = (RadioButton) view.findViewById(R.id.power1_2);
        rg2 = (RadioGroup) view.findViewById(R.id.rg2);
        rg2.check(power1_2.getId());
        rg2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == power1_2.getId()){
                    arrIndex = 0;
                }else
                    arrIndex = 1;
            }
        });
//        view.findViewById(R.id.button_choose2).setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                powerChoice(v);
//            }
//        });
        button_bind = (Button) view.findViewById(R.id.button_bind);
        button_bind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (index == 0) {
                    Snackbar.make(v, "请重新输入", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else
                    bind_post("http://222.210.113.185/Power/bind.php", String.valueOf(index),String.valueOf(arrIndex), v);
            }
        });

        return view;
    }

    public void bind_post(String str_url, String str_bind, String arr_index, final View view) {
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
                    String data = "bind=" + URLEncoder.encode(params[1], "UTF-8")
                            + "&p_index=" + URLEncoder.encode(params[2], "UTF-8");
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
        }.execute(str_url, str_bind, arr_index);

    }

//    public void powerChoice(final View v) {
//        String[] arr = {"电源一", "电源二"};
//        AlertDialog builder = new AlertDialog.Builder(getContext())
//                .setTitle("选择")
//                .setSingleChoiceItems(arr, arrIndex, new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        arrIndex = which;
//                    }
//                })
//                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Snackbar.make(v, "电源" + (arrIndex+1), Snackbar.LENGTH_LONG)
//                                .setAction("Action", null).show();
//                    }
//                })
////                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
////                    @Override
////                    public void onClick(DialogInterface dialog, int which) {
////
////                    }
////                })
//                .create();
//        builder.show();
//    }

}
