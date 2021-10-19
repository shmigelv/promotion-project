package com.shmigel.promotionproject.model;

public enum Roles {
    ROLE_ADMIN, ROLE_INSTRUCTOR, ROLE_STUDENT;

    public static Roles fromValue(String value) {
        for (Roles role : Roles.values()) {
            if (role.name().equalsIgnoreCase(value)) {
                return role;
            }
        }
        return null;
    }

}
