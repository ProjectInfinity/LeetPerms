package cc.leet.leetperms.persistence;

import cc.leet.leetperms.data.PermissionsGroup;
import cc.leet.leetperms.data.PermissionsWorld;

import java.util.ArrayList;
import java.util.HashMap;

public interface DataProvider {

    ArrayList<PermissionsGroup> getGroups();

    HashMap<String, Object> getPlayer(String player, String world);

    String getPlayerGroup(String player, String world);

    void setPlayerGroup(String player, String group, String world);

    void setDefaultGroup(String group, String world);

    ArrayList<PermissionsWorld> getWorlds();

    void close();

}