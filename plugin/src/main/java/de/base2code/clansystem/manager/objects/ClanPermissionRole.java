package de.base2code.clansystem.manager.objects;

public enum ClanPermissionRole {
    MEMBER(0), MODERATOR(10), OWNER(100);

    private final int level;

    ClanPermissionRole(int level) {
        this.level = level;
    }

    public int getLevel() {
        return level;
    }
}
