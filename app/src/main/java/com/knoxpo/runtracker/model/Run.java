package com.knoxpo.runtracker.model;

import java.util.Date;

/**
 * Created by Tejas Sherdiwala on 12/5/2016.
 * &copy; Knoxpo
 */

public class Run {
    private long mId;
    private Date mStartDate;

    public Run(){
        mId = -1;
        mStartDate = new Date();
    }

    public Date getStartDate() {
        return mStartDate;
    }

    public void setStartDate(Date startDate) {
        mStartDate = startDate;
    }

    public int getDurationSecond(long endMillis){
            return (int) ((endMillis - mStartDate.getTime())/1000);
    }

    public static String FormatDuration(int durationSeconds){
        int seconds = durationSeconds % 60;
        int minute = ((durationSeconds - seconds)/60) % 60;
        int hour = (durationSeconds - (minute * 60)-seconds)/3600;
        return String.format("%02d:%02d:%02d",hour,minute,seconds);
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        mId = id;
    }
}
