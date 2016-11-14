package tech.zhangchi.power;


import android.content.Intent;
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
public class Tab3 extends Fragment {

    private Spinner spinner;
    private Button button;
    private int index = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.layout3, null);

        spinner = (Spinner) view.findViewById(R.id.spinner_show);
        final String[] arr = {"请选择", "温度", "光照", "湿度"};
        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getActivity().getBaseContext(), android.R.layout.simple_list_item_1, arr);
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
        button = (Button) view.findViewById(R.id.button_show);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                show_post("http://222.210.113.185/Power/show.php", String.valueOf(index), v);
            }
        });

        return view;
    }

    public void show_post(String str_url, String str_show, final View view) {
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
                    String data = "show=" + URLEncoder.encode(params[1], "UTF-8");
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
                if (s.equals("1") || s.equals("2") || s.equals("3")) {
                    Snackbar.make(view, "失败", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Intent intent = new Intent(getActivity(), ShowActivity.class);
                    intent.putExtra("data", s);
                    intent.putExtra("index", index);
                    startActivity(intent);
                }
            }
        }.execute(str_url, str_show);

    }

}
