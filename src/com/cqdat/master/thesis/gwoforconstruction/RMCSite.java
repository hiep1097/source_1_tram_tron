package com.cqdat.master.thesis.gwoforconstruction;

public class RMCSite {
    public int siteID;  // Mã công trường
    public int value;   // Giá trị do thuật toán GWO trả về để thực hiện sắp lịch khởi tạo ban đầu
    public int k = 0;   // Lần đồ thứ k của công trường

    @Override
    public String toString(){
        return siteID + "\t" + k + "\t" + value;
    }
}
