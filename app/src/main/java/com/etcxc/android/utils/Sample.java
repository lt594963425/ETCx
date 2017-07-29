package com.etcxc.android.utils;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.etcxc.android.base.App;
import com.etcxc.android.bean.ApiRecord;
import com.etcxc.android.modle.db.DbUtil;
import com.etcxc.android.modle.db.PublicDbHelper;
import com.etcxc.android.modle.db.PublicUriField;

import java.util.ArrayList;
import java.util.List;

/**
 * 代码示例，复制粘贴提高效率
 * Created by xwpeng on 2017/7/28.
 */

public class Sample {
    /**
     * 网络请求示例
     */
    private void netModel() {
  /*      Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {

            }
        }).compose(RxUtil.io())
                .compose(RxUtil.activityLifecycle(this)).subscribe(new Consumer<String>() {
            @Override
            public void accept(@NonNull String s) throws Exception {

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(@NonNull Throwable throwable) throws Exception {
                LogUtil.e(TAG, "net", throwable);
                ToastUtils.showToast(R.string.request_failed);
            }
        });*/
    }

    private static Context mContext = App.get();
    /**
     * 数据库insert操作
     */
    public static long insert(ApiRecord apiRecord) {
        Cursor cursor = mContext.getContentResolver().query(PublicUriField.API_RECORD_URI, null,
                PublicDbHelper.ApiRecordColumns.NAME + "=? ", new String[]{apiRecord.name}, null);
        if (cursor == null || !cursor.moveToFirst()) { // 不存在
            DbUtil.closeCursor(cursor);
            Uri uri = mContext.getContentResolver().insert(PublicUriField.API_RECORD_URI, setContentValues(apiRecord));
            if (uri != null) {
                return ContentUris.parseId(uri);
            } else {
                return 0L;
            }
        } else {
            DbUtil.closeCursor(cursor);
            return mContext.getContentResolver().update(PublicUriField.API_RECORD_URI, setContentValues(apiRecord),
                    PublicDbHelper.ApiRecordColumns.NAME + "=? ", new String[]{apiRecord.name});
        }
    }

    /**
     *查询所有
     * @return
     */
    public static List<ApiRecord> queryAll() {
        List<ApiRecord> results = new ArrayList<>();
        Cursor cursor = mContext.getContentResolver().query(PublicUriField.API_RECORD_URI, null, null, null, null);
        if (cursor != null)
            while (cursor.moveToNext()) {
                ApiRecord apiRecord = cursorToNextApiRecord(cursor);
                results.add(apiRecord);
            }
        DbUtil.closeCursor(cursor);
        return results;
    }

    public static ContentValues setContentValues(ApiRecord apiRecord) {
        ContentValues values = new ContentValues();
        values.put(PublicDbHelper.ApiRecordColumns.NAME, apiRecord.name);
        values.put(PublicDbHelper.ApiRecordColumns.REQUEST_DATA, apiRecord.requestData);
        values.put(PublicDbHelper.ApiRecordColumns.REQUEST_TTIME, apiRecord.requestTime);
        values.put(PublicDbHelper.ApiRecordColumns.RESPONSE_DATA, apiRecord.responseData);
        values.put(PublicDbHelper.ApiRecordColumns.RESPONSE_TIME, apiRecord.responseTime);
        values.put(PublicDbHelper.ApiRecordColumns.URL, apiRecord.url);
        values.put(PublicDbHelper.ApiRecordColumns.UID, apiRecord.uid);
        return values;
    }

    private static ApiRecord cursorToNextApiRecord(Cursor cursor) {
        ApiRecord apiRecord = new ApiRecord();
        apiRecord.name = cursor.getString(cursor.getColumnIndex(PublicDbHelper.ApiRecordColumns.NAME));
        apiRecord.uid = cursor.getString(cursor.getColumnIndex(PublicDbHelper.ApiRecordColumns.UID));
        apiRecord.url = cursor.getString(cursor.getColumnIndex(PublicDbHelper.ApiRecordColumns.URL));
        apiRecord.requestData = cursor.getString(cursor.getColumnIndex(PublicDbHelper.ApiRecordColumns.REQUEST_DATA));
        apiRecord.requestTime = cursor.getLong(cursor.getColumnIndex(PublicDbHelper.ApiRecordColumns.REQUEST_TTIME));
        apiRecord.responseData = cursor.getString(cursor.getColumnIndex(PublicDbHelper.ApiRecordColumns.RESPONSE_DATA));
        apiRecord.responseTime = cursor.getLong(cursor.getColumnIndex(PublicDbHelper.ApiRecordColumns.RESPONSE_TIME));
        return apiRecord;
    }

}
