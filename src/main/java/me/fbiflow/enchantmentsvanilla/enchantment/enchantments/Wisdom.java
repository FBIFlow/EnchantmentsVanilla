package me.fbiflow.enchantmentsvanilla.enchantment.enchantments;

import me.fbiflow.enchantmentsvanilla.enchantment.ApplicableType;
import me.fbiflow.enchantmentsvanilla.enchantment.CycledTask;
import me.fbiflow.enchantmentsvanilla.enchantment.Enchantment;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.stream.Stream;

//TODO: IN TESTS
public class Wisdom extends Enchantment {

    @Override
    public @NotNull String getEnchantmentName() {
        return "Мудрость";
    }

    @Override
    public @NotNull List<Material> getApplicableTypes() {
        return Stream.of(
                        ApplicableType.SWORD,
                        ApplicableType.AXE)
                .flatMap(applicableType -> applicableType.materials.stream())
                .toList();
    }

    @Override
    public @NotNull String getDescription() {
        return "Увеличивает количество опыта при убийстве всех мобов";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public @NotNull Listener getEventListener() {
        return new Listener() {

            @EventHandler
            public void onEntityDeath(EntityDeathEvent event) {
                if (event.getDamageSource().getCausingEntity() instanceof Player killer) {
                    if (!getEnchantmentFacade().hasEnchantment(killer.getInventory().getItemInMainHand(), Wisdom.class)) {
                        return;
                    }
                    if (event.getEntity() instanceof Player) {
                        return;
                    }
                    event.setDroppedExp(event.getDroppedExp() * 2);
                }
            }
        };
    }

    @Override
    public @NotNull List<CycledTask> getCycledTasks() {
        return List.of();
    }
}
