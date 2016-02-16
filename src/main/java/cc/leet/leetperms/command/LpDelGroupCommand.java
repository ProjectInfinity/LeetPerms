package cc.leet.leetperms.command;

import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.util.DataManager;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class LpDelGroupCommand extends Command {

    private LeetPerms plugin;
    private DataManager dataManager;

    public LpDelGroupCommand(LeetPerms plugin) {
        super("lpdelgroup", "Deletes a permission group from a world", "/lpdelgroup [group] [world]");
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {

        if(!sender.hasPermission("leetperms.lpdelgroup")) {
            sender.sendMessage(TextFormat.RED + "You do not have permission to do that.");
            return true;
        }

        if(args.length < 1 || (!plugin.globalPerms && args.length < 2)) {
            sender.sendMessage(TextFormat.RED + "You need to specify a " + (plugin.globalPerms ? "group to be removed." : "group and the world to remove it from."));
            return true;
        }

        String world = plugin.globalPerms ? "global" : args[1];
        if(!dataManager.worldExists(world)) {
            sender.sendMessage(TextFormat.RED + "Target world " + TextFormat.AQUA + world +
                    TextFormat.RED + " does not exist."
            );
            return true;
        }

        String group = args[0];

        if(!dataManager.groupExists(group, world)) {
            sender.sendMessage(TextFormat.RED + "Target group " + TextFormat.AQUA + group +
                    TextFormat.RED + " does not exist in " + TextFormat.AQUA + world +
                    TextFormat.RED + "."
            );
            return true;
        }

        if(dataManager.deleteGroup(group, world))
            sender.sendMessage(TextFormat.GREEN + "Deleted group " + TextFormat.AQUA + group +
                    TextFormat.GREEN + " from world " + TextFormat.AQUA + world + TextFormat.GREEN + "."
            );
        else
            sender.sendMessage(TextFormat.RED + "Failed to delete group " + TextFormat.AQUA + group +
                    TextFormat.RED + " from world " + TextFormat.AQUA + world + TextFormat.RED + "."
            );

        return true;

    }
}