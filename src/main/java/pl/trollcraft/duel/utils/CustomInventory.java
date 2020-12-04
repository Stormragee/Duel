package pl.trollcraft.duel.utils;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import pl.trollcraft.duel.Core;
import pl.trollcraft.duel.arena.Arena;
import pl.trollcraft.pvp.help.gui.GUI;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CustomInventory {

    public Core plugin;

    public CustomInventory(Core plugin) {
        this.plugin = plugin;
    }

    public static void openArenaSettings(Player p, Arena arena) {
        Inventory inv = Bukkit.createInventory(null, 27, "Arena: " + arena.getArenaName());

        if (arena.getPlayerOneSpawn() != null) {
            String locationStr = Core.getInstance().locationToString(arena.getPlayerOneSpawn());
            inv.setItem(12, Core.getInstance().createItem(Material.RED_ROSE, "§a§lPlayer One Spawn", Arrays.asList("§fYou already set it to:", "§2" + locationStr, " ", "§fRight-Click to teleport", "§fLeft-Click to set to current location.")));
        }
        else {
            inv.setItem(12, Core.getInstance().createItem(Material.RED_ROSE, "§c§lPlayer One Spawn", Arrays.asList("§fYou already set it to:", "§cNOT SET", " ", "§fLeft-Click to set to current location.")));
        }

        if (arena.getPlayerTwoSpawn() != null) {
            String locationStr = Core.getInstance().locationToString(arena.getPlayerTwoSpawn());
            inv.setItem(13, Core.getInstance().createItem(Material.LAPIS_BLOCK, "§a§lPlayer Two Spawn", Arrays.asList("§fYou already set it to:", "§2" + locationStr, " ", "§fRight-Click to teleport", "§fLeft-Click to set to current location.")));
        }
        else {
            inv.setItem(13, Core.getInstance().createItem(Material.LAPIS_BLOCK, "§c§lPlayer Two Spawn", Arrays.asList("§fYou already set it to:", "§cNOT SET", " ", "§fLeft-Click to set to current location.")));
        }

        if (arena.getSpawn() != null) {
            String locationStr = Core.getInstance().locationToString(arena.getSpawn());
            inv.setItem(14, Core.getInstance().createItem(Material.IRON_DOOR, "§a§lMain Spawn", Arrays.asList("§fYou already set it to:", "§2" + locationStr, " ", "§fRight-Click to teleport", "§fLeft-Click to set to current location.")));
        }
        else {
            inv.setItem(14, Core.getInstance().createItem(Material.IRON_DOOR, "§c§lMain Spawn", Arrays.asList("§fYou already set it to:", "§cNOT SET", " ", "§fLeft-Click to set to current location.")));
        }

        p.openInventory(inv);
    }

    public static void getKitGUI(Player p) {
        GUI gui = new GUI(45,"§7Kity");
        int j = 0;
        for (String kit : Core.getInstance().kitFile.getConfig().getConfigurationSection("kits").getKeys(false)) {
            String[] items = Core.getInstance().kitFile.getConfig().getString("kits." + kit + ".display").split(":");

            ItemStack item = new ItemStack(Material.valueOf(items[0]), Integer.parseInt(items[1]), Byte.parseByte(items[2]));
            ItemMeta meta = item.getItemMeta();
            List<String> lore = new ArrayList<>();

            meta.setDisplayName("§7Kit: §a" + kit);

            if (items.length > 3) {
                for (int i = 3; i < items.length; i++) {
                    lore.add(items[i].replace("&", "§"));
                }
            }

            if (!lore.isEmpty()) {
                meta.setLore(lore);
            }

            item.setItemMeta(meta);

            gui.addItem(j,item,e -> {});
            j++;
        }

        gui.open(p);
    }

    public static void getArenaListGUI(Player p) {
        GUI gui = new GUI( 45, "§a§lPojedynki");
        int k = 0;
        for (Arena a : Core.getInstance().arenaManager.getAllArena()) {
            String arenaName = a.getArenaName();
            String arenaState = a.getState().toString();
            int playersJoin = a.getPlayers().size();
            int maxPlayers = a.getMaxPlayers();
            switch (arenaState){
                case "START":
                    arenaState = "§cZajeta";
                case "END":
                    arenaState = "§cResetowanie";
                case "IDLE":
                    arenaState = "§aWolna!";
                case "LOBBY":
                    arenaState = "§eOczekiwanie";

            }
            gui.addItem(k,Core.getInstance().createItem(Material.GOLD_SWORD, "§7Arena: " + arenaName, Arrays.asList("", "§7Status: " + arenaState, "§7Graczy: §a" + playersJoin + "/" + maxPlayers, " ", "§7Kliknij aby dolaczyc!.")),e->{});
            k++;
        }

        gui.open(p);
    }

}

