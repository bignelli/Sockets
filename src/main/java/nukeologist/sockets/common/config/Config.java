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

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public final class Config {

    public static final ServerConfig SERVER;
    public static final ForgeConfigSpec SERVER_SPEC;

    static {
        final Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
        SERVER_SPEC = specPair.getRight();
        SERVER = specPair.getLeft();
    }

    public static class ServerConfig {

        public final ForgeConfigSpec.BooleanValue enableRubyGen;
        public final ForgeConfigSpec.BooleanValue enableSapphireGen;

        public final ForgeConfigSpec.IntValue dungeonEnchantfulChance;

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
        }
    }
}
