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

package nukeologist.sockets.api;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.LazyOptional;
import nukeologist.sockets.api.cap.IGem;
import nukeologist.sockets.api.cap.ISocketableItem;
import nukeologist.sockets.api.cap.SocketsCapability;

public final class SocketsAPI {

    private SocketsAPI() {
    }

    /**
     * The Sockets' Mod ID.
     */
    public static final String ID = "sockets";

    /**
     * The Network Protocol Version for Sockets.
     */
    public static final String NETWORK_VERSION = "1.0.0";

    /**
     * Gets the gem capability attached to the ItemStack
     *
     * @param stack item with possible capability
     * @return a {@link LazyOptional} possibly with the cap
     */
    public static LazyOptional<IGem> getGem(ItemStack stack) {
        return stack.getCapability(SocketsCapability.GEM_ITEM_CAPABILITY);
    }

    /**
     * Gets the socket capability attached to the ItemStack
     *
     * @param stack item with possible capability
     * @return a {@link LazyOptional} possibly with the cap
     */
    public static LazyOptional<ISocketableItem> getSockets(ItemStack stack) {
        return stack.getCapability(SocketsCapability.SOCKETABLE_ITEM_CAPABILITY);
    }
}
