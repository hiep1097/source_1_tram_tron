package com.cqdat.master.thesis.gwoforconstruction;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.stage.Stage;

import static java.lang.Double.parseDouble;

public class ParetoChart_ extends Application {

    public static int flagShowDetail = 1; // 0: show only Pareto chart; 1: Show all value (Data + Pareto)

    public static ObservableList<Data<Number, Number>> lstData_DA_GWO = FXCollections.observableArrayList();
    public static ObservableList<Data<Number, Number>> lstParetoData_DA_GWO = FXCollections.observableArrayList();

    public static ObservableList<Data<Number, Number>> lstData_GWO = FXCollections.observableArrayList();
    public static ObservableList<Data<Number, Number>> lstParetoData_GWO = FXCollections.observableArrayList();

    public int max_x = 0;
    public int min_x = 100000;

    public int max_y = 0;
    public int min_y = 100000;

    @Override
    public void start(Stage stage) {
        if(flagShowDetail == 1) {
            for (int i = 0; i < lstData_DA_GWO.size(); i++) {
                if (max_x < (lstData_DA_GWO.get(i).getXValue().intValue() + 1))
                    max_x = lstData_DA_GWO.get(i).getXValue().intValue() + 1;

                if (max_y < (lstData_DA_GWO.get(i).getYValue().intValue() + 1))
                    max_y = lstData_DA_GWO.get(i).getYValue().intValue() + 1;

                if (min_x > (lstData_DA_GWO.get(i).getXValue().intValue() + 1))
                    min_x = lstData_DA_GWO.get(i).getXValue().intValue() + 1;

                if (min_y > (lstData_DA_GWO.get(i).getYValue().intValue() + 1))
                    min_y = lstData_DA_GWO.get(i).getYValue().intValue() + 1;

            }
            for (int i = 0; i < lstData_GWO.size(); i++) {
                if (max_x < (lstData_GWO.get(i).getXValue().intValue() + 1))
                    max_x = lstData_GWO.get(i).getXValue().intValue() + 1;

                if (max_y < (lstData_GWO.get(i).getYValue().intValue() + 1))
                    max_y = lstData_GWO.get(i).getYValue().intValue() + 1;

                if (min_x > (lstData_GWO.get(i).getXValue().intValue() + 1))
                    min_x = lstData_GWO.get(i).getXValue().intValue() + 1;

                if (min_y > (lstData_GWO.get(i).getYValue().intValue() + 1))
                    min_y = lstData_GWO.get(i).getYValue().intValue() + 1;

            }
        } else {
            for (int i = 0; i < lstParetoData_DA_GWO.size(); i++) {
                if (max_x < (lstParetoData_DA_GWO.get(i).getXValue().intValue() + 1))
                    max_x = lstParetoData_DA_GWO.get(i).getXValue().intValue() + 1;

                if (max_y < (lstParetoData_DA_GWO.get(i).getYValue().intValue() + 1))
                    max_y = lstParetoData_DA_GWO.get(i).getYValue().intValue() + 1;

                if (min_x > (lstParetoData_DA_GWO.get(i).getXValue().intValue() + 1))
                    min_x = lstParetoData_DA_GWO.get(i).getXValue().intValue() + 1;

                if (min_y > (lstParetoData_DA_GWO.get(i).getYValue().intValue() + 1))
                    min_y = lstParetoData_DA_GWO.get(i).getYValue().intValue() + 1;
            }
            for (int i = 0; i < lstParetoData_GWO.size(); i++) {
                if (max_x < (lstParetoData_GWO.get(i).getXValue().intValue() + 1))
                    max_x = lstParetoData_GWO.get(i).getXValue().intValue() + 1;

                if (max_y < (lstParetoData_GWO.get(i).getYValue().intValue() + 1))
                    max_y = lstParetoData_GWO.get(i).getYValue().intValue() + 1;

                if (min_x > (lstParetoData_GWO.get(i).getXValue().intValue() + 1))
                    min_x = lstParetoData_GWO.get(i).getXValue().intValue() + 1;

                if (min_y > (lstParetoData_GWO.get(i).getYValue().intValue() + 1))
                    min_y = lstParetoData_GWO.get(i).getYValue().intValue() + 1;
            }
        }

        stage.setTitle("Master Thesis about GWO for Construction");
        final NumberAxis xAxis = new NumberAxis(min_x - 3, max_x, (max_x - min_x) / 10);
        final NumberAxis yAxis = new NumberAxis(min_y - 5, max_y, (max_y - min_y) / 10);
        final ScatterChart<Number, Number> sc = new ScatterChart<>(xAxis,
                yAxis);
        xAxis.setLabel("Construction Wait Truck - CWT (minutes)");
        yAxis.setLabel("Truck Wait Construction - TWC (minutes)");
        sc.setTitle("Pareto font for GWO algorithm");

        Series<Number, Number> series_DA_GWO = new Series<>();
        series_DA_GWO.setName("Value DA_GWO");
        series_DA_GWO.setData(lstData_DA_GWO);

        Series<Number, Number> series_pareto_DA_GWO = new Series<>();
        series_pareto_DA_GWO.setName("Pareto value DA_GWO");
        series_pareto_DA_GWO.setData(lstParetoData_DA_GWO);

        Series<Number, Number> series_GWO = new Series<>();
        series_GWO.setName("Value GWO");
        series_GWO.setData(lstData_GWO);

        Series<Number, Number> series_pareto_GWO = new Series<>();
        series_pareto_GWO.setName("Pareto value GWO");
        series_pareto_GWO.setData(lstParetoData_GWO);

        Series<Number, Number> s1 = new Series<>();
        Series<Number, Number> s2 = new Series<>();
        Series<Number, Number> s3 = new Series<>();

        if(flagShowDetail == 1) {
            sc.getData().addAll(series_DA_GWO, series_GWO, series_pareto_DA_GWO, series_pareto_GWO);
        } else {
            sc.getData().addAll(s1, s2, series_pareto_DA_GWO, series_pareto_GWO);
        }
        Scene scene = new Scene(sc, 500, 400);
        stage.setScene(scene);
        stage.show();
    }


    public ObservableList<Data<Number, Number>> parseToObservableList(String[] lstString) {
        ObservableList<Data<Number, Number>> lstData = FXCollections.observableArrayList();
        for(int i = 0; i < lstString.length; i++)
        {
            String[] sTemp = lstString[i].split("|");

            Data<Number, Number> data = new Data<>();

            data.setXValue(parseDouble(sTemp[0]));
            data.setYValue(parseDouble(sTemp[1]));

            lstData.add(data);
        }
        return lstData;
    }

    public void main(String[] args) {
        launch(args);
    }
}
