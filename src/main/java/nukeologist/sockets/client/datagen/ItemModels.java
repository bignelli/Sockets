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
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ExistingFileHelper;
import net.minecraftforge.client.model.generators.ItemModelBuilder;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.client.model.generators.ModelFile;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.common.registry.SocketsBlocks;
import nukeologist.sockets.common.registry.SocketsItems;

import java.util.function.Supplier;

public class ItemModels extends ItemModelProvider {

    public ItemModels(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator, SocketsAPI.ID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        generated(SocketsItems.DIAMOND_GEM);
        generated(SocketsItems.EMERALD_GEM);
        generated(SocketsItems.ENCHANTFUL_GEM);
        generated(SocketsItems.LAZULI_GEM);
        generated(SocketsItems.RUBY);
        generated(SocketsItems.SAPPHIRE);
        generated(SocketsItems.RUBY_GEM);
        generated(SocketsItems.SAPPHIRE_GEM);
        generated(SocketsItems.CHARGEFUL_GEM);

        //blocks
        itemBlock(SocketsBlocks.SOCKET_REMOVER);
        itemBlock(SocketsBlocks.RUBY_ORE);
        itemBlock(SocketsBlocks.SAPPHIRE_ORE);
        itemBlock(SocketsBlocks.RUBY_BLOCK);
        itemBlock(SocketsBlocks.SAPPHIRE_BLOCK);
    }

    private void itemBlock(final Block block) {
        final String path = block.getRegistryName().getPath();
        getBuilder(path).parent(new ModelFile.UncheckedModelFile(modLoc("block/" + path)));
    }

    private void itemBlock(final Supplier<Block> block) {
        itemBlock(block.get());
    }

    private ItemModelBuilder generated(Supplier<? extends IItemProvider> item) {
        return generated(item, itemTexture(item));
    }

    private ItemModelBuilder generated(Supplier<? extends IItemProvider> item, ResourceLocation texture) {
        return getBuilder(name(item)).parent(new ModelFile.UncheckedModelFile("item/generated")).texture("layer0", texture);
    }

    private String name(Supplier<? extends IItemProvider> item) {
        return item.get().asItem().getRegistryName().getPath();
    }

    private ResourceLocation itemTexture(Supplier<? extends IItemProvider> item) {
        return modLoc("item/" + name(item));
    }

    @Override
    public String getName() {
        return "Sockets Item Models";
    }
}
