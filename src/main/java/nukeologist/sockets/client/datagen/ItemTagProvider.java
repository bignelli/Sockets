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

import net.minecraft.data.DataGenerator;
import net.minecraft.data.ItemTagsProvider;
import net.minecraft.item.Item;
import net.minecraftforge.common.Tags;
import net.minecraftforge.fml.RegistryObject;
import nukeologist.sockets.common.registry.SocketsItems;
import nukeologist.sockets.common.registry.SocketsTags;

import java.util.regex.Pattern;

public class ItemTagProvider extends ItemTagsProvider {

    public ItemTagProvider(DataGenerator generatorIn) {
        super(generatorIn);
    }

    private static final Pattern GEM = Pattern.compile(".*_gem$");

    @Override
    protected void registerTags() {
        //ores
        copy(SocketsTags.Blocks.ORES_RUBY, SocketsTags.Items.ORES_RUBY);
        copy(SocketsTags.Blocks.ORES_SAPPHIRE, SocketsTags.Items.ORES_SAPPHIRE);

        //StorageBlocks
        getBuilder(Tags.Items.STORAGE_BLOCKS).add(SocketsTags.Items.STORAGE_BLOCKS_RUBY, SocketsTags.Items.STORAGE_BLOCKS_SAPPHIRE);
        copy(SocketsTags.Blocks.STORAGE_BLOCKS_RUBY, SocketsTags.Items.STORAGE_BLOCKS_RUBY);
        copy(SocketsTags.Blocks.STORAGE_BLOCKS_SAPPHIRE, SocketsTags.Items.STORAGE_BLOCKS_SAPPHIRE);

        //Forge Gems
        getBuilder(Tags.Items.GEMS).add(SocketsTags.Items.GEMS_RUBY, SocketsTags.Items.GEMS_SAPPHIRE);
        getBuilder(SocketsTags.Items.GEMS_RUBY).add(SocketsItems.RUBY.get());
        getBuilder(SocketsTags.Items.GEMS_SAPPHIRE).add(SocketsItems.SAPPHIRE.get());

        getBuilder(SocketsTags.Items.GEMS).add(getGems());
    }

    //Trying something new
    private Item[] getGems() {
        return SocketsItems.ITEMS.getEntries().stream().map(RegistryObject::get).filter(i -> GEM.matcher(i.getRegistryName().getPath()).matches()).toArray(Item[]::new);
    }
}
