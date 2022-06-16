package com.cqdat.master.thesis.gwoforconstruction;

public class ConstructionType {
    public String constructionName;
    public int TPT;

    @Override
    public String toString(){
        return constructionName + " - " + TPT;
    }
}
