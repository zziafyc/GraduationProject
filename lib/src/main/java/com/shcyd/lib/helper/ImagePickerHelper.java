package com.shcyd.lib.helper;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author dongyu.wang
 * @version 1.0.0
 * @desc
 * @date 2015-11-02
 * @time 16:48
 * @changlog
 * @fixme
 */
public class ImagePickerHelper {

    private static ImagePickerHelper instance;

    private Context mContext;
    private ContentResolver mContentResolver;

    /**
     * 缩略图集合：key=image_id，value=image_path
     */
    private HashMap<String, String> mThumbnailList;
    /**
     * 相册集合：key=bucket_id，value=ImageBucket
     *
     * @see ImageBucket
     */
    private HashMap<String, ImageBucket> mBucketList;

    private ImagePickerHelper() {
    }

    public static ImagePickerHelper getInstance() {
        if (instance == null) {
            synchronized (ImagePickerHelper.class) {
                if (instance == null) {
                    instance = new ImagePickerHelper();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化
     *
     * @param context
     */
    public void init(Context context) {
        if (this.mContext == null) {
            this.mContext = context;
            this.mContentResolver = context.getContentResolver();
            mThumbnailList = new HashMap<String, String>();
            mBucketList = new HashMap<String, ImageBucket>();
        }
    }

    /**
     * 获取相册列表
     *
     * @return
     * @see ImageBucket 相册实体类
     */
    public List<ImageBucket> getImageBucketList() {
        _buildImagesBucketList();
        List<ImageBucket> tmpList = new ArrayList<ImageBucket>();
        Iterator<Map.Entry<String, ImageBucket>> iterator = mBucketList.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, ImageBucket> entry = iterator.next();
            tmpList.add(entry.getValue());
        }
        return tmpList;
    }

    public List<MediaItem> getMediaItemList() {
        String[] columns = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.PICASA_ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };

        Cursor cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);
        ArrayList<MediaItem> imageItems = null;
        if (cursor.moveToFirst()) {
            imageItems = new ArrayList<MediaItem>();
            int photoIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int photoPathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

            do {
                String bucketCoverId = cursor.getString(photoIdIndex);
                String bucketCoverPath = cursor.getString(photoPathIndex);

                MediaItem imageItem = new MediaItem();
                imageItem.setImageId(bucketCoverId);
                imageItem.setImagePath(bucketCoverPath);
                imageItem.setThumbnailPath(mThumbnailList.get(bucketCoverId));
                imageItems.add(imageItem);

            } while (cursor.moveToNext());
        }
        return imageItems;
    }

    boolean hasBuildImagesBucketList = false;

    private void _buildImagesBucketList() {
        _getThumbnail();
        mBucketList.clear();

        String[] columns = new String[]{
                MediaStore.Images.Media._ID,
                MediaStore.Images.Media.BUCKET_ID,
                MediaStore.Images.Media.PICASA_ID,
                MediaStore.Images.Media.DATA,
                MediaStore.Images.Media.DISPLAY_NAME,
                MediaStore.Images.Media.TITLE,
                MediaStore.Images.Media.SIZE,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME
        };

        Cursor cursor = mContentResolver.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null, null, null);

        if (cursor.moveToFirst()) {
            int photoIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
            int photoPathIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            int bucketDisplayNameIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
            int bucketIdIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_ID);

            do {
                String bucketId = cursor.getString(bucketIdIndex);
                String bucketName = cursor.getString(bucketDisplayNameIndex);
                String bucketCoverId = cursor.getString(photoIdIndex);
                String bucketCoverPath = cursor.getString(photoPathIndex);

                ImageBucket bucket = mBucketList.get(bucketId);
                if (bucket == null) {
                    bucket = new ImageBucket();
                    mBucketList.put(bucketId, bucket);
                    bucket.imageItems = new ArrayList<MediaItem>();
                    bucket.bucketName = bucketName;
                }

                bucket.count++;
                MediaItem imageItem = new MediaItem();
                imageItem.setImageId(bucketCoverId);
                imageItem.setImagePath(bucketCoverPath);
                imageItem.setThumbnailPath(mThumbnailList.get(bucketCoverId));
                bucket.imageItems.add(imageItem);

            } while (cursor.moveToNext());
            cursor.close();

        }
        hasBuildImagesBucketList = true;
    }

    /**
     * 加载图片缩略图
     */
    private void _getThumbnail() {

        mThumbnailList.clear();
        String[] projection = {
                MediaStore.Images.Thumbnails._ID,
                MediaStore.Images.Thumbnails.IMAGE_ID,
                MediaStore.Images.Thumbnails.DATA
        };

        Cursor cursor = mContentResolver.query(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, projection, null, null, null);
        if (cursor.moveToFirst()) {
            int image_id;
            String image_path;
            int image_idColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.IMAGE_ID);
            int dataColumn = cursor.getColumnIndex(MediaStore.Images.Thumbnails.DATA);
            do {

                image_id = cursor.getInt(image_idColumn);
                image_path = cursor.getString(dataColumn);

                mThumbnailList.put(String.format("%d", image_id), image_path);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

}
