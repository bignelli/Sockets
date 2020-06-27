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

package nukeologist.sockets.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.common.registry.SocketsContainers;
import nukeologist.sockets.common.tileentity.SocketRemoverTileEntity;

import javax.annotation.Nonnull;

public class SocketRemoverContainer extends Container {

    private final SocketRemoverTileEntity te;

    public final BlockPos pos;

    public SocketRemoverContainer(int windowId, PlayerInventory playerInv, PacketBuffer extraData) {
        this(windowId, playerInv, extraData.readBlockPos());
    }

    public SocketRemoverContainer(int windowId, PlayerInventory playerInv, BlockPos pos) {
        this(SocketsContainers.SOCKET_REMOVER.get(), windowId, playerInv, pos);
    }

    public SocketRemoverContainer(ContainerType<?> type, int windowId, PlayerInventory playerInv, BlockPos pos) {
        super(type, windowId);
        final TileEntity tile = playerInv.player.world.getTileEntity(pos);
        this.te = tile instanceof SocketRemoverTileEntity ? (SocketRemoverTileEntity) tile : null;
        this.pos = pos;

        final IItemHandler wrapper = new InvWrapper(playerInv);

        //Socket Remover Slots
        if (te != null) te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> {
            //input
            this.addSlot(new SlotItemHandler(h, 0, 44, 35) {
                @Override
                public boolean isItemValid(@Nonnull ItemStack stack) {
                    return SocketsAPI.getSockets(stack).isPresent();
                }
            });
            //output
            this.addSlot(createOutput(h, 1, 98, 26));
            this.addSlot(createOutput(h, 2, 116, 26));
            this.addSlot(createOutput(h, 3, 98, 44));
            this.addSlot(createOutput(h, 4, 116, 44));
        });

        //Player Slots
        for (int k = 0; k < 3; ++k) {
            for (int i1 = 0; i1 < 9; ++i1) {
                this.addSlot(new SlotItemHandler(wrapper, i1 + k * 9 + 9, 8 + i1 * 18, 84 + k * 18));
            }
        }

        for (int l = 0; l < 9; ++l) {
            this.addSlot(new SlotItemHandler(wrapper, l, 8 + l * 18, 142));
        }

    }

    private SlotItemHandler createOutput(final IItemHandler handler, final int index, final int x, final int y) {
        return new SlotItemHandler(handler, index, x, y) {
            @Override
            public boolean isItemValid(@Nonnull ItemStack stack) {
                return false;
            }
        };
    }

    //I ABSOLUTELY hate this method, couldn't there possibly have been a better way to do shift clicking??!?
    @Override
    public ItemStack transferStackInSlot(PlayerEntity playerIn, int index) {
        ItemStack stack = ItemStack.EMPTY;
        final Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack slotStack = slot.getStack();
            if (index >= 1 && index <= 4) {
                if (!this.mergeItemStack(slotStack, 5, 41, true)) {
                    return ItemStack.EMPTY;
                }
                slot.onSlotChange(slotStack, slotStack.copy());
            } else if (index != 0) {
                if (SocketsAPI.getSockets(slotStack).isPresent()) {
                    if (!this.mergeItemStack(slotStack, 0, 1, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 5 && index < 32) {
                    if (!this.mergeItemStack(slotStack, 32, 41, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= 32 && index < 41 && !this.mergeItemStack(slotStack, 5, 32, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.mergeItemStack(slotStack, 5, 41, false)) {
                return ItemStack.EMPTY;
            }

            if (slotStack.isEmpty()) {
                slot.putStack(ItemStack.EMPTY);
            } else {
                slot.onSlotChanged();
            }

            if (slotStack.getCount() == stack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, slotStack);
        }

        return stack;
    }

    public SocketRemoverTileEntity getTile() {
        return te;
    }

    @Override
    public boolean canInteractWith(PlayerEntity playerIn) {
        return te != null && playerIn.getDistanceSq(Vector3d.func_237489_a_(te.getPos())) <= 64D;
    }
}
