package cc.leet.leetperms.command;

import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.util.DataManager;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class LpDelPermCommand extends Command {

    private LeetPerms plugin;
    private DataManager dataManager;

    public LpDelPermCommand(LeetPerms plugin) {
        super("lpdelperm", "Deletes a permission node", "/lpdelperm [group] [permission.node] [world]", new String[]{"lpdelnode"});
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {

        if(!sender.hasPermission("leetperms.lpdelperm")) {
            sender.sendMessage(TextFormat.RED + "You do not have permission to do that.");
            return true;
        }
        if(args.length == 0 || args.length < 2) {
            sender.sendMessage(TextFormat.RED + "You need to specify a group" + (plugin.globalPerms ? " " : " a world ") + "and a permission node.");
            return true;
        }

        String group = args[0];
        String world;

        if(args.length < 3 && !(sender instanceof Player)) {
            world = plugin.getServer().getDefaultLevel().getName();
        } else {
            world = plugin.globalPerms ? "global" : (args.length > 2 ? args[2] : ((Player) sender).getLevel().getName());
        }

        if(!dataManager.worldExists(world)) {
            sender.sendMessage(TextFormat.RED + "Target world " + TextFormat.AQUA + world + TextFormat.RED + " does not exist.");
            return true;
        }

        if(!dataManager.groupExists(group, world)) {
            sender.sendMessage(TextFormat.RED + "Target group " + TextFormat.AQUA + group + TextFormat.RED + " does not exist in " + TextFormat.AQUA + world + TextFormat.RED);
            return true;
        }

        String permission = args[1];
        int type = 0;
        if(permission.startsWith("^") || permission.startsWith("-") ||
                (permission.split(":").length > 1 && permission.split(":")[1].equalsIgnoreCase("false"))) {
            if(permission.startsWith("^")) type = 1;
            if(permission.startsWith("-")) type = 2;
            if(permission.split(":").length > 1 && permission.split(":")[1].equalsIgnoreCase("false")) type = 3;
        }

        boolean result = false;
        String nodeMsg = "";

        switch(type) {

            // True permission node
            case 0:
                if(!Character.isLetterOrDigit(permission.substring(0, 1).toCharArray()[0]) || permission.contains(":")) {
                    sender.sendMessage(TextFormat.RED + permission + " is a invalid permission node.");
                    return true;
                }
                result = this.dataManager.removePermission(group, world, permission);
                nodeMsg = permission;
                break;

            // ^node
            case 1:
                result = this.dataManager.removePermission(group, world, permission);
                nodeMsg = permission.substring(1);
                break;

            // -node
            case 2:
                StringBuilder node = new StringBuilder();
                result = this.dataManager.removePermission(group, world, node.append(permission.substring(1)).insert(0, "^").toString());
                nodeMsg = permission.substring(1);
                break;

            // node:false
            case 3:
                StringBuilder permNode = new StringBuilder();
                result = this.dataManager.removePermission(group, world, permNode.append(permission.split(":")[0]).insert(0, "^").toString());
                nodeMsg = permNode.toString().substring(1);
                break;

        }

        if(result)
            sender.sendMessage(TextFormat.GREEN + "Deleted permission node " + TextFormat.AQUA + nodeMsg +
                    TextFormat.GREEN + " from group " + TextFormat.AQUA +
                    group + TextFormat.GREEN + " in world " + TextFormat.AQUA + world + TextFormat.GREEN + "."
            );
        else
            sender.sendMessage(TextFormat.RED + "Failed to delete permission node " + TextFormat.AQUA + nodeMsg +
                    TextFormat.RED + " from group " + TextFormat.AQUA +
                    group + TextFormat.RED + " in " + TextFormat.AQUA + world + TextFormat.RED + "."
            );

        return true;

    }
}