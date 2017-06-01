package com.shcyd.lib.helper;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * @author dongyu.wang
 * @version 1.0.0
 * @desc
 * @date 2015-11-02
 * @time 16:53
 * @changlog
 * @fixme
 */
public class ImageBucket implements Parcelable {
    public int count;
    public String bucketName;
    public ArrayList<MediaItem> imageItems;

    public ImageBucket() {
    }

    protected ImageBucket(Parcel in) {
        this.count = in.readInt();
        this.bucketName = in.readString();
        this.imageItems = in.createTypedArrayList(MediaItem.CREATOR);
    }

    public static final Creator<ImageBucket> CREATOR = new Creator<ImageBucket>() {
        @Override
        public ImageBucket createFromParcel(Parcel in) {
            return new ImageBucket(in);
        }

        @Override
        public ImageBucket[] newArray(int size) {
            return new ImageBucket[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.count);
        dest.writeString(this.bucketName);
        dest.writeTypedList(imageItems);
    }

    public int getCount(){
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getBucketName() {
        return bucketName;
    }

    public void setBucketName(String bucketName) {
        this.bucketName = bucketName;
    }

    public ArrayList<MediaItem> getMediaItems() {
        return imageItems;
    }

    public void setMediaItems(ArrayList<MediaItem> imageItems) {
        this.imageItems = imageItems;
    }
}
