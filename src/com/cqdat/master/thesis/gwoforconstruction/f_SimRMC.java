package com.cqdat.master.thesis.gwoforconstruction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class f_SimRMC {

    public ArrayList<Site> lstSite = new ArrayList<>();                                         // Danh sách Công trường
    public ArrayList<ConstructionType> lstCT = new ArrayList<>();                               // Danh sách loại cấu kiện (Dầm, sàn, cột)
    public ArrayList<RMCTruckSchedule> lstRMCTruckSchedule = new ArrayList<>();                 // Thông tin chi tiết 1 lần đổ bên tông của một xe
    public ArrayList<ScheduleTruck> lstScheduleTrucks = new ArrayList<ScheduleTruck>();         // Thông tin lịch đổ bê tông của các xe tải
    public RMCStation rmcStation;                                   // 1 trạm trộn
    public boolean[] arrUsedTruckTBBMin = new boolean[1000];       // Mảng đánh dấu đã sử dụng TBB làm min

    public int powerOfTruck = 0; // Khả năng của xe có thể chở được tối đa bao nhiêu m3 bê tông
    public float vOfGo = 0;      // Vận tốc xe từ trạm trộn đến công trường
    public float vOfBack = 0;    // Vận tốc xe từ công trường về trạm

    public float TWC = 0;       // Tổng thời gian Xe đợi Công trường
    public float CWT = 0;       // Tổng thời gian Công trường đợi xe

    public double[] x_rand;

    public int N = 0;        // Tổng số xe cần bàn cho tất cả các công trường
    public int m = 0;        // Tổng số công trường

    int infinity = 1000000000;

    public f_SimRMC(){
        try {
            rmcStation = new RMCStation();
            ReadFile("Data/input_1_tram.data");
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Tính tổng số xe

        for(Site s : lstSite){
            N += s.numOfTruck;
        }

    }

    public int strHourToMinute(String strHour){
        String data[] = strHour.split(":");
        int iHour = Integer.parseInt(data[0]);
        int iMinute = Integer.parseInt(data[1]);
        return (iHour * 60) + iMinute;
    }

    public String strMinuteToHour(int iNum){
        int iHour = iNum / 60;
        int iMinute = iNum % 60;
        return String.format("%02d", iHour) + ":" + String.format("%02d", iMinute);
    }

    public String readLineFromFile(Scanner sc){
        String sLine = "";

        do {
            if(sc.hasNextLine() == false)
                return null;
            sLine = sc.nextLine();
        } while(sLine.length() == 0);

        return sLine;
    }

    public void ReadFile(String fileName) throws IOException {
        File f = new File(fileName);

        if(f.exists()) {
            try (Scanner scan = new Scanner(f)) {
                //Đọc thông tin công trường

                if (scan.hasNextLine()){
                    String line = readLineFromFile(scan);
                    String data[] = line.split("\\:");
                    m = Integer.parseInt(data[1]);
                }

                for(int i = 0; i < m; i++){
                    if(scan.hasNextLine()){
                        String line = readLineFromFile(scan);
                        String data[] = line.split("\\|");
                        Site s = new Site();
                        s.siteID = Integer.parseInt(data[0]);
                        s.SCT = strHourToMinute(data[1]);
                        s.R = Integer.parseInt(data[2]);
                        s.PT = data[3];
                        lstSite.add(s);
                    }
                }

                //Đọc thông tin Loại cấu kiện
                readLineFromFile(scan); //Đọc bỏ dòng -----
                for(int i = 0; i < 3; i++){
                    if(scan.hasNextLine()){
                        String line = readLineFromFile(scan);
                        String data[] = line.split("\\:");
                        ConstructionType c = new ConstructionType();
                        c.constructionName = data[0];
                        c.TPT = Integer.parseInt(data[1]);
                        lstCT.add(c);
                    }
                }

                readLineFromFile(scan); //Đọc bỏ dòng -----

                //Khả năng chở bê tông của một xe tải
                if(scan.hasNextLine()){
                    String line = readLineFromFile(scan);
                    String data[] = line.split("\\:");
                    powerOfTruck = Integer.parseInt(data[1]);
                }
                //vận tốc đi
                if(scan.hasNextLine()){
                    String line = readLineFromFile(scan);
                    String data[] = line.split("\\:");
                    vOfGo = Float.parseFloat(data[1]);
                }
                //vận tốc về
                if(scan.hasNextLine()){
                    String line = readLineFromFile(scan);
                    String data[] = line.split("\\:");
                    vOfBack = Float.parseFloat(data[1]);
                }

                readLineFromFile(scan); //Đọc bỏ dòng -----

                if(scan.hasNextLine()){
                    String line = readLineFromFile(scan);
                    String data[] = line.split("\\:");
                    RMCStation.MD = Integer.parseInt(data[1]);
                }

                readLineFromFile(scan); //Đọc bỏ dòng -----

                int numberOfStation = 0;
                if(scan.hasNextLine()){
                    String line = readLineFromFile(scan);
                    String data[] = line.split("\\:");
                    numberOfStation = Integer.parseInt(data[1]);
                }

                for(int i = 0; i < numberOfStation; i++){
                    readLineFromFile(scan); //Đọc bỏ dòng -----

                    String line = readLineFromFile(scan);
                    String data[] = line.split("\\:");
                    rmcStation.StationID = data[1];

                    line = readLineFromFile(scan);
                    data = line.split("\\:");
                    rmcStation.c = Integer.parseInt(data[1]);

                    line = readLineFromFile(scan);
                    data = line.split("\\:");
                    String arrD[] = data[1].split("\\|");

                    for(int j = 0; j < arrD.length; j++){
                        rmcStation.lstD.add(Integer.parseInt(arrD[i]));
                    }
                }
            }

            rmcStation.calTimeGoBack(vOfGo, vOfBack);

            for(Site s : lstSite) {
                s.calNumOfTruck(powerOfTruck);

                for(ConstructionType t : lstCT){
                    if(s.PT.equals(t.constructionName)) {
                        s.CD = t.TPT;
                    }
                }
            }
        }
        else {
            System.out.println("--> Error 404: File input.data not found!!!");
        }
    }

    public void calFDT(){
        // Tính min thời gian khởi hành của trạm trộn tới công trường
        int fdt = lstSite.get(0).calTimeTruckMove(rmcStation.lstTDG.get(0));

        for(int i = 1; i < lstSite.size(); i++){
            if(fdt > lstSite.get(i).calTimeTruckMove(rmcStation.lstTDG.get(i))){
                fdt = lstSite.get(i).calTimeTruckMove(rmcStation.lstTDG.get(i));
            }
        }

        rmcStation.timeFirstTruckGo = fdt;
        rmcStation.initListOfIDT(N);
    }

    public void initRMC(){
        ArrayList<RMCSite> arrSite = new ArrayList<RMCSite>();

        //Thực hiện khởi tạo quần thể sắp lịch ban đầu từ kết quả của thuật toán GWO thông qua x_rand[]
        int t = 0;
        for(Site s : lstSite){
            for(int i = 0; i < s.numOfTruck; i++){
                RMCSite rmcSite = new RMCSite();
                rmcSite.siteID = s.siteID;
                rmcSite.value = (int) (x_rand[t++] * 10000);
                arrSite.add(rmcSite);
            }
        }

        // Dựa và giá trị value của dãy Random/Thuật toán GWO, sắp sếp lại thứ tự đổ tại các công trường
        for(int i = 0; i < arrSite.size() - 1; i++){
            for(int j = i + 1; j < arrSite.size(); j++){
                if( arrSite.get(i).value > arrSite.get(j).value){
                    int tempSiteID = arrSite.get(i).siteID;
                    int tempValue = arrSite.get(i).value;

                    arrSite.get(i).siteID = arrSite.get(j).siteID;
                    arrSite.get(i).value = arrSite.get(j).value;

                    arrSite.get(j).siteID = tempSiteID;
                    arrSite.get(j).value = tempValue;
                }
            }
        }

        //Cập nhật k lại cho RMCSite
        for(int i = 0; i < arrSite.size(); i++){
            int k = 1;
            for(int j = i - 1; j >= 0; j--){
                if(arrSite.get(i).siteID == arrSite.get(j).siteID)
                    k++;
            }
            arrSite.get(i).k = k;
        }

        int No = 1;
        for(RMCSite rm : arrSite) {
            RMCTruckSchedule rmc = new RMCTruckSchedule();
            rmc.rmcID = No++;
            rmc.s = lstSite.get(rm.siteID - 1);
            rmc.k = rm.k;
            rmc.calDelivery(powerOfTruck);
            rmc.TBB = infinity;
            lstRMCTruckSchedule.add(rmc);
        }
    }

    public void initScheduleTruck(){
        int truckID = 0;
        for (int i = 0; i < rmcStation.c; i++) {
            ScheduleTruck scheduleTruck = new ScheduleTruck();
            scheduleTruck.truckID = truckID++ + 1;
            lstScheduleTrucks.add(scheduleTruck);
        }
    }


    public void ExecuteAlgorithm(){

        for(int i = 0; i < N; i++){
            arrUsedTruckTBBMin[i] = false;
        }

        initScheduleTruck();

        //Khởi tạo quần thể RMCTruckSchedule
        initRMC();

        //Tính FDT, IDT
        calFDT();

        //tinh toan cac gia tri
        for(int i=0; i< N; i++){
            RMCTruckSchedule rmcTruckSchedule = lstRMCTruckSchedule.get(i);
            if (i < rmcStation.c){
                rmcTruckSchedule.SDT = rmcStation.lstIDT.get(i).outputTime;
                rmcTruckSchedule.truckID = rmcStation.lstIDT.get(i).truckID;
            } else {
                int minTBB = infinity;
                int position_minTBB = -1;
                for (int l=0; l<i; l++){
                    if (!arrUsedTruckTBBMin[l] && minTBB > lstRMCTruckSchedule.get(l).TBB){
                        minTBB = lstRMCTruckSchedule.get(l).TBB;
                        position_minTBB = l;
                    }
                }
                arrUsedTruckTBBMin[position_minTBB] = true;
                rmcTruckSchedule.SDT = minTBB + RMCStation.MD;
                rmcTruckSchedule.truckID = lstRMCTruckSchedule.get(position_minTBB).truckID;
            }

            rmcTruckSchedule.CD_RMC = rmcTruckSchedule.delivery * rmcTruckSchedule.s.CD;

            rmcTruckSchedule.TDG = rmcStation.lstTDG.get(rmcTruckSchedule.s.siteID-1);
            rmcTruckSchedule.TDB = rmcStation.lstTDB.get(rmcTruckSchedule.s.siteID-1);

            rmcTruckSchedule.TAC = rmcTruckSchedule.SDT + rmcTruckSchedule.TDG;

            //Tính PTF
            if(rmcTruckSchedule.k == 1) {
                rmcTruckSchedule.PTF = rmcTruckSchedule.s.SCT;
            } else {
                //find LT of (k-1)th truck leaves site j
                int siteID = rmcTruckSchedule.s.siteID;
                int position = -1;
                for (int l=0; l<i; l++){
                    if (lstRMCTruckSchedule.get(l).s.siteID == siteID && lstRMCTruckSchedule.get(l).k == rmcTruckSchedule.k-1){
                        position = l;
                    }
                }
                rmcTruckSchedule.PTF = lstRMCTruckSchedule.get(position).LT;
            }

            rmcTruckSchedule.WC = rmcTruckSchedule.PTF - rmcTruckSchedule.TAC;

            if(rmcTruckSchedule.WC >= 0){
                rmcTruckSchedule.LT = rmcTruckSchedule.TAC + rmcTruckSchedule.WC + rmcTruckSchedule.CD_RMC;
            } else {
                rmcTruckSchedule.LT = rmcTruckSchedule.TAC + rmcTruckSchedule.CD_RMC;
            }

            rmcTruckSchedule.TBB = rmcTruckSchedule.LT + rmcTruckSchedule.TDB;
            lstRMCTruckSchedule.set(i, rmcTruckSchedule);
        }

        calcSum_TWC_CWT();
    }

    public void PrintRMC(){
        System.out.println("----------------------------------------------------------------------------------");

        for(RMCTruckSchedule rmc : lstRMCTruckSchedule){
            System.out.println(rmc.toString());
        }
        System.out.println("=> TWC: " + TWC);
        System.out.println("=> CWT: " + CWT);

        System.out.println("Chuoi phan phoi: ");
        System.out.print("[");
        for(int i=0; i<lstRMCTruckSchedule.size(); i++){
            if (i!=lstRMCTruckSchedule.size()-1){
                System.out.print(lstRMCTruckSchedule.get(i).s.siteID+",");
            } else {
                System.out.print(lstRMCTruckSchedule.get(i).s.siteID);
            }
        }
        System.out.print("]");
        System.out.println();
    }

    public void PrintPlanOfTruck(){
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println("Arrive at plant\tGo to site \t Arrive at site \t Leave from site \t Return to plant");
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        for (ScheduleTruck scheduleTruck : lstScheduleTrucks){
            PlanOfTruck planOfTruck = new PlanOfTruck();
            planOfTruck.TruckID = scheduleTruck.truckID;

            for (RMCTruckSchedule rmcTruckSchedule : lstRMCTruckSchedule){
                if(rmcTruckSchedule.truckID == planOfTruck.TruckID){
                    Plan p = new Plan();
                    p.SiteID = rmcTruckSchedule.s.siteID;
                    p.ArriveAtStation = strMinuteToHour(rmcTruckSchedule.SDT);
                    p.ArriveAtSite = strMinuteToHour(rmcTruckSchedule.TAC);
                    p.LeaveFromSite = strMinuteToHour(rmcTruckSchedule.LT);
                    p.ReturnToPlant = strMinuteToHour(rmcTruckSchedule.TBB);
                    planOfTruck.lstPlan.add(p);
                }
            }
            System.out.println(planOfTruck);
        }
    }

    public void calcSum_TWC_CWT(){
        int sumTWC = 0;
        int sumCWT = 0;
        for(RMCTruckSchedule rmc : lstRMCTruckSchedule){
            if(rmc.WC > 0)
                sumTWC += rmc.WC;
            else
                sumCWT += rmc.WC;
        }

        TWC = sumTWC;
        CWT = Math.abs(sumCWT);
    }

    public double Execute_CWT(double x[]) {
        x_rand = x;
        ExecuteAlgorithm();
        return CWT;
    }

    public double Execute_TWC(double x[]) {
        x_rand = x;
        ExecuteAlgorithm();
        return TWC;
    }

    public void Execute(double x[]){
        x_rand = x;
        ExecuteAlgorithm();
        //PrintPlanOfTruck();
    }
}
