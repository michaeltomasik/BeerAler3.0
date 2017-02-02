package alessandro.firebaseandroid.model;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Alessandro Barreto on 22/06/2016.
 */
public class UserModel implements Serializable {

    private String id;
    private String name;
    private String photo_profile;
    private double latitude;
    private double longitude;

    public UserModel() {
    }

    public UserModel(String name, String photo_profile, String id) {
        this.name = name;
        this.photo_profile = photo_profile;
        this.id = id;
    }

    public UserModel(String id, String name, String photo_profile, double latitude, double longitude) {
        this.id = id;
        this.name = name;
        this.photo_profile = photo_profile;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getPhoto_profile() {
        return photo_profile;
    }

    public void setPhoto_profile(String photo_profile) {
        this.photo_profile = photo_profile;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }


}
