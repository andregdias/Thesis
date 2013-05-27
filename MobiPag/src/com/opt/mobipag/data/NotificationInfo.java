package com.opt.mobipag.data;

import android.os.Parcel;
import android.os.Parcelable;

class NotificationInfo implements Parcelable {
    private final String email;
    private final String title;
    private final int titleid;
    private final int time;
    private final String date;

    public NotificationInfo(String email, String title, int titleid, int time, String date) {
        this.email = email;
        this.title = title;
        this.titleid = titleid;
        this.time = time;
        this.date = date;
    }

// --Commented out by Inspection START (21/05/13 15:25):
//    public NotificationInfo(Parcel in) {
//        this.email = in.readString();
//        this.title = in.readString();
//        this.titleid = in.readInt();
//        this.time = in.readInt();
//        this.date = in.readString();
//    }
// --Commented out by Inspection STOP (21/05/13 15:25)

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.email);
        dest.writeString(this.title);
        dest.writeInt(this.titleid);
        dest.writeInt(this.time);
        dest.writeString(this.date);
    }
}