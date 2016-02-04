package cc.leet.leetperms.command;

import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.util.DataManager;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class LpSetGroupCommand extends Command {

    private LeetPerms plugin;
    private DataManager dataManager;

    public LpSetGroupCommand(LeetPerms plugin) {
        super("lpsetgroup", "Sets the target players group in a specified world.", "/lpsetgroup [player] [group] [world]");
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {

        if(!sender.hasPermission("leetperms.lpsetgroup")) {
            sender.sendMessage(TextFormat.RED + "You do not have permission to do that.");
            return true;
        }

        if(args.length < 3 && !plugin.globalPerms) {
            sender.sendMessage(TextFormat.RED + "Type /lpsetgroup [player] [group] [world]");
            return true;
        }

        if(args.length != 2 && plugin.globalPerms) {
            sender.sendMessage(TextFormat.RED + "Type /lpsetgroup [player] [group]");
            return true;
        }

        String player = args[0];
        String group = args[1];
        String world = (plugin.globalPerms ? "global" : args[2]);

        if(plugin.getServer().getPlayer(player) == null && !plugin.getServer().getOfflinePlayer(player).hasPlayedBefore()) {
            sender.sendMessage(TextFormat.RED + "The target player " + TextFormat.AQUA + player + TextFormat.RED +
                    " has not played on this server before.");
            return true;
        }

        if(!dataManager.worldExists(world)) {
            sender.sendMessage(TextFormat.RED + "Target world " + TextFormat.AQUA + world + TextFormat.RED + " does not exist.");
            return true;
        }

        if(!dataManager.groupExists(group, world)) {
            sender.sendMessage(TextFormat.AQUA + world + TextFormat.RED + " does not contain the group " + TextFormat.AQUA + group + TextFormat.RED + ".");
            return true;
        }

        if(dataManager.setGroup(player, group, world))
            sender.sendMessage(TextFormat.GREEN + player + "'s group was set to " + TextFormat.AQUA + group + TextFormat.GREEN +
                    " in " + TextFormat.AQUA + world + TextFormat.GREEN + ".");
        else
            sender.sendMessage(TextFormat.RED + "Failed to set " + player + "'s group to " + TextFormat.AQUA + group +
                    TextFormat.RED + " in " + TextFormat.AQUA + world + TextFormat.RED + ".");

        return true;
    }
}