package me.fbiflow.enchantmentsvanilla.enchantment;

import org.bukkit.scheduler.BukkitRunnable;

public interface CycledTask {

    BukkitRunnable getTask();

    /**
     * Waiting before first task running
     */
    int getDelay();

    /**
     * If period <= 0 - task will run one-time
     */
    int getPeriod();

}