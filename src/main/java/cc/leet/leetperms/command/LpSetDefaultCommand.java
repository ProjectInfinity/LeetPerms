package cc.leet.leetperms.command;

import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.util.DataManager;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class LpSetDefaultCommand extends Command {

    private LeetPerms plugin;
    private DataManager dataManager;

    public LpSetDefaultCommand(LeetPerms plugin) {
        super("lpsetdefault", "Sets the default group in the target world.", "/lpsetdefault [group] [world]");
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {

        if(!sender.hasPermission("leetperms.lpsetdefault")) {
            sender.sendMessage(TextFormat.RED + "You do not have permission to do that.");
            return true;
        }

        if((args.length < 2 && !plugin.globalPerms) || (plugin.globalPerms && args.length == 0)) {
            sender.sendMessage(TextFormat.RED + "You need to specify " +
                    (plugin.globalPerms ? "a group " : "a group and a world ") + "to be made default.");
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
                TextFormat.RED + " does not exist in world " + TextFormat.AQUA + world +
                TextFormat.RED + "."
            );
            return true;
        }

        if(this.dataManager.setDefaultGroup(group, world))
            sender.sendMessage(TextFormat.GREEN + "Set default group to " + TextFormat.AQUA + group +
                TextFormat.GREEN + " in world " + TextFormat.AQUA + world + TextFormat.GREEN + "."
            );
        else
            sender.sendMessage(TextFormat.RED + "Failed to set default group to " + TextFormat.AQUA + group +
                    TextFormat.RED + " in world " + TextFormat.AQUA + world + TextFormat.RED + "."
            );

        return true;
    }
}