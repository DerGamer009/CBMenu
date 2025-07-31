package com.example.cbmenu;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

public class CBMenuPlugin extends JavaPlugin implements Listener, CommandExecutor {

    private Inventory mainMenu;
    private Inventory cb01Menu;
    private Inventory cb02Menu;
    private final Map<Inventory, String> titles = new HashMap<>();
    private final Random random = new Random();

    @Override
    public void onEnable() {
        createMenus();
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
        if (getCommand("cb") != null) {
            getCommand("cb").setExecutor(this);
        }
    }

    private void createMenus() {
        mainMenu = Bukkit.createInventory(null, 27, ChatColor.GREEN + "CityBuild Menü");
        titles.put(mainMenu, ChatColor.GREEN + "CityBuild Menü");
        cb01Menu = Bukkit.createInventory(null, 27, ChatColor.GREEN + "CB01");
        titles.put(cb01Menu, ChatColor.GREEN + "CB01");
        cb02Menu = Bukkit.createInventory(null, 27, ChatColor.GREEN + "CB02");
        titles.put(cb02Menu, ChatColor.GREEN + "CB02");

        fillFrame(mainMenu);
        fillFrame(cb01Menu);
        fillFrame(cb02Menu);

        mainMenu.setItem(11, createItem(Material.GRASS_BLOCK, ChatColor.GREEN + "CB01", "Klicke für CityBuild01/Farm01"));
        mainMenu.setItem(15, createItem(Material.BRICKS, ChatColor.GREEN + "CB02", "Klicke für CityBuild02/Farm02"));
        mainMenu.setItem(22, createItem(Material.BARRIER, ChatColor.RED + "Schließen"));

        cb01Menu.setItem(11, createItem(Material.GRASS_BLOCK, ChatColor.GREEN + "CityBuild01", "Klicke, um auf CityBuild01 zu wechseln"));
        cb01Menu.setItem(15, createItem(Material.WHEAT, ChatColor.GOLD + "FarmServer", "Klicke, um auf Farm01 zu wechseln"));
        cb01Menu.setItem(22, createItem(Material.BARRIER, ChatColor.RED + "Zurück"));

        cb02Menu.setItem(11, createItem(Material.GRASS_BLOCK, ChatColor.GREEN + "CityBuild02", "Klicke, um auf CityBuild02 zu wechseln"));
        cb02Menu.setItem(15, createItem(Material.WHEAT, ChatColor.GOLD + "FarmServer", "Klicke, um auf Farm02 zu wechseln"));
        cb02Menu.setItem(22, createItem(Material.BARRIER, ChatColor.RED + "Zurück"));
    }

    private void fillFrame(Inventory inv) {
        ItemStack glass = new ItemStack(Material.GREEN_STAINED_GLASS_PANE);
        ItemMeta meta = glass.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            glass.setItemMeta(meta);
        }
        int size = inv.getSize();
        for (int i = 0; i < size; i++) {
            if (i < 9 || i >= size - 9 || i % 9 == 0 || i % 9 == 8) {
                inv.setItem(i, glass);
            }
        }
    }

    private ItemStack createItem(Material material, String name, String... loreLines) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            List<String> lore = new ArrayList<>();
            for (String l : loreLines) {
                lore.add(ChatColor.GRAY + l);
            }
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Nur Spieler können dieses Kommando benutzen.");
            return true;
        }
        Player player = (Player) sender;
        openWithAnimation(player, mainMenu);
        return true;
    }

    private void openWithAnimation(Player player, Inventory source) {
        String title = titles.getOrDefault(source, "");
        Inventory inv = Bukkit.createInventory(null, source.getSize(), title);
        for (int i = 0; i < source.getSize(); i++) {
            ItemStack item = source.getItem(i);
            if (item != null) {
                inv.setItem(i, item.clone());
            }
        }

        player.openInventory(inv);

        List<Integer> frameSlots = new ArrayList<>();
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if (item != null && item.getType() == Material.GREEN_STAINED_GLASS_PANE) {
                frameSlots.add(i);
            }
        }

        new BukkitRunnable() {
            int ticks = 0;

            @Override
            public void run() {
                if (ticks >= 10) {
                    cancel();
                    return;
                }

                if (!frameSlots.isEmpty()) {
                    int slot = frameSlots.get(random.nextInt(frameSlots.size()));
                    ItemStack glass = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
                    inv.setItem(slot, glass);
                }

                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
                ticks++;
            }
        }.runTaskTimer(this, 0L, 2L);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player player)) return;
        Inventory inv = event.getInventory();
        if (inv.equals(mainMenu)) {
            handleMainMenu(event, player);
        } else if (inv.equals(cb01Menu)) {
            handleCB01Menu(event, player);
        } else if (inv.equals(cb02Menu)) {
            handleCB02Menu(event, player);
        }
    }

    private void handleMainMenu(InventoryClickEvent event, Player player) {
        event.setCancelled(true);
        switch (event.getSlot()) {
            case 11 -> openWithAnimation(player, cb01Menu);
            case 15 -> openWithAnimation(player, cb02Menu);
            case 22 -> player.closeInventory();
        }
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
    }

    private void handleCB01Menu(InventoryClickEvent event, Player player) {
        event.setCancelled(true);
        switch (event.getSlot()) {
            case 11 -> connect(player, "cb01");
            case 15 -> connect(player, "Farm01");
            case 22 -> openWithAnimation(player, mainMenu);
        }
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
    }

    private void handleCB02Menu(InventoryClickEvent event, Player player) {
        event.setCancelled(true);
        switch (event.getSlot()) {
            case 11 -> connect(player, "cb02");
            case 15 -> connect(player, "Farm02");
            case 22 -> openWithAnimation(player, mainMenu);
        }
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, 1f, 1f);
    }

    private void connect(Player player, String server) {
        try {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(this, "BungeeCord", b.toByteArray());
        } catch (IOException e) {
            player.sendMessage(ChatColor.RED + "Fehler beim Wechseln des Servers.");
            e.printStackTrace();
        }
    }
}
