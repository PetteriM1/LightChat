package petterim1.lightchat;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;

public class Main extends PluginBase {

    boolean usePlaceholderAPI;

    API api;
    Settings settings;
    Commands commands;
    Events events;

    public void onEnable() {
        checkDependencies();
        api = new API(this);
        settings = new Settings(this);
        commands = new Commands(this);
        getServer().getPluginManager().registerEvents(events = new Events(this), this);
    }

    private void checkDependencies() {
        Plugin papi = getServer().getPluginManager().getPlugin("PlaceholderAPI");
        if (papi == null) {
            getLogger().warning("PlaceholderAPI not found! Only default placeholders can be used");
        } else {
            usePlaceholderAPI = true;
        }
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commands.handle(sender, command.getName(), args);
    }
}
