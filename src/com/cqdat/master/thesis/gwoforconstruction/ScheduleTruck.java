package com.cqdat.master.thesis.gwoforconstruction;

public class ScheduleTruck {
    public int truckID;
    public int inputTime = 0;
    public int outputTime = 0;
    public String stationID = "";

    @Override
    public String toString(){
        return truckID + "\t" + inputTime + "\t" + outputTime + "\tat:" + stationID;
    }
}
