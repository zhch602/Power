package tech.zhangchi.power;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ShowActivity extends AppCompatActivity {

    private LineData data;
    private ArrayList<String> xVals;
    private LineDataSet dataSet;
    private ArrayList<Entry> yVals;
    private LineChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show);
        getSupportActionBar().hide();
        Intent intent = getIntent();
        String data = intent.getStringExtra("data");
        chart = (LineChart) findViewById(R.id.chart);
        xVals = new ArrayList<>();
        yVals = new ArrayList<>();
        int index = intent.getIntExtra("index", -1);
        switch (index) {
            case 1:
                ShowData(data, "temperature");
                break;
            case 2:
                ShowData(data, "light");
                break;
            case 3:
                ShowData(data, "humidity");
                break;
        }
    }

    private void ShowData(String jsonData, String type) {
        try {
            JSONArray jsonArray = new JSONArray(jsonData);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                int ymdhi = jsonObject.getInt("ymdhi");
                int data = jsonObject.getInt(type);
                yVals.add(new Entry(data, i));
                int time = ymdhi % 10000;
                int hour = time / 100;
                int sec = time % 100;
                xVals.add(hour + ":" + sec);
            }
            dataSet = new LineDataSet(yVals, type);
            dataSet.setColors(ColorTemplate.COLORFUL_COLORS);
            data = new LineData(xVals, dataSet);
            chart.setData(data);
            chart.setDescription("time");
            chart.animateY(2000);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
