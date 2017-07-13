package com.etcxc.android.net.download;

import android.os.Parcel;
import android.os.Parcelable;

/**
 *
 * Created by xwpeng on 17-2-8.
 */

public class DownloadOptions implements Parcelable {
    //唯一标识
    public String url;
    public String targetPath;
    public String cookie;
    //断点续传的开始位置
    public long start;
    //断点续传的结束位置
    public long end;
    public long finished;
    public long total;
    //在通知栏显示下载进度
    public boolean showNotification;
    public int step;
    public int reason;//下载失败的原因，分为网络错误，取消下载

    public DownloadOptions() {}

    protected DownloadOptions(Parcel in) {
        url = in.readString();
        targetPath = in.readString();
        cookie = in.readString();
        start = in.readLong();
        end = in.readLong();
        finished = in.readLong();
        total = in.readLong();
        showNotification = in.readByte() != 0;
        step = in.readInt();
        reason = in.readInt();
    }

    public static final Creator<DownloadOptions> CREATOR = new Creator<DownloadOptions>() {
        @Override
        public DownloadOptions createFromParcel(Parcel in) {
            return new DownloadOptions(in);
        }

        @Override
        public DownloadOptions[] newArray(int size) {
            return new DownloadOptions[size];
        }
    };

    /**
     * 下载进度，以100为总长度。
     * @return 分子，分母是100。如：进度为30%，则return 30.
     */
    public int progress() {
        return total == 0 ? 0 : (int) (finished * 100 / total);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(targetPath);
        dest.writeString(cookie);
        dest.writeLong(start);
        dest.writeLong(end);
        dest.writeLong(finished);
        dest.writeLong(total);
        dest.writeByte((byte) (showNotification ? 1 : 0));
        dest.writeInt(step);
        dest.writeInt(reason);
    }

    public static class Builder {
        private String url;
        private String targetPath;
        private String cookie;
        private long start;
        private long end;
        private boolean showNotification;
/*        这些值不需要初始化构建
public long finished;
        public long total;
        public int step;
        public int reason;
        */

        public Builder() {
        }

        public Builder setUrl(String url) {
            this.url = url;
            return this;
        }

        public Builder setTargetPath(String targetPath) {
            this.targetPath = targetPath;
            return this;
        }

        public Builder setCookie(String cookie) {
            this.cookie = cookie;
            return this;
        }

        public Builder setStart(long start) {
            this.start = start;
            return this;
        }

        public Builder setEnd(long end) {
            this.end = end;
            return this;
        }

        public Builder setShowNotification(boolean showNotification) {
            this.showNotification = showNotification;
            return this;
        }

        public DownloadOptions build() {
            DownloadOptions p = new DownloadOptions();
            p.url = this.url;
            p.targetPath = this.targetPath;
            p.cookie = this.cookie;
            p.start = this.start;
            p.end = this.end;
            p.showNotification = this.showNotification;
            return p;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DownloadOptions)) return false;
        DownloadOptions options = (DownloadOptions) o;
        return url.equals(options.url) ;
    }

    @Override
    public int hashCode() {
        return url.hashCode();
    }
}
