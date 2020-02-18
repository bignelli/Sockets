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

import nukeologist.sockets.api.SocketStackHandler;

/**
 * Items which have sockets should expose this interface as a capability.
 */
public interface ISocketableItem {

    /**
     * @return the number of sockets this ItemStack has. At max, should be 4.
     */
    default int getSlots() {
        return 1;
    }

    /**
     * Determines if this ItemStack can accept the Gem.
     *
     * @param gem the gem
     * @return true if accepts, false otherwise.
     */
    default boolean accepts(IGem gem) {
        return true;
    }

    /**
     * Gets the 'inventory' associated with this ItemStack
     *
     * @return the {@link SocketStackHandler} 'inventory' handler.
     */
    SocketStackHandler getStackHandler();

}
