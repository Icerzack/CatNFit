package com.example.max.rcthrowmqtt;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

public class ReportActivity extends AppCompatActivity {
    static int[] massivD = {0,0,0,0,0,0,0};
    static int[] massivH = {0,0,0,0,0,0,0};
    static int one;
    static int two;

    int counter =0 ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        setTitle(R.string.app_name_for_ReportActivity);
        GraphView graph = (GraphView) findViewById(R.id.graph);
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(1, 1005),
                new DataPoint(2, 1170),
                new DataPoint(3, 1281),
                new DataPoint(4, 1416),
                new DataPoint(5, 1863),
                new DataPoint(6, 1890),
                new DataPoint(7, 2010)
        });
        graph.addSeries(series);
        graph.setBackgroundColor(Color.WHITE);
        graph.setTitle("Статистика суммарного пробега");
        series.setColor(Color.GREEN);
        series.setAnimated(true);
        series.setSpacing(5);
        series.setDrawValuesOnTop(true);
        series.setValuesOnTopColor(Color.WHITE);

        GraphView graph2 = (GraphView) findViewById(R.id.graph2);
        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(1, 0),
                new DataPoint(2, 0),
                new DataPoint(3, 93),
                new DataPoint(4, 90),
                new DataPoint(5, 147),
                new DataPoint(6, 134),
                new DataPoint(7, 162)
        });
        graph2.addSeries(series2);
        graph2.setBackgroundColor(Color.WHITE);
        graph2.setTitle("Статистика суммарного подъема");
        series2.setColor(Color.YELLOW);
        series2.setAnimated(true);
        series2.setSpacing(5);
        series2.setDrawValuesOnTop(true);
        series2.setValuesOnTopColor(Color.WHITE);

        counter++;
    }
    public void counting(){

    }

}
