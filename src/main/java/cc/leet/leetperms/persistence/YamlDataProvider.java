package cc.leet.leetperms.persistence;

import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.data.PermissionsGroup;
import cc.leet.leetperms.data.PermissionsWorld;
import cc.leet.leetperms.util.DataManager;
import cc.leet.leetperms.util.ToolBox;

import cn.nukkit.level.Level;
import cn.nukkit.utils.Config;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;

public class YamlDataProvider implements DataProvider {

    private LeetPerms plugin;

    private Map<String, Config> permissionsFiles;

    public YamlDataProvider(LeetPerms plugin) {
        this.plugin = plugin;
        this.permissionsFiles = new HashMap<>();

        if(!Files.isDirectory(Paths.get(plugin.getDataFolder().getAbsolutePath() + "/worlds"))) {
            try {
                Files.createDirectory(Paths.get(plugin.getDataFolder().getAbsolutePath() + "/worlds"));
                plugin.getLogger().info("Created 'worlds' in plugins/LeetPerms.");
            } catch (IOException e) {
                plugin.getLogger().critical("Failed to create worlds folder, LeetPerms will not function correctly!");
                e.printStackTrace();
            }
        }

        if (plugin.globalPerms) {
            Config permFile = new Config(plugin.getDataFolder().getAbsolutePath() + "/permissions.yml");
            this.permissionsFiles.put("permissions", permFile);
        } else {
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(Paths.get(plugin.getDataFolder().getAbsolutePath() + "/worlds"),
                    entry -> {
                        return entry.getFileName().toString().endsWith(".yml");
                    })) {
                for (Path path : directoryStream) {
                    this.permissionsFiles.put(path.getFileName().toString().toLowerCase().substring(0, path.getFileName().toString().length() - 4),
                            new Config(path.toString()));
                }
            } catch(IOException e) {
                plugin.getLogger().critical("Failed to load a permissions file!");
                e.printStackTrace();
            }
            // Figure out what needs to be mirrored so we can just point the permissions file to the target world.
            if(!DataManager.mirroredWorlds.isEmpty()) {
                for(Map.Entry<String, String> entry : DataManager.mirroredWorlds.entrySet()) {
                    if(!this.permissionsFiles.containsKey(entry.getKey()) || !this.permissionsFiles.containsKey(entry.getValue())) continue;
                    this.permissionsFiles.put(entry.getKey(), this.permissionsFiles.get(entry.getValue()));
                }
            }
        }
    }

    @Override
    public ArrayList<PermissionsGroup> getGroups() {

        double start = 0D;

        if(plugin.debug) {
            start = System.nanoTime();
        }

        Map<String, PermissionsGroup> groups = new HashMap<>();

        if(plugin.globalPerms) {
            Config permFile = this.permissionsFiles.get("permissions");
            groups.putAll(getGroupsFromWorld("global", permFile));
        } else {
            for(Map.Entry<String, Config> permMap : this.permissionsFiles.entrySet()) {
                groups.putAll(getGroupsFromWorld(permMap.getKey(), permMap.getValue()));
            }
        }

        if(plugin.debug) {
            plugin.getLogger().info("Loading all groups took " + ToolBox.getTimeSpent(start) + "ms");
        }

        return new ArrayList<>(groups.values());
    }

    private HashMap<String, PermissionsGroup> getGroupsFromWorld(String world, Config permFile) {

        HashMap<String, PermissionsGroup> groups = new HashMap<>();

        Object grps = permFile.get("groups");

        if(grps instanceof LinkedHashMap) {
            for(Object group : ((LinkedHashMap) grps).entrySet()) {
                Map.Entry entry = (Map.Entry) group;
                PermissionsGroup permGroup = new PermissionsGroup(
                        (String) entry.getKey(),
                        world
                );

                // Check if there are any permissions.
                if(permFile.get("groups." + permGroup.getGroupName() + ".permissions") == null || !(permFile.get("groups." + permGroup.getGroupName() + ".permissions") instanceof ArrayList)) {
                    plugin.getLogger().alert(permGroup.getGroupName() + " was not loaded in world " + permGroup.getGroupWorld() + " because of missing permissions.");
                    continue;
                }

                List permissions = permFile.getList("groups." + permGroup.getGroupName() + ".permissions");

                if(permissions == null) {
                    plugin.getLogger().error("Missing permissions entry for " + permGroup.getGroupName() + " in permissions.yml!");
                    continue;
                }

                for(Object o : permissions) {
                    String permission = (String) o;
                    boolean isTrue = !permission.startsWith("^");
                    permGroup.addGroupPermission(isTrue ? permission : permission.substring(1), isTrue);
                }

                // Check if meta is specified.
                if(permFile.get("groups." + permGroup.getGroupName() + ".meta") == null) {
                    plugin.getLogger().alert(permGroup.getGroupName() + " was not loaded in world " + permGroup.getGroupWorld() + " because of missing meta.");
                    continue;
                }

                Object meta = permFile.get("groups." + permGroup.getGroupName() + ".meta");

                if(meta instanceof LinkedHashMap) {
                    permGroup.setGroupPrefix(((LinkedHashMap) meta).get("prefix").toString());
                }

                // Check if inheritance is specified.
                if(permFile.get("groups." + permGroup.getGroupName() + ".inheritance") == null) {
                    plugin.getLogger().alert(permGroup.getGroupName() + " was not loaded in world " + permGroup.getGroupWorld() + " because of missing inheritance.");
                    continue;
                }

                Object inheritance = permFile.get("groups." + permGroup.getGroupName() + ".inheritance");

                if(inheritance instanceof ArrayList) {
                    for(Object inheritGroup : (ArrayList) inheritance) {
                        permGroup.addGroupInheritance(inheritGroup.toString());
                    }
                }

                groups.put(permGroup.getGroupWorld() + "_" + permGroup.getGroupName(), permGroup);
            }
        }
        return groups;
    }

    @Override
    public ArrayList<PermissionsGroup> getGroupsFromWorld(String world) {
        if(!this.permissionsFiles.containsKey(world)) return null;
        return new ArrayList<>(getGroupsFromWorld(world, this.permissionsFiles.get(world)).values());
    }

    @Override
    public HashMap<String, Object> getPlayer(String player, String world) {

        HashMap<String, Object> data = new HashMap<>();

        world = world.toLowerCase();
        player = player.toLowerCase();

        if(!this.permissionsFiles.containsKey(world)) return null;

        Config permFile = this.permissionsFiles.get(world);

        if(permFile.get("users." + player) == null) return data;

        data.put("group", permFile.get("users." + player + ".group"));
        data.put("permissions", permFile.get("users." + player + ".permissions"));
        data.put("meta", permFile.get("users." + player + ".meta"));

        return data;

    }

    @Override
    public boolean setPermission(String group, String world, String permission) {

        group = group.toLowerCase();
        world = world.toLowerCase();

        if(!this.permissionsFiles.containsKey(plugin.globalPerms ? "permissions" : world)) return false;

        Config permFile = this.permissionsFiles.get((plugin.globalPerms ? "permissions" : world));

        List<String> permissions = permFile.getStringList("groups." + group + ".permissions");

        if(permissions.contains(permission)) return false;

        if(permissions.contains("^" + permission)) permissions.remove("^" + permission);

        if(permission.startsWith("^") && permissions.contains(permission.substring(1))) permissions.remove(permission.substring(1));

        permissions.add(permission);

        permFile.set("groups." + group + ".permissions", permissions);

        if(plugin.autoSave) permFile.save();

        return permFile.getStringList("groups." + group + ".permissions").contains(permission);

    }

    @Override
    public boolean removePermission(String group, String world, String permission) {

        group = group.toLowerCase();
        world = world.toLowerCase();

        if(!this.permissionsFiles.containsKey(plugin.globalPerms ? "permissions" : world)) return false;

        Config permFile = this.permissionsFiles.get((plugin.globalPerms ? "permissions" : world));

        List<String> permissions = permFile.getStringList("groups." + group + ".permissions");

        if(!permissions.contains(permission)) return false;

        permissions.remove(permission);

        permFile.set("groups." + group + ".permissions", permissions);

        if(plugin.autoSave) permFile.save();

        return !permFile.getStringList("groups." + group + ".permissions").contains(permission);

    }

    @Override
    public boolean setPlayerPermission(String player, String world, String permission) {
        return false; // TODO: Create setPlayerPermission logic.
    }

    @Override
    public String getPlayerGroup(String player, String world) {

        if(plugin.globalPerms) {
            Object p = permissionsFiles.get("permissions").get("users." + player.toLowerCase() + ".group");
            if(p == null) p = permissionsFiles.get("permissions").get("default");
            return p.toString();
        } else {
            Object p = permissionsFiles.get(world.toLowerCase()).get("users." + player.toLowerCase() + ".group");
            if(p == null) p = permissionsFiles.get(world.toLowerCase()).get("default");
            return p.toString();
        }
    }

    @Override
    public void setPlayerGroup(String player, String group, String world) {

        Config permFile = this.permissionsFiles.get(plugin.globalPerms ? "permissions" : world);

        permFile.set("users." + player + ".group", group);

        if(plugin.autoSave) permFile.save();

    }

    @Override
    public void setDefaultGroup(String group, String world) {
        world = plugin.globalPerms ? "permissions" : world.toLowerCase();
        if(!this.permissionsFiles.containsKey(world)) return;
        this.permissionsFiles.get(world).set("default", group.toLowerCase());
        if(plugin.autoSave) this.permissionsFiles.get(world).save();
    }

    @Override
    public boolean setInheritance(String group, String world, String[] groups) {
        world = plugin.globalPerms ? "permissions" : world.toLowerCase();
        if(!this.permissionsFiles.containsKey(world)) return false;
        this.permissionsFiles.get(world).set("groups." + group.toLowerCase() + ".inheritance", groups);
        if(plugin.autoSave) this.permissionsFiles.get(world).save();
        return this.permissionsFiles.get(world).get("groups." + group.toLowerCase() + ".inheritance").equals(groups);
    }

    @Override
    public boolean addGroup(String group, String world, HashMap<String, Object> meta) {
        group = group.toLowerCase();
        world = plugin.globalPerms ? "permissions" : world.toLowerCase();

        if(!this.permissionsFiles.containsKey(world)) return false;

        Config permFile = this.permissionsFiles.get(world);

        permFile.set("groups." + group + ".permissions", new ArrayList<>());

        if(meta == null) {
            permFile.set("groups." + group + ".inheritance", new ArrayList<>());
            permFile.set("groups." + group + ".meta.prefix", "");
        } else {
            // TODO: Look for permissions.
            if(meta.containsKey("prefix")) {
                permFile.set("groups." + group + ".meta.prefix", meta.get("prefix"));
            } else {
                permFile.set("groups." + group + ".meta.prefix", "");
            }

            if(meta.containsKey("inheritance")) {
                permFile.set("groups." + group + ".inheritance", meta.get("inheritance"));
            } else {
                permFile.set("groups." + group + ".inheritance", new ArrayList<>());
            }
        }
        if(plugin.autoSave) permFile.save();

        PermissionsGroup permGroup = this.loadGroup(group, world);
        if(permGroup == null) return false;

        plugin.getDataManager().addGroupToMap(permGroup);

        return permFile.get("groups." + group) != null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean deleteGroup(String group, String world) {

        group = group.toLowerCase();
        world = world.toLowerCase();

        if(!this.permissionsFiles.containsKey(world)) return false;

        Config permFile = this.permissionsFiles.get(world);

        permFile.remove("groups." + group);

        // Remove group from groups inheriting it.
        for(Map.Entry<String, Map> entry : new ArrayList<>(((Map<String, Map>) permFile.get("groups")).entrySet())) {
            if(!(entry.getValue().get("inheritance") instanceof ArrayList)) continue;
            ArrayList inheritance = (ArrayList) entry.getValue().get("inheritance");
            if(!inheritance.contains(group)) continue;
            inheritance.remove(group);
            permFile.set("groups." + entry.getKey() + ".inheritance", inheritance);
        }

        String defaultGroup = permFile.getString("default");

        // Change players with this group to default.
        for(Map.Entry<String, Map> entry : new ArrayList<>(((Map<String, Map>) permFile.get("users")).entrySet())) {
            if(!entry.getValue().get("group").toString().equalsIgnoreCase(group)) continue;
            permFile.set("users." + entry.getKey() + ".group", defaultGroup);
        }

        if(plugin.autoSave) permFile.save();

        return permFile.get("groups." + group) == null;

    }

    @Override
    public PermissionsGroup loadGroup(String group, String world) {

        group = group.toLowerCase();
        world = world.toLowerCase();

        if(!this.permissionsFiles.containsKey(world)) return null;

        Config permFile = this.permissionsFiles.get(world);

        Object object = permFile.get("groups." + group);

        if(!(object instanceof LinkedHashMap)) return null;

        PermissionsGroup permGroup = new PermissionsGroup(
                group,
                world
        );

        // Check if there are any permissions.
        if(permFile.get("groups." + group + ".permissions") == null || !(permFile.get("groups." + group + ".permissions") instanceof ArrayList)) {
            plugin.getLogger().alert(group + " was not loaded in world " + world + " because of missing permissions.");
            return null;
        }

        List permissions = permFile.getList("groups." + group + ".permissions");

        if(permissions == null) {
            plugin.getLogger().error("Missing permissions entry for " + group + " in permissions.yml!");
            return null;
        }

        for(Object o : permissions) {
            String permission = (String) o;
            boolean isTrue = !permission.startsWith("^");
            permGroup.addGroupPermission(isTrue ? permission : permission.substring(1), isTrue);
        }

        // Check if meta is specified.
        if(permFile.get("groups." + group + ".meta") == null) {
            plugin.getLogger().alert(group + " was not loaded in world " + world + " because of missing meta.");
            return null;
        }

        Object meta = permFile.get("groups." + group + ".meta");

        if(meta instanceof LinkedHashMap) {
            permGroup.setGroupPrefix(((LinkedHashMap) meta).get("prefix").toString());
        }

        // Check if inheritance is specified.
        if(permFile.get("groups." + group + ".inheritance") == null) {
            plugin.getLogger().alert(group + " was not loaded in world " + world + " because of missing inheritance.");
            return null;
        }

        Object inheritance = permFile.get("groups." + group + ".inheritance");

        if(inheritance instanceof ArrayList) {
            for(Object inheritGroup : (ArrayList) inheritance) {
                permGroup.addGroupInheritance(inheritGroup.toString());
            }
        }

        return permGroup;
    }

    @Override
    public PermissionsWorld loadWorld(String world) {

        world = world.toLowerCase();

        if(this.permissionsFiles.containsKey(world)) return null;

        if(Files.notExists(Paths.get(plugin.getDataFolder().getAbsolutePath() + "/worlds/" + world + ".yml"))) plugin.saveResource("permissions.yml", "worlds/" + world + ".yml", false);

        this.permissionsFiles.put(world, new Config(plugin.getDataFolder().getAbsolutePath() + "/worlds/" + world + ".yml"));

        Config permFile = this.permissionsFiles.get(world);

        return new PermissionsWorld(
                world,
                permFile.get("default").toString().toLowerCase()
        );

    }

    @Override
    public ArrayList<PermissionsWorld> getWorlds() {
        ArrayList<PermissionsWorld> worlds = new ArrayList<>();
        if(plugin.globalPerms) {

            Config permFile = permissionsFiles.get("permissions");

            worlds.add(new PermissionsWorld(
                    "global",
                    permFile.get("default").toString().toLowerCase()
            ));

        } else {

            for(Level level : plugin.getServer().getLevels().values()) {
                // Create file if it does not exist.
                if(!permissionsFiles.containsKey(level.getName().toLowerCase())) {
                    plugin.getLogger().info("Creating permissions file for " + level.getName().toLowerCase());
                    plugin.saveResource("permissions.yml", "worlds/" + level.getName().toLowerCase() + ".yml", false);
                    this.permissionsFiles.put(level.getName().toLowerCase(), new Config(plugin.getDataFolder().getAbsolutePath() + "/worlds/" + level.getName().toLowerCase() + ".yml"));
                }

                Config permFile = this.permissionsFiles.get(level.getName().toLowerCase());

                worlds.add(new PermissionsWorld(
                        level.getName().toLowerCase(),
                        permFile.get("default").toString().toLowerCase()
                ));
            }
        }
        return worlds;
    }

    @Override
    public void close() {
        permissionsFiles.values().forEach(Config::save);
    }

    @Override
    public void updateLastLogin(String player) {
        player = player.toLowerCase();
        for(Config permFile : permissionsFiles.values()) {
            if(permFile.get("users." + player) == null) continue;
            permFile.set("users." + player + ".meta.last-login", System.currentTimeMillis());
            if(plugin.autoSave) permFile.save();
        }
    }

    public void save() {
        permissionsFiles.values().forEach(Config::save);
    }

}