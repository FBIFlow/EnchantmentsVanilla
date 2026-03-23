package me.fbiflow.enchantmentsvanilla.service.item;

import me.fbiflow.enchantmentsvanilla.enchantment.Enchantment;
import me.fbiflow.enchantmentsvanilla.service.constants.EnchantmentConstants;
import me.fbiflow.enchantmentsvanilla.util.ComponentUtil;
import me.fbiflow.enchantmentsvanilla.util.EnchantmentLevelUtil;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import me.fbiflow.enchantmentsvanilla.service.registry.EnchantmentRegistry;
import me.fbiflow.enchantmentsvanilla.service.storage.EnchantmentDataStorage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

public class EnchantmentItemService {

    private final EnchantmentRegistry registry;
    private final EnchantmentDataStorage dataStorage;

    public EnchantmentItemService(@NotNull EnchantmentRegistry registry, @NotNull EnchantmentDataStorage dataStorage) {
        this.registry = registry;
        this.dataStorage = dataStorage;
    }

    @NotNull
    public ItemStack createEnchantedBook(@NotNull Class<? extends Enchantment> enchantmentClass, int level) {
        ItemStack book = new ItemStack(Material.ENCHANTED_BOOK);
        return applyEnchantmentToItem(book, enchantmentClass, level).orElse(book);
    }

    @NotNull
    public Optional<ItemStack> applyEnchantmentToItem(@NotNull ItemStack item, @NotNull Class<? extends Enchantment> enchantmentClass) {
        return applyEnchantmentToItem(item, enchantmentClass, 1);
    }

    @NotNull
    public Optional<ItemStack> applyEnchantmentToItem(@NotNull ItemStack item,
                                                      @NotNull Class<? extends Enchantment> enchantmentClass,
                                                      int level) {
        Enchantment enchantment = registry.getByClass(enchantmentClass);
        if (enchantment == null) {
            return Optional.empty();
        }

        if (!isEnchantmentApplicable(item, enchantment)) {
            return Optional.empty();
        }

        int normalizedLevel = EnchantmentLevelUtil.normalizeLevel(level, enchantment.getMaxLevel());

        ItemStack result = item.clone();
        result = dataStorage.addEnchantment(result, enchantment, normalizedLevel);

        ItemMeta meta = result.getItemMeta();
        if (meta != null) {
            updateItemLore(meta, enchantment, normalizedLevel);
            meta.setEnchantmentGlintOverride(true);
            result.setItemMeta(meta);
        }

        return Optional.of(result);
    }

    @NotNull
    public ItemStack removeEnchantment(@NotNull ItemStack item, @NotNull Class<? extends Enchantment> enchantmentClass) {
        Enchantment enchantment = registry.getByClass(enchantmentClass);
        if (enchantment == null) {
            return item;
        }
        return dataStorage.removeEnchantment(item, enchantment);
    }

    public boolean hasEnchantment(@NotNull ItemStack item, @NotNull Class<? extends Enchantment> enchantmentClass) {
        Enchantment enchantment = registry.getByClass(enchantmentClass);
        if (enchantment == null) {
            return false;
        }
        return dataStorage.hasEnchantment(item, enchantment);
    }

    public int getEnchantmentLevel(@NotNull ItemStack item, @NotNull Class<? extends Enchantment> enchantmentClass) {
        Enchantment enchantment = registry.getByClass(enchantmentClass);
        if (enchantment == null) {
            return 0;
        }
        return dataStorage.getEnchantmentLevel(item, enchantment);
    }

    @NotNull
    public Map<Enchantment, Integer> getEnchantmentsWithLevels(@NotNull ItemStack item) {
        Map<String, Integer> levelsMap = dataStorage.getEnchantmentsWithLevels(item);
        Map<Enchantment, Integer> result = new HashMap<>();

        levelsMap.forEach((name, level) -> {
            Enchantment enchantment = registry.getByName(name);
            if (enchantment != null) {
                result.put(enchantment, level);
            }
        });

        return result;
    }

    @NotNull
    public List<Enchantment> getEnchantments(@NotNull ItemStack item) {
        List<String> enchantmentNames = dataStorage.getEnchantmentNames(item);
        return enchantmentNames.stream().map(registry::getByName).filter(Objects::nonNull).toList();
    }

    @NotNull
    public List<Class<? extends Enchantment>> getEnchantmentClasses(@NotNull ItemStack item) {
        return getEnchantments(item).stream().map(Enchantment::getClass).collect(Collectors.toList());
    }

    public boolean isEnchantmentApplicable(@NotNull ItemStack item, @NotNull Enchantment enchantment) {
        if (item.getType() == Material.ENCHANTED_BOOK) {
            return true;
        }
        return enchantment.getApplicableTypes().contains(item.getType());
    }

    public boolean isEnchantmentApplicable(@NotNull ItemStack item, @NotNull Class<? extends Enchantment> enchantmentClass) {
        Enchantment enchantment = registry.getByClass(enchantmentClass);
        return enchantment != null && isEnchantmentApplicable(item, enchantment);
    }

    private void updateItemLore(@NotNull ItemMeta meta, @NotNull Enchantment enchantment, int level) {
        List<Component> lore = new ArrayList<>();

        if (meta.hasLore()) {
            lore.addAll(Objects.requireNonNull(meta.lore()));
        }

        String levelRoman = EnchantmentLevelUtil.toRoman(level);
        String displayName = level > 1 ?
                enchantment.getEnchantmentName() + " " + levelRoman :
                enchantment.getEnchantmentName();

        Component enchantmentComponent = Component.text(displayName)
                .color(EnchantmentConstants.ENCHANTMENT_TEXT_COLOR)
                .decoration(TextDecoration.ITALIC, false);

        int existingIndex = -1;
        String enchantmentName = enchantment.getEnchantmentName();

        for (int i = 0; i < lore.size(); i++) {
            Component component = lore.get(i);
            String componentString = ComponentUtil.componentToString(component);

            if (componentString.startsWith(enchantmentName)) {
                existingIndex = i;
                break;
            }
        }

        if (existingIndex != -1) {
            lore.set(existingIndex, enchantmentComponent);
        } else {
            lore.add(enchantmentComponent);
        }

        lore.sort((c1, c2) -> {
            String s1 = ComponentUtil.componentToString(c1);
            String s2 = ComponentUtil.componentToString(c2);
            return s1.compareTo(s2);
        });

        meta.lore(lore);
    }
}