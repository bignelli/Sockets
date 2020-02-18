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
import nukeologist.sockets.api.SocketsAPI;
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
