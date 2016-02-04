package cc.leet.leetperms.data;

import java.util.ArrayList;
import java.util.HashMap;

public class PermissionsGroup {

    private String groupName;
    private String groupWorld;

    private String groupPrefix;

    private HashMap<String, Boolean> groupPermissions;

    private ArrayList<String> groupInheritance;

    public PermissionsGroup() {
        groupPermissions = new HashMap<>();
    }

    public PermissionsGroup(String groupName, String groupWorld) {
        this.groupName = groupName;
        this.groupWorld = groupWorld;
        this.groupPermissions = new HashMap<>();
        this.groupPrefix = "";
        this.groupInheritance = new ArrayList<>();
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getGroupWorld() {
        return groupWorld;
    }

    public void setGroupWorld(String groupWorld) {
        this.groupWorld = groupWorld;
    }

    public HashMap<String, Boolean> getGroupPermissions() {
        return groupPermissions;
    }

    public void setGroupPermissions(HashMap<String, Boolean> groupPermissions) {
        this.groupPermissions = groupPermissions;
    }

    public void addGroupPermission(String node, boolean isTrue) {
        this.groupPermissions.put(node.toLowerCase(), isTrue);
    }

    public String getGroupPrefix() {
        return groupPrefix;
    }

    public void setGroupPrefix(String groupPrefix) {
        this.groupPrefix = groupPrefix;
    }

    public ArrayList<String> getGroupInheritance() {
        return groupInheritance;
    }

    public void addGroupInheritance(String groupName) {
        if(!this.groupInheritance.contains(groupName)) this.groupInheritance.add(groupName);
    }

    public void setGroupInheritance(ArrayList<String> groupInheritance) {
        this.groupInheritance = groupInheritance;
    }
}
