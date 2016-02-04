package cc.leet.leetperms.persistence;

import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.data.PermissionsGroup;
import cc.leet.leetperms.data.PermissionsWorld;
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

    public void save() {
        permissionsFiles.values().forEach(Config::save);
    }

}