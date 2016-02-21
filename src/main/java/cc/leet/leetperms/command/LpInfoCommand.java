package cc.leet.leetperms.command;

import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.data.PermissionsPlayer;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.permission.PermissionAttachment;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LpInfoCommand extends Command {

    private LeetPerms plugin;

    public LpInfoCommand(LeetPerms plugin) {
        super("lpinfo", "Show your or another players current permissions.", "/lpinfo [optional player]");
        this.plugin = plugin;
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {

        // TODO: Show other info about the player such as meta, lastlogin etc.

        if(!sender.hasPermission("leetperms.lpinfo")) {
            sender.sendMessage(TextFormat.RED + "You do not have permission to do that.");
            return true;
        }

        String target = args.length == 0 ? sender.getName() : args[0];
        String world;

        if(args.length == 1 && !(sender instanceof Player)) {
            world = plugin.getServer().getDefaultLevel().getName();
        } else {
            world = args.length == 0 ? "global" : (args.length > 1 ? args[1] : ((Player) sender).getLevel().getName());
        }

        if(!plugin.getDataManager().worldExists(world)) {
            sender.sendMessage(TextFormat.RED + "Target world " + TextFormat.AQUA + world + TextFormat.RED + " does not exist.");
            return true;
        }

        if(plugin.getServer().getPlayer(target) == null && !plugin.getServer().getOfflinePlayer(target).hasPlayedBefore()) {
            sender.sendMessage(TextFormat.RED + "The target player " + TextFormat.AQUA + target + TextFormat.RED +
                " has not played on this server before.");
            return true;
        }

        HashMap<String, Boolean> permissions = new HashMap<>();

        PermissionAttachment permissionAttachment = plugin.getDataManager().getAttachment(target);
        PermissionsPlayer permPlayer = plugin.getDataManager().getPlayer(target, world);

        if(permissionAttachment == null && permPlayer == null) {
            sender.sendMessage(TextFormat.RED + "An error occurred. You or your target have no permissions assigned.");
            return true;
        }

        if(permissionAttachment != null) {
            permissions.putAll(permissionAttachment.getPermissions());
        }

        if(permPlayer != null) {
            if(plugin.getDataManager().groupExists(permPlayer.getPlayerGroup() , permPlayer.getPlayerWorld())) permissions.putAll(plugin.getDataManager().getGroup(permPlayer.getGroupPointer()).getGroupPermissions());
            permissions.putAll(permPlayer.getPlayerPermissions());
        }

        ArrayList<String> allowed = new ArrayList<>();
        ArrayList<String> denied = new ArrayList<>();

        for(Map.Entry<String, Boolean> permission : permissions.entrySet()) {
            if(permission.getValue()) {
                allowed.add(permission.getKey());
            } else {
                denied.add(permission.getKey());
            }
        }

        StringBuilder allowedMsg = new StringBuilder();
        StringBuilder deniedMsg = new StringBuilder();

        boolean color = true;
        for(String permission : allowed) {
            allowedMsg.append((color ? TextFormat.WHITE : TextFormat.GRAY)).append(permission).append(", ");
            color = !color;
        }

        color = true;
        for(String permission : denied) {
            deniedMsg.append((color ? TextFormat.WHITE : TextFormat.GRAY)).append(permission).append(", ");
            color = !color;
        }

        sender.sendMessage(TextFormat.YELLOW + "Permission data for " + TextFormat.AQUA + target +
                TextFormat.YELLOW + " in world " + TextFormat.AQUA + world + TextFormat.YELLOW + ":");
        if(permPlayer != null) sender.sendMessage(TextFormat.YELLOW + "Group: " + TextFormat.AQUA + permPlayer.getPlayerGroup());

        sender.sendMessage(TextFormat.GREEN + "Permissions granted:");
        sender.sendMessage(TextFormat.AQUA + (allowedMsg.length() > 0 ? allowedMsg.toString().substring(0, allowedMsg.length() - 2) : "No permissions."));

        sender.sendMessage(TextFormat.RED + "Permissions negated:");
        sender.sendMessage(TextFormat.AQUA + (deniedMsg.length() > 0 ? deniedMsg.toString().substring(0, deniedMsg.length() - 2) : "No negated permissions."));

        return true;

    }
}