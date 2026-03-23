package me.fbiflow.enchantmentsvanilla;

import me.fbiflow.enchantmentsvanilla.command.EnchantsssListCommand;
import me.fbiflow.enchantmentsvanilla.command.GiveBookCommand;
import me.fbiflow.enchantmentsvanilla.enchantment.enchantments.*;
import me.fbiflow.enchantmentsvanilla.service.anvil.AnvilService;
import me.fbiflow.enchantmentsvanilla.service.generator.LootGenerationListener;
import me.fbiflow.enchantmentsvanilla.service.EnchantmentFacade;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class Loader extends JavaPlugin {

    private static Loader instance;
    private EnchantmentFacade enchantmentFacade;

    public Loader() {
        instance = this;
    }

    @Override
    public void onEnable() {
        getLogger().info("Loading AdvancedEnchantments...");

        this.enchantmentFacade = new EnchantmentFacade(this);
        registerCommands();
        registerListeners();
        List.of(
                new AutoRepair(),
                new Cute(),
                new LavaWalker(),
                new Resistance(),
                new Telekinesis(),
                new TreeCutter(),
                new Wisdom(),
                new WitherProtection()
        ).forEach(enchantment -> enchantmentFacade.registerEnchantment(enchantment));

        getLogger().info("AdvancedEnchantments enabled!");
    }

    @Override
    public void onDisable() {
        if (enchantmentFacade != null) {
            enchantmentFacade.getAllEnchantments().forEach(
                    enchantment -> enchantmentFacade.unregisterEnchantment(enchantment)
            );
        }
        getLogger().info("AdvancedEnchantments disabled!");
    }

    private void registerCommands() {
        GiveBookCommand giveBookCommand = new GiveBookCommand(enchantmentFacade);

        var giveBookPluginCommand = getCommand("give-book");
        if (giveBookPluginCommand != null) {
            giveBookPluginCommand.setExecutor(giveBookCommand);
            giveBookPluginCommand.setTabCompleter(giveBookCommand);
        }

        var enchantsssCommand = getCommand("enchantsss");
        if (enchantsssCommand != null) {
            enchantsssCommand.setExecutor(new EnchantsssListCommand(enchantmentFacade));
        }
    }

    private void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new AnvilService(), this);
        Bukkit.getPluginManager().registerEvents(new LootGenerationListener(), this);
    }

    public static Loader getInstance() {
        return instance;
    }

    public EnchantmentFacade getEnchantmentFacade() {
        return enchantmentFacade;
    }
}