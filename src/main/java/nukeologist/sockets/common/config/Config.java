/*
 *    Copyright 2020 Nukeologist
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package nukeologist.sockets.common.config;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public final class Config {

    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    private static final String[] defaultSockets = {"minecraft:diamond_sword=1", "minecraft:diamond_shovel=1", "minecraft:diamond_pickaxe=1", "minecraft:diamond_axe=1", "minecraft:diamond_hoe=1",
    "minecraft:diamond_helmet=1", "minecraft:diamond_chestplate=1", "minecraft:diamond_leggings=1", "minecraft:diamond_boots=1", "minecraft:crossbow=1", "minecraft:bow=1"};

    static {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class ServerConfig {

        public final ForgeConfigSpec.BooleanValue enableRubyGen;
        public final ForgeConfigSpec.BooleanValue enableSapphireGen;
        public final ForgeConfigSpec.BooleanValue enableAllTools;
        public final ForgeConfigSpec.BooleanValue enableAllArmor;

        public final ForgeConfigSpec.IntValue dungeonEnchantfulChance;
        public final ForgeConfigSpec.IntValue chargefulChance;
        public final ForgeConfigSpec.IntValue lightningChance;

        public final ForgeConfigSpec.ConfigValue<List<? extends String>> socketsItems;

        ServerConfig(ForgeConfigSpec.Builder builder) {
            builder.push("Sockets Configuration Options");

            enableRubyGen = builder
                    .comment("Enables Ruby world generation (NETHER)")
                    .worldRestart()
                    .define("enableRubyGen", true);
            enableSapphireGen = builder
                    .comment("Enables Sapphire world generation (NETHER)")
                    .worldRestart()
                    .define("enableSapphireGen", true);

            dungeonEnchantfulChance = builder
                    .comment("Chance that an Enchantful Gem spawns in a Dungeon Chest")
                    .defineInRange("dungeonEnchantfulChance", 50, 0, 100);

            chargefulChance = builder
                    .comment("Chance that a Chargeful Gem drops from the specified mob in the loot modifier. By default, an elder guardian.")
                    .defineInRange("chargefulChance", 80, 0, 100);

            lightningChance = builder
                    .comment("Chance that a lightning will strike the mob affected by the Chargeful Gem")
                    .defineInRange("lightningChance", 10, 0, 100);

            enableAllTools = builder
                    .comment("Makes all verifiable tools have 1 Socket. Overriden by the map registrykey=int. WARNING: making this false will remove Gems from its affected items.")
                    .define("enableAllTools", false);

            enableAllArmor = builder
                    .comment("Makes all verifiable armors have 1 Socket. Overriden by the map registrykey=int. WARNING: making this false will remove Gems from its affected items.")
                    .define("enableAllArmor", false);

            socketsItems = builder
                    .comment("A Map of registrykey=int pairs. Adds the int value as the number of sockets to a given item (with a max of 4 sockets.)" + "\n" +
                            "WARNING: removing an item from this will remove all Gems it had. The item MUST also be not stackable.")
                    .worldRestart()
                    .defineList("socketString2IntMap", Lists.newArrayList(defaultSockets), o -> o instanceof String);
        }
    }
}
