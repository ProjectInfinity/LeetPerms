package cc.leet.leetperms.util;

import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.data.PermissionsGroup;

import cc.leet.leetperms.data.PermissionsPlayer;
import cc.leet.leetperms.data.PermissionsWorld;
import cc.leet.leetperms.persistence.DataProvider;
import cc.leet.leetperms.persistence.YamlDataProvider;
import cn.nukkit.Player;
import cn.nukkit.permission.PermissionAttachment;

import java.util.*;

public class DataManager {
    // TODO: Handle world load event and create file for it! Temporary workaround is softdepend on MultiWorld

    // TODO: Remove all printlns.

    private LeetPerms plugin;

    private Map<String, PermissionsGroup> groups;
    private Map<String, PermissionsWorld> worlds;
    private Map<String, PermissionsPlayer> players;

    private Map<String, PermissionAttachment> attachments;

    private DataProvider provider;

    public DataManager(LeetPerms plugin) {
        this.plugin = plugin;
        this.groups = new HashMap<>();
        this.worlds = new HashMap<>();
        this.players = new HashMap<>();
        this.attachments = new HashMap<>();
        /**
         * TODO: This needs to be possible to change
         * whenever other data providers are supported.
         */
        this.provider = new YamlDataProvider(plugin);

        this.load();
    }

    public void load() {

        for(PermissionsWorld world : provider.getWorlds()) {
            this.worlds.put(world.getWorldName(), world);
        }

        for(PermissionsGroup group : provider.getGroups()) {
            this.groups.put(group.getGroupWorld() + "_" + group.getGroupName(), group);
        }

        recalculatePermissions();

    }

    public DataProvider getDataProvider() {
        return this.provider;
    }

    public void updateAttachment(String player, PermissionAttachment attachment) {
        this.attachments.put(player.toLowerCase(), attachment);
    }

    public PermissionAttachment getAttachment(String player) {
        player = player.toLowerCase();
        if(!this.attachments.containsKey(player)) return null;
        return this.attachments.get(player);
    }

    public boolean hasAttachment(String player) {
        return this.attachments.containsKey(player.toLowerCase());
    }

    public void updatePermissions(Player player) {
        this.updatePermissions(player.getName(), (plugin.globalPerms ? "global" : player.getLevel().getName()));
    }
    public void updatePermissions(String name,  String world) {

        Player player = plugin.getServer().getPlayer(name);
        name = name.toLowerCase();

        if(player == null) {
            removeAttachment(name.toLowerCase()); // Posssible todo? Remove all traces of player.
            return;
        }

        if(!this.attachments.containsKey(name)) this.attachments.put(name, player.addAttachment(plugin));

        PermissionsPlayer permPlayer = this.getPlayer(name, (plugin.globalPerms ? "global" : world));

        this.attachments.get(name).clearPermissions();
        this.attachments.get(name).setPermissions(this.groups.get(permPlayer.getGroupPointer()).getGroupPermissions());
        this.attachments.get(name).setPermissions(permPlayer.getPlayerPermissions());

    }

    public void removeAttachment(String player) {
        this.attachments.remove(player.toLowerCase());
    }

    /**
     * Combine permission nodes from inheritance etc.
     */
    public void recalculatePermissions() {

        // TODO: Handle "*"

        double start = 0D;

        if(plugin.debug) {
            start = System.nanoTime();
        }

        // TODO: Need a way to skip the groups that are not part of the target world.
        for(Map.Entry<String, PermissionsGroup> entry : this.groups.entrySet()) {

            Map<String, PermissionsGroup> inheritanceMap = new TreeMap<>();

            PermissionsGroup rootGroup = entry.getValue();
            String rootName = entry.getKey();

            if(rootGroup.getGroupInheritance().size() == 0) continue;

            for(String directInherit : rootGroup.getGroupInheritance()) {
                if(inheritanceMap.containsKey(rootName) || directInherit.equalsIgnoreCase(rootGroup.getGroupName())) continue;
                inheritanceMap.put(directInherit, this.groups.get(rootGroup.getGroupWorld() + "_" + directInherit));
            }

            int i = 1;
            while(true) {
                if(i > 499) {
                    plugin.getLogger().alert("Detected infinite loop while iterating " + rootName);
                    break;
                }
                int size = inheritanceMap.size();
                Map<String, PermissionsGroup> temp = new HashMap<>();
                for(PermissionsGroup group : inheritanceMap.values()) {
                    for(String inherit : group.getGroupInheritance()) {
                        temp.put(inherit, this.groups.get(group.getGroupWorld() + "_" + inherit));
                    }
                }
                inheritanceMap.putAll(temp);
                if(inheritanceMap.size() == size) break;
                i++;
            }

            // TODO: This doesn't sort by who is a higher rank than others...

            System.out.println(rootName + " will inherit:");
            System.out.println(inheritanceMap.keySet().toString());

            System.out.println("Permissions prior to merge:" + rootGroup.getGroupPermissions().toString());

            HashMap<String, Boolean> permissions = new HashMap<>();
            for(PermissionsGroup group : inheritanceMap.values()) permissions.putAll(group.getGroupPermissions());
            permissions.putAll(rootGroup.getGroupPermissions());

            rootGroup.setGroupPermissions(permissions);

            this.groups.put(rootName, rootGroup);

            System.out.println("Permissions after merge: " + rootGroup.getGroupPermissions().entrySet().toString());
            System.out.println("--------------------------------------");

        }

        if(plugin.debug) {
            plugin.getLogger().info("Recalculating permissions took: " + ToolBox.getTimeSpent(start) + "ms");
        }

    }

    public PermissionsPlayer getPlayer(String player, String world) {

        player = player.toLowerCase();
        world = world.toLowerCase();

        // Player is not loaded, get from provider!
        if(!this.players.containsKey(world + "_" + player)) {
            String group = provider.getPlayerGroup(player, world);
            PermissionsPlayer permPlayer = new PermissionsPlayer(
                    player,
                    group,
                    world
            );
            permPlayer.setGroupPointer(world + "_" + group);
            HashMap<String, Object> data = provider.getPlayer(player, world);

            if(data == null) return null;

            // Data size 0 means default group, no entry for user.
            if(data.size() > 0) {
                if(data.get("permissions") instanceof ArrayList) {
                    ArrayList permissions = (ArrayList) data.get("permissions");
                    for(Object permission : permissions) {
                        boolean isTrue = !permission.toString().startsWith("^");
                        permPlayer.setPlayerPermission(isTrue ? permission.toString() : permission.toString().substring(1), isTrue);
                    }
                }

                if(data.get("meta") instanceof LinkedHashMap) {
                    LinkedHashMap meta = (LinkedHashMap) data.get("meta");
                    if(meta.containsKey("last-login")) permPlayer.setLastLogin((int) meta.get("last-login"));
                }
            }
            this.players.put(world + "_" + player, permPlayer);
        }

        return this.players.get(world + "_" + player);

    }

    public boolean setGroup(String player, String group, String world) {

        player = player.toLowerCase();
        group = group.toLowerCase();
        world = world.toLowerCase();

        PermissionsPlayer permPlayer = getPlayer(player, world);


        if(!this.worlds.containsKey(world) || !this.groups.containsKey(world + "_" + group) ||
               permPlayer.getPlayerGroup().equalsIgnoreCase(group)) return false;

        // Update player's group permissions.
        permPlayer.setPlayerGroup(group);
        permPlayer.setGroupPointer(world + "_" + group);

        this.players.put(world + "_" + player, permPlayer);
        this.provider.setPlayerGroup(player, group, world);

        this.updatePermissions(player, world);

        return true;

    }

    public boolean setDefaultGroup(String group, String world) {
        group = group.toLowerCase();
        world = world.toLowerCase();

        if(!this.worlds.containsKey(world) || !this.groups.containsKey(world + "_" + group)) return false;

        PermissionsWorld permWorld = this.worlds.get(world);
        permWorld.setDefaultGroup(group);
        this.worlds.put(world, permWorld);

        this.provider.setDefaultGroup(group, world);

        return this.worlds.get(world).getDefaultGroup().equalsIgnoreCase(group);
    }

    public PermissionsWorld[] getWorlds() {
        return this.worlds.values().toArray(new PermissionsWorld[this.worlds.size()]);
    }

    public boolean worldExists(String world) {
        return this.worlds.containsKey(world.toLowerCase());
    }

    public boolean groupExists(String group, String world) {
        return this.groups.containsKey(world.toLowerCase() + "_" + group.toLowerCase());
    }

    public PermissionsGroup getGroup(String groupPointer) {
        return this.groups.containsKey(groupPointer) ? this.groups.get(groupPointer) : null;
    }

    public PermissionsGroup[] getGroups() {
        return this.groups.values().toArray(new PermissionsGroup[this.groups.size()]);
    }

    public PermissionsGroup getGroup(String group, String world) {
        return this.groups.containsKey(world.toLowerCase() + "_" + group.toLowerCase()) ? this.groups.get(world.toLowerCase() + "_" + group.toLowerCase()) : null;
    }

    public void shutdown() {
        provider.close();
    }

}