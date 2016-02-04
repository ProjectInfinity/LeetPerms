package cc.leet.leetperms.data;

public class PermissionsWorld {

    private String worldName;
    private String defaultGroup;

    public PermissionsWorld() {}

    public PermissionsWorld(String worldName, String defaultGroup) {
        this.worldName = worldName;
        this.defaultGroup = defaultGroup;
    }

    public String getWorldName() {
        return worldName;
    }

    public void setWorldName(String worldName) {
        this.worldName = worldName;
    }

    public String getDefaultGroup() {
        return defaultGroup;
    }

    public void setDefaultGroup(String defaultGroup) {
        this.defaultGroup = defaultGroup;
    }
}