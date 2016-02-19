package cc.leet.leetperms.command;

import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.util.DataManager;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class LpReloadCommand extends Command {

    private DataManager dataManager;

    public LpReloadCommand(LeetPerms plugin) {
        super("lpreload", "Reloads everything from disk and recalculates permissions.", "/lpreload");
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {

        if(!sender.hasPermission("leetperms.lpreload")) {
            sender.sendMessage(TextFormat.RED + "You do not have permission to do that.");
            return true;
        }

        dataManager.reloadWorlds();
        dataManager.reloadGroups();
        dataManager.recalculatePermissions();
        dataManager.updatePermissions();

        sender.sendMessage(TextFormat.GREEN + "All worlds, groups and permissions have been reloaded.");

        return true;

    }
}