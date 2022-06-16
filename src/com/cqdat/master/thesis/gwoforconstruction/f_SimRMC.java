package com.cqdat.master.thesis.gwoforconstruction;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class f_SimRMC {

    public ArrayList<Site> lstSite = new ArrayList<Site>();             // Danh sách Công trường
    public ArrayList<ConstructionType> lstCT = new ArrayList<ConstructionType>();   // Danh sách loại công trường (Dầm, sàn, cột)
    public ArrayList<RMCTruckSchedule> lstRMCTruckSchedule = new ArrayList<RMCTruckSchedule>();    // Thông tin chi tiết 1 lần đổ bên tông của một xe
    public ArrayList<ScheduleTruck> lstScheduleTrucks = new ArrayList<ScheduleTruck>();         // Thông tin lịch đổ bê tông của một xe
    public ArrayList<RMCStation> lstRMCStation = new ArrayList<RMCStation>();                // Danh sách các trạm trộn

    public int[] arrTruckTBB = new int[1000];                      // Danh TBB của từng xe tại
    public int[] arrLT = new int[1000];                            // Thời điểm xe rời công trường
    public int numOfTruck = 0;                                     // Tổng tất cả các Truck đang

    public int powerOfTruck = 0; // Khả năng của xe có thể chở được tối đa bao nhiêu m3 bê tông
    public float vOfGo = 0;      // Vận tốc xe từ trạm trộn đến công trường
    public float vOfBack = 0;    // Vận tốc xe từ công trường về trạm

    public float TWC = 0;       // Tổng thời gian Xe đợi Công trường
    public float CWT = 0;       // Tổng thời gian Công trường đợi xe

    public double[] x_rand;

    public int N = 0;        // Tổng số xe cần bàn cho tất cả các công trường
    public int m = 0;        // Tổng số công trường

    public ArrayList<FDT> lstFDT = new ArrayList<>();      //Thời gian khởi hành sớm nhất

    public f_SimRMC(){
        try {
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

                    RMCStation rmcStation = new RMCStation();

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

                    lstRMCStation.add(rmcStation);
                }
            }

            for(RMCStation r : lstRMCStation){
                r.calTimeGoBack(vOfGo, vOfBack);
            }

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
        int truckID = 1;
        // Tính min thời gian khởi hành của mỗi trạm trộn tới công trường
        for(RMCStation rmc : lstRMCStation){
            int fdt = lstSite.get(0).calTimeTruckMove(rmc.lstTDG.get(0)) - (lstRMCTruckSchedule.get(0).delivery * rmc.timeMD);

            for(int i = 0; i < lstSite.size(); i++){
                if(fdt > lstSite.get(i).calTimeTruckMove(rmc.lstTDG.get(i)) - (lstRMCTruckSchedule.get(0).delivery * rmc.timeMD)){
                    fdt = lstSite.get(i).calTimeTruckMove(rmc.lstTDG.get(i) - (lstRMCTruckSchedule.get(0).delivery * rmc.timeMD));
                }
            }

            FDT objFDT = new FDT();
            objFDT.rmcstationID = rmc.StationID;
            objFDT.fdtValue = fdt;

            rmc.timeFirstTruckGo = fdt;
            rmc.initListOfIDT(truckID, powerOfTruck);
            truckID += rmc.c;

            lstFDT.add(objFDT);
        }
    }

    public void initListTruckTBB(){
        numOfTruck = 0;
        for(RMCStation r : lstRMCStation)
            numOfTruck += r.c;

        for(int i = 0; i < numOfTruck; i++)
            arrTruckTBB[i] = 0;
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

            lstRMCTruckSchedule.add(rmc);
        }

    }

    public void initScheduleTruck(){
        int truckID = 0;

        for(RMCStation r : lstRMCStation) {
            //c += r.c;

            for (int i = 0; i < r.c; i++) {
                ScheduleTruck schTruck = new ScheduleTruck();
                schTruck.truckID = truckID++ + 1;
                int deltaTimeOutputOfTruck = (i < lstRMCTruckSchedule.size()) ? ((i == 0) ? 0 : RMCStation.MD) : 0;
                schTruck.outputTime = r.timeFirstTruckGo + (i * deltaTimeOutputOfTruck);
                schTruck.stationID = r.StationID;
                lstScheduleTrucks.add(schTruck);
            }
        }
    }

    public void swapScheduleTruck(ScheduleTruck a, ScheduleTruck b) {
        int tempTruckID = a.truckID;
        int tempInputTime = a.inputTime;
        int tempOutputTime = a.outputTime;
        String tempStationID = a.stationID;

        a.truckID = b.truckID;
        a.inputTime = b.inputTime;
        a.outputTime = b.outputTime;
        a.stationID = b.stationID;

        b.truckID = tempTruckID;
        b.inputTime = tempInputTime;
        b.outputTime = tempOutputTime;
        b.stationID = tempStationID;
    }

    public void calScheduleTruck(int startRMCID, int c, int timeMD){
        for(int i = 0; i < c - 1; i++){
            for(int j = i + 1; j < c; j++){
                if(lstScheduleTrucks.get(i).inputTime > lstScheduleTrucks.get(j).inputTime){
                    swapScheduleTruck(lstScheduleTrucks.get(i), lstScheduleTrucks.get(j));
                }
            }
        }

        int IDT = lstScheduleTrucks.get(0).inputTime + (lstRMCTruckSchedule.get(startRMCID + 1).delivery * timeMD);

        for(int i = 0; i < c; i++){

            int deltaTimeOutputOfTruck = ((i == 0 || (startRMCID + i) >= lstRMCTruckSchedule.size()) ? 0 : (lstRMCTruckSchedule.get(startRMCID + i).delivery * timeMD));
            lstScheduleTrucks.get(i).outputTime = IDT + (i * deltaTimeOutputOfTruck);
        }
    }

    public void initArrayLT(){
        for(int i = 0; i < lstSite.size(); i++){
            arrLT[i] = 0;
        }

    }

    public RMCStation findRMCStation(String stationID){
        for(RMCStation station : lstRMCStation)
            if(station.StationID.equals(stationID))
                return station;
        return null;
    }

    private ScheduleTruck CalTruckFromStation(Site s) {
        ScheduleTruck scheduleTruck = new ScheduleTruck();

        RMCStation goodStation = lstRMCStation.get(0);

        for(int i = 1; i < lstRMCStation.size(); i++){
            if(goodStation.lstIDT.size() == 0 || goodStation.calTimeComedToSite(s.siteID) > lstRMCStation.get(i).calTimeComedToSite(s.siteID))
                goodStation = lstRMCStation.get(i);
        }

        scheduleTruck.truckID = goodStation.lstIDT.get(0).truckID;
        scheduleTruck.inputTime = goodStation.lstIDT.get(0).inputTime;
        scheduleTruck.outputTime = goodStation.lstIDT.get(0).outputTime;
        scheduleTruck.stationID = goodStation.StationID;

        goodStation.lstIDT.remove(0);

        return scheduleTruck;
    }

    private ScheduleTruck CalTruckComeBackStation(Site s, int LT){
        ScheduleTruck scheduleTruck = new ScheduleTruck();

        RMCStation goodStation = lstRMCStation.get(0);

        for(int i = 1; i < lstRMCStation.size(); i++){
            if(goodStation.lstIDT.size() == 0 || goodStation.calTimeOut(s.siteID, LT, powerOfTruck) > lstRMCStation.get(i).calTimeOut(s.siteID, LT, powerOfTruck))
                goodStation = lstRMCStation.get(i);
        }

        //scheduleTruck.truckID = goodStation.lstIDT.get(0).truckID;
        scheduleTruck.inputTime = (goodStation.lstIDT.size() == 0) ? LT : goodStation.lstIDT.get(0).inputTime;
        scheduleTruck.outputTime = goodStation.calTimeOut(s.siteID, LT, powerOfTruck);
        scheduleTruck.stationID = goodStation.StationID;

        return scheduleTruck;
    }


    public void ExecuteAlgorithm(){

        //Khởi tạo quần thể RMCTruckSchedule
        initRMC();

        initListTruckTBB();

        //Tính FDT
        calFDT();

        //Khởi tạo quần thể ScheduleTruck
        initScheduleTruck();
        initArrayLT();

        //Cập nhật Giá trị SDT của Block Schedule đầu tiên
        for(RMCTruckSchedule rts : lstRMCTruckSchedule){
            ScheduleTruck scheduleTruck = CalTruckFromStation(rts.s);

            RMCStation station = findRMCStation(scheduleTruck.stationID);
            rts.StationID_Go = station.StationID;
            rts.truckID = scheduleTruck.truckID;
            rts.calMD_and_CD_RMC();

            int ltOfSite  = arrLT[rts.s.siteID - 1];
            int TDG = station.lstTDG.get(rts.s.siteID - 1);

            ScheduleTruck scheduleTruck_2 = CalTruckComeBackStation(rts.s, rts.LT);
            scheduleTruck_2.truckID = scheduleTruck.truckID;

            RMCStation stationBack = findRMCStation(scheduleTruck_2.stationID);
            int TDB = stationBack.lstTDB.get(rts.s.siteID - 1);

            rts.StationID_Back = stationBack.StationID;
            rts.TBB = rts.LT + stationBack.lstTimeBack.get(rts.s.siteID - 1);

            stationBack.lstIDT.add(scheduleTruck_2);

            int SDT = scheduleTruck.outputTime;
            int truckTBB = arrTruckTBB[scheduleTruck.truckID - 1];
            if(truckTBB != 0 && (truckTBB + RMCStation.MD) > SDT)
                SDT = truckTBB + RMCStation.MD;

            rts.calSDT(SDT, ltOfSite, TDG, TDB);

            arrLT[rts.s.siteID - 1] = rts.LT;
            arrTruckTBB[scheduleTruck.truckID - 1] = rts.TBB;
        }

        calcSum_TWC_CWT();

//        PrintRMC();
    }

    public void PrintRMC(){
        System.out.println("----------------------------------------------------------------------------------");

        for(RMCTruckSchedule rmc : lstRMCTruckSchedule){
            System.out.println(rmc.toString());
        }
        System.out.println("=> TWC: " + TWC);
        System.out.println("=> CWT: " + CWT);
    }

    public void PrintPlanOfTruck(){
        System.out.println("------------------------------------------------------------------------------------------------------------------");
        System.out.println("Arrive at plant\tGo to site \t Arrive at site \t Leave from site \t Return to plant \t Plant go \t Plant back");
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
                    p.GoStation = rmcTruckSchedule.StationID_Go;
                    p.BackStation = rmcTruckSchedule.StationID_Back;
                    planOfTruck.lstPlan.add(p);
                }
            }

            System.out.println(planOfTruck.toString());
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
