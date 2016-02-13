package cc.leet.leetperms.command;

import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.data.PermissionsGroup;
import cc.leet.leetperms.data.PermissionsWorld;
import cc.leet.leetperms.util.DataManager;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LpGroupsCommand extends Command {

    private LeetPerms plugin;
    private DataManager dataManager;

    public LpGroupsCommand(LeetPerms plugin) {
        super("lpgroups", "Shows available groups.", "/lpgroups");
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {

        if(!sender.hasPermission("leetperms.lpgroups")) {
            sender.sendMessage(TextFormat.RED + "You do not have permission to do that.");
            return true;
        }

        HashMap<String, ArrayList<String>> data = new HashMap<>();

        for(PermissionsWorld world : dataManager.getWorlds()) {
            data.put(world.getWorldName(), new ArrayList<>());
        }

        for(PermissionsGroup group : dataManager.getGroups()) {
            if(!data.containsKey(group.getGroupWorld())) data.put(group.getGroupWorld(), new ArrayList<>());
            data.get(group.getGroupWorld()).add(group.getGroupName());
        }

        for(Map.Entry<String, ArrayList<String>> entry : data.entrySet()) {
            sender.sendMessage(TextFormat.YELLOW + "Groups in " + TextFormat.AQUA + entry.getKey() + TextFormat.YELLOW + ":");
            boolean color = true;
            StringBuilder msg = new StringBuilder();
            for(String group : entry.getValue()) {
                msg.append(color ? TextFormat.WHITE : TextFormat.GRAY).append(group).append(", ");
                color = !color;
            }
            if(msg.length() == 0)
                sender.sendMessage(TextFormat.GRAY + "No groups.");
            else
                sender.sendMessage(msg.toString().substring(0, msg.length() - 2));
        }

        return true;
    }
}