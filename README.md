# EnchantmentsVanilla

**EnchantmentsVanilla** is a Bukkit/Paper plugin that adds custom enchantments to Minecraft with a flexible architecture, anvil integration, and loot generation support.

---

## 📋 Table of Contents

- [Project Overview](#project-overview)
- [Architecture](#architecture)
- [How It Works](#how-it-works)
- [Creating an Enchantment](#creating-an-enchantment)
- [Adding Periodic Tasks](#adding-periodic-tasks)
- [Registering Your Enchantment](#registering-your-enchantment)
- [Configuration](#configuration)
- [Key Classes](#key-classes)
- [Commands](#commands)
- [API Usage Examples](#api-usage-examples)
- [Technical Details](#technical-details)

---

## Project Overview

This project demonstrates:

- ✅ Custom enchantment system with full lifecycle management
- ✅ Persistent data storage using `PersistentDataContainer`
- ✅ Anvil mechanics for combining and applying enchantments
- ✅ Loot generation in Nether and End chests
- ✅ Event-driven enchantment effects with scheduled tasks
- ✅ Service-oriented architecture with clear separation of concerns

---

## Architecture

```
me/fbiflow/enchantmentsvanilla/
├── command/
│   ├── EnchantsssListCommand.java      # List all enchantments
│   └── GiveBookCommand.java            # Give enchanted books
├── enchantment/
│   ├── enchantments/                   # Custom enchantment implementations
│   ├── ApplicableType.java             # Equipment type definitions
│   ├── CycledTask.java                 # Scheduled task interface
│   └── Enchantment.java                # Abstract base class
├── service/
│   ├── anvil/
│   │   └── AnvilService.java           # Anvil event handling
│   ├── constants/
│   │   └── EnchantmentConstants.java
│   ├── generator/
│   │   └── LootGenerationListener.java
│   ├── item/
│   │   └── EnchantmentItemService.java
│   ├── lifecycle/
│   │   └── EnchantmentLifecycleService.java
│   ├── registry/
│   │   └── EnchantmentRegistry.java
│   ├── storage/
│   │   └── EnchantmentDataStorage.java
│   └── EnchantmentFacade.java          # Main API facade
├── util/
│   ├── ComponentUtil.java              # Adventure component utilities
│   └── EnchantmentLevelUtil.java       # Level normalization & Roman numerals
└── Loader.java                         # Main plugin class
```

---

## How It Works

| Component | Responsibility |
|-----------|----------------|
| **Enchantment Registration** | Each enchantment extends the abstract `Enchantment` class and is registered in `Loader.onEnable()` via `EnchantmentFacade`. |
| **Data Storage** | Enchantments are stored on items using Minecraft's `PersistentDataContainer` with two keys: enchantment names and their levels. |
| **Lifecycle Management** | `EnchantmentLifecycleService` registers event listeners and scheduled tasks for each enchantment when enabled, and unregisters them on disable. |
| **Anvil Integration** | `AnvilService` handles applying enchanted books to items, combining two enchanted books, and calculating repair costs. |
| **Loot Generation** | `LootGenerationListener` adds enchanted books to chests in Nether and End dimensions with a configurable chance. |
| **Item Presentation** | `EnchantmentItemService` manages lore updates, enchantment glint, and item modifications. |

---

## Creating an Enchantment

Extend `Enchantment` and implement all abstract methods:

```java
package me.fbiflow.enchantmentsvanilla.enchantment.enchantments;

import me.fbiflow.enchantmentsvanilla.enchantment.ApplicableType;
import me.fbiflow.enchantmentsvanilla.enchantment.CycledTask;
import me.fbiflow.enchantmentsvanilla.enchantment.Enchantment;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import java.util.List;

public class YourEnchantment extends Enchantment {

    @Override
    public @NotNull String getEnchantmentName() {
        return "Enchantment name";
    }

    @Override
    public @NotNull List<Material> getApplicableTypes() {
        // Return materials this enchantment can be applied to
        return ApplicableType.SWORD.materials;
        // Or combine multiple types:
        // return Stream.of(ApplicableType.SWORD, ApplicableType.AXE)
        //         .flatMap(type -> type.materials.stream())
        //         .toList();
    }

    @Override
    public @NotNull String getDescription() {
        return "Enchantment description";
    }

    @Override
    public int getMaxLevel() {
        return 3; // Maximum enchantment level
    }

    @Override
    public @NotNull Listener getEventListener() {
        return new Listener() {
            @EventHandler
            public void onPlayerEvent(YourCustomEvent event) {
                // Your enchantment logic here
                // Use getEnchantmentFacade() to access the API:
                if (getEnchantmentFacade().hasEnchantment(item, YourEnchantment.class)) {
                    int level = getEnchantmentFacade().getEnchantmentLevel(item, YourEnchantment.class);
                    // Apply effects based on level
                }
            }
        };
    }

    @Override
    public @NotNull List<CycledTask> getCycledTasks() {
        // Return scheduled tasks if needed
        return List.of();
    }
}
```

---

## Adding Periodic Tasks

For enchantments that need recurring effects:

```java
@Override
public @NotNull List<CycledTask> getCycledTasks() {
    return List.of(new CycledTask() {
        @Override
        public BukkitRunnable getTask() {
            return new BukkitRunnable() {
                @Override
                public void run() {
                    // Periodic logic
                }
            };
        }

        @Override
        public int getDelay() {
            return 20; // Delay in ticks before first execution
        }

        @Override
        public int getPeriod() {
            return 40; // Period in ticks (20 ticks = 1 second)
            // Return 0 or negative for one-time execution
        }
    });
}
```

---

## Registering Your Enchantment

In `Loader.onEnable()`, add your enchantment to the registration list:

```java
List.of(
    new YourEnchantment()  // Add your enchantment here
).forEach(enchantment -> enchantmentFacade.registerEnchantment(enchantment));
```

---

## Configuration

The plugin uses no external configuration files. All settings are defined in `EnchantmentConstants.java`:

| Constant | Default | Description |
|----------|---------|-------------|
| `ENCHANTMENT_NAME_COLOR` | `#a8a8a8` | Hex color for enchantment display names |
| `PDC_ENCHANTMENTS_KEY` | `custom_enchantments` | PDC key for enchantment storage |
| `PDC_ENCHANTMENTS_LEVEL_KEY` | `custom_enchantments_level` | PDC key for enchantment levels |
| `BASE_ANVIL_REPAIR_COST` | `2` | Base cost multiplier for anvil operations |
| `LOOT_GENERATION_CHANCE` | `5` | Percentage chance (0-100) for books in loot |

---

## Key Classes

| Class | Responsibility |
|-------|----------------|
| `Enchantment` | Abstract base class for all custom enchantments |
| `EnchantmentFacade` | Main API facade providing all enchantment operations |
| `EnchantmentRegistry` | Stores and retrieves enchantments by name and class |
| `EnchantmentDataStorage` | Handles persistent data storage on items |
| `EnchantmentItemService` | Manages item modifications (apply, remove, get level) |
| `EnchantmentLifecycleService` | Registers listeners and schedules tasks |
| `AnvilService` | Handles anvil combining and application logic |
| `LootGenerationListener` | Adds enchanted books to loot chests |

---

## Commands

| Command | Description | Permission | Usage |
|---------|-------------|------------|-------|
| `/give-book <enchantment> [level]` | Gives an enchanted book to the player | `enchantmentsvanilla.givebook` | `/give-book enchantment_name 3` |
| `/enchantsss` | Lists all available enchantments | `enchantmentsvanilla.list` | `/enchantsss` |

### Command Features

- **Fuzzy matching**: The `/give-book` command will try to find the closest match if the exact name isn't found
- **Tab completion**: Auto-completes enchantment names and valid levels
- **Level validation**: Automatically caps levels at the enchantment's maximum and global maximum of 10

---

## API Usage Examples

```java
// Get EnchantmentFacade instance
EnchantmentFacade facade = Loader.getInstance().getEnchantmentFacade();

// Check if item has enchantment
boolean hasEnchantment = facade.hasEnchantment(itemStack, YourEnchantment.class);

// Get enchantment level
int level = facade.getEnchantmentLevel(itemStack, YourEnchantment.class);

// Apply enchantment to item
Optional<ItemStack> enchanted = facade.applyEnchantment(itemStack, YourEnchantment.class, 1);

// Remove enchantment
ItemStack cleaned = facade.removeEnchantment(itemStack, YourEnchantment.class);

// Get all enchantments on item
List<Enchantment> enchantments = facade.getItemEnchantments(itemStack);

// Get enchantments with levels
Map<Enchantment, Integer> enchantmentsWithLevels = facade.getItemEnchantmentsWithLevels(itemStack);

// Create enchanted book
ItemStack book = facade.createEnchantedBook(YourEnchantment.class, 1);

// Check if enchantment is applicable to a material
boolean applicable = facade.isApplicable(Material.DIAMOND_SWORD, YourEnchantment.class);
```

---

## Technical Details

### Data Storage Format

Enchantments are stored in `PersistentDataContainer` as:

| Key | Value |
|-----|-------|
| `custom_enchantments` | List of enchantment names (strings) |
| `custom_enchantments_level` | List of corresponding levels (integers) |

### Anvil Mechanics

- **Base cost**: 2 experience levels per enchantment level
- Books can be combined to increase levels up to the enchantment's max
- Multiple enchantments from different books can be applied to a single item
- Existing enchantments are upgraded if a higher level is applied

### Loot Generation

- Only applies to chests in Nether and End dimensions
- Configurable chance (default 5%)
- Books always spawn with level 1
- Enchantment is randomly selected from all registered enchantments

---

> The plugin includes example enchantments demonstrating various patterns and use cases.