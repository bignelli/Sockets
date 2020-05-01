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

public class PortugueseProvider extends LanguageProvider {

    public PortugueseProvider(DataGenerator datagen) {
        super(datagen, SocketsAPI.ID, "pt_br");
    }

    @Override
    protected void addTranslations() {
        //blocks
        block(SocketsBlocks.SOCKET_REMOVER, "Removedor de Soquetes");
        block(SocketsBlocks.RUBY_ORE, "Min\u00E9rio de Rubi");
        block(SocketsBlocks.SAPPHIRE_ORE, "Min\u00E9rio de Safira");
        block(SocketsBlocks.RUBY_BLOCK, "Bloco de Rubi");
        block(SocketsBlocks.SAPPHIRE_BLOCK, "Bloco de Safira");

        add(StringTranslations.SOCKET_REMOVER_BUTTON, "Remover");

        //items
        add(SocketsItems.DIAMOND_GEM, "Gema de Diamante");
        add(SocketsItems.EMERALD_GEM, "Gema de Esmeralda");
        add(SocketsItems.ENCHANTFUL_GEM, "Gema Encant\u00E1vel");
        add(SocketsItems.LAZULI_GEM, "Gema laz\u00FAli");
        add(SocketsItems.RUBY, "Rubi");
        add(SocketsItems.SAPPHIRE, "Safira");
        add(SocketsItems.RUBY_GEM, "Gema de Rubi");
        add(SocketsItems.SAPPHIRE_GEM, "Gema de Safira");
        add(SocketsItems.CHARGEFUL_GEM, "Gema Carregada");

        add("itemGroup.sockets", "Soquetes");

        add(StringTranslations.ENCHANTFUL_TOOLTIP, "Encante esta Gema e seu soquete tamb\u00E9m ser\u00E1.");
        add(StringTranslations.ENCHANTFUL_EXTRA_TOOLTIP, "Encantado pela Gema Encant\u00E1vel");
        add(StringTranslations.DEFAULT_GEM_EXTRA_TOOLTIP, "Gemas Contidas: ");
        add(StringTranslations.SHIFT_KEY_DOWN, "[SHIFT] para mais.");

        tooltip(SocketsItems.DIAMOND_GEM, "+1 dano; +1 prote\u00E7\u00E3o");
        tooltip(SocketsItems.EMERALD_GEM, "Mais xp quebrando min\u00E9rio");
        tooltip(SocketsItems.LAZULI_GEM, "+1 fortuna");
        tooltip(SocketsItems.RUBY_GEM, "Autocozimento");
        tooltip(SocketsItems.SAPPHIRE_GEM, "+1 saque");
        tooltip(SocketsItems.CHARGEFUL_GEM, "Poss\u00EDvel Raio!");

        //Jei
        add(StringTranslations.DIAMOND_GEM_JEI, "Adiciona dano quando dentro de ferramenta/arma. Reduz danos quando em armaduras.");
        add(StringTranslations.EMERALD_GEM_JEI, "Adiciona mais experi\u00EAncia a blocos minerados quando dentro de ferramenta/arma.");
        add(StringTranslations.ENCHANTFUL_GEM_JEI, "Adiciona os encantamentos que essa gema tem. N\u00E3o funciona com Bigornas. Pode ser encontrada em calabou\u00E7os.");
        add(StringTranslations.LAZULI_GEM_JEI, "Adiciona fortuna quando dentro de ferramenta/arma.");
        add(StringTranslations.RUBY_GEM_JEI, "Adiciona autocozimento quando dentro de ferramenta.");
        add(StringTranslations.SAPPHIRE_GEM_JEI, "Adiciona saque quando dentro de ferramenta/arma.");
        add(StringTranslations.CHARGEFUL_GEM_JEI, "Adiciona uma chance de raios quando dentro de ferramenta/arma, e atacando.");
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
