package cc.leet.leetperms.command;

import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.util.DataManager;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;

public class LpInheritCommand extends Command {

    private LeetPerms plugin;
    private DataManager dataManager;

    public LpInheritCommand(LeetPerms plugin) {
        super("lpinherit", "Sets the groups to be inherited for a target group.", "/lpinherit [group] [world] [groups to inherit]");
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {

        if(!sender.hasPermission("leetperms.lpinherit")) {
            sender.sendMessage(TextFormat.RED + "You do not have permission to do that.");
            return true;
        }

        if(args.length < 3 && !plugin.globalPerms) {
            sender.sendMessage(TextFormat.RED + "Type /lpsetgroup [group] [world] [groups to inherit]");
            return true;
        }

        if(args.length < 2 && plugin.globalPerms) {
            sender.sendMessage(TextFormat.RED + "Type /lpsetgroup [group] [groups to inherit]");
            return true;
        }

        String group = args[0];
        String world = (plugin.globalPerms ? "global" : args[1]);

        if(!dataManager.worldExists(world)) {
            sender.sendMessage(TextFormat.RED + "Target world " + TextFormat.AQUA + world + TextFormat.RED + " does not exist.");
            return true;
        }

        if(!dataManager.groupExists(group, world)) {
            sender.sendMessage(TextFormat.AQUA + world + TextFormat.RED + " does not contain the group " + TextFormat.AQUA + group + TextFormat.RED + ".");
            return true;
        }

        ArrayList<String> groups = new ArrayList<>();

        int i = 0;
        for(String arg : args) {
            if(i < 2) {
                i++;
                continue;
            }

            if(arg.equalsIgnoreCase("*NONE*")) break;

            if(arg.equalsIgnoreCase(group)) {
                sender.sendMessage(TextFormat.RED + "Tried to inherit the target group. Please revise your inheritance.");
                return true;
            }

            if(groups.contains(arg)) continue;

            if(!dataManager.groupExists(arg, world)) {
                sender.sendMessage(TextFormat.AQUA + world + TextFormat.RED + " does not contain the group " + TextFormat.AQUA + group + TextFormat.RED + ", please revise your inheritance.");
                return true;
            }

            groups.add(arg.toLowerCase());
        }

        if(dataManager.setInheritance(group, world, groups.toArray(new String[groups.size()])))
            sender.sendMessage(TextFormat.GREEN + group + "'s inheritance was set to " + TextFormat.AQUA + groups.toString() + TextFormat.GREEN +
                    " in " + TextFormat.AQUA + world + TextFormat.GREEN + ".");
        else
            sender.sendMessage(TextFormat.RED + "Failed to set " + group + "'s inheritance to " + TextFormat.AQUA + group +
                    TextFormat.RED + " in " + TextFormat.AQUA + world + TextFormat.RED + ".");

        return true;


    }
}