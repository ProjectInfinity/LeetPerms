package cc.leet.leetperms.command;

import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.util.DataManager;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class LpSetPlayerPermCommand extends Command {

    private LeetPerms plugin;
    private DataManager dataManager;

    public LpSetPlayerPermCommand(LeetPerms plugin) {
        super("lpsetplayerperm", "Sets a permission for a player.", "/lpsetplayerperm [player] [permission.node] <world>", new String[]{"lpsetplayernode"});
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {

        if(!sender.hasPermission("leetperms.lpsetplayerperm")) {
            sender.sendMessage(TextFormat.RED + "You do not have permission do to that.");
            return true;
        }

        if(args.length == 0 || args.length < 2) {
            sender.sendMessage(TextFormat.RED + "You need to specify a player" + (plugin.globalPerms ? " " : " a world ") + "and a permission node.");
            return true;
        }

        String player = args[0];
        String permission = args[1];
        String world;

        if(plugin.getServer().getPlayer(player) == null && !plugin.getServer().getOfflinePlayer(player).hasPlayedBefore()) {
            sender.sendMessage(TextFormat.RED + "The target player " + TextFormat.AQUA + player + TextFormat.RED +
                    " has not played on this server before.");
            return true;
        }

        if(args.length < 3 && !(sender instanceof Player)) {
            world = plugin.getServer().getDefaultLevel().getName();
        } else {
            world = plugin.globalPerms ? "global" : (args.length > 2 ? args[2] : ((Player) sender).getLevel().getName());
        }

        boolean isTrue = true;
        int type = 0;
        if(permission.startsWith("^") || permission.startsWith("-") ||
                (permission.split(":").length > 1 && permission.split(":")[1].equalsIgnoreCase("false"))) {
            isTrue = false;
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
                result = this.dataManager.setPlayerPermission(player, world, permission);
                nodeMsg = permission;
                break;

            // ^node
            case 1:
                result = this.dataManager.setPlayerPermission(player, world, permission);
                nodeMsg = permission.substring(1);
                break;

            // -node
            case 2:
                StringBuilder node = new StringBuilder();
                result = this.dataManager.setPlayerPermission(player, world, node.append(permission.substring(1)).insert(0, "^").toString());
                nodeMsg = permission.substring(1);
                break;

            // node:false
            case 3:
                StringBuilder permNode = new StringBuilder();
                result = this.dataManager.setPlayerPermission(player, world, permNode.append(permission.split(":")[0]).insert(0, "^").toString());
                nodeMsg = permNode.toString().substring(1);
                break;

        }

        if(result)
            sender.sendMessage(TextFormat.GREEN + "Set permission node " + TextFormat.AQUA + nodeMsg +
                    TextFormat.GREEN + " to " + TextFormat.AQUA + isTrue + TextFormat.GREEN + " for player " + TextFormat.AQUA +
                    player + TextFormat.GREEN + " in world " + TextFormat.AQUA + world + TextFormat.GREEN + "."
            );
        else
            sender.sendMessage(TextFormat.RED + "Failed to set permission node " + TextFormat.AQUA + nodeMsg +
                    TextFormat.RED + " to " + TextFormat.AQUA + isTrue + TextFormat.RED + " for player " + TextFormat.AQUA +
                    player + TextFormat.RED + " in world " + TextFormat.AQUA + world + TextFormat.RED + "."
            );

        return true;

    }
}