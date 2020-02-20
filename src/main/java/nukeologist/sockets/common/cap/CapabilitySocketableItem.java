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

package nukeologist.sockets.common.cap;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import nukeologist.sockets.api.SocketStackHandler;
import nukeologist.sockets.api.cap.ISocketableItem;
import nukeologist.sockets.api.cap.SocketsCapability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CapabilitySocketableItem {

    private CapabilitySocketableItem() {
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(ISocketableItem.class, new Capability.IStorage<ISocketableItem>() {

            @Override
            public INBT writeNBT(Capability<ISocketableItem> capability, ISocketableItem instance, Direction side) {
                final CompoundNBT nbt = new CompoundNBT();
                nbt.put("socketInv", instance.getStackHandler().serializeNBT());
                return nbt;
            }

            @Override
            public void readNBT(Capability<ISocketableItem> capability, ISocketableItem instance, Direction side, INBT nbt) {
                final CompoundNBT tag = (CompoundNBT) nbt;
                instance.getStackHandler().deserializeNBT(tag.getCompound("socketInv"));
            }
        }, SocketWrapper::new);
    }

    public static class SocketWrapper implements ISocketableItem {

        private final SocketStackHandler handler;

        private int size = 1;

        public SocketWrapper() {
            handler = new SocketStackHandler(this);
        }

        public SocketWrapper(int size) {
            this.size = Math.min(size, 4);
            handler = new SocketStackHandler(this);
        }

        @Override
        public int getSlots() {
            return size;
        }

        @Override
        public SocketStackHandler getStackHandler() {
            return handler;
        }
    }

    public static ICapabilityProvider createProvider(ISocketableItem item) {
        return new Provider(item);
    }

    public static ICapabilityProvider createProvider() {
        return createProvider(new SocketWrapper());
    }

    public static class Provider implements ICapabilitySerializable<INBT> {

        final LazyOptional<ISocketableItem> socketableItem;
        final ISocketableItem instance;

        Provider(ISocketableItem item) {
            this.instance = item;
            this.socketableItem = LazyOptional.of(() -> item);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return SocketsCapability.SOCKETABLE_ITEM_CAPABILITY.orEmpty(cap, socketableItem);
        }

        @Override
        public INBT serializeNBT() {
            return SocketsCapability.SOCKETABLE_ITEM_CAPABILITY.writeNBT(instance, null);
        }

        @Override
        public void deserializeNBT(INBT nbt) {
            SocketsCapability.SOCKETABLE_ITEM_CAPABILITY.readNBT(instance, null, nbt);
        }
    }
}
