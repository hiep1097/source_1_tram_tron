package com.cqdat.master.thesis.gwoforconstruction;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;

import java.io.IOException;

public class Main_All {
    public static void main(String[] args) throws IOException {

        f_GWO_RMC_CWT ff_cwt = new f_GWO_RMC_CWT();
        f_GWO_RMC_CWT ff_twc = new f_GWO_RMC_TWC();

        int maxiter = 50;
        int N = 60;

        DA_GWO qbpso_cwt_DA_GWO = new DA_GWO(ff_cwt, ff_cwt.Lower, ff_cwt.Upper, maxiter, N);
        DA_GWO qbpso_twc_DA_GWO = new DA_GWO(ff_twc, ff_twc.Lower, ff_twc.Upper, maxiter, N);

        GWO qbpso_cwt_GWO = new GWO(ff_cwt, ff_cwt.Lower, ff_cwt.Upper, maxiter, N);
        GWO qbpso_twc_GWO = new GWO(ff_twc, ff_twc.Lower, ff_twc.Upper, maxiter, N);

        long startTime = System.currentTimeMillis();

        qbpso_cwt_DA_GWO.execute();
        qbpso_twc_DA_GWO.execute();

        qbpso_cwt_GWO.execute();
        qbpso_twc_GWO.execute();

        int lengthOfItem = maxiter * 2;
        int numberOfTruckWanted = ff_cwt.Upper.length;

        double[][] data_DA_GWO = new double[lengthOfItem][numberOfTruckWanted];
        double[][] data_GWO = new double[lengthOfItem][numberOfTruckWanted];

        double[][] temp_cwt_DA_GWO = qbpso_cwt_DA_GWO.getArrayRandomResult();
        double[][] temp_cwt_GWO = qbpso_twc_DA_GWO.getArrayRandomResult();
        for (int i = 0; i < maxiter; i++) {
            data_DA_GWO[i] = temp_cwt_DA_GWO[i];
            data_GWO[i] = temp_cwt_GWO[i];
        }

        double[][] temp_twc_DA_GWO = qbpso_twc_DA_GWO.getArrayRandomResult();
        double[][] temp_twc_GWO = qbpso_twc_GWO.getArrayRandomResult();
        for (int i = 0; i < maxiter; i++) {
            data_DA_GWO[maxiter + i] = temp_twc_DA_GWO[i];
            data_GWO[maxiter + i] = temp_twc_GWO[i];
        }

        ObservableList<XYChart.Data<Number, Number>> lstData_DA_GWO = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<Number, Number>> lstData_GWO = FXCollections.observableArrayList();


        for (int i = 0; i < lengthOfItem; i++) {
            f_SimRMC fSimRMC_DA_GWO = new f_SimRMC();
            fSimRMC_DA_GWO.Execute(data_DA_GWO[i]);

            XYChart.Data<Number, Number> _data_DA_GWO = new XYChart.Data<>();

            _data_DA_GWO.setXValue(fSimRMC_DA_GWO.CWT);
            _data_DA_GWO.setYValue(fSimRMC_DA_GWO.TWC);

            lstData_DA_GWO.add(_data_DA_GWO);

            f_SimRMC fSimRMC_GWO = new f_SimRMC();
            fSimRMC_GWO.Execute(data_GWO[i]);

            XYChart.Data<Number, Number> _data_GWO = new XYChart.Data<>();

            _data_GWO.setXValue(fSimRMC_GWO.CWT);
            _data_GWO.setYValue(fSimRMC_GWO.TWC);

            lstData_GWO.add(_data_GWO);
        }

        f_SimRMC _fSimRMC_CWT_DA_GWO = new f_SimRMC();
        f_SimRMC _fSimRMC_TWC_DA_GWO = new f_SimRMC();
        f_SimRMC _fSimRMC_CWT_GWO = new f_SimRMC();
        f_SimRMC _fSimRMC_TWC_GWO = new f_SimRMC();

        _fSimRMC_CWT_DA_GWO.Execute(qbpso_cwt_DA_GWO.getBestArray());
        _fSimRMC_TWC_DA_GWO.Execute(qbpso_twc_DA_GWO.getBestArray());
        _fSimRMC_CWT_GWO.Execute(qbpso_cwt_GWO.getBestArray());
        _fSimRMC_TWC_GWO.Execute(qbpso_twc_GWO.getBestArray());

        System.out.println("----->> Best of CWT DA GWO <<-----");
        System.out.println("CWT = " + _fSimRMC_CWT_DA_GWO.CWT + " - TWC = " + _fSimRMC_CWT_DA_GWO.TWC);
        qbpso_cwt_DA_GWO.toStringNew("Optimized value CWT = ");

        System.out.println("----->> Best of TWC DA GWO <<-----");
        System.out.println("CWT = " + _fSimRMC_TWC_DA_GWO.CWT + " - TWC = " + _fSimRMC_TWC_DA_GWO.TWC);
        qbpso_twc_DA_GWO.toStringNew("Optimized value TWC = ");

        System.out.println("----->> Best of CWT GWO <<-----");
        System.out.println("CWT = " + _fSimRMC_CWT_GWO.CWT + " - TWC = " + _fSimRMC_CWT_GWO.TWC);
        qbpso_cwt_GWO.toStringNew("Optimized value CWT = ");

        System.out.println("----->> Best of TWC GWO <<-----");
        System.out.println("CWT = " + _fSimRMC_TWC_GWO.CWT + " - TWC = " + _fSimRMC_TWC_GWO.TWC);
        qbpso_twc_GWO.toStringNew("Optimized value TWC = ");

        ObservableList<XYChart.Data<Number, Number>> lstParetoData_DA_GWO = FXCollections.observableArrayList();
        ObservableList<XYChart.Data<Number, Number>> lstParetoData_GWO = FXCollections.observableArrayList();

        for (int i = 0; i < lstData_DA_GWO.size(); i++){
            float cwt_value_i = (float) lstData_DA_GWO.get(i).getXValue();
            float twc_value_i = (float) lstData_DA_GWO.get(i).getYValue();
            boolean isPareto = true;
            for (int j=0; j<lstData_DA_GWO.size(); j++){
                float cwt_value_j = (float) lstData_DA_GWO.get(j).getXValue();
                float twc_value_j = (float) lstData_DA_GWO.get(j).getYValue();
                if (i != j) {
                    if ((cwt_value_i > cwt_value_j && twc_value_i > twc_value_j) || (cwt_value_i == cwt_value_j && twc_value_i == twc_value_j) ||
                            (cwt_value_i > cwt_value_j && twc_value_i == twc_value_j) || (cwt_value_i == cwt_value_j && twc_value_i > twc_value_j)) {
                        isPareto = false;
                        break;
                    }

                }
            }
            if (isPareto) {
                XYChart.Data<Number, Number> _pareto_data = new XYChart.Data<>();
                _pareto_data.setXValue(cwt_value_i);
                _pareto_data.setYValue(twc_value_i);

                lstParetoData_DA_GWO.add(_pareto_data);
            }
        }

        for (int i = 0; i < lstData_GWO.size(); i++){
            float cwt_value_i = (float) lstData_GWO.get(i).getXValue();
            float twc_value_i = (float) lstData_GWO.get(i).getYValue();
            boolean isPareto = true;
            for (int j=0; j<lstData_GWO.size(); j++){
                float cwt_value_j = (float) lstData_GWO.get(j).getXValue();
                float twc_value_j = (float) lstData_GWO.get(j).getYValue();
                if (i != j) {
                    if ((cwt_value_i > cwt_value_j && twc_value_i > twc_value_j) || (cwt_value_i == cwt_value_j && twc_value_i == twc_value_j) ||
                            (cwt_value_i > cwt_value_j && twc_value_i == twc_value_j) || (cwt_value_i == cwt_value_j && twc_value_i > twc_value_j)) {
                        isPareto = false;
                        break;
                    }

                }
            }
            if (isPareto) {
                XYChart.Data<Number, Number> _pareto_data = new XYChart.Data<>();
                _pareto_data.setXValue(cwt_value_i);
                _pareto_data.setYValue(twc_value_i);

                lstParetoData_GWO.add(_pareto_data);
            }
        }

        System.out.println("--> Complete Check fix Pareto data");

        long endTime = System.currentTimeMillis();
        long totalTime = endTime - startTime;

        System.out.println((totalTime / 1000.0) + " sec");

        System.out.println("=============== RESULT ===============");

//        for(int i = 0; i < lst_fSimRMC.size(); i++) {
//            System.out.println("--> No. " + (i + 1) + " <--" );
//            lst_fSimRMC.get(i).PrintRMC();
//            lst_fSimRMC.get(i).PrintPlanOfTruck();
//        }

        System.out.println("Result DA_GWO have " + lstParetoData_DA_GWO.size() + " values");
        System.out.println("Result GWO have " + lstParetoData_GWO.size() + " values");

        ParetoChart_ chart = new ParetoChart_();
        ParetoChart_.lstData_DA_GWO = lstData_DA_GWO;
        ParetoChart_.lstParetoData_DA_GWO = lstParetoData_DA_GWO;
        ParetoChart_.lstData_GWO = lstData_GWO;
        ParetoChart_.lstParetoData_GWO = lstParetoData_GWO;

        chart.main(args);
    }
}
