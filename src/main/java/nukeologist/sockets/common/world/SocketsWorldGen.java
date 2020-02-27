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

package nukeologist.sockets.common.world;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.common.BiomeDictionary;
import nukeologist.sockets.common.registry.SocketsBlocks;

public final class SocketsWorldGen {

    private SocketsWorldGen() {
    }

    private static final ConfiguredFeature<?, ?> RUBY_FEATURE = Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, SocketsBlocks.RUBY_ORE.get().getDefaultState(), 5))
            .withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(10, 0, 60, 100)));
    private static final ConfiguredFeature<?, ?> SAPPHIRE_FEATURE = Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NETHERRACK, SocketsBlocks.SAPPHIRE_ORE.get().getDefaultState(), 5))
            .withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(10, 0, 60, 100)));

    public static void setupOres() {
        BiomeDictionary.getBiomes(BiomeDictionary.Type.NETHER).forEach(SocketsWorldGen::addOres);
    }

    private static void addOres(final Biome biome) {
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, RUBY_FEATURE);
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, SAPPHIRE_FEATURE);
    }
}
