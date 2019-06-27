package com.kotchasaan.stocktaking.util;

public class StockTake {
    String TagID ;
    String TagDOC ;
    String TagCODE ;
    String TotalCount ;
    String TimerCount ;
    public StockTake(String TagID, String TagDOC, String TagCODE, String TotalCount, String TimerCount){
        this.TagID = TagID ;
        this.TagDOC = TagDOC ;
        this.TagCODE = TagCODE ;
        this.TotalCount = TotalCount ;
        this.TimerCount = TimerCount ;
    }
    public String getTagID() {
        return TagID;
    }
    public String getTagCODE() {
        return TagCODE;
    }
    public String getTotalCount() {
        return TotalCount;
    }
    public String getTimerCount() {
        return TimerCount;
    }
    public String getTagDOC() {
        return TagDOC;
    }
}
