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

import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import nukeologist.sockets.api.cap.IGem;
import nukeologist.sockets.api.cap.SocketsCapability;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class CapabilityGemItem {

    private CapabilityGemItem() {
    }

    public static void register() {
        CapabilityManager.INSTANCE.register(IGem.class, new Capability.IStorage<IGem>() {
            @Nullable
            @Override
            public INBT writeNBT(Capability<IGem> capability, IGem instance, Direction side) {
                return null;
            }

            @Override
            public void readNBT(Capability<IGem> capability, IGem instance, Direction side, INBT nbt) {

            }
        }, GemWrapper::new);
    }

    public static ICapabilityProvider createProvider(final IGem gem) {
        return new Provider(gem);
    }

    private static class GemWrapper implements IGem {

    }

    public static class Provider implements ICapabilityProvider {

        final LazyOptional<IGem> gemItem;

        Provider(IGem item) {
            this.gemItem = LazyOptional.of(() -> item);
        }

        @Nonnull
        @Override
        public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
            return SocketsCapability.GEM_ITEM_CAPABILITY.orEmpty(cap, gemItem);
        }
    }
}
