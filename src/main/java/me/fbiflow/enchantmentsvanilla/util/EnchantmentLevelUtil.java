package me.fbiflow.enchantmentsvanilla.util;

import org.jetbrains.annotations.NotNull;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EnchantmentLevelUtil {

    private static final Map<Integer, String> ROMAN_NUMERALS = new ConcurrentHashMap<>();

    static {
        ROMAN_NUMERALS.put(1, "I");
        ROMAN_NUMERALS.put(2, "II");
        ROMAN_NUMERALS.put(3, "III");
        ROMAN_NUMERALS.put(4, "IV");
        ROMAN_NUMERALS.put(5, "V");
        ROMAN_NUMERALS.put(6, "VI");
        ROMAN_NUMERALS.put(7, "VII");
        ROMAN_NUMERALS.put(8, "VIII");
        ROMAN_NUMERALS.put(9, "IX");
        ROMAN_NUMERALS.put(10, "X");
    }

    @NotNull
    public static String toRoman(int level) {
        return ROMAN_NUMERALS.getOrDefault(level, "");
    }

    public static boolean isValidLevel(int level) {
        return level >= 1 && level <= 10;
    }

    public static int normalizeLevel(int level, int maxLevel) {
        level = Math.max(1, level);
        return Math.min(level, Math.min(maxLevel, 10));
    }
}