package me.fbiflow.enchantmentsvanilla.service.generator;

import me.fbiflow.enchantmentsvanilla.Loader;
import me.fbiflow.enchantmentsvanilla.enchantment.Enchantment;
import me.fbiflow.enchantmentsvanilla.service.constants.EnchantmentConstants;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.world.LootGenerateEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class LootGenerationListener implements Listener {

    @EventHandler
    public void onLootGenerate(LootGenerateEvent event) {
        var environment = event.getLootContext().getLocation().getWorld().getEnvironment();
        if (environment != World.Environment.NETHER && environment != World.Environment.THE_END) {
            return;
        }
        var loot = new ArrayList<>(event.getLoot());
        var enchantmentFacade = Loader.getInstance().getEnchantmentFacade();
        List<Enchantment> enchantments = enchantmentFacade.getAllEnchantments();
        var random = new Random();
        if (random.nextInt(100) <= EnchantmentConstants.LOOT_GENERATION_CHANCE) {
            Class<? extends Enchantment> enchantmentClass = enchantments.get(random.nextInt(0, enchantments.size())).getClass();
            loot.add(enchantmentFacade.createEnchantedBook(enchantmentClass, 1));
            event.setLoot(loot);
        }
    }

}
