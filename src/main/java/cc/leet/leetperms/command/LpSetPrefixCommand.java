package cc.leet.leetperms.command;

import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.util.DataManager;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;

public class LpSetPrefixCommand extends Command {

    private LeetPerms plugin;
    private DataManager dataManager;

    public LpSetPrefixCommand(LeetPerms plugin) {
        super("lpsetprefix", "Set prefix for a group.", "/lpsetprefix [group] [world] [prefix]");
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {

        if(!sender.hasPermission("leetperms.lpsetprefix")) {
            sender.sendMessage(TextFormat.RED + "You do not have permission to do that.");
            return true;
        }

        if(args.length < 3 && !plugin.globalPerms) {
            sender.sendMessage(TextFormat.RED + "Type /lpsetprefix [group] [world] [prefix]");
            return true;
        }

        if(args.length < 2 && plugin.globalPerms) {
            sender.sendMessage(TextFormat.RED + "Type /lpsetprefix [group] [prefix]");
            return true;
        }

        String group = args[0];
        String world = (plugin.globalPerms ? "global" : args[1]);
        StringBuilder prefix = new StringBuilder();

        int i = 0;
        for(String arg : args) {
            if(i < 2) {
                i++;
                continue;
            }
            prefix.append(arg);
        }

        if(!dataManager.worldExists(world)) {
            sender.sendMessage(TextFormat.RED + "Target world " + TextFormat.AQUA + world + TextFormat.RED + " does not exist.");
            return true;
        }

        if(!dataManager.groupExists(group, world)) {
            sender.sendMessage(TextFormat.AQUA + world + TextFormat.RED + " does not contain the group " + TextFormat.AQUA + group + TextFormat.RED + ".");
            return true;
        }

        if(dataManager.setPrefix(group, world, prefix.toString()))
            sender.sendMessage(TextFormat.GREEN + group + "'s prefix was set to " + TextFormat.RESET + prefix.toString() + TextFormat.GREEN +
                    " in " + TextFormat.AQUA + world + TextFormat.GREEN + ".");
        else
            sender.sendMessage(TextFormat.RED + "Failed to set " + group + "'s prefix to " + TextFormat.RESET + prefix.toString() +
                    TextFormat.RED + " in " + TextFormat.AQUA + world + TextFormat.RED + ".");

        return true;

    }
}
