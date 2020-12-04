package pl.trollcraft.duel.arena;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import pl.trollcraft.duel.Core;
import pl.trollcraft.duel.file.PlayerFile;

import java.util.*;

public class Arena {

    private int maxPlayers;
    private int matchTime;

    private String name;
    private String winner;

    private Location spawnLoc;
    private Location playerOneLoc;
    private Location playerTwoLoc;

    private GameState state;

    private List<UUID> players;

    private Timer timer;

    private Map<UUID, Location> playerLoc;
    private Map<UUID, ItemStack[]> playerInv;
    private Map<UUID, ItemStack[]> playerArmor;
    private Map<UUID, String> playerKit;

    public Arena( String name, Location spawnLoc, Location playerOne, Location playerTwo, int matchTime) {
        this.name = name;
        this.spawnLoc = spawnLoc;
        this.playerOneLoc = playerOne;
        this.playerTwoLoc = playerTwo;
        this.winner = null;

        maxPlayers = 2;
        this.matchTime = matchTime;

        state = GameState.IDLE;

        players =  new ArrayList<>();
        playerLoc = new HashMap<>();
        playerInv = new HashMap<>();
        playerKit = new HashMap<>();
        playerArmor = new HashMap<>();
        timer = new Timer(this);
        timer.start();
    }

    // Funkcje
    public void broadcastMessage(String msg) {
        for (int i = 0; i < players.size(); i++) {
            Bukkit.getPlayer(players.get(i)).sendMessage(msg);
        }
    }

    public void userJoin(Player p) {
        if (Core.getInstance().arenaManager.getPlayersArena(p.getUniqueId()) != null) {
            p.sendMessage(Core.getInstance().msgFile.getAlreadyInGameMsg());
            return;
        }
        /*
        PlayerFile playerFile = new PlayerFile(p);

        if (!playerFile.isExists()) {
            playerFile.createNewFile();
            playerFile.save();
        }

        if (!Core.getInstance().storageFile.isPlayerExists(p)) {
            Core.getInstance().storageFile.addPlayerUUID(p);
            Core.getInstance().storageFile.save();
        }
        */
        if (!players.contains(p.getUniqueId())) {
            if (spawnLoc != null || playerOneLoc != null || playerTwoLoc != null) {
                if (players.size() < maxPlayers) {

                    players.add(p.getUniqueId());

                    playerInv.put(p.getUniqueId(), p.getInventory().getContents());
                    playerLoc.put(p.getUniqueId(), p.getLocation());
                    playerArmor.put(p.getUniqueId(), p.getInventory().getArmorContents());

                    broadcastMessage(Core.getInstance().msgFile.getJoinMsg(p.getName()));
                    for(PotionEffect effect : p.getActivePotionEffects()){
                        p.removePotionEffect(effect.getType());
                    }
                    p.getInventory().setArmorContents(null);
                    p.getInventory().clear();
                    p.teleport(spawnLoc);
                    p.setHealth(p.getMaxHealth());
                    p.setFoodLevel(20);

                    // Daj item do wybierania kita
                    p.getInventory().addItem(Core.getInstance().createItem(Material.WATCH, "§7Kity", Arrays.asList("Aby wybrać kit kilknij PPM.")));

                    if (players.size() >= maxPlayers) {
                        broadcastMessage(Core.getInstance().msgFile.getGameStartingMsg());
                        setState(GameState.LOBBY);
                    }
                }
                else {
                    p.sendMessage(Core.getInstance().msgFile.getArenaFullMsg());
                }
            }
            else {
                p.sendMessage(Core.getInstance().msgFile.getArenaNotSetupMsg());
            }
        }
        else {
            p.sendMessage(Core.getInstance().msgFile.getAlreadyInGameMsg());
        }
    }

    public void userLeave(Player p) {
        if (players.contains(p.getUniqueId())) {
            if (getState() == GameState.LOBBY) {
                broadcastMessage(Core.getInstance().msgFile.getLeftMsg(p.getName()));
                setState(GameState.IDLE);
                timer.reset();
            }

            if (getState() == GameState.START) {
                broadcastMessage(Core.getInstance().msgFile.getLeftInDuelMsg(p.getName()));
                reset();
            }

            if (getState() == GameState.IDLE) {
                broadcastMessage(Core.getInstance().msgFile.getLeftMsg(p.getName()));
            }

            if (playerLoc.containsKey(p.getUniqueId())) {
                p.teleport(playerLoc.get(p.getUniqueId()));
            }

            if (playerInv.containsKey(p.getUniqueId())) {
                p.getInventory().clear();
                p.getInventory().setContents(playerInv.get(p.getUniqueId()));
            }

            if (playerArmor.containsKey(p.getUniqueId())) {
                p.getInventory().setArmorContents(null);
                p.getInventory().setArmorContents(playerArmor.get(p.getUniqueId()));
            }

            playerArmor.remove(p.getUniqueId());
            playerInv.remove(p.getUniqueId());
            playerLoc.remove(p.getUniqueId());
            players.remove(p.getUniqueId());

        }
    }

    public void teleportToArena() {
        Bukkit.getPlayer(players.get(0)).teleport(playerOneLoc);
        Bukkit.getPlayer(players.get(1)).teleport(playerTwoLoc);
    }

    public void reset() {
        for (int i = 0; i < players.size(); i++) {
            Player p = Bukkit.getPlayer(players.get(i));
            p.setFireTicks(0);
            p.getWorld().getEntities().stream().filter(Item.class::isInstance).forEach(Entity::remove);
            for(PotionEffect effect : p.getActivePotionEffects()){
                p.removePotionEffect(effect.getType());
            }
            if (playerLoc.containsKey(p.getUniqueId())) {
                p.teleport(playerLoc.get(p.getUniqueId()));
            }

            if (playerInv.containsKey(p.getUniqueId())) {
                p.getInventory().clear();
                p.getInventory().setContents(playerInv.get(p.getUniqueId()));
            }

            if (playerArmor.containsKey(p.getUniqueId())) {
                p.getInventory().setArmorContents(null);
                p.getInventory().setArmorContents(playerArmor.get(p.getUniqueId()));
            }
        }
        players.clear();
        playerLoc.clear();
        playerInv.clear();
        playerArmor.clear();
        playerKit.clear();
        timer.reset();
        setState(GameState.IDLE);
    }

    public void givePlayerKits() {
        for (int i = 0; i < players.size(); i++) {
            Player p = Bukkit.getPlayer(getPlayers().get(i));

            p.closeInventory();
            p.getInventory().clear();
            p.getInventory().setArmorContents(null);

            p.setFoodLevel(100);
            p.setHealth(p.getMaxHealth());
            for(PotionEffect effect:p.getActivePotionEffects()){
                p.removePotionEffect(effect.getType());
            }
            if (playerKit.containsKey(p.getUniqueId())) {
                Core.getInstance().kitFile.givePlayerKit(p, playerKit.get(p.getUniqueId()));
            }
            else {
                Core.getInstance().kitFile.givePlayerKit(p, Core.getInstance().kitFile.getDefaultKits());
            }
        }
    }
    // Getters
    public String getArenaName() {
        return this.name;
    }

    public List<UUID> getPlayers() {
        return this.players;
    }

    public Map<UUID, String> getPlayerKits() {
        return this.playerKit;
    }

    public Location getSpawn() {
        return this.spawnLoc;
    }

    public GameState getState() {
        return this.state;
    }

    public Location getPlayerOneSpawn() {
        return playerOneLoc;
    }

    public Location getPlayerTwoSpawn() {
        return playerTwoLoc;
    }

    public int getMaxPlayers() {
        return this.maxPlayers;
    }

    // Setters
    public void setState(GameState state) {
        this.state = state;
    }

    public void setSpawn(Location loc) {
        this.spawnLoc = loc;
    }

    public void setPlayerOneSpawn(Location loc) {
        this.playerOneLoc = loc;
    }

    public void setPlayerTwoSpawn(Location loc) {
        this.playerTwoLoc = loc;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

}
