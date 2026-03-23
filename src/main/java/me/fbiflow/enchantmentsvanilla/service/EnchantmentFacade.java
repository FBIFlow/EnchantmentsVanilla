package me.fbiflow.enchantmentsvanilla.service;

import me.fbiflow.enchantmentsvanilla.enchantment.Enchantment;
import me.fbiflow.enchantmentsvanilla.service.constants.EnchantmentConstants;
import me.fbiflow.enchantmentsvanilla.service.item.EnchantmentItemService;
import me.fbiflow.enchantmentsvanilla.service.lifecycle.EnchantmentLifecycleService;
import me.fbiflow.enchantmentsvanilla.service.registry.EnchantmentRegistry;
import me.fbiflow.enchantmentsvanilla.service.storage.EnchantmentDataStorage;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EnchantmentFacade {

    private final EnchantmentRegistry registry;
    private final EnchantmentItemService itemService;
    private final EnchantmentLifecycleService lifecycleService;

    public EnchantmentFacade(@NotNull Plugin plugin) {
        NamespacedKey enchantmentKey = new NamespacedKey(plugin, EnchantmentConstants.PDC_ENCHANTMENTS_KEY);
        NamespacedKey enchantmentLevelKey = new NamespacedKey(plugin, EnchantmentConstants.PDC_ENCHANTMENTS_LEVEL_KEY);
        EnchantmentDataStorage dataStorage = new EnchantmentDataStorage(enchantmentKey, enchantmentLevelKey);

        this.registry = new EnchantmentRegistry();
        this.itemService = new EnchantmentItemService(registry, dataStorage);
        this.lifecycleService = new EnchantmentLifecycleService(registry, plugin);
    }

    public void registerEnchantment(@NotNull Enchantment enchantment) {
        registry.register(enchantment);
        lifecycleService.startCycle(enchantment);
    }

    public void unregisterEnchantment(@NotNull Enchantment enchantment) {
        registry.unregister(enchantment);
        lifecycleService.stopCycle(enchantment);
    }

    public boolean isApplicable(@NotNull ItemStack item, @NotNull Enchantment enchantment) {
        return itemService.isEnchantmentApplicable(item, enchantment);
    }

    public boolean isApplicable(@NotNull ItemStack item, @NotNull Class<? extends Enchantment> enchantmentClass) {
        return itemService.isEnchantmentApplicable(item, enchantmentClass);
    }

    public boolean isApplicable(@NotNull Material material, @NotNull Enchantment enchantment) {
        ItemStack tempItem = new ItemStack(material);
        return isApplicable(tempItem, enchantment);
    }

    public boolean isApplicable(@NotNull Material material, @NotNull Class<? extends Enchantment> enchantmentClass) {
        ItemStack tempItem = new ItemStack(material);
        return isApplicable(tempItem, enchantmentClass);
    }

    public int getEnchantmentLevel(@NotNull ItemStack item, @NotNull Class<? extends Enchantment> enchantment) {
        return itemService.getEnchantmentLevel(item, enchantment);
    }

    @NotNull
    public Map<Enchantment, Integer> getItemEnchantmentsWithLevels(@NotNull ItemStack item) {
        return itemService.getEnchantmentsWithLevels(item);
    }

    public boolean hasEnchantment(@NotNull ItemStack item, @NotNull Class<? extends Enchantment> enchantment) {
        return itemService.hasEnchantment(item, enchantment);
    }

    @NotNull
    public Optional<ItemStack> applyEnchantment(@NotNull ItemStack item,
                                                @NotNull Class<? extends Enchantment> enchantment,
                                                int level) {
        return itemService.applyEnchantmentToItem(item, enchantment, level);
    }

    @NotNull
    public Optional<ItemStack> applyEnchantment(@NotNull ItemStack item,
                                                @NotNull Class<? extends Enchantment> enchantment) {
        return itemService.applyEnchantmentToItem(item, enchantment);
    }

    @NotNull
    public ItemStack removeEnchantment(@NotNull ItemStack item, @NotNull Class<? extends Enchantment> enchantment) {
        return itemService.removeEnchantment(item, enchantment);
    }

    @NotNull
    public ItemStack createEnchantedBook(@NotNull Class<? extends Enchantment> enchantment, int level) {
        return itemService.createEnchantedBook(enchantment, level);
    }

    @NotNull
    public List<Enchantment> getItemEnchantments(@NotNull ItemStack item) {
        return itemService.getEnchantments(item);
    }

    @NotNull
    public List<Class<? extends Enchantment>> getItemEnchantmentClasses(@NotNull ItemStack item) {
        return itemService.getEnchantmentClasses(item);
    }

    @Nullable
    public Enchantment getEnchantment(@NotNull Class<? extends Enchantment> clazz) {
        return registry.getByClass(clazz);
    }

    @Nullable
    public Enchantment getEnchantment(@NotNull String name) {
        return registry.getByName(name);
    }

    @NotNull
    public List<Enchantment> getAllEnchantments() {
        return registry.getAll();
    }

    @NotNull
    public Optional<Class<? extends Enchantment>> findEnchantmentClass(@NotNull String name) {
        return registry.findByName(name).map(Enchantment::getClass);
    }
}