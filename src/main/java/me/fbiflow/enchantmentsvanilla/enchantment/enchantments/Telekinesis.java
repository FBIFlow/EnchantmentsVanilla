package me.fbiflow.enchantmentsvanilla.enchantment.enchantments;

import me.fbiflow.enchantmentsvanilla.enchantment.ApplicableType;
import me.fbiflow.enchantmentsvanilla.enchantment.CycledTask;
import me.fbiflow.enchantmentsvanilla.enchantment.Enchantment;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Item;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

//TODO: IN TEST
public class Telekinesis extends Enchantment {

    @Override
    public @NotNull String getEnchantmentName() {
        return "Телекинез";
    }

    @Override
    public @NotNull List<Material> getApplicableTypes() {
        return Stream.of(
                        ApplicableType.PICKAXE,
                        ApplicableType.AXE,
                        ApplicableType.HOE,
                        ApplicableType.SHOVEL)
                .flatMap(applicableType -> applicableType.materials.stream())
                .toList();
    }

    @Override
    public @NotNull String getDescription() {
        return "Сломанные блоки попадают сразу в инвентарь";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public @NotNull Listener getEventListener() {
        return new Listener() {

            @EventHandler
            public void onItemDrop(BlockDropItemEvent event) {
                var handItem = event.getPlayer().getInventory().getItemInMainHand();
                if (handItem.getType() == Material.AIR) {
                    return;
                }
                if (getEnchantmentFacade().hasEnchantment(handItem, Telekinesis.class)) {
                    event.setCancelled(true);
                }
                handleItemDrop(event.getPlayer().getInventory(), event.getItems(), event.getBlock().getLocation());
            }
        };
    }

    public void handleItemDrop(PlayerInventory playerInv, List<Item> items, Location blockLocation) {
        handleItemDrop(playerInv, blockLocation, items.stream().map(Item::getItemStack).toList());
    }

    public void handleItemDrop(PlayerInventory playerInv, Location blockLocation, List<ItemStack> itemStacks) {
        var handItem = playerInv.getItemInMainHand();
        if (handItem.getType() == Material.AIR) {
            return;
        }
        if (!getEnchantmentFacade().hasEnchantment(playerInv.getItemInMainHand(), Telekinesis.class)) {
            return;
        }
        List<ItemStack> notStored = new ArrayList<>();
        for (ItemStack itemStack : itemStacks) {
            notStored.add(playerInv.addItem(itemStack).get(0));
        }
        for (ItemStack item : notStored) {
            if (item == null) {
                continue;
            }
            blockLocation.getWorld().dropItemNaturally(blockLocation, item);
        }
    }

    @Override
    public @NotNull List<CycledTask> getCycledTasks() {
        return List.of();
    }
}