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
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.RegistryObject;
import nukeologist.sockets.common.registry.SocketsItems;
import nukeologist.sockets.common.registry.SocketsTags;

import java.util.regex.Pattern;

public class ItemTagProvider extends ItemTagsProvider {

    public ItemTagProvider(DataGenerator generatorIn, BlockTagsProvider blockTagProvider) {
        super(generatorIn, blockTagProvider);
    }

    private static final Pattern GEM = Pattern.compile(".*_gem$");

    @Override
    protected void registerTags() {
        //ores
        func_240521_a_(SocketsTags.Blocks.ORES_RUBY, SocketsTags.Items.ORES_RUBY);
        func_240521_a_(SocketsTags.Blocks.ORES_SAPPHIRE, SocketsTags.Items.ORES_SAPPHIRE);

        //StorageBlocks
        func_240522_a_(Tags.Items.STORAGE_BLOCKS).addTags(SocketsTags.Items.STORAGE_BLOCKS_RUBY, SocketsTags.Items.STORAGE_BLOCKS_SAPPHIRE);
        func_240521_a_(SocketsTags.Blocks.STORAGE_BLOCKS_RUBY, SocketsTags.Items.STORAGE_BLOCKS_RUBY);
        func_240521_a_(SocketsTags.Blocks.STORAGE_BLOCKS_SAPPHIRE, SocketsTags.Items.STORAGE_BLOCKS_SAPPHIRE);

        //Forge Gems
        func_240522_a_(Tags.Items.GEMS).addTags(SocketsTags.Items.GEMS_RUBY, SocketsTags.Items.GEMS_SAPPHIRE);
        func_240522_a_(SocketsTags.Items.GEMS_RUBY).func_240534_a_(SocketsItems.RUBY.get());
        func_240522_a_(SocketsTags.Items.GEMS_SAPPHIRE).func_240534_a_(SocketsItems.SAPPHIRE.get());

        func_240522_a_(SocketsTags.Items.GEMS).func_240534_a_(getGems());
    }

    //Trying something new
    private Item[] getGems() {
        return SocketsItems.ITEMS.getEntries().stream().map(RegistryObject::get).filter(i -> GEM.matcher(i.getRegistryName().getPath()).matches()).toArray(Item[]::new);
    }
}
