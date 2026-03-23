package me.fbiflow.enchantmentsvanilla.enchantment.enchantments;

import me.fbiflow.enchantmentsvanilla.enchantment.ApplicableType;
import me.fbiflow.enchantmentsvanilla.enchantment.CycledTask;
import me.fbiflow.enchantmentsvanilla.enchantment.Enchantment;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class Cute extends Enchantment {

    @Override
    public @NotNull String getEnchantmentName() {
        return "Лапушка";
    }

    @Override
    public @NotNull List<Material> getApplicableTypes() {
        return ApplicableType.SWORD.materials;
    }

    @Override
    public @NotNull String getDescription() {
        return "Создаёт частицы вокруг сущности, по которой нанесён критический удар";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public @NotNull Listener getEventListener() {
        return new Listener() {

            @EventHandler
            public void onDamage(EntityDamageByEntityEvent event) {
                if (!(event.getDamager() instanceof Player damager)) {
                    return;
                }
                var weapon = damager.getInventory().getItemInMainHand();
                if (weapon.getType() == Material.AIR) {
                    return;
                }
                if (getEnchantmentFacade().hasEnchantment(weapon, Cute.class) && event.isCritical()) {
                    var location = event.getEntity().getLocation();
                    var newLoc = location.add(0, 2.2, 0);
                    location.getWorld().spawnParticle(Particle.HEART, newLoc, 3, 0.24, 0.24, 0.24);
                }
            }
        };
    }

    @Override
    public @NotNull List<CycledTask> getCycledTasks() {
        return List.of();
    }
}