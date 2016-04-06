package cc.leet.leetperms.command;

import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.data.PermissionsGroup;
import cc.leet.leetperms.util.DataManager;
import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.Map;

public class LpGroupInfoCommand extends Command {

    private LeetPerms plugin;
    private DataManager dataManager;

    public LpGroupInfoCommand(LeetPerms plugin) {
        super("lpgroupinfo", "Views information about a group", "/lpgroupinfo [group] [world]");
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {

        if(!sender.hasPermission("leetperms.lpgroupinfo")) {
            sender.sendMessage(TextFormat.RED + "You do not have permission to do that.");
            return true;
        }

        if(args.length == 0) {
            sender.sendMessage(TextFormat.RED + "You need to specify a group" + (plugin.globalPerms ? "." : " and world."));
            return true;
        }

        String group = args[0];
        String world;

        if(args.length == 1 && !(sender instanceof Player)) {
            world = plugin.getServer().getDefaultLevel().getName();
        } else {
            world = plugin.globalPerms ? "global" : (args.length > 1 ? args[1] : ((Player) sender).getLevel().getName());
        }

        if(!dataManager.worldExists(world)) {
            sender.sendMessage(TextFormat.RED + "Target world " + TextFormat.AQUA + world + TextFormat.RED + " does not exist.");
            return true;
        }

        if(!dataManager.groupExists(group, world)) {
            sender.sendMessage(TextFormat.RED + "Target group " + TextFormat.AQUA + group + TextFormat.RED + " does not exist in world " + TextFormat.AQUA + world + TextFormat.RED);
            return true;
        }

        PermissionsGroup permGroup = dataManager.getGroup(group, world);

        if(permGroup == null) {
            sender.sendMessage(TextFormat.RED + "An error occurred when getting the permissions group.");
            return true;
        }

        ArrayList<String> allowed = new ArrayList<>();
        ArrayList<String> denied = new ArrayList<>();

        for(Map.Entry<String, Boolean> permission : permGroup.getGroupPermissions().entrySet()) {
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

        sender.sendMessage(TextFormat.YELLOW + "Data for " + TextFormat.AQUA + group +
                TextFormat.YELLOW + " in " + TextFormat.AQUA + world + TextFormat.YELLOW + ":");

        if(!permGroup.getGroupPrefix().isEmpty()) sender.sendMessage(TextFormat.YELLOW + "Prefix: " + TextFormat.RESET + permGroup.getGroupPrefix());

        if(permGroup.getGroupInheritance().size() > 0) {
            StringBuilder groups = new StringBuilder();
            color = true;
            for(String inheritedGroup : permGroup.getGroupInheritance()) {
                groups.append(color ? TextFormat.WHITE : TextFormat.GRAY).append(inheritedGroup).append(", ");
                color = !color;
            }
            sender.sendMessage(TextFormat.YELLOW + "Inherited groups: " + groups.toString().substring(0, groups.length() - 2));
        }

        sender.sendMessage(TextFormat.GREEN + "Permissions granted:");
        sender.sendMessage(TextFormat.AQUA + (allowedMsg.length() > 0 ? allowedMsg.toString().substring(0, allowedMsg.length() - 2) : "No permissions."));

        sender.sendMessage(TextFormat.RED + "Permissions negated:");
        sender.sendMessage(TextFormat.AQUA + (deniedMsg.length() > 0 ? deniedMsg.toString().substring(0, deniedMsg.length() - 2) : "No negated permissions."));

        return true;
    }
}