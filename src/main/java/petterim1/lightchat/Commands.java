package petterim1.lightchat;

import cn.nukkit.command.CommandSender;

public class Commands {

    private final Main plugin;

    Commands(Main plugin) {
        this.plugin = plugin;
    }

    boolean handle(CommandSender sender, String command, String[] args) {
        if (command.equals("lightchat")) {
            if (args.length > 0) {
                switch (args[0].toLowerCase()) {
                    case "reload":
                        plugin.settings.reload();
                        sender.sendMessage("§aReload completed");
                        return true;
                }
                return false;
            }
        }
        return false;
    }
}
