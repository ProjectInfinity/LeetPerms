package cc.leet.leetperms.util;

import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.data.PermissionsGroup;
import cc.leet.leetperms.data.PermissionsPlayer;
import cn.nukkit.Player;

public class PermissionAPI {

    private LeetPerms plugin;
    private DataManager dataManager;

    public PermissionAPI() {
        this.plugin = LeetPerms.getPlugin();
        this.dataManager = plugin.getDataManager();
    }

    /**
     * Gets the prefix of the target player in their current world.
     *
     * @param player Player object
     * @return String
     */
    public String getPrefix(Player player) {
        if(!dataManager.worldExists(player.getLevel().getName())) return null;
        PermissionsPlayer permPlayer = dataManager.getPlayer(player.getName(), player.getLevel().getName());
        if(permPlayer == null) return null;
        return this.getPrefix(permPlayer.getPlayerGroup(), permPlayer.getPlayerWorld());
    }

    /**
     * Gets the prefix for the target group in the target world.
     *
     * @param group Group name
     * @param world World name
     * @return String
     */
    public String getPrefix(String group, String world) {
        if(!dataManager.groupExists(group, world)) return null;
        PermissionsGroup permGroup = dataManager.getGroup(group, world);
        if(permGroup == null) return null;
        return permGroup.getGroupPrefix();
    }

}