package me.fbiflow.enchantmentsvanilla.service.storage;

import me.fbiflow.enchantmentsvanilla.enchantment.Enchantment;
import me.fbiflow.enchantmentsvanilla.util.EnchantmentLevelUtil;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EnchantmentDataStorage {

    private final NamespacedKey enchantmentKey;
    private final NamespacedKey enchantmentLevelKey;

    public EnchantmentDataStorage(@NotNull NamespacedKey enchantmentKey, @NotNull NamespacedKey enchantmentLevelKey) {
        this.enchantmentKey = enchantmentKey;
        this.enchantmentLevelKey = enchantmentLevelKey;
    }

    @NotNull
    public List<String> getEnchantmentNames(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return List.of();
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        List<String> enchantments = pdc.get(enchantmentKey, PersistentDataType.LIST.strings());
        return enchantments != null ? enchantments : List.of();
    }

    @NotNull
    public Map<String, Integer> getEnchantmentsWithLevels(@NotNull ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if (meta == null) {
            return Map.of();
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        List<String> enchantments = pdc.get(enchantmentKey, PersistentDataType.LIST.strings());
        if (enchantments == null || enchantments.isEmpty()) {
            return Map.of();
        }

        List<Integer> levels = pdc.get(enchantmentLevelKey, PersistentDataType.LIST.integers());

        Map<String, Integer> result = new HashMap<>();
        for (int i = 0; i < enchantments.size(); i++) {
            String enchantName = enchantments.get(i);
            int level = (levels != null && i < levels.size()) ? levels.get(i) : 1;
            result.put(enchantName, level);
        }

        return result;
    }

    public int getEnchantmentLevel(@NotNull ItemStack item, @NotNull Enchantment enchantment) {
        return getEnchantmentsWithLevels(item).getOrDefault(enchantment.getEnchantmentName(), 0);
    }

    public boolean hasEnchantment(@NotNull ItemStack item, @NotNull Enchantment enchantment) {
        return getEnchantmentNames(item).contains(enchantment.getEnchantmentName());
    }

    @NotNull
    public ItemStack addEnchantment(@NotNull ItemStack item, @NotNull Enchantment enchantment, int level) {
        int normalizedLevel = EnchantmentLevelUtil.normalizeLevel(level, enchantment.getMaxLevel());

        ItemStack result = item.clone();
        ItemMeta meta = result.getItemMeta();
        if (meta == null) {
            return result;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        List<String> enchantments = new ArrayList<>(getEnchantmentNames(result));
        Map<String, Integer> currentWithLevels = getEnchantmentsWithLevels(result);

        String enchantName = enchantment.getEnchantmentName();
        Integer currentLevel = currentWithLevels.get(enchantName);

        if (!enchantments.contains(enchantName)) {
            enchantments.add(enchantName);

            List<Integer> levels = new ArrayList<>();
            for (String name : enchantments) {
                if (name.equals(enchantName)) {
                    levels.add(normalizedLevel);
                } else {
                    levels.add(currentWithLevels.getOrDefault(name, 1));
                }
            }

            pdc.set(enchantmentKey, PersistentDataType.LIST.strings(), enchantments);
            pdc.set(enchantmentLevelKey, PersistentDataType.LIST.integers(), levels);

            result.setItemMeta(meta);
        } else if (currentLevel != null && currentLevel != normalizedLevel) {
            List<Integer> levels = new ArrayList<>();
            for (String name : enchantments) {
                if (name.equals(enchantName)) {
                    levels.add(normalizedLevel);
                } else {
                    levels.add(currentWithLevels.get(name));
                }
            }

            pdc.set(enchantmentLevelKey, PersistentDataType.LIST.integers(), levels);
            result.setItemMeta(meta);
        }

        return result;
    }

    @NotNull
    public ItemStack removeEnchantment(@NotNull ItemStack item, @NotNull Enchantment enchantment) {
        ItemStack result = item.clone();
        ItemMeta meta = result.getItemMeta();
        if (meta == null) {
            return result;
        }

        PersistentDataContainer pdc = meta.getPersistentDataContainer();

        List<String> enchantments = new ArrayList<>(getEnchantmentNames(result));
        Map<String, Integer> levelsMap = getEnchantmentsWithLevels(result);

        String enchantName = enchantment.getEnchantmentName();

        if (enchantments.remove(enchantName)) {
            levelsMap.remove(enchantName);

            if (enchantments.isEmpty()) {
                pdc.remove(enchantmentKey);
                pdc.remove(enchantmentLevelKey);
            } else {
                pdc.set(enchantmentKey, PersistentDataType.LIST.strings(), enchantments);

                List<Integer> levels = new ArrayList<>();
                for (String name : enchantments) {
                    levels.add(levelsMap.getOrDefault(name, 1));
                }
                pdc.set(enchantmentLevelKey, PersistentDataType.LIST.integers(), levels);
            }

            result.setItemMeta(meta);
        }

        return result;
    }
}