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

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.network.PacketDistributor;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.common.network.BulkSyncSocketPacket;
import nukeologist.sockets.common.network.Network;
import nukeologist.sockets.common.network.SyncSocketPacket;

public class CapabilityContainerListener implements IContainerListener {

    private final ServerPlayerEntity player;

    public CapabilityContainerListener(ServerPlayerEntity player) {
        this.player = player;
    }

    @Override
    public void sendAllContents(Container containerToSend, NonNullList<ItemStack> itemsList) {
        final NonNullList<ItemStack> syncList = NonNullList.withSize(itemsList.size(), ItemStack.EMPTY);
        for (int i = 0; i < itemsList.size(); i++) {
            final ItemStack stack = itemsList.get(i);
            if (SocketsAPI.getSockets(stack).isPresent()) {
                syncList.set(i, stack);
            }
        }
        Network.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new BulkSyncSocketPacket(player.getEntityId(), syncList));
    }

    @Override
    public void sendSlotContents(Container containerToSend, int slotInd, ItemStack stack) {
        if (containerToSend.getSlot(slotInd) instanceof CraftingResultSlot) return;
        SocketsAPI.getSockets(stack).ifPresent(s -> sendSyncPacket(slotInd, stack));
    }

    @Override
    public void sendWindowProperty(Container containerIn, int varToUpdate, int newValue) {
        //Nothing to do here
    }

    private void sendSyncPacket(final int slot, ItemStack socket) {
        Network.CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncSocketPacket(player.getEntityId(), slot, socket));
    }
}
