package com.cqdat.master.thesis.gwoforconstruction;

import java.util.ArrayList;

public class RMCStation {
    public String StationID;            // Mã trạm trộn
    public int  c;                      // Số xe ban đầu thuộc trạm trộn tương ứng
    public static int MD;                      // thời gian trộn bê tông tại trạm, giống nhau đối với tất cả các trạm trộn
    public ArrayList<Integer> lstD;     // Danh sách khoảng cách từ trạm trộn đến các công trường
    public int timeFirstTruckGo;

    public ArrayList<ScheduleTruck> lstIDT;     // Danh sách thời gian khởi hành của Xe từ trạm hiện tại (đã được xếp lịch) => IDT[i].OutputTime la gi tri cua IDT
    public ArrayList<Integer> lstTDG;           // Danh sách Thời gian từ trạm trộn đến công trường
    public ArrayList<Integer> lstTDB;           // Danh sách Thời gian từ công trường về trạm trộn

    public RMCStation(){
        lstD = new ArrayList<Integer>();
        lstIDT = new ArrayList<ScheduleTruck>();

        lstTDG = new ArrayList<Integer>();
        lstTDB = new ArrayList<Integer>();

        timeFirstTruckGo = 0;
    }

    public void calTimeGoBack(float vOfGo, float vOfBack){
        for(int i = 0; i < lstD.size(); i++){
            int TDG = (int)(((lstD.get(i) * 3600) / vOfGo) / 60);
            int TDB = (int)(((lstD.get(i) * 3600) / vOfBack) / 60);

            lstTDG.add(TDG);
            lstTDB.add(TDB);
        }
    }

    public void initListOfIDT(int N){
        for(int i = 1; i <= N; i++){
            ScheduleTruck st = new ScheduleTruck();
            st.truckID = i;
            st.inputTime = 0;
            st.outputTime = timeFirstTruckGo + MD * (i - 1);
            st.stationID = StationID;

            lstIDT.add(st);
        }
    }

    @Override
    public String toString(){
        String s = StationID + "\t" + c + "\t" + MD + "\t";
        for(Integer i : lstD){
            s += i + "|";
        }
        return s.substring(0,s.length() - 1);
    }
}
