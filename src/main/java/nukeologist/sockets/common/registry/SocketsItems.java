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

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.common.item.GemItem;

public final class SocketsItems {

    private SocketsItems() {
    }

    public static final ItemGroup SOCKETS_GROUP = new ItemGroup("sockets") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(DIAMOND_GEM.get());
        }
    };

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, SocketsAPI.ID);

    public static final RegistryObject<Item> DIAMOND_GEM = ITEMS.register("diamond_gem", () -> new GemItem(defaultGem()).onEquip(s -> System.out.println("Equipped!")));
    public static final RegistryObject<Item> SOCKET_REMOVER = ITEMS.register("socket_remover", () -> new BlockItem(SocketsBlocks.SOCKET_REMOVER.get(), defaultGem()));

    private static Item.Properties defaultGem() {
        return new Item.Properties()
                .group(SOCKETS_GROUP);
    }
}
