package pl.trollcraft.duel.events;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import pl.trollcraft.duel.Core;
import pl.trollcraft.duel.arena.GameState;
import pl.trollcraft.duel.arena.Arena;
import static pl.trollcraft.duel.utils.Broadcast.broadcastWinner;

public class PlayerDeathEvents implements Listener {

    public Core plugin;

    public PlayerDeathEvents(Core plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerDied(EntityDeathEvent e) {
        if (e.getEntity() instanceof  Player) {
            Player p = (Player) e.getEntity();
            Arena arena = plugin.arenaManager.getPlayersArena(p.getUniqueId());

            if (arena != null) {
                if (arena.getPlayers().contains(p.getUniqueId())) {
                    if (arena.getState() == GameState.START) {

                        e.getDrops().clear();
                        p.setHealth(p.getMaxHealth());
                        p.getInventory().clear();
                        p.getInventory().setArmorContents(null);
                        e.setDroppedExp(0);

                        if (arena.getPlayers().get(0) == p.getUniqueId()) {
                            Player winner = Bukkit.getPlayer(arena.getPlayers().get(1));

                            arena.broadcastMessage(plugin.msgFile.getDeathMsg(p.getName(), winner.getName()));

                            winner.setHealth(p.getMaxHealth());
                            winner.getInventory().clear();
                            winner.getInventory().setArmorContents(null);

                            arena.setWinner(winner.getName());
                            broadcastWinner(winner.getName(),p.getName());
                            arena.setState(GameState.END);
                        }
                        else {
                            Player winner = Bukkit.getPlayer(arena.getPlayers().get(0));

                            arena.broadcastMessage(plugin.msgFile.getDeathMsg(p.getName(), winner.getName()));
                            broadcastWinner(winner.getName(),p.getName());
                            winner.setHealth(p.getMaxHealth());
                            winner.getInventory().clear();
                            winner.getInventory().setArmorContents(null);

                            arena.setWinner(winner.getName());
                            arena.setState(GameState.END);
                        }

                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDiedMsg(PlayerDeathEvent e) {
        Player p = e.getEntity();
        Arena arena = plugin.arenaManager.getPlayersArena(p.getUniqueId());

        if (arena != null) {
            if (arena.getPlayers().contains(p.getUniqueId())) {
                e.setDeathMessage(null);
            }
        }
    }

}
