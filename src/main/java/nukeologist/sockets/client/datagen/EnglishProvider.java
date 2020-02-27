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
import net.minecraft.item.Item;
import net.minecraftforge.common.data.LanguageProvider;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.common.registry.SocketsBlocks;
import nukeologist.sockets.common.registry.SocketsItems;

import java.util.function.Supplier;

public class EnglishProvider extends LanguageProvider {

    public EnglishProvider(DataGenerator gen) {
        super(gen, SocketsAPI.ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        //blocks
        block(SocketsBlocks.SOCKET_REMOVER, "Socket Remover");
        block(SocketsBlocks.RUBY_ORE, "Ruby Ore");
        block(SocketsBlocks.SAPPHIRE_ORE, "Sapphire Ore");

        //items
        add(SocketsItems.DIAMOND_GEM, "Diamond Gem");
        add(SocketsItems.EMERALD_GEM, "Emerald Gem");
        add(SocketsItems.ENCHANTFUL_GEM, "Enchantful Gem");
        add(SocketsItems.LAZULI_GEM, "Lazuli Gem");
        add(SocketsItems.RUBY, "Ruby");
        add(SocketsItems.SAPPHIRE, "Sapphire");
        add(SocketsItems.RUBY_GEM, "Ruby Gem");
        add(SocketsItems.SAPPHIRE_GEM, "Sapphire Gem");

        add("itemGroup.sockets", "Sockets");
    }

    private void add(final Supplier<Item> item, final String name) {
        add(item.get(), name);
    }

    private void block(final Supplier<Block> item, final String name) {
        add(item.get(), name);
    }
}
