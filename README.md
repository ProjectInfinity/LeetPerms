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
