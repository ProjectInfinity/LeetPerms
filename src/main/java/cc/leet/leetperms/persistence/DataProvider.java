package cc.leet.leetperms.persistence;

import cc.leet.leetperms.data.PermissionsGroup;
import cc.leet.leetperms.data.PermissionsWorld;
import cn.nukkit.utils.Config;

import java.util.ArrayList;
import java.util.HashMap;

public interface DataProvider {

    ArrayList<PermissionsGroup> getGroups();

    ArrayList<PermissionsGroup> getGroupsFromWorld(String world);

    HashMap<String, Object> getPlayer(String player, String world);

    boolean setPermission(String group, String world, String permission);

    boolean removePermission(String group, String world, String permission);

    boolean setPlayerPermission(String player, String world, String permission);

    String getPlayerGroup(String player, String world);

    void setPlayerGroup(String player, String group, String world);

    void setDefaultGroup(String group, String world);

    boolean addGroup(String group, String world, HashMap<String, Object> meta);

    boolean deleteGroup(String group, String world);

    PermissionsGroup loadGroup(String group, String world);

    PermissionsWorld loadWorld(String world);

    ArrayList<PermissionsWorld> getWorlds();

    void close();

    void updateLastLogin(String player);

}