package me.fbiflow.enchantmentsvanilla.service.anvil;

import me.fbiflow.enchantmentsvanilla.Loader;
import me.fbiflow.enchantmentsvanilla.enchantment.Enchantment;
import me.fbiflow.enchantmentsvanilla.service.constants.EnchantmentConstants;
import me.fbiflow.enchantmentsvanilla.service.EnchantmentFacade;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.HashMap;
import java.util.Map;

public class AnvilService implements Listener {

    private final EnchantmentFacade enchantmentFacade = Loader.getInstance().getEnchantmentFacade();

    @EventHandler
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        var firstItem = event.getInventory().getFirstItem();
        var secondItem = event.getInventory().getSecondItem();

        if (firstItem == null || firstItem.getType() == Material.AIR) {
            return;
        }

        if (secondItem == null || secondItem.getType() == Material.AIR) {
            return;
        }

        if (secondItem.getType() == Material.ENCHANTED_BOOK) {
            handleItemWithBook(event, firstItem, secondItem);
        }
        else if (firstItem.getType() == Material.ENCHANTED_BOOK &&
                secondItem.getType() == Material.ENCHANTED_BOOK) {
            handleBookCombining(event, firstItem, secondItem);
        }
    }

    private void handleItemWithBook(PrepareAnvilEvent event, ItemStack item, ItemStack book) {
        Map<Enchantment, Integer> bookEnchantments = enchantmentFacade.getItemEnchantmentsWithLevels(book);

        if (bookEnchantments.isEmpty()) {
            return;
        }

        ItemStack currentResult = item.clone();
        boolean anyChanges = false;
        int totalCost = 0;

        for (Map.Entry<Enchantment, Integer> entry : bookEnchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int bookLevel = entry.getValue();

            if (enchantmentFacade.isApplicable(item.getType(), enchantment)) {
                int currentLevel = enchantmentFacade.getEnchantmentLevel(item, enchantment.getClass());

                int newLevel;
                if (currentLevel == 0) {
                    newLevel = bookLevel;
                } else if (bookLevel > currentLevel) {
                    newLevel = bookLevel;
                } else if (bookLevel == currentLevel && currentLevel < enchantment.getMaxLevel()) {
                    newLevel = Math.min(currentLevel + 1, enchantment.getMaxLevel());
                } else {
                    continue;
                }

                var optional = enchantmentFacade.applyEnchantment(currentResult, enchantment.getClass(), newLevel);
                if (optional.isPresent()) {
                    currentResult = optional.get();
                    anyChanges = true;
                    totalCost += EnchantmentConstants.BASE_ANVIL_REPAIR_COST * newLevel;
                }
            }
        }

        if (anyChanges) {
            applyAnvilResult(event, currentResult, totalCost);
        }
    }

    private void handleBookCombining(PrepareAnvilEvent event, ItemStack firstBook, ItemStack secondBook) {
        Map<Enchantment, Integer> firstEnchantments = enchantmentFacade.getItemEnchantmentsWithLevels(firstBook);
        Map<Enchantment, Integer> secondEnchantments = enchantmentFacade.getItemEnchantmentsWithLevels(secondBook);

        if (firstEnchantments.isEmpty() || secondEnchantments.isEmpty()) {
            return;
        }

        ItemStack resultBook = new ItemStack(Material.ENCHANTED_BOOK);
        Map<Enchantment, Integer> resultEnchantments = new HashMap<>();
        int totalCost = 0;
        boolean anyChanges = false;

        for (Map.Entry<Enchantment, Integer> entry : firstEnchantments.entrySet()) {
            resultEnchantments.put(entry.getKey(), entry.getValue());
        }

        for (Map.Entry<Enchantment, Integer> entry : secondEnchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            int secondLevel = entry.getValue();
            Integer currentLevel = resultEnchantments.get(enchantment);

            if (currentLevel == null) {
                resultEnchantments.put(enchantment, secondLevel);
                totalCost += EnchantmentConstants.BASE_ANVIL_REPAIR_COST * secondLevel;
                anyChanges = true;
            } else {
                int newLevel;

                if (currentLevel.equals(secondLevel)) {
                    newLevel = Math.min(currentLevel + 1, enchantment.getMaxLevel());
                    if (newLevel > currentLevel) {
                        anyChanges = true;
                    }
                } else {
                    newLevel = Math.max(currentLevel, secondLevel);
                }

                if (!currentLevel.equals(newLevel)) {
                    resultEnchantments.put(enchantment, newLevel);
                    totalCost += EnchantmentConstants.BASE_ANVIL_REPAIR_COST * newLevel;
                    anyChanges = true;
                }
            }
        }

        if (anyChanges) {
            for (Map.Entry<Enchantment, Integer> entry : resultEnchantments.entrySet()) {
                var optional = enchantmentFacade.applyEnchantment(resultBook,
                        entry.getKey().getClass(), entry.getValue());
                if (optional.isPresent()) {
                    resultBook = optional.get();
                }
            }

            ItemMeta firstMeta = firstBook.getItemMeta();
            ItemMeta resultMeta = resultBook.getItemMeta();
            if (firstMeta != null && resultMeta != null && firstMeta.hasDisplayName()) {
                resultMeta.setDisplayName(firstMeta.getDisplayName());
                resultBook.setItemMeta(resultMeta);
            }

            applyAnvilResult(event, resultBook, totalCost);
        }
    }

    private void applyAnvilResult(PrepareAnvilEvent event, ItemStack result, int cost) {
        int finalCost = Math.max(cost, EnchantmentConstants.BASE_ANVIL_REPAIR_COST);
        finalCost += event.getView().getRepairCost();

        event.getView().setRepairCost(finalCost);
        event.setResult(result);
    }
}