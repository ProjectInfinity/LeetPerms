package cc.leet.leetperms.listener;

import cc.leet.leetperms.LeetPerms;
import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.entity.EntityLevelChangeEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerKickEvent;
import cn.nukkit.event.player.PlayerQuitEvent;

public class PermsListener implements Listener {

    private LeetPerms plugin;

    public PermsListener(LeetPerms plugin) {
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        plugin.getDataManager().updatePermissions(event.getPlayer());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        plugin.getDataManager().removeAttachment(event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerKick(PlayerKickEvent event) {
        plugin.getDataManager().removeAttachment(event.getPlayer().getName());
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerLevelChange(EntityLevelChangeEvent event) {
        if(!(event.getEntity() instanceof Player)) return;
        plugin.getDataManager().updatePermissions((Player) event.getEntity());
    }


}