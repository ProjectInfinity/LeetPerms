LeetPerms
=========

A powerful permissions manager for Nukkit.

Follow development [here](https://trello.com/b/XnoFvH1i/leetperms).

Interested in contributing?
===========================

Please read the
[license](https://github.com/ProjectInfinity/LeetPerms/blob/master/LICENSE)
prior to forking the project!

We encourage users to help out in any way they can, but require that they
understand the terms and conditions that apply.

Configuration
=============

| Option                 | Default Value | Description                                                                                                                                                                                                                           |
|------------------------|---------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| auto-save              | true          | If set to true LeetPerms will save changes to disk upon any command that alters permissions.                                                                                                                                          |
| global-permissions     | false         | If set to true, LeetPerms will not allow you to have separate sets of permissions depending on the world the player is in. If set to true the only permissions file is the one called permissions.yml in the top LeetPerms directory. |
| lock-permissions-files | false         | If set to true, every command that is capable of altering permissions on the server will be disabled and as a result locking the permission files to their current data.                                                              |
| data-provider          | yaml          | Currently only yaml is supported. Changing this will cause LeetPerms to malfunction.                                                                                                                                                  |
| enable-mirrors         | false         | If set to true, LeetPerms will mirror world permissions if specified in mirrors.yml. Meaning you can have world Y mirror world X without ever having to make any changes to world Y.                                                  |
| debug                  | false         | Shows some debug output from LeetPerms like timings and maybe some other undocumented stuff. Usually it’s best to keep this off if you don’t have a really good reason to keep it on.                                                 |

Commands & Permissions
======================

-   **/lpaddgroup [group] [world] [optional meta]**

    -   Description: Adds a group to the target world.

    -   Example: /lpaddgroup member world inherit:guest prefix:[Member]

    -   Permission: *leetperms.lpaddgroup*

    -   [group] is the name of the group.

    -   [world] is the world in which that group is active in. *This is not
        needed if global permissions are enabled.*

    -   [optional meta] is where you can specify data that is specific to that
        group.

        -   Valid meta is “inherit” and “prefix”, start by typing the meta
            followed by colon “:” and then the value (with no space!).

-   **/lpdelgroup [group] [world]**

    -   Description: Removes a group from the target world.

    -   Example: /lpdelgroup member world

    -   Permission: *leetperms.lpdelgroup*

    -   [group] is the group to be removed.

    -   [world] is the target world in which the group will be removed from.
        *This is not needed if global permissions are enabled.*

-   **/lpdelperm [group] [permission.node] [world]**

    -   Description: Removes a permission node from a group in a target world.

    -   Example: /lpdelperm member amazing.permission.node world

    -   Permission: *leetperms.lpdelperm*

    -   [group] is the group to have it’s permissions altered.

    -   [permission.node] is the permission node to be removed.

    -   [world] is the target world in which the [group] will have it’s
        permission removed. *This is not needed if global permissions are
        enabled.*

-   **/lpgroupinfo [group] [world]**

    -   Description: Displays information about the target group in the target
        world.

    -   Example: /lpgroupinfo member world

    -   Permission: *leetperms.lpgroupinfo*

    -   [group] is the target group that will have it’s information displayed.

    -   [world] is the target world. If there are several groups named similarly
        across worlds, this is how you distinguish between them. *This is not
        needed if global permissions are enabled.*

-   **/lpgroups**

    -   Description: Displays all group names and the world that they reside in.

    -   Example: /lpgroups

    -   Permission: *leetperms.lpgroups*

-   **/lpinfo [optional player] [optional world]**

    -   Description: Displays permissions information about a player.

    -   Example: /lpinfo ProjectInfinity

    -   Permission: *leetperms.lpinfo*

    -   If [optional player] is specified then the plugin will attempt to show
        information regarding this player. Otherwise if not specified it
        defaults to the sender of the command.

    -   If [optional world] is specified then the plugin will attempt to show
        information about the [optional player] in the target world. If it is
        not specified it will default to the current world of the sender of the
        command.

-   **/lpinherit [group] [world] [groups to inherit]**

    -   Description: Sets the groups that the target group will inherit in the
        target world. This will override previously inherited groups, always
        specify EVERY group you want to inherit.

    -   Example: /lpinherit member world guest builder moderator

    -   Permission: *leetperms.lpinherit*

    -   [group] is the group that will inherit the specified groups.

    -   [world] is the world in which [group] will inherit the specified groups.
        *This is not needed when global permissions are enabled.*

    -   [groups to inherit] are the groups that [group] will inherit. Here’s
        some tips.

        1.  Try to make logical “trees” to avoid overlapping, this doesn’t
            actually affect the plugin as it will attempt to understand which
            rank is “more important” than the other but it will reduce the time
            in which the plugin spend computing this.

        2.  If one of the group inherits the other, only specify the top group
            to avoid extra computation during permissions calculation. If group
            A inherits B and C, do not add group B and C if the user already has
            group A as they are already inherited.

-   **/lpreload**

    -   Description: Reloads permissions from disk and recalculates permissions.

    -   Example: /lpreload

    -   Permission: *leetperms.lpreload*

-   **/lpsave**

    -   Description: Saves permissions to disk.

    -   Example: /lpsave

    -   Permission: *leetperms.lpsave*

-   **/lpsetdefault [group] [world]**

    -   Description: Sets the default group a player will get when they are in
        the target world.

    -   Example: /lpsetdefault guest world

    -   Permission: *leetperms.lpsetdefault*

    -   [group] is the group that will be made default.

    -   [world] is the world in which [group] will be made default. *This is not
        needed if global permissions are enabled.*

-   **/lpsetgroup [player] [group] [world]**

    -   Description: Sets the target player’s primary group to the target group
        in the target world.

    -   Example: /lpsetgroup ProjectInfinity owner world

    -   Permission: *leetperms.lpsetgroup*

    -   [player] is the target player that will be altered.

    -   [group] is the group that will be set as the player’s primary group.

    -   [world] is the world in which this group will be set to the player.
        *This is not needed if global permissions are enabled.*

-   **/lpsetperm [group] [permission.node] [optional world]**

    -   Description: Sets a permission node for a group in a target world.

    -   Example: /lpsetperm member amazing.permission.node world

    -   Permission: *leetperms.lpsetperm*

    -   [group] is the group that will have the permission added.

    -   [permission.node] is the permission node that is going to be added. To
        negate a permission note you can do some of the following things.

        -   Prepend your permission node with “\^” or “-” to negate your
            permission. Alternatively the following is also supported
            “permission.node:false”.

            -   DO NOTE THAT YOU CAN ONLY USE “\^” IN THE CONFIGURATION FILES IF
                MANUALLY EDITING PERMISSIONS!

    -   [optional world] is the world in which the group resides in. This will
        default to the world of the sender if not specified. *This is not needed
        if global permissions are enabled.*

-   **/lpsetplayerperm [player] [permission.node] [optional world]**

    -   Description: Adds a permission node to a specific player in a target
        world.

    -   Example: /lpsetplayerperm ProjectInfinity amazing.vip.node world

    -   Permission: *leetperms.lpsetplayerperm*

    -   [player] is the player that will have their permissions altered.

    -   [permission.node] is the permission node that the player will get.

    -   [optional world] is the world in which the player resides. It will
        default to the sender’s current world if not specified. *This is not
        needed if global permissions are enabled.*

-   **/lpsetprefix [group] [world] [prefix]**

    -   Description: Sets the prefix for the target group in the target world.

    -   Example: /lpsetprefix member world [Member]

    -   [group] is the group that will get the prefix.

    -   [world] is the target world where the group resides. *This is not needed
        if global permissions are enabled.*

    -   [prefix] is the prefix the target group will get. The prefix CAN contain
        several words unlike the prefix you can make when you create a group.

Developers
==========

You can access the **safe** API by doing

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
import cc.leet.leetperms.util.PermissionAPI;
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

then somewhere in your code do

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
PermissionAPI api = new PermissionAPI();
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 

If this API is missing some features you’d like, PLEASE request it in the issues
tab. If you for some reason **absolutely** must have access immediately to any
feature not present in PermissionAPI, you can do the following

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.util.DataManager;
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

then somewhere in your code do

~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
DataManager dataManager = LeetPerms.getPlugin().getDataManager();
~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~

 

You now have access to *exactly* the same API that LeetPerms uses internally. It
is however HIGHLY recommended to not use this.
