package id.kiosku.xbug;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Dodi on 12/16/2016.
 */

public class DataBug implements Parcelable {
    @SerializedName("package_name")
    public String packageName;
    public String tanggal;
    @SerializedName("device_id")
    public String deviceId;
    @SerializedName("device_brand")
    public String deviceBrand;
    @SerializedName("device_type")
    public String deviceType;
    @SerializedName("device_os")
    public String deviceOs;
    @SerializedName("device_version")
    public String deviceVersion;
    @SerializedName("device_res")
    public String deviceRes;
    public String subject;
    public String message;
    public int type;


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.packageName);
        dest.writeString(this.tanggal);
        dest.writeString(this.deviceId);
        dest.writeString(this.deviceBrand);
        dest.writeString(this.deviceType);
        dest.writeString(this.deviceOs);
        dest.writeString(this.deviceVersion);
        dest.writeString(this.deviceRes);
        dest.writeString(this.subject);
        dest.writeString(this.message);
        dest.writeInt(this.type);
    }

    public DataBug() {
    }

    protected DataBug(Parcel in) {
        this.packageName = in.readString();
        this.tanggal = in.readString();
        this.deviceId = in.readString();
        this.deviceBrand = in.readString();
        this.deviceType = in.readString();
        this.deviceOs = in.readString();
        this.deviceVersion = in.readString();
        this.deviceRes = in.readString();
        this.subject = in.readString();
        this.message = in.readString();
        this.type = in.readInt();
    }

    public static final Creator<DataBug> CREATOR = new Creator<DataBug>() {
        @Override
        public DataBug createFromParcel(Parcel source) {
            return new DataBug(source);
        }

        @Override
        public DataBug[] newArray(int size) {
            return new DataBug[size];
        }
    };
}
