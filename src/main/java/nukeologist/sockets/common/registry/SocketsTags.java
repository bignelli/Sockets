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
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ResourceLocation;
import nukeologist.sockets.Sockets;

public final class SocketsTags {

    private SocketsTags() {
    }

    public static class Blocks {

        public static final Tag<Block> ORES_RUBY = forge("ores/ruby");
        public static final Tag<Block> ORES_SAPPHIRE = forge("ores/sapphire");

        private static Tag<Block> forge(final String s) {
            return new BlockTags.Wrapper(new ResourceLocation("forge", s));
        }
    }

    public static class Items {

        public static final Tag<Item> ORES_RUBY = forge("ores/ruby");
        public static final Tag<Item> ORES_SAPPHIRE = forge("ores/sapphire");

        public static final Tag<Item> GEMS = tag("gems");

        private static Tag<Item> forge(final String s) {
            return new ItemTags.Wrapper(new ResourceLocation("forge", s));
        }

        private static Tag<Item> tag(final String s) {
            return new ItemTags.Wrapper(Sockets.modLoc(s));
        }

    }

}
