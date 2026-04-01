package me.fbiflow.enchantmentsvanilla.command;

import me.fbiflow.enchantmentsvanilla.enchantment.Enchantment;
import me.fbiflow.enchantmentsvanilla.service.EnchantmentFacade;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GiveBookCommand implements CommandExecutor, TabCompleter {

    private final EnchantmentFacade enchantmentFacade;

    public GiveBookCommand(EnchantmentFacade enchantmentFacade) {
        this.enchantmentFacade = enchantmentFacade;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (args.length == 1) {
            String partialName = args[0].toLowerCase();
            return enchantmentFacade.getAllEnchantments().stream()
                    .map(Enchantment::getEnchantmentName)
                    .filter(name -> name.toLowerCase().startsWith(partialName))
                    .collect(Collectors.toList());
        }
        else if (args.length == 2) {
            String enchantmentName = args[0];
            Enchantment enchantment = enchantmentFacade.getEnchantment(enchantmentName);

            if (enchantment != null) {
                int maxLevel = enchantment.getMaxLevel();
                return IntStream.rangeClosed(1, maxLevel)
                        .mapToObj(String::valueOf)
                        .collect(Collectors.toList());
            }
        }

        return List.of();
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String @NotNull [] args) {
        if (!(sender instanceof Player player)) {
            return true;
        }

        if (args.length < 1) {
            player.sendMessage("§cИспользование: /give-book <название_зачарования> [уровень]");
            return false;
        }

        String enchantmentName = args[0];

        Enchantment enchantment = enchantmentFacade.getEnchantment(enchantmentName);

        if (enchantment == null) {
            String lowerInput = enchantmentName.toLowerCase();
            List<Enchantment> matches = enchantmentFacade.getAllEnchantments().stream()
                    .filter(e -> e.getEnchantmentName().toLowerCase().contains(lowerInput))
                    .toList();

            if (matches.size() == 1) {
                enchantment = matches.getFirst();
                player.sendMessage("§7Используется зачарование: §e" + enchantment.getEnchantmentName());
            } else if (matches.size() > 1) {
                player.sendMessage("§cНайдено несколько зачарований, содержащих \"" + enchantmentName + "\":");
                matches.forEach(e -> player.sendMessage("§7- §e" + e.getEnchantmentName()));
                player.sendMessage("§cУточните запрос");
                return false;
            } else {
                player.sendMessage("§cЗачарование не найдено: " + enchantmentName);
                return false;
            }
        }

        int level = 1;
        if (args.length >= 2) {
            try {
                level = Integer.parseInt(args[1]);
                if (level < 1) {
                    player.sendMessage("§cУровень должен быть положительным числом");
                    return false;
                }
            } catch (NumberFormatException e) {
                player.sendMessage("§cНеверный формат уровня. Используйте число");
                return false;
            }
        }

        if (level > enchantment.getMaxLevel()) {
            player.sendMessage(String.format("§cМаксимальный уровень для зачарования \"%s\": %d",
                    enchantment.getEnchantmentName(), enchantment.getMaxLevel()));
            return false;
        }

        if (level > 10) {
            player.sendMessage("§cУровень не может быть больше 10");
            return false;
        }

        var book = enchantmentFacade.createEnchantedBook(enchantment.getClass(), level);
        player.getInventory().addItem(book);

        if (level > 1) {
            player.sendMessage(String.format("§aВы получили книгу с зачарованием §e%s %d§a уровня!",
                    enchantment.getEnchantmentName(), level));
        } else {
            player.sendMessage(String.format("§aВы получили книгу с зачарованием §e%s§a!",
                    enchantment.getEnchantmentName()));
        }

        return true;
    }
}