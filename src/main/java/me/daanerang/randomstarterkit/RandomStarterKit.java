package me.daanerang.randomstarterkit;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.loot.LootContext;
import org.bukkit.loot.LootTable;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collection;
import java.util.Random;

public class RandomStarterKit extends JavaPlugin implements Listener {

    private final Random random = new Random();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        Player player = event.getPlayer();

        // Add a delay to give the starter kit after the player is fully respawned
        new BukkitRunnable() {
            @Override
            public void run() {
                giveRandomStarterKit(player);
            }
        }.runTaskLater(this, 20L); // 20 ticks = 1 second delay
    }

    private void giveRandomStarterKit(Player player) {
        // Generate items from custom loot tables using NamespacedKey
        LootTable meleeLootTable = getCustomLootTable("melee_loot_table");
        LootTable rangedLootTable = getCustomLootTable("ranged_loot_table");
        LootTable potionLootTable = getCustomLootTable("potion_loot_table");
        LootTable foodLootTable = getCustomLootTable("food_loot_table");
        LootTable armorLootTable = getCustomLootTable("armor_loot_table");

        if (meleeLootTable != null) {
            player.getInventory().addItem(generateLootFromTable(meleeLootTable, player));
        }
        if (rangedLootTable != null) {
            player.getInventory().addItem(generateLootFromTable(rangedLootTable, player));
        }
        if (potionLootTable != null) {
            if (random.nextBoolean()) {
                player.getInventory().addItem(generateLootFromTable(potionLootTable, player));
            } else {
                player.getInventory().addItem(new ItemStack(Material.SPLASH_POTION));
            }
        }
        if (foodLootTable != null) {
            player.getInventory().addItem(generateLootFromTable(foodLootTable, player));
        }
        if (armorLootTable != null) {
            player.getInventory().addItem(generateLootFromTable(armorLootTable, player));
            player.getInventory().addItem(generateLootFromTable(armorLootTable, player));
            player.getInventory().addItem(generateLootFromTable(armorLootTable, player));
            player.getInventory().addItem(generateLootFromTable(armorLootTable, player));
        }

        // Knockback Stick with Rarity
        if (random.nextDouble() < 0.001) {
            ItemStack knockbackStick = new ItemStack(Material.STICK);
            knockbackStick.addUnsafeEnchantment(Enchantment.KNOCKBACK, 1000);
            player.getInventory().addItem(knockbackStick);
        }
    }

    private LootTable getCustomLootTable(String tableName) {
        NamespacedKey key = new NamespacedKey(this, tableName);
        return Bukkit.getLootTable(key);
    }

    private ItemStack generateLootFromTable(LootTable lootTable, Player player) {
        Inventory tempInventory = getServer().createInventory(null, 9);
        LootContext context = new LootContext.Builder(player.getLocation()).lootedEntity(player).build();
        Collection<ItemStack> loot = lootTable.populateLoot(random, context);
        for (ItemStack item : loot) {
            if (item != null && item.getType() != Material.AIR) {
                tempInventory.addItem(item);
            }
        }
        return tempInventory.getItem(0) != null ? tempInventory.getItem(0) : new ItemStack(Material.AIR);
    }
}
