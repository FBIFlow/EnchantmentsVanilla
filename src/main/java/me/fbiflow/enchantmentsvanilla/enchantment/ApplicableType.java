package me.fbiflow.enchantmentsvanilla.enchantment;

import org.bukkit.Material;

import java.util.List;

public enum ApplicableType {

    ELYTRA(List.of(Material.ELYTRA)),

    SWORD(List.of(Material.WOODEN_SWORD, Material.STONE_SWORD,
            Material.COPPER_SWORD, Material.IRON_SWORD,
            Material.GOLDEN_SWORD, Material.DIAMOND_SWORD,
            Material.NETHERITE_SWORD)),
    AXE(List.of(
            Material.WOODEN_AXE, Material.STONE_AXE,
            Material.COPPER_AXE, Material.IRON_AXE,
            Material.GOLDEN_AXE, Material.DIAMOND_AXE,
            Material.NETHERITE_AXE)),
    PICKAXE(List.of(
            Material.WOODEN_PICKAXE, Material.STONE_PICKAXE,
            Material.COPPER_PICKAXE, Material.IRON_PICKAXE,
            Material.GOLDEN_PICKAXE, Material.DIAMOND_PICKAXE,
            Material.NETHERITE_PICKAXE)),
    SHOVEL(List.of(
            Material.WOODEN_SHOVEL, Material.STONE_SHOVEL,
            Material.COPPER_SHOVEL, Material.IRON_SHOVEL,
            Material.GOLDEN_SHOVEL, Material.DIAMOND_SHOVEL,
            Material.NETHERITE_SHOVEL)),
    HOE(List.of(
            Material.WOODEN_HOE, Material.STONE_HOE,
            Material.COPPER_HOE, Material.IRON_HOE,
            Material.GOLDEN_HOE, Material.DIAMOND_HOE,
            Material.NETHERITE_HOE)),
    HELMET(List.of(
            Material.LEATHER_HELMET, Material.CHAINMAIL_HELMET,
            Material.COPPER_HELMET, Material.IRON_HELMET,
            Material.GOLDEN_HELMET, Material.DIAMOND_HELMET,
            Material.NETHERITE_HELMET, Material.TURTLE_HELMET)),
    CHESTPLATE(List.of(
            Material.LEATHER_CHESTPLATE, Material.CHAINMAIL_CHESTPLATE,
            Material.COPPER_CHESTPLATE, Material.IRON_CHESTPLATE,
            Material.GOLDEN_CHESTPLATE, Material.DIAMOND_CHESTPLATE,
            Material.NETHERITE_CHESTPLATE)),
    LEGGINGS(List.of(
            Material.LEATHER_LEGGINGS, Material.CHAINMAIL_LEGGINGS,
            Material.COPPER_LEGGINGS, Material.IRON_LEGGINGS,
            Material.GOLDEN_LEGGINGS, Material.DIAMOND_LEGGINGS,
            Material.NETHERITE_LEGGINGS)),
    BOOTS(List.of(
            Material.LEATHER_BOOTS, Material.CHAINMAIL_BOOTS,
            Material.COPPER_BOOTS, Material.IRON_BOOTS,
            Material.GOLDEN_BOOTS, Material.DIAMOND_BOOTS,
            Material.NETHERITE_BOOTS));

    public final List<Material> materials;

    ApplicableType(List<Material> materials) {
        this.materials = materials;
    }
}