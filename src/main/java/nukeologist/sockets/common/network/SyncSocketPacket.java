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

package nukeologist.sockets.common.network;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import nukeologist.sockets.api.SocketsAPI;

import java.util.function.Supplier;

public class SyncSocketPacket {

    private final int entityId;
    private final int slot;
    private final CompoundNBT tag;


    public SyncSocketPacket(final int entityId, final int slot, final ItemStack stack) {
        this.entityId = entityId;
        this.slot = slot;
        this.tag = SocketsAPI.getSockets(stack).map(i -> i.getStackHandler().serializeNBT()).orElseGet(CompoundNBT::new);
    }

    public SyncSocketPacket(final int entityId, final int slot, final CompoundNBT tag) {
        this.entityId = entityId;
        this.slot = slot;
        this.tag = tag;
    }

    public static void encode(SyncSocketPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.entityId);
        buf.writeInt(pkt.slot);
        buf.writeCompoundTag(pkt.tag);
    }

    public static SyncSocketPacket decode(PacketBuffer buf) {
        return new SyncSocketPacket(buf.readInt(), buf.readInt(), buf.readCompoundTag());
    }

    public static class Handler {

        public static void handle(final SyncSocketPacket pkt, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                final Entity entity = Minecraft.getInstance().world.getEntityByID(pkt.entityId);

                if (entity instanceof PlayerEntity) {
                    final Container container = ((PlayerEntity) entity).openContainer;
                    final Slot slot = InsertGemPacket.Handler.validateAndGet(container, pkt.slot);
                    if (slot == null) return;
                    SocketsAPI.getSockets(slot.getStack()).ifPresent(s -> s.getStackHandler().deserializeNBT(pkt.tag));
                    //((PlayerEntity) entity).inventory.markDirty();
                    //container.detectAndSendChanges();
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
