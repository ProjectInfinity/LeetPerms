package cc.leet.leetperms.command;

import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.util.DataManager;
import cc.leet.leetperms.util.ToolBox;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

import java.util.ArrayList;
import java.util.HashMap;

public class LpAddGroupCommand extends Command {

    private LeetPerms plugin;
    private DataManager dataManager;

    public LpAddGroupCommand(LeetPerms plugin) {
        super("lpaddgroup", "Adds a group to the target world.", "/lpaddgroup [group] [world] [optional meta]");
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {

        if(!sender.hasPermission("leetperms.lpaddgroup")) {
            sender.sendMessage(TextFormat.RED + "You do not have permission to do that.");
            return true;
        }

        if(args.length < 1 || (!plugin.globalPerms && args.length < 2)) {
            sender.sendMessage(TextFormat.RED + "You need to specify a " + (plugin.globalPerms ? "group to be added." : "group and the world add it to."));
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

        if(!ToolBox.isAlphaNumeric(group)) {
            sender.sendMessage(TextFormat.RED + "Group name " + TextFormat.AQUA + group + TextFormat.RED + " is invalid.");
            return true;
        }

        if(dataManager.groupExists(group, world)) {
            sender.sendMessage(TextFormat.RED + "Target group " + TextFormat.AQUA + group +
                    TextFormat.RED + " already exists in world " + TextFormat.AQUA + world +
                    TextFormat.RED + "."
            );
            return true;
        }

        boolean result;

        if((plugin.globalPerms && args.length > 1) || args.length > 2) {
            args[0] = null;
            if(!plugin.globalPerms) args[1] = null;
            HashMap<String, Object> meta = new HashMap<>();
            for(String arg : args) {
                // Only continue if the argument is not null and the meta is split with a colon.
                if(arg == null || arg.split(":").length < 2) continue;
                String[] a = arg.split(":", 2);

                switch(a[0].toUpperCase()) {

                    // TODO: Allow specifying permissions.

                    case "INHERITANCE":
                    case "INHERIT":

                        if(!meta.containsKey("inheritance")) meta.put("inheritance", new ArrayList<>());
                        ArrayList inherited = (ArrayList) meta.get("inheritance");

                        if(a[1].split(",").length > 1) {

                            String[] b = a[1].split(",");
                            for(String grp : b) {
                                grp = grp.toLowerCase();
                                if(inherited.contains(grp)) continue;
                                inherited.add(grp);
                                meta.put("inheritance", inherited);
                            }

                        } else {
                            inherited.add(a[1].toLowerCase());
                        }

                        meta.put("inheritance", inherited);

                        // Double check that all inherited groups exist, otherwise fail.
                        for(Object grp : (ArrayList) meta.get("inheritance")) {
                            if(!dataManager.groupExists(grp.toString(), world)) {
                                sender.sendMessage(TextFormat.AQUA + group + TextFormat.RED + " can't inherit " +
                                        TextFormat.AQUA + grp.toString() + TextFormat.RED + " because it doesn't exist.");
                                return true;
                            }
                        }

                        break;

                    case "TITLE":
                    case "PREFIX":

                        meta.put("prefix", a[1]);

                        break;

                }
            }
            result = dataManager.addGroup(group, world, meta);
        } else {
            result = dataManager.addGroup(group, world, null);
        }

        if(result)
            sender.sendMessage(TextFormat.GREEN + "Added group " + TextFormat.AQUA + group +
                TextFormat.GREEN + " to world " + TextFormat.AQUA + world + TextFormat.GREEN + "."
            );
        else
            sender.sendMessage(TextFormat.RED + "Failed to add group " + TextFormat.AQUA + group +
                    TextFormat.RED + " to world " + TextFormat.AQUA + world + TextFormat.RED + "."
            );

        return true;
    }
}