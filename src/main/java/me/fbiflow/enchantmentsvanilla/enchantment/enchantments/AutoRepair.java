package me.fbiflow.enchantmentsvanilla.enchantment.enchantments;

import me.fbiflow.enchantmentsvanilla.enchantment.ApplicableType;
import me.fbiflow.enchantmentsvanilla.enchantment.CycledTask;
import me.fbiflow.enchantmentsvanilla.enchantment.Enchantment;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.*;

//TODO: IN PROCESS
public class AutoRepair extends Enchantment {

    private final Set<Player> currentBreakingBlock = new HashSet<>();

    @Override
    public @NotNull String getEnchantmentName() {
        return "Автопочинка";
    }

    @Override
    public @NotNull List<Material> getApplicableTypes() {
        return Arrays.stream(ApplicableType.values())
                .flatMap(applicableType -> applicableType.materials.stream())
                .toList();
    }

    @Override
    public @NotNull String getDescription() {
        return "Автоматически восстанавливает прочность предмета";
    }

    @Override
    public int getMaxLevel() {
        return 3;
    }

    @Override
    public @NotNull Listener getEventListener() {
        return new Listener() {

            @EventHandler
            public void onBlockBreakStart(BlockDamageEvent event) {
                currentBreakingBlock.add(event.getPlayer());
            }

            @EventHandler
            public void onBlockBreakEnd(BlockDamageAbortEvent event) {
                currentBreakingBlock.remove(event.getPlayer());
            }

            @EventHandler
            public void onBlockBreak(BlockBreakEvent event) {
                currentBreakingBlock.remove(event.getPlayer());
            }

        };
    }

    @Override
    public @NotNull List<CycledTask> getCycledTasks() {
        return List.of(new CycledTask() {
            @Override
            public BukkitRunnable getTask() {

                return new BukkitRunnable() {

                    public void run() {
                        Set<ItemStack> autoRepairItems = new HashSet<>();
                        Bukkit.getOnlinePlayers().forEach(player -> {
                            PlayerInventory inv = player.getInventory();
                            handle(autoRepairItems, inv.getHelmet());
                            handle(autoRepairItems, inv.getChestplate());
                            handle(autoRepairItems, inv.getLeggings());
                            handle(autoRepairItems, inv.getBoots());
                            if (!currentBreakingBlock.contains(player)) {
                                handle(autoRepairItems, inv.getItemInMainHand());
                            }
                            handle(autoRepairItems, inv.getItemInOffHand());
                        });
                        autoRepairItems.forEach(itemStack -> {
                            var itemMeta = itemStack.getItemMeta();
                            if (itemMeta == null) return;
                            if (!(itemMeta instanceof Damageable damageable)) return;
                            if (damageable.hasDamage()) {
                                int newDamage = damageable.getDamage() -
                                        (getEnchantmentFacade().getEnchantmentLevel(itemStack, AutoRepair.class));
                                damageable.setDamage(Math.max(0, newDamage));
                                itemStack.setItemMeta(itemMeta);
                            }
                        });
                    }

                    private void handle(Set<ItemStack> set, ItemStack item) {
                        if (item != null && getEnchantmentFacade().hasEnchantment(item, AutoRepair.class)) {
                            set.add(item);
                        }
                    }

                };
            }

            @Override
            public int getDelay() {
                return 1;
            }

            @Override
            public int getPeriod() {
                return 40;
            }
        });
    }
}
