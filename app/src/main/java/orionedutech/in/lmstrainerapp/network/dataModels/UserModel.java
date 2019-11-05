package orionedutech.in.lmstrainerapp.network.dataModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserModel {
    @SerializedName("success")
    @Expose
    private String success;
    @SerializedName("userdata")
    @Expose
    private Userdata userdata;

    public String getSuccess() {
        return success;
}

    public Userdata getUserdata() {
        return userdata;
    }
}
