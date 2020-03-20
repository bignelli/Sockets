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
import nukeologist.sockets.common.util.StringTranslations;

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
        block(SocketsBlocks.RUBY_BLOCK, "Block of Ruby");
        block(SocketsBlocks.SAPPHIRE_BLOCK, "Block of Sapphire");

        add(StringTranslations.SOCKET_REMOVER_BUTTON, "Remove");

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

        add(StringTranslations.ENCHANTFUL_TOOLTIP, "Enchant this Gem and it's socket shall be as well.");
        add(StringTranslations.ENCHANTFUL_EXTRA_TOOLTIP, "Enchanted by Enchantful Gem");
        add(StringTranslations.DEFAULT_GEM_EXTRA_TOOLTIP, "Contains Gems: ");
        add(StringTranslations.SHIFT_KEY_DOWN, "[SHIFT] for more.");

        tooltip(SocketsItems.DIAMOND_GEM, "+1 damage; +1 protection");
        tooltip(SocketsItems.EMERALD_GEM, "More xp on ore breaking");
        tooltip(SocketsItems.LAZULI_GEM, "+1 fortune");
        tooltip(SocketsItems.RUBY_GEM, "Auto Smelting");
        tooltip(SocketsItems.SAPPHIRE_GEM, "+1 looting");

        //Jei
        add(StringTranslations.DIAMOND_GEM_JEI, "Adds damage when socketed into a tool/weapon. Resists some damage in armor.");
        add(StringTranslations.EMERALD_GEM_JEI, "Adds extra xp to block drops when socketed into a tool/weapon.");
        add(StringTranslations.ENCHANTFUL_GEM_JEI, "Adds the enchantments this gem has. Can't work with Anvils. May be found in dungeons.");
        add(StringTranslations.LAZULI_GEM_JEI, "Adds fortune when socketed into a tool/weapon.");
        add(StringTranslations.RUBY_GEM_JEI, "Adds auto smelting when socketed into a tool.");
        add(StringTranslations.SAPPHIRE_GEM_JEI, "Adds looting when socketed into a tool/weapon.");
    }

    private void add(final Supplier<Item> item, final String name) {
        add(item.get(), name);
    }

    private void tooltip(final Supplier<Item> item, final String tooltip) {
        add(item.get().getTranslationKey() + ".shift.info", tooltip);
    }

    private void block(final Supplier<Block> item, final String name) {
        add(item.get(), name);
    }
}
