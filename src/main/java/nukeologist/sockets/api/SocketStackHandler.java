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

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.items.ItemStackHandler;
import nukeologist.sockets.api.cap.IGem;
import nukeologist.sockets.api.cap.ISocketableItem;

import javax.annotation.Nonnull;

public class SocketStackHandler extends ItemStackHandler {

    private final ISocketableItem item;

    public SocketStackHandler(ISocketableItem item) {
        this(Math.min(item.getSlots(), 4), item);
    }

    protected SocketStackHandler(int size, ISocketableItem item) {
        super(size);
        this.item = item;
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    public void forEach(final NonNullConsumer<IGem> action) {
        for (final ItemStack stack : stacks) {
            SocketsAPI.getGem(stack).ifPresent(action);
        }
    }

    public void forEachExcluding(final NonNullConsumer<IGem> action, final IGem exclusion) {
        for (final ItemStack stack : stacks) {
            SocketsAPI.getGem(stack).ifPresent(gem -> {
                if (!gem.equals(exclusion)) action.accept(gem);
            });

        }
    }

    public boolean hasItem(final Item item) {
        for (final ItemStack stack : stacks) {
            if (stack.getItem() == item)
                return true;
        }
        return false;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return SocketsAPI.getGem(stack).map(item::accepts).orElse(false);
    }
}
