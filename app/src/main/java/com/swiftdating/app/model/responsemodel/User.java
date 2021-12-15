package com.swiftdating.app.model.responsemodel;

import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class User {
    @SerializedName("reactionForUser")
    @Expose
    public List<ReactionForUser> reactionForUser;
    @SerializedName("isVip")
    @Expose
    public boolean isVip;
    @SerializedName("isPremium")
    @Expose
    public boolean isPremium;
    @SerializedName("isDeluxe")
    @Expose
    public boolean isDeluxe;
    @SerializedName("VIPExpiry")
    @Expose
    public String VIPExpiry;
    @SerializedName("viptokenAppliedOn")
    @Expose
    public String viptokenAppliedOn;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("roleId")
    @Expose
    private Integer roleId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("isLinkedinUser")
    @Expose
    private String isLinkedinUser;
    @SerializedName("deletedAt")
    @Expose
    private String deletedAt;
    @SerializedName("createdAt")
    @Expose
    private String createdAt;
    @SerializedName("updatedAt")
    @Expose
    private String updatedAt;
    @SerializedName("insta")
    @Expose
    private ArrayList<InstagramImageModel.Datum> insta;
    @SerializedName("profileOfUser")
    @Expose
    private ProfileOfUser profileOfUser;
    @SerializedName("ImageForUser")
    @Expose
    private List<ImageForUser> imageForUser;
    @SerializedName("selfieVerificationStatus")
    @Expose
    private String selfieVerificationStatus;

    public String getSelfieVerificationStatus() {
        return selfieVerificationStatus;
    }

    public void setSelfieVerificationStatus(String selfieVerificationStatus) {
        this.selfieVerificationStatus = selfieVerificationStatus;
    }

    public List<ReactionForUser> getReactionForUser() {
        return reactionForUser;
    }

    public void setReactionForUser(List<ReactionForUser> reactionForUser) {
        this.reactionForUser = reactionForUser;
    }

    public String getViptokenAppliedOn() {
        return viptokenAppliedOn;
    }

    public void setViptokenAppliedOn(String viptokenAppliedOn) {
        this.viptokenAppliedOn = viptokenAppliedOn;
    }

    public String getVIPExpiry() {
        return VIPExpiry;
    }

    public void setVIPExpiry(String VIPExpiry) {
        this.VIPExpiry = VIPExpiry;
    }

    public boolean isVip() {
        return isVip;
    }

    public void setVip(boolean vip) {
        isVip = vip;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void setPremium(boolean premium) {
        isPremium = premium;
    }

    public boolean isDeluxe() {
        return isDeluxe;
    }

    public void setDeluxe(boolean deluxe) {
        isDeluxe = deluxe;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getRoleId() {
        return roleId;
    }

    public void setRoleId(Integer roleId) {
        this.roleId = roleId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(String deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getisLinkedinUser() {
        return isLinkedinUser;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public ProfileOfUser getProfileOfUser() {
        return profileOfUser;
    }

    public void setProfileOfUser(ProfileOfUser profileOfUser) {
        this.profileOfUser = profileOfUser;
    }

    public List<ImageForUser> getImageForUser() {
        return imageForUser;
    }

    public void setImageForUser(List<ImageForUser> imageForUser) {
        this.imageForUser = imageForUser;
    }

    public ArrayList<InstagramImageModel.Datum> getInsta() {
        return insta;
    }

    public void setInsta(ArrayList<InstagramImageModel.Datum> insta) {
        this.insta = insta;
    }

    @Override
    public String toString() {
        return "User{" +
                "reactionForUser=" + reactionForUser +
                ", isVip=" + isVip +
                ", isPremium=" + isPremium +
                ", isDeluxe=" + isDeluxe +
                ", VIPExpiry='" + VIPExpiry + '\'' +
                ", viptokenAppliedOn='" + viptokenAppliedOn + '\'' +
                ", id=" + id +
                ", email='" + email + '\'' +
                ", roleId=" + roleId +
                ", status='" + status + '\'' +
                ", isLinkedinUser='" + isLinkedinUser + '\'' +
                ", deletedAt='" + deletedAt + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", updatedAt='" + updatedAt + '\'' +
                ", insta=" + insta +
                ", profileOfUser=" + profileOfUser +
                ", imageForUser=" + imageForUser +
                ", selfieVerificationStatus='" + selfieVerificationStatus + '\'' +
                '}';
    }

    public User(List<ReactionForUser> reactionForUser, boolean isVip, boolean isPremium, boolean isDeluxe, String VIPExpiry, String viptokenAppliedOn, Integer id, String email, Integer roleId, String status, String isLinkedinUser, String deletedAt, String createdAt, String updatedAt, ArrayList<InstagramImageModel.Datum> insta, ProfileOfUser profileOfUser, List<ImageForUser> imageForUser) {
        this.reactionForUser = reactionForUser;
        this.isVip = isVip;
        this.isPremium = isPremium;
        this.isDeluxe = isDeluxe;
        this.VIPExpiry = VIPExpiry;
        this.viptokenAppliedOn = viptokenAppliedOn;
        this.id = id;
        this.email = email;
        this.roleId = roleId;
        this.status = status;
        this.isLinkedinUser = isLinkedinUser;
        this.deletedAt = deletedAt;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.insta = insta;
        this.profileOfUser = profileOfUser;
        this.imageForUser = imageForUser;
    }

    public static class InstagramModel {
        String id;
        String media_type;
        String media_url;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getMedia_type() {
            return media_type;
        }

        public void setMedia_type(String media_type) {
            this.media_type = media_type;
        }

        public String getMedia_url() {
            return media_url;
        }

        public void setMedia_url(String media_url) {
            this.media_url = media_url;
        }

    }
}
