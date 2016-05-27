package gmrr.kidzarea.activity;

import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Rifqi Zuliansyah on 15/04/2016.
 */
public class Lokasi implements Parcelable {

    private String unique_id;
    private String name;
    private double longitude;
    private double latitude;
    private String waktu;


    public Lokasi(Location lokasi) {
        this.longitude = lokasi.getLongitude();
        this.latitude = lokasi.getLatitude();
    }

    public Lokasi(String name, double longitude, double latitude) {
        this.name = name;
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Lokasi(String unique_id, String name,  double longitude, double latitude) {
        this(name, longitude, latitude);
        this.unique_id = unique_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getWaktu() {
        return waktu;
    }

    public void setWaktu(String waktu) {
        this.waktu = waktu;
    }

    /*Parcelable implements*/

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(unique_id);
        dest.writeString(name);
        dest.writeDouble(longitude);
        dest.writeDouble(latitude);
        dest.writeString(waktu);

    }

    public static final Parcelable.Creator<Lokasi> CREATOR = new Parcelable.Creator<Lokasi>() {
        @Override
        public Lokasi createFromParcel(Parcel source) {
            return new Lokasi(source);
        }

        @Override
        public Lokasi[] newArray(int size) {
            return new Lokasi[size];
        }
    };

    private Lokasi(Parcel source) {
        unique_id = source.readString();
        name = source.readString();
        longitude = source.readDouble();
        latitude = source.readDouble();
        waktu = source.readString();
    }

}