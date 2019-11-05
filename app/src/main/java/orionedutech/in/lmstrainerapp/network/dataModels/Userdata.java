package orionedutech.in.lmstrainerapp.network.dataModels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Userdata {
    @SerializedName("userid")
    @Expose
    private String userid;
    @SerializedName("user_name")
    @Expose
    private String userName;
    @SerializedName("userprofileid")
    @Expose
    private String userprofileid;
    @SerializedName("user_password")
    @Expose
    private String userPassword;
    @SerializedName("user_blocked")
    @Expose
    private String userBlocked;
    @SerializedName("user_first_attempt_time")
    @Expose
    private String userFirstAttemptTime;
    @SerializedName("user_attempts")
    @Expose
    private String userAttempts;
    @SerializedName("user_role_id")
    @Expose
    private String userRoleId;
    @SerializedName("user_role_name")
    @Expose
    private String userRoleName;
    @SerializedName("user_role_permissions")
    @Expose
    private Object userRolePermissions;
    @SerializedName("user_admin_id")
    @Expose
    private String userAdminId;
    @SerializedName("user_fullname")
    @Expose
    private String userFullname;
    @SerializedName("user_fullname_sub")
    @Expose
    private String userFullnameSub;
    @SerializedName("useremail")
    @Expose
    private String useremail;
    @SerializedName("user_phone_no")
    @Expose
    private String userPhoneNo;
    @SerializedName("user_code")
    @Expose
    private Object userCode;
    @SerializedName("user_admin_type")
    @Expose
    private String userAdminType;
    @SerializedName("batch_id")
    @Expose
    private String batchId;
    @SerializedName("center_id")
    @Expose
    private String centerId;
    @SerializedName("batch_name")
    @Expose
    private String batchName;
    @SerializedName("center_name")
    @Expose
    private String centerName;
    @SerializedName("created_date")
    @Expose
    private String createdDate;

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserprofileid() {
        return userprofileid;
    }

    public void setUserprofileid(String userprofileid) {
        this.userprofileid = userprofileid;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserBlocked() {
        return userBlocked;
    }

    public void setUserBlocked(String userBlocked) {
        this.userBlocked = userBlocked;
    }

    public String getUserFirstAttemptTime() {
        return userFirstAttemptTime;
    }

    public void setUserFirstAttemptTime(String userFirstAttemptTime) {
        this.userFirstAttemptTime = userFirstAttemptTime;
    }

    public String getUserAttempts() {
        return userAttempts;
    }

    public void setUserAttempts(String userAttempts) {
        this.userAttempts = userAttempts;
    }

    public String getUserRoleId() {
        return userRoleId;
    }

    public void setUserRoleId(String userRoleId) {
        this.userRoleId = userRoleId;
    }

    public String getUserRoleName() {
        return userRoleName;
    }

    public void setUserRoleName(String userRoleName) {
        this.userRoleName = userRoleName;
    }

    public Object getUserRolePermissions() {
        return userRolePermissions;
    }

    public void setUserRolePermissions(Object userRolePermissions) {
        this.userRolePermissions = userRolePermissions;
    }

    public String getUserAdminId() {
        return userAdminId;
    }

    public void setUserAdminId(String userAdminId) {
        this.userAdminId = userAdminId;
    }

    public String getUserFullname() {
        return userFullname;
    }

    public void setUserFullname(String userFullname) {
        this.userFullname = userFullname;
    }

    public String getUserFullnameSub() {
        return userFullnameSub;
    }

    public void setUserFullnameSub(String userFullnameSub) {
        this.userFullnameSub = userFullnameSub;
    }

    public String getUseremail() {
        return useremail;
    }

    public void setUseremail(String useremail) {
        this.useremail = useremail;
    }

    public String getUserPhoneNo() {
        return userPhoneNo;
    }

    public void setUserPhoneNo(String userPhoneNo) {
        this.userPhoneNo = userPhoneNo;
    }

    public Object getUserCode() {
        return userCode;
    }

    public void setUserCode(Object userCode) {
        this.userCode = userCode;
    }

    public String getUserAdminType() {
        return userAdminType;
    }

    public void setUserAdminType(String userAdminType) {
        this.userAdminType = userAdminType;
    }

    public String getBatchId() {
        return batchId;
    }

    public void setBatchId(String batchId) {
        this.batchId = batchId;
    }

    public String getCenterId() {
        return centerId;
    }

    public void setCenterId(String centerId) {
        this.centerId = centerId;
    }

    public String getBatchName() {
        return batchName;
    }

    public void setBatchName(String batchName) {
        this.batchName = batchName;
    }

    public String getCenterName() {
        return centerName;
    }

    public void setCenterName(String centerName) {
        this.centerName = centerName;
    }

    public String getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }
}
