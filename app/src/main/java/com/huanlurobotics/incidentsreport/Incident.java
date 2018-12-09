/*
 * Copyright (c) 2018. HuanLu Robotics. Todos los derechos reservados / All rigths reserved.
 */

package com.huanlurobotics.incidentsreport;

import java.util.Date;
import java.util.UUID;

public class Incident {

    private UUID mId;
    private String mTitle;
    private String mDetails;
    private Date mDate;
    private boolean mSolved;
    private String mSuspect;
    private String mSuspectPhone;

    public Incident () {
        this(UUID.randomUUID());
    }

    public Incident (UUID id) {
        mId = id;
        mTitle = "";
        mDetails = "";
        mDate = new Date();
        mSolved = false;
        mSuspect = "";
        mSuspectPhone = "";
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public String getDetails() {
        return mDetails;
    }

    public void setDetails(String details) {
        mDetails = details;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getSuspectPhone() {
        return mSuspectPhone;
    }

    public void setSuspectPhone(String suspectPhone) {
        mSuspectPhone = suspectPhone;
    }

    public String getPhotoFilename () { //unique name for the image for the incident
        return "IMG_" + getId().toString() + ".jpg";
    }

    @Override
    public String toString() {
        return "Incident{" +
                "mId=" + mId +
                ", mTitle='" + mTitle + '\'' +
                ", mDetails='" + mDetails + '\'' +
                ", mDate=" + mDate +
                ", mSolved=" + mSolved +
                ", mSuspect='" + mSuspect + '\'' +
                ", mSuspectPhone='" + mSuspectPhone + '\'' +
                '}';
    }
}
