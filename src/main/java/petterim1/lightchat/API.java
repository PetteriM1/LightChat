package petterim1.lightchat;

import cn.nukkit.Player;
import com.creeperface.nukkit.placeholderapi.api.PlaceholderAPI;
import net.luckperms.api.LuckPermsProvider;
import ru.nukkit.multipass.Multipass;

public class API {

    private final Main plugin;

    API(Main plugin) {
        this.plugin = plugin;
    }

    String getGroup(Player player) {
        try {
            if (plugin.settings.provider == Settings.PROVIDER_LUCKPERMS) {
                return LuckPermsProvider.get().getUserManager().getUser(player.getUniqueId()).getPrimaryGroup();
            } else if (plugin.settings.provider == Settings.PROVIDER_MULTIPASS) {
                return Multipass.getGroups(player).get(0);
            }
        } catch (Exception ex) {
            plugin.getLogger().error("Failed to get group info for " + player.getName(), ex);
        }
        return "";
    }

    String formatMessage(Player player, String message, String group) {
        String format = plugin.settings.format.get(group);
        if (format == null) {
            format = plugin.settings.defaultFormat;
        }
        if (plugin.usePlaceholderAPI) {
            try {
                return PlaceholderAPI.getInstance().translateString(format.replace("%event_player%", player.getName()).replace("%event_player_display%", player.getDisplayName()).replace("%event_message%", message), player);
            } catch (Exception ex) {
                plugin.getLogger().error("", ex);
            }
        }
        return format.replace("%event_player%", player.getName()).replace("%event_player_display%", player.getDisplayName()).replace("%event_message%", message);
    }

    String formatDisplayName(Player player, String group) {
        String format = plugin.settings.nameFormat.get(group);
        if (format == null) {
            format = plugin.settings.defaultNameFormat;
        }
        if (plugin.usePlaceholderAPI) {
            try {
                return PlaceholderAPI.getInstance().translateString(format.replace("%username%", player.getName()).replace("%displayname%", player.getDisplayName()), player);
            } catch (Exception ex) {
                plugin.getLogger().error("", ex);
            }
        }
        return format.replace("%username%", player.getName()).replace("%displayname%", player.getDisplayName());
    }

    void initPlayer(Player player) {
        String group = plugin.api.getGroup(player);
        plugin.events.groupCache.put(player.getId(), group);
        if (plugin.settings.formatDisplayName) {
            player.setDisplayName(plugin.api.formatDisplayName(player, group));
        }
    }
}
