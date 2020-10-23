package pl.trollcraft.duel.utils;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Broadcast {
    public static void broadcastWinner(String winner, String loser){
        for(Player p1: Bukkit.getOnlinePlayers()){
            p1.sendMessage("§7§l>> §e§lGracz §a§l" + winner + " §e§lpokonal gracza §c§l" + loser);
        }
    }
}
