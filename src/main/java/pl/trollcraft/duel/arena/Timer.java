package pl.trollcraft.duel.arena;

import org.bukkit.scheduler.BukkitRunnable;
import pl.trollcraft.duel.Core;

import java.util.List;


public class Timer extends BukkitRunnable {
    private Arena arena;
    private int lobby;
    private int end;

    public Timer(Arena arena) {
        this.arena = arena;
        this.lobby = 10;
        this.end = 2;
    }

    public void start() {
        this.runTaskTimer(Core.getInstance(), 0, 20);
    }

    public void reset() {
        this.lobby = 10;
        this.end = 2;
    }

    @Override
    public void run() {
        if (arena.getState() == GameState.LOBBY) {
            lobby--;

            if (lobby <= 5) {
                arena.broadcastMessage(Core.getInstance().msgFile.getStartCountdownMsg(lobby));
            }
            if (lobby == 0) {
                arena.setState(GameState.START);
                arena.teleportToArena();
                arena.givePlayerKits();
            }
        }
        if (arena.getState() == GameState.END) {
            end--;
            if (end == 0) {
                arena.reset();
                arena.setState(GameState.IDLE);
            }
            if( end < 0){
                arena.reset();
                arena.setState(GameState.IDLE);
            }

        }
    }
}
