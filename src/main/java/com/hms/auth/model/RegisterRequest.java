package com.hms.auth.model;

public record RegisterRequest(String email, String firstName, String lastName, String password) {}
