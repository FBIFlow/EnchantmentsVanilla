package me.fbiflow.enchantmentsvanilla.enchantment.enchantments;

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent;
import me.fbiflow.enchantmentsvanilla.enchantment.ApplicableType;
import me.fbiflow.enchantmentsvanilla.enchantment.CycledTask;
import me.fbiflow.enchantmentsvanilla.enchantment.Enchantment;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.List;
//TODO: IN TEST
public class Resistance extends Enchantment {

    @Override
    public @NotNull String getEnchantmentName() {
        return "Стойкость";
    }

    @Override
    public @NotNull List<Material> getApplicableTypes() {
        return ApplicableType.CHESTPLATE.materials;
    }

    @Override
    public @NotNull String getDescription() {
        return "Накладывает эффект Сопротивления";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public @NotNull Listener getEventListener() {
        return new Listener() {

            @EventHandler
            public void onPlayerJoin(PlayerJoinEvent event) {
                var player = event.getPlayer();
                handlePotionEffect(player);
            }

            @EventHandler
            public void onPlayerArmorApply(PlayerArmorChangeEvent event) {
                Player player = event.getPlayer();
                handlePotionEffect(player);
            }

            private void handlePotionEffect(Player player) {
                var chestplate = player.getInventory().getChestplate();
                if (chestplate == null) {
                    player.removePotionEffect(PotionEffectType.RESISTANCE);
                    return;
                }
                if (getEnchantmentFacade().hasEnchantment(chestplate, Resistance.class)
                ) {
                    player.addPotionEffect(
                            getInfinityResistance());
                } else {
                    if (isResistanceInfinity(player)) {
                        player.removePotionEffect(PotionEffectType.RESISTANCE);
                    }
                }
            }

            private boolean isResistanceInfinity(Player player) {
                var effect = player.getPotionEffect(PotionEffectType.RESISTANCE);
                if (effect == null) {
                    return false;
                }
                return effect.getDuration() == -1;
            }

            private PotionEffect getInfinityResistance() {
                return new PotionEffect(
                        PotionEffectType.RESISTANCE,
                        -1,
                        0,
                        false,
                        false,
                        false
                );
            }
        };
    }

    @Override
    public @NotNull List<CycledTask> getCycledTasks() {
        return List.of();
    }
}
