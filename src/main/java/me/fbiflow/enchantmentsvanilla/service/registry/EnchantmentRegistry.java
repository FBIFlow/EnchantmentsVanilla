package me.fbiflow.enchantmentsvanilla.service.registry;

import me.fbiflow.enchantmentsvanilla.enchantment.Enchantment;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EnchantmentRegistry {

    private final Map<Class<? extends Enchantment>, Enchantment> enchantmentsByClass = new ConcurrentHashMap<>();
    private final Map<String, Enchantment> enchantmentsByName = new ConcurrentHashMap<>();
    private final List<Enchantment> enchantmentsList = new ArrayList<>();

    public void register(@NotNull Enchantment enchantment) {
        enchantmentsByClass.put(enchantment.getClass(), enchantment);
        enchantmentsByName.put(enchantment.getEnchantmentName().toLowerCase(), enchantment);
        enchantmentsList.add(enchantment);
    }

    public void unregister(@NotNull Enchantment enchantment) {
        enchantmentsByClass.remove(enchantment.getClass());
        enchantmentsByName.remove(enchantment.getEnchantmentName().toLowerCase());
        enchantmentsList.remove(enchantment);
    }

    @Nullable
    public Enchantment getByClass(@NotNull Class<? extends Enchantment> clazz) {
        return enchantmentsByClass.get(clazz);
    }

    @Nullable
    public Enchantment getByName(@NotNull String name) {
        return enchantmentsByName.get(name.toLowerCase());
    }

    @NotNull
    public List<Enchantment> getAll() {
        return Collections.unmodifiableList(enchantmentsList);
    }

    @NotNull
    public Optional<Enchantment> findByName(@NotNull String name) {
        return Optional.ofNullable(getByName(name));
    }

    @NotNull
    public Optional<Enchantment> findByClass(@NotNull Class<? extends Enchantment> clazz) {
        return Optional.ofNullable(getByClass(clazz));
    }
}