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

package nukeologist.sockets.api.cap;

import net.minecraft.util.text.ITextComponent;

import java.util.Collections;
import java.util.List;

/**
 * Items which can be put inside other items' sockets should expose this interface as a
 * capability.
 */
public interface IGem {

    /**
     * Determines if this ItemStack can be equipped in a socket.
     *
     * @param item item with the socket
     * @return true if it can be equipped, false otherwise.
     */
    default boolean canEquipOn(ISocketableItem item) {
        return true;
    }

    /**
     * Determines if this ItemStack can be unequipped from a socket.
     *
     * @param item item with the socket
     * @return true if can be unequipped, false otherwise.
     */
    default boolean canUnequipOn(ISocketableItem item) {
        return true;
    }

    /**
     * Called when this ItemStack was equipped on a socket.
     *
     * @param item item with the socket.
     */
    default void equipped(ISocketableItem item) {

    }

    /**
     * Called when this ItemStack was unequipped on a socket.
     *
     * @param item item with the socket.
     */
    default void unequipped(ISocketableItem item) {

    }

    /**
     * Called every tick while the ItemStack is on a socket. Do NOT modify
     * the {@link nukeologist.sockets.api.SocketStackHandler} inventory.
     *
     * @param item item with the socket.
     */
    default void socketTick(ISocketableItem item) {

    }

    /**
     * Called on the client to give extra tooltip information when hovering an
     * {@link ISocketableItem} with this inside it.
     *
     * @return extra tooltip information
     */
    default List<ITextComponent> getExtraTooltip() {
        return Collections.emptyList();
    }
}
