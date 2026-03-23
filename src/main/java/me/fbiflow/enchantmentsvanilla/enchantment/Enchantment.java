package me.fbiflow.enchantmentsvanilla.enchantment;

import java.util.List;

import me.fbiflow.enchantmentsvanilla.Loader;
import me.fbiflow.enchantmentsvanilla.service.EnchantmentFacade;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;

public abstract class Enchantment {

    private final EnchantmentFacade enchantmentFacade;

    {
        this.enchantmentFacade = Loader.getInstance().getEnchantmentFacade();
        if (this.enchantmentFacade == null) {
            throw new RuntimeException("Cannot load enchantment util");
        }
        if (getMaxLevel() <= 0) {
            throw new RuntimeException("Max level must be greater than 0");
        }
    }

    protected EnchantmentFacade getEnchantmentFacade() {
        return enchantmentFacade;
    }

    public abstract @NotNull String getEnchantmentName();

    public abstract @NotNull List<Material> getApplicableTypes();

    public abstract @NotNull String getDescription();

    public abstract int getMaxLevel();

    public abstract @NotNull Listener getEventListener();

    public abstract @NotNull List<CycledTask> getCycledTasks();

}