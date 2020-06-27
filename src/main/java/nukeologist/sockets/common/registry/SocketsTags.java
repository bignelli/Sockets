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

package nukeologist.sockets.common.registry;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ITag;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import nukeologist.sockets.Sockets;

/**
 * Holder class for all Socket's tags. For now, not used anywhere but in datagen.
 * That may change in the future.
 */
public final class SocketsTags {

    private SocketsTags() {
    }

    public static class Blocks {

        public static final ITag.INamedTag<Block> ORES_RUBY = forge("ores/ruby");
        public static final ITag.INamedTag<Block> ORES_SAPPHIRE = forge("ores/sapphire");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_RUBY = forge("storage_blocks/ruby");
        public static final ITag.INamedTag<Block> STORAGE_BLOCKS_SAPPHIRE = forge("storage_blocks/sapphire");

        private static ITag.INamedTag<Block> forge(final String s) {
            return BlockTags.makeWrapperTag(new ResourceLocation("forge", s).toString());
        }
    }

    public static class Items {

        public static final ITag.INamedTag<Item> ORES_RUBY = forge("ores/ruby");
        public static final ITag.INamedTag<Item> ORES_SAPPHIRE = forge("ores/sapphire");
        public static final ITag.INamedTag<Item> GEMS_RUBY = forge("gems/ruby");
        public static final ITag.INamedTag<Item> GEMS_SAPPHIRE = forge("gems/sapphire");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_RUBY = forge("storage_blocks/ruby");
        public static final ITag.INamedTag<Item> STORAGE_BLOCKS_SAPPHIRE = forge("storage_blocks/sapphire");

        public static final ITag.INamedTag<Item> GEMS = tag("gems");

        private static ITag.INamedTag<Item> forge(final String s) {
            return ItemTags.makeWrapperTag(new ResourceLocation("forge", s).toString());
        }

        private static ITag.INamedTag<Item> tag(final String s) {
            return ItemTags.makeWrapperTag(Sockets.modLoc(s).toString());
        }

    }

}
