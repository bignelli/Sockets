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

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.common.registry.SocketsBlocks;

import java.util.function.Supplier;

public class BlockModels extends BlockStateProvider {

    public BlockModels(DataGenerator gen, ExistingFileHelper exFileHelper) {
        super(gen, SocketsAPI.ID, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        horizontal(SocketsBlocks.SOCKET_REMOVER, modLoc("block/socket_remover_side"), modLoc("block/socket_remover_front"), modLoc("block/socket_remover_top"));
        simple(SocketsBlocks.RUBY_ORE);
        simple(SocketsBlocks.SAPPHIRE_ORE);
    }

    private void horizontal(Supplier<Block> block, ResourceLocation side, ResourceLocation front, ResourceLocation rest) {
        horizontalBlock(block.get(), side, front, rest);
    }

    private void simple(Supplier<Block> block) {
        simpleBlock(block.get());
    }
}
