package cc.leet.leetperms.data;

import java.util.HashMap;

public class PermissionsPlayer {

    private String playerWorld;
    private String playerName;
    private String playerGroup;
    private String groupPointer;

    private HashMap<String, Boolean> playerPermissions;

    private int lastLogin;

    public PermissionsPlayer() {
        this.playerPermissions = new HashMap<>();
    }

    public PermissionsPlayer(String playerName, String playerGroup, String playerWorld) {
        this.playerName = playerName;
        this.playerGroup = playerGroup;
        this.playerWorld = playerWorld;
        this.playerPermissions = new HashMap<>();
        this.groupPointer = playerWorld.toLowerCase() + "_" + playerGroup;
    }

    public String getPlayerWorld() {
        return playerWorld;
    }

    public void setPlayerWorld(String playerWorld) {
        this.playerWorld = playerWorld;
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public String getPlayerGroup() {
        return playerGroup;
    }

    public void setPlayerGroup(String playerGroup) {
        this.playerGroup = playerGroup;
    }

    public HashMap<String, Boolean> getPlayerPermissions() {
        return playerPermissions;
    }

    public void setPlayerPermissions(HashMap<String, Boolean> playerPermissions) {
        this.playerPermissions = playerPermissions;
    }

    public void setPlayerPermission(String node, boolean isTrue) {
        this.playerPermissions.put(node, isTrue);
    }

    public String getGroupPointer() {
        return groupPointer;
    }

    public void setGroupPointer(String groupPointer) {
        this.groupPointer = groupPointer;
    }

    public int getLastLogin() {
        return lastLogin;
    }

    public void setLastLogin(int lastLogin) {
        this.lastLogin = lastLogin;
    }
}