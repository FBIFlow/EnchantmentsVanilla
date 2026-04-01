package me.fbiflow.enchantmentsvanilla.enchantment.enchantments;

import me.fbiflow.enchantmentsvanilla.enchantment.ApplicableType;
import me.fbiflow.enchantmentsvanilla.enchantment.Enchantment;
import me.fbiflow.enchantmentsvanilla.enchantment.CycledTask;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LavaWalker extends Enchantment {

    private final int SECONDS_TO_REMOVE_BASALT = 10;

    @Override
    public @NotNull String getEnchantmentName() {
        return "Лавоход";
    }

    @Override
    public @NotNull List<Material> getApplicableTypes() {
        return ApplicableType.BOOTS.materials;
    }

    @Override
    public @NotNull String getDescription() {
        return "Позволяет ходить по лаве";
    }

    @Override
    public int getMaxLevel() {
        return 1;
    }

    private final Map<Location, Integer> basaltRemoveTimeMap = new HashMap<>();

    @Override
    public @NotNull Listener getEventListener() {
        return new Listener() {

            @EventHandler
            public void onPlayerMove(PlayerMoveEvent event) {
                Player player = event.getPlayer();
                Location location = player.getLocation();
                ItemStack boots = player.getInventory().getBoots();
                if (boots == null || !getEnchantmentFacade().hasEnchantment(boots, LavaWalker.class)) {
                    return;
                }
                List<Location> blocksToReplace = getBlocksToReplace(location);
                World world = location.getWorld();
                blocksToReplace.forEach(loc -> {
                    basaltRemoveTimeMap.put(loc, SECONDS_TO_REMOVE_BASALT);
                    world.setType(loc, Material.BASALT);
                });
            }

            private List<Location> getBlocksToReplace(Location playerLocation) {
                List<Location> blocksToReplace = new ArrayList<>();

                var world = playerLocation.getWorld();

                int x = playerLocation.getBlockX(), y = playerLocation.getBlockY() - 1, z = playerLocation.getBlockZ();
                int maxX = x + 3, maxZ = z + 3, minX = x - 3, minZ = z - 3;
                for (int lx = minX; lx <= maxX; lx++) {
                    for (int lz = minZ; lz <= maxZ; lz++) {
                        addIfLava(world, lx, y, lz, blocksToReplace);
                    }
                }
                int[] offsets = {-1, 0, 1};

                for (int zOffset : offsets) {
                    addIfLava(world, maxX, y, z + zOffset, blocksToReplace);
                    addIfLava(world, minX, y, z + zOffset, blocksToReplace);
                }

                for (int xOffset : offsets) {
                    addIfLava(world, x + xOffset, y, maxZ, blocksToReplace);
                    addIfLava(world, x + xOffset, y, minZ, blocksToReplace);
                }

                return blocksToReplace;
            }

            private void addIfLava(World world, int x, int y, int z, List<Location> blocksToReplace) {
                var location = new Location(world, x, y, z);
                var type = world.getBlockAt(x, y, z).getType();
                if (type == Material.LAVA || (type == Material.BASALT && basaltRemoveTimeMap.containsKey(location))) {
                    blocksToReplace.add(new Location(world, x, y, z));
                }
            }
        };
    }

    @Override
    public @NotNull List<CycledTask> getCycledTasks() {
        return List.of(
                new CycledTask() {

                    @Override
                    public BukkitRunnable getTask() {
                        return new BukkitRunnable() {
                            @Override
                            public void run() {
                                basaltRemoveTimeMap.forEach(((location, integer) -> {
                                    if (integer == 0) {
                                        basaltRemoveTimeMap.remove(location);
                                        location.getBlock().setType(Material.LAVA);
                                        return;
                                    }
                                    basaltRemoveTimeMap.put(location, integer - 1);
                                }));
                            }
                        };
                    }

                    @Override
                    public int getDelay() {
                        return 20;
                    }

                    @Override
                    public int getPeriod() {
                        return 20;
                    }
                }
        );
    }
}