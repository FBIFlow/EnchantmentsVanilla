package me.fbiflow.enchantmentsvanilla.enchantment.enchantments;

import me.fbiflow.enchantmentsvanilla.Loader;
import me.fbiflow.enchantmentsvanilla.enchantment.ApplicableType;
import me.fbiflow.enchantmentsvanilla.enchantment.CycledTask;
import me.fbiflow.enchantmentsvanilla.enchantment.Enchantment;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3i;

import java.util.*;

//TODO: IN PROCESS
public class TreeCutter extends Enchantment {

    private final List<Material> woodBlocks = List.of(
            Material.ACACIA_LOG,
            Material.BIRCH_LOG,
            Material.CHERRY_LOG,
            Material.DARK_OAK_LOG,
            Material.JUNGLE_LOG,
            Material.MANGROVE_LOG,
            Material.OAK_LOG,
            Material.PALE_OAK_LOG,
            Material.SPRUCE_LOG
    );

    private final List<Material> leavesBlocks = List.of(
            Material.ACACIA_LEAVES,
            Material.BIRCH_LEAVES,
            Material.CHERRY_LEAVES,
            Material.DARK_OAK_LEAVES,
            Material.JUNGLE_LEAVES,
            Material.MANGROVE_LEAVES,
            Material.OAK_LEAVES,
            Material.PALE_OAK_LEAVES,
            Material.SPRUCE_LEAVES
    );

    @Override
    public @NotNull String getEnchantmentName() {
        return "Лесоруб";
    }

    @Override
    public @NotNull List<Material> getApplicableTypes() {
        return ApplicableType.AXE.materials;
    }

    @Override
    public @NotNull String getDescription() {
        return "Позволяет топору срубить всё дерево сразу";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    @Override
    public @NotNull Listener getEventListener() {
        return new Listener() {

            @EventHandler
            public void onBlockDamage(BlockDamageEvent event) {
                if (event.getPlayer().isSneaking()) {
                    return;
                }
                if (!getEnchantmentFacade().hasEnchantment(event.getPlayer().getInventory().getItemInMainHand(), TreeCutter.class)) {
                    return;
                }
                System.out.println(event.getBlock().getType());
                if (!woodBlocks
                        .contains(event.getBlock().getType())) {
                    return;
                }
            }

            @EventHandler
            public void onTreeCut(BlockBreakEvent event) {
                if (!getEnchantmentFacade().hasEnchantment(event.getPlayer().getInventory().getItemInMainHand(), TreeCutter.class)) {
                    return;
                }
                if (event.getPlayer().isSneaking()) {
                    return;
                }
                System.out.println(event.getBlock().getType());
                if (!woodBlocks
                        .contains(event.getBlock().getType())) {
                    return;
                }
                breakTree(event.getBlock().getLocation(), event.getPlayer());

                event.setCancelled(true);
            }
        };
    }

    @Override
    public @NotNull List<CycledTask> getCycledTasks() {
        return List.of();
    }

    private void breakTree(Location treeLocation, Player player) {
        PlayerInventory playerInv = player.getInventory();
        World world = treeLocation.getWorld();
        Set<Vector3i> woodBlocks = findWoodBlocks(world,
                new Vector3i(
                        treeLocation.getBlockX(),
                        treeLocation.getBlockY(),
                        treeLocation.getBlockZ()
                ));
        Set<Vector3i> leavesBlocks = findLeavesBlocks(world, woodBlocks);
        new BukkitRunnable() {
            @Override
            public void run() {
                ItemStack itemStack = playerInv.getItemInMainHand();
                handleDrop(itemStack, woodBlocks, world, playerInv);
                //handleDrop(itemStack, leavesBlocks, world, playerInv);
            }
        }.runTask(Loader.getPlugin(Loader.class));
        var handItem = player.getInventory().getItemInMainHand();
        if (handItem.getType() != Material.AIR) {
            ItemMeta meta = handItem.getItemMeta();
            if (meta == null) {
                return;
            }
            var damageable = (Damageable) meta;
            damageable.setDamage(damageable.getDamage() + woodBlocks.size());
            handItem.setItemMeta(meta);
        }
    }

    private void handleDrop(ItemStack itemStack, Set<Vector3i> blocks, World world, PlayerInventory playerInv) {
        for (Vector3i block : blocks) {
            if (!getEnchantmentFacade().hasEnchantment(itemStack, Telekinesis.class)) {
                world.getBlockAt(block.x, block.y, block.z)
                        .breakNaturally(itemStack);
            } else {
                ((Telekinesis) getEnchantmentFacade().getEnchantment(Telekinesis.class))
                        .handleItemDrop(playerInv,
                                new Location(world, block.x, block.y, block.z),
                                world.getBlockAt(block.x, block.y, block.z).getDrops(itemStack).stream().toList()
                        );
                if (world.getType(block.x, block.y, block.z) != Material.AIR) {
                    world.setType(block.x, block.y, block.z, Material.AIR);
                }
            }
        }
    }

    private Set<Vector3i> findWoodBlocks(World world, Vector3i startLocation) {
        Set<Vector3i> woodLocations = new HashSet<>(List.of(startLocation));
        Queue<Vector3i> handleQueue = new LinkedList<>(List.of(startLocation));

        while (!handleQueue.isEmpty()) {
            var handle = handleQueue.poll();
            var result = handleNearWood(world, handle.x, handle.y, handle.z);
            for (Vector3i woodLocation : result) {
                if (!woodLocations.contains(woodLocation)) {
                    handleQueue.add(woodLocation);
                }
                woodLocations.add(woodLocation);
            }
        }
        return woodLocations;
    }

    private Set<Vector3i> findLeavesBlocks(World world, Set<Vector3i> woodBlocks) {
        Set<Vector3i> leavesBlocks = new HashSet<>();
        for (Vector3i woodLocation : woodBlocks) {
            int x = woodLocation.x, y = woodLocation.y, z = woodLocation.z;
            for (int xo = -2; xo <= 2; xo++) {
                for (int yo = -2; yo <= 2; yo++) {
                    for (int zo = -2; zo <= 2; zo++) {
                        if (xo == 0 && yo == 0 && zo == 0) {
                            continue;
                        }
                        int lx = x + xo, ly = y + yo, lz = z + zo;
                        if (isLeaves(world.getType(lx, ly, lz))) {
                            leavesBlocks.add(new Vector3i(lx, ly, lz));
                        }
                    }
                }
            }
        }
        return leavesBlocks;
    }

    private List<Vector3i> handleNearWood(World world, int x, int y, int z) {
        List<Vector3i> woodBlocks = new ArrayList<>();
        for (int xo = -1; xo <= 1; xo++) {
            for (int yo = -1; yo <= 1; yo++) {
                for (int zo = -1; zo <= 1; zo++) {
                    if (xo == 0 && yo == 0 && zo == 0) {
                        continue;
                    }
                    int lx = x + xo, ly = y + yo, lz = z + zo;
                    if (isWood(world.getType(lx, ly, lz))) {
                        woodBlocks.add(new Vector3i(lx, ly, lz));
                    }
                }
            }
        }
        return woodBlocks;
    }

    private boolean isWood(Material material) {
        return woodBlocks.contains(material);
    }

    private boolean isLeaves(Material material) {
        return leavesBlocks.contains(material);
    }
}