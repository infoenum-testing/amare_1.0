package com.swift.dating.model.requestmodel;

public class ResendRequest {

    private String email;

    public ResendRequest(String s) {
        this.email = s;
    }

    public String getEmail() {
        return email;
    }
}
