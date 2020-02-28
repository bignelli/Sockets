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

package nukeologist.sockets.client.datagen;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.LootTableProvider;
import net.minecraft.data.loot.BlockLootTables;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.*;
import net.minecraftforge.fml.RegistryObject;
import nukeologist.sockets.common.registry.SocketsBlocks;
import nukeologist.sockets.common.registry.SocketsItems;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class LootProvider extends LootTableProvider {

    public LootProvider(DataGenerator dataGeneratorIn) {
        super(dataGeneratorIn);
    }

    private final List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> tables = ImmutableList.of(
            Pair.of(BlockTables::new, LootParameterSets.BLOCK)
            //Pair.of(FishingLootTables::new, LootParameterSets.FISHING),
    );

    @Override
    protected List<Pair<Supplier<Consumer<BiConsumer<ResourceLocation, LootTable.Builder>>>, LootParameterSet>> getTables() {
        return tables;
    }

    @Override
    protected void validate(Map<ResourceLocation, LootTable> map, ValidationTracker validationtracker) {
        map.forEach((loc, table) -> LootTableManager.func_227508_a_(validationtracker, loc, table));
    }

    public static class BlockTables extends BlockLootTables {

        @Override
        protected void addTables() {
            selfDrop(SocketsBlocks.SOCKET_REMOVER);
            fortune(SocketsBlocks.RUBY_ORE, SocketsItems.RUBY);
            fortune(SocketsBlocks.SAPPHIRE_ORE, SocketsItems.SAPPHIRE);
        }

        private void fortune(final Supplier<Block> block, final Supplier<Item> item) {
            this.registerLootTable(block.get(), b -> droppingItemWithFortune(b, item.get()));
        }

        private void selfDrop(final Supplier<Block> block) {
            this.registerDropSelfLootTable(block.get());
        }

        @Override
        protected Iterable<Block> getKnownBlocks() {
            return SocketsBlocks.BLOCKS.getEntries().stream().map(RegistryObject::get).collect(Collectors.toList());
        }
    }
}
