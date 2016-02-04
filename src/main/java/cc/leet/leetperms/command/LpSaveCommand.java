package cc.leet.leetperms.command;

import cc.leet.leetperms.LeetPerms;
import cc.leet.leetperms.persistence.YamlDataProvider;
import cc.leet.leetperms.util.DataManager;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.utils.TextFormat;

public class LpSaveCommand extends Command {

    private LeetPerms plugin;
    private DataManager dataManager;

    public LpSaveCommand(LeetPerms plugin) {
        super("lpsave", "Saves permissions files to disk.", "/lpsave");
        this.plugin = plugin;
        this.dataManager = plugin.getDataManager();
    }

    @Override
    public boolean execute(CommandSender sender, String s, String[] args) {

        if(!sender.hasPermission("leetperms.lpsave")) {
            sender.sendMessage(TextFormat.RED + "You do not have permission to do that.");
            return true;
        }

        switch(plugin.dataProviderType.toUpperCase()) {

            case "YML":
            case "YAML":
                ((YamlDataProvider) dataManager.getDataProvider()).save();
                break;

        }

        sender.sendMessage(TextFormat.GREEN + "Saved all permission files to disk.");

        return true;

    }
}