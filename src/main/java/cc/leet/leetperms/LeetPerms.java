package cc.leet.leetperms;

import cc.leet.leetperms.command.*;
import cc.leet.leetperms.listener.PermsListener;
import cc.leet.leetperms.util.DataManager;

import cn.nukkit.plugin.PluginBase;

public class LeetPerms extends PluginBase {

    private static LeetPerms plugin;
    private DataManager dataManager;

    public boolean globalPerms;
    public boolean autoSave;
    public boolean debug;
    public boolean isPermissionsLocked;

    public String dataProviderType;

    @Override
    public void onEnable() {
        plugin = this;
        saveDefaultConfig();
        reloadSettings();

        dataManager = new DataManager(plugin);

        /** Register all commands **/
        getServer().getCommandMap().register("lpinfo", new LpInfoCommand(plugin));
        getServer().getCommandMap().register("lpgroups", new LpGroupsCommand(plugin));

        // All commands that let you change permissions should be placed below this line.
        if(!isPermissionsLocked) {
            getServer().getCommandMap().register("lpsave", new LpSaveCommand(plugin));
            getServer().getCommandMap().register("lpsetgroup", new LpSetGroupCommand(plugin));
            getServer().getCommandMap().register("lpsetdefault", new LpSetDefaultCommand(plugin));
            getServer().getCommandMap().register("lpaddgroup", new LpAddGroupCommand(plugin));
        }
        /** Done registering commands **/

        getServer().getPluginManager().registerEvents(new PermsListener(plugin), plugin);

    }

    @Override
    public void onDisable() {
        getDataManager().shutdown();
    }

    public void reloadSettings() {
        autoSave = getConfig().getBoolean("auto-save", true);
        globalPerms = getConfig().getBoolean("global-perms", false);
        debug = getConfig().getBoolean("debug", false);
        isPermissionsLocked = getConfig().getBoolean("lock-permission-files", false);
        dataProviderType = getConfig().getString("data-provider", "yaml");
    }

    public static LeetPerms getPlugin() {
        return plugin;
    }

    public DataManager getDataManager() {
        return dataManager;
    }

}