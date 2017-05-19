package com.example.criminalintent.bean;

import java.util.Date;
import java.util.UUID;

/**
 * Created by yubin on 2017/4/23.
 */

public class Crime {

    private UUID mId;

    private String mTitle;

    private Date mDate;

    private Date mTime;

    private String mSuspect;

    private boolean mIsSolved;

    public Crime() {
        this(UUID.randomUUID());
    }

    public Crime(UUID Id) {
        mId = Id;
        mDate = new Date();
        mTime = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public void setId(UUID id) {
        mId = id;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public Date getTime() {
        return mTime;
    }

    public void setTime(Date time) {
        mTime = time;
    }

    public boolean isSolved() {
        return mIsSolved;
    }

    public void setSolved(boolean solved) {
        mIsSolved = solved;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
