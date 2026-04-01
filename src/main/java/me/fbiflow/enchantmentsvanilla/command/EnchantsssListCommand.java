package me.fbiflow.enchantmentsvanilla.command;

import me.fbiflow.enchantmentsvanilla.service.EnchantmentFacade;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class EnchantsssListCommand implements CommandExecutor {

    private final EnchantmentFacade enchantmentFacade;

    public EnchantsssListCommand(EnchantmentFacade enchantmentFacade) {
        this.enchantmentFacade = enchantmentFacade;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        sender.sendMessage("§6=== Доступные зачарования ===");

        enchantmentFacade.getAllEnchantments().forEach(enchantment -> {
            sender.sendMessage(String.format("§e%s §7- %s", enchantment.getEnchantmentName(), enchantment.getDescription()));
        });

        return true;
    }
}