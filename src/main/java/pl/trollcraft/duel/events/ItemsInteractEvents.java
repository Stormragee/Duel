package pl.trollcraft.duel.events;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import pl.trollcraft.duel.Core;
import pl.trollcraft.duel.arena.Arena;
import pl.trollcraft.duel.arena.GameState;
import pl.trollcraft.duel.utils.CustomInventory;

public class ItemsInteractEvents implements Listener {

    @EventHandler
    public void onPlayerClickKitSelector(PlayerInteractEvent e) {
        if (e.getAction() != null) {
            if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
                ItemStack item = e.getItem();
                Player p = e.getPlayer();
                Arena arena = Core.getInstance().arenaManager.getPlayersArena(p.getUniqueId());

                if (arena != null) {
                    if (arena.getPlayers().contains(p.getUniqueId())) {
                        if (arena.getState() == GameState.IDLE || arena.getState() == GameState.LOBBY) {
                            if (item != null && item.getType() != Material.AIR) {
                                if (item.getType() == Material.WATCH) {
                                    CustomInventory.getKitGUI(p);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
