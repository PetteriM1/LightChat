package petterim1.lightchat;

import cn.nukkit.Player;
import cn.nukkit.event.EventHandler;
import cn.nukkit.event.EventPriority;
import cn.nukkit.event.Listener;
import cn.nukkit.event.player.PlayerChatEvent;
import cn.nukkit.event.player.PlayerJoinEvent;
import cn.nukkit.event.player.PlayerQuitEvent;
import cn.nukkit.utils.TextFormat;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Events implements Listener {

    private final Main plugin;

    final Map<Long, String> groupCache = new HashMap<>();
    private final Map<Long, Integer> lastTime = new HashMap<>();
    private final Map<Long, String> lastMessage = new HashMap<>();

    private static final Pattern pattern = Pattern.compile("[^\\p{L}\\p{N}\\p{P}\\p{Z}\\p{Sm}\\p{Sc}\\p{Sk}]", Pattern.UNICODE_CHARACTER_CLASS);

    Events(Main plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(PlayerChatEvent event) {
        Player player = event.getPlayer();
        long id = player.getId();
        int tick = plugin.getServer().getTick();
        int time = lastTime.getOrDefault(id, 0);
        if (plugin.settings.antiSpam && time > tick) {
            player.sendMessage(plugin.settings.antispamMessage);
            lastTime.put(id, tick + plugin.settings.antiSpamThreshold);
            event.setCancelled(true);
            return;
        }
        String message = plugin.settings.cleanMessages ? TextFormat.clean(event.getMessage()) : event.getMessage();
        if (message.length() > plugin.settings.maxMessageLength) {
            player.sendMessage(plugin.settings.messageTooLongMessage);
            lastTime.put(id, tick + plugin.settings.antiSpamThreshold);
            event.setCancelled(true);
            return;
        }
        if (plugin.settings.duplicatedMessageCheck && lastMessage.getOrDefault(id, "").equals(message) && time + plugin.settings.duplicatedMessageThreshold > tick) {
            player.sendMessage(plugin.settings.duplicatedMessageMessage);
            lastTime.put(id, tick + plugin.settings.antiSpamThreshold);
            event.setCancelled(true);
            return;
        }
        for (String banned : plugin.settings.blacklist) {
            if (message.contains(banned)) {
                player.sendMessage(plugin.settings.blacklistMessage);
                lastTime.put(id, tick + plugin.settings.antiSpamThreshold);
                event.setCancelled(true);
                return;
            }
        }
        lastTime.put(id, tick + plugin.settings.antiSpamThreshold);
        lastMessage.put(id, message);
        for (String filtered : plugin.settings.filter) {
            message = Pattern.compile(filtered, Pattern.CASE_INSENSITIVE).matcher(message).replaceAll(plugin.settings.replaceFilter);
        }
        if (plugin.settings.limitUnicode) {
            message = pattern.matcher(message).replaceAll(plugin.settings.replaceUnicode);
        }
        String group = groupCache.get(event.getPlayer().getId());
        if (group == null) {
            group = plugin.api.getGroup(event.getPlayer());
            groupCache.put(event.getPlayer().getId(), group);
        }
        event.setFormat(plugin.api.formatMessage(player, message, group));
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent event) {
        plugin.api.initPlayer(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        long id = event.getPlayer().getId();
        groupCache.remove(id);
        lastTime.remove(id);
        lastMessage.remove(id);
    }
}
