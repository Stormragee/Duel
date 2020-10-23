package pl.trollcraft.duel.events;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import pl.trollcraft.duel.Core;
import pl.trollcraft.duel.arena.Arena;

public class FoodEvents implements Listener {

    @EventHandler
    public void onFoodLevelDown(FoodLevelChangeEvent e) {
        Player p = (Player) e.getEntity();
        Arena arena = Core.getInstance().arenaManager.getPlayersArena(p.getUniqueId());

        if (arena != null) {
            if (arena.getPlayers().contains(p.getUniqueId())) {
                e.setFoodLevel(20);
                e.setCancelled(true);
            }
        }
    }

}
