package com.example.onetour.enumeration;

public enum RoleEnum {

    TOURISTGUIDE("TouristGuide"),
    USER("User");

    private final String roleName;

    RoleEnum(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleName() {
        return roleName;
    }

    public static RoleEnum fromString(String roleName) {
        for (RoleEnum role : RoleEnum.values()) {
            if (role.getRoleName().equalsIgnoreCase(roleName)) {
                return role;
            }
        }
        throw new IllegalArgumentException("No enum constant for role: " + roleName);
    }
}
