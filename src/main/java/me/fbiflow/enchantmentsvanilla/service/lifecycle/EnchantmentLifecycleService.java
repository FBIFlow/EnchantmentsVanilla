package me.fbiflow.enchantmentsvanilla.service.lifecycle;

import me.fbiflow.enchantmentsvanilla.enchantment.Enchantment;
import me.fbiflow.enchantmentsvanilla.service.registry.EnchantmentRegistry;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitTask;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class EnchantmentLifecycleService {

    private final EnchantmentRegistry registry;
    private final Plugin plugin;
    private final Map<Enchantment, BukkitTask> activeTasks = new HashMap<>();

    public EnchantmentLifecycleService(@NotNull EnchantmentRegistry registry, @NotNull Plugin plugin) {
        this.registry = registry;
        this.plugin = plugin;
    }

    public void startCycleAll() {
        registry.getAll().forEach(this::startCycle);
    }

    public void stopCycleAll() {
        registry.getAll().forEach(this::stopCycle);
    }

    public void startCycle(Enchantment enchantment) {
        if (!registry.getAll().contains(enchantment)) {
            throw new IllegalStateException("Trying to handle Enchantment is not contained in the registry: " + enchantment);
        }
        Bukkit.getPluginManager().registerEvents(enchantment.getEventListener(), plugin);
        enchantment.getCycledTasks().forEach(task -> {
            BukkitTask bukkitTask;
            if (task.getPeriod() > 0) {
                bukkitTask = task.getTask().runTaskTimer(plugin, task.getDelay(), task.getPeriod());
            } else {
                bukkitTask = task.getTask().runTaskLater(plugin, task.getDelay());
            }
            activeTasks.put(enchantment, bukkitTask);
        });
    }

    public void stopCycle(Enchantment enchantment) {
        HandlerList.unregisterAll(enchantment.getEventListener());
        activeTasks.remove(enchantment).cancel();
    }
}