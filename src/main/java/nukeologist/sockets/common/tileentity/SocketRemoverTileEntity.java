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

package nukeologist.sockets.common.tileentity;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RangedWrapper;
import nukeologist.sockets.api.SocketStackHandler;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.api.cap.IGem;
import nukeologist.sockets.api.cap.ISocketableItem;
import nukeologist.sockets.common.container.SocketRemoverContainer;
import nukeologist.sockets.common.registry.SocketsTileEntities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SocketRemoverTileEntity extends TileEntity implements INamedContainerProvider {

    private final ItemStackHandler inv = createInv();

    private final LazyOptional<IItemHandler> total = LazyOptional.of(() -> inv);
    private final LazyOptional<IItemHandler> input = LazyOptional.of(this::createInput);
    private final LazyOptional<IItemHandler> output = LazyOptional.of(this::createOutput);

    public SocketRemoverTileEntity(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    public SocketRemoverTileEntity() {
        this(SocketsTileEntities.SOCKET_REMOVER.get());
    }

    private ItemStackHandler createInv() {
        return new ItemStackHandler(5) {

            @Override
            protected void onContentsChanged(int slot) {
                SocketRemoverTileEntity.this.markDirty();
            }
        };
    }

    @Nonnull
    private IItemHandler createInput() {
        return new RangedWrapper(inv, 0, 1) {
            @Override
            public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
                return super.isItemValid(slot, stack) && SocketsAPI.getSockets(stack).isPresent();
            }

            @Nonnull
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                if (!SocketsAPI.getSockets(stack).isPresent())
                    return stack;
                return super.insertItem(slot, stack, simulate);
            }
        };
    }

    @Nonnull
    private IItemHandler createOutput() {
        return new RangedWrapper(inv, 1, 5) {
            @Override
            public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
                return stack;
            }
        };
    }

    public void tryAndRemove(ServerPlayerEntity player) {
        final ItemStack input = inv.getStackInSlot(0);
        if (input.isEmpty()) return;
        final LazyOptional<ISocketableItem> possible = SocketsAPI.getSockets(input);
        if (!possible.isPresent()) return;
        final ISocketableItem socket = possible.orElseThrow(IllegalStateException::new);
        final SocketStackHandler handler = socket.getStackHandler();
        if (!acceptsRemoval(handler)) return;
        final IGem[] gems = new IGem[handler.getSlots()];
        int xp = 0;
        for (int i = 0; i < handler.getSlots(); i++) {
            gems[i] = SocketsAPI.getGem(handler.getStackInSlot(i)).orElse(null);
        }
        for (final IGem gem : gems) {
            if (gem != null) {
                xp += gem.xpForRemoval(socket);
            }
        }
        if (xp > 0) {
            if (player.experienceTotal >= xp && allGemsAccept(gems, socket, player)) {
                player.addExperienceLevel(-xp);
                removeSockets(handler, socket, player);
            }
        } else {
            if (allGemsAccept(gems, socket, player))
                removeSockets(handler, socket, player);
        }
    }

    private void removeSockets(final SocketStackHandler handler, final ISocketableItem item, final ServerPlayerEntity player) {
        final int slots = handler.getSlots();
        for (int i = 1; i < 5 && i - 1 < slots; i++) {
            final ItemStack gem = handler.getStackInSlot(i - 1);
            if (!gem.isEmpty()) {
                inv.setStackInSlot(i, gem);
                handler.setStackInSlot(i - 1, ItemStack.EMPTY);
                SocketsAPI.getGem(gem).ifPresent(g -> g.unequipped(item, player));
            }
        }
    }

    public static boolean allGemsAccept(final IGem[] gems, final ISocketableItem item, final PlayerEntity player) {
        for (final IGem gem : gems) {
            if (gem != null && !gem.canUnequipOn(item, player)) {
                return false;
            }
        }
        return true;
    }

    private boolean acceptsRemoval(final SocketStackHandler handler) {
        final int slots = handler.getSlots();
        for (int i = 1; i < 5 && i - 1 < slots; i++) {
            if (!handler.getStackInSlot(i - 1).isEmpty()) {
                if (!inv.getStackInSlot(i).isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        compound.put("inv", inv.serializeNBT());
        return super.write(compound);
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        inv.deserializeNBT(compound.getCompound("inv"));
    }

    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (side == null) return total.cast();
            else if (side == Direction.DOWN) return output.cast();
            else return input.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public ITextComponent getDisplayName() {
        return new TranslationTextComponent("container.socket_remover");
    }

    @Override
    public Container createMenu(int id, PlayerInventory playerInv, PlayerEntity player) {
        return new SocketRemoverContainer(id, playerInv, this.getPos());
    }
}
