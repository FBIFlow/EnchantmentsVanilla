package me.fbiflow.enchantmentsvanilla.enchantment.enchantments;

import me.fbiflow.enchantmentsvanilla.enchantment.ApplicableType;
import me.fbiflow.enchantmentsvanilla.enchantment.CycledTask;
import me.fbiflow.enchantmentsvanilla.enchantment.Enchantment;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

public class WitherProtection extends Enchantment {

    @Override
    public @NotNull String getEnchantmentName() {
        return "Элементарная защита";
    }

    @Override
    public @NotNull List<Material> getApplicableTypes() {
        return Stream.of(
                        ApplicableType.HELMET,
                        ApplicableType.CHESTPLATE,
                        ApplicableType.LEGGINGS,
                        ApplicableType.BOOTS)
                .flatMap(applicableType -> applicableType.materials.stream())
                .toList();
    }

    @Override
    public @NotNull String getDescription() {
        return "Снижает урон от иссушения";
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public @NotNull Listener getEventListener() {
        return new Listener() {

            @EventHandler
            public void onPlayerDamage(EntityDamageEvent event) {
                if (!(event.getEntity() instanceof Player player)) {
                    return;
                }
                if (event.getCause() == EntityDamageEvent.DamageCause.WITHER
                || event.getCause() == EntityDamageEvent.DamageCause.POISON) {
                    var damage = event.getDamage();
                    var inventory = player.getInventory();
                    int protectionPercent = 0;
                    if (inventory.getHelmet() != null) {
                        protectionPercent += (getEnchantmentFacade()
                                .getEnchantmentLevel(inventory.getHelmet(), WitherProtection.class) * 5);
                    }
                    if (inventory.getChestplate() != null) {
                        protectionPercent += (getEnchantmentFacade()
                                .getEnchantmentLevel(inventory.getChestplate(), WitherProtection.class) * 5);
                    }
                    if (inventory.getLeggings() != null) {
                        protectionPercent += (getEnchantmentFacade()
                                .getEnchantmentLevel(inventory.getLeggings(), WitherProtection.class) * 5);
                    }
                    if (inventory.getBoots() != null) {
                        protectionPercent += (getEnchantmentFacade()
                                .getEnchantmentLevel(inventory.getBoots(), WitherProtection.class) * 5);
                    }
                    player.sendMessage("Default damage: " + event.getDamage());
                    event.setDamage(damage * (100 - protectionPercent) / 100);
                    player.sendMessage("New damage: " + event.getDamage());
                }
            }

        };
    }

    @Override
    public @NotNull List<CycledTask> getCycledTasks() {
        return List.of();
    }
}
