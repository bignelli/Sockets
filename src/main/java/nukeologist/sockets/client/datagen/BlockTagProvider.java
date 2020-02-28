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
        getBuilder(Tags.Blocks.ORES).add(SocketsTags.Blocks.ORES_RUBY, SocketsTags.Blocks.ORES_SAPPHIRE);
        getBuilder(SocketsTags.Blocks.ORES_RUBY).add(SocketsBlocks.RUBY_ORE.get());
        getBuilder(SocketsTags.Blocks.ORES_SAPPHIRE).add(SocketsBlocks.SAPPHIRE_ORE.get());
    }
}
