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

import net.minecraft.data.BlockTagsProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.Tags;
import nukeologist.sockets.common.registry.SocketsBlocks;
import nukeologist.sockets.common.registry.SocketsTags;

public class BlockTagProvider extends BlockTagsProvider {

    public BlockTagProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerTags() {
        //Ores
        func_240522_a_(Tags.Blocks.ORES).addTags(SocketsTags.Blocks.ORES_RUBY, SocketsTags.Blocks.ORES_SAPPHIRE);
        func_240522_a_(SocketsTags.Blocks.ORES_RUBY).func_240534_a_(SocketsBlocks.RUBY_ORE.get());
        func_240522_a_(SocketsTags.Blocks.ORES_SAPPHIRE).func_240534_a_(SocketsBlocks.SAPPHIRE_ORE.get());

        //Storage Blocks
        func_240522_a_(Tags.Blocks.STORAGE_BLOCKS).addTags(SocketsTags.Blocks.STORAGE_BLOCKS_RUBY, SocketsTags.Blocks.STORAGE_BLOCKS_SAPPHIRE);
        func_240522_a_(SocketsTags.Blocks.STORAGE_BLOCKS_RUBY).func_240534_a_(SocketsBlocks.RUBY_BLOCK.get());
        func_240522_a_(SocketsTags.Blocks.STORAGE_BLOCKS_SAPPHIRE).func_240534_a_(SocketsBlocks.SAPPHIRE_BLOCK.get());
    }
}
