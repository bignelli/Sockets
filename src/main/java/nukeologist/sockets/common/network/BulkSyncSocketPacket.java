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

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.NonNullList;
import net.minecraftforge.fml.network.NetworkEvent;
import nukeologist.sockets.api.SocketsAPI;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;
import java.util.stream.IntStream;

public class BulkSyncSocketPacket {

    private final int entityId;
    private final int size;
    private final Int2ObjectMap<CompoundNBT> map;

    public BulkSyncSocketPacket(final int entityId, final NonNullList<ItemStack> list) {
        this.entityId = entityId;
        map = new Int2ObjectOpenHashMap<>();
        final AtomicInteger temp = new AtomicInteger(0);
        IntStream.range(0, list.size()).forEach(slot -> SocketsAPI.getSockets(list.get(slot)).ifPresent(s -> {
            map.put(slot, s.getStackHandler().serializeNBT());
            temp.getAndIncrement();
        }));
        this.size = temp.get();
    }

    public BulkSyncSocketPacket(PacketBuffer buf) {
        map = new Int2ObjectOpenHashMap<>();
        entityId = buf.readInt();
        size = buf.readInt();
        for (int i = 0; i < size; i++) {
            final int slot = buf.readInt();
            final CompoundNBT nbt = buf.readCompoundTag();
            map.put(slot, nbt);
        }
    }

    public static void encode(BulkSyncSocketPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.entityId);
        buf.writeInt(pkt.size);
        Int2ObjectMaps.fastForEach(pkt.map, entry -> {
            buf.writeInt(entry.getIntKey());
            buf.writeCompoundTag(entry.getValue());
        });
    }

    public static BulkSyncSocketPacket decode(PacketBuffer buf) {
        return new BulkSyncSocketPacket(buf);
    }

    public static class Handler {

        public static void handle(final BulkSyncSocketPacket pkt, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                final Entity entity = Minecraft.getInstance().world.getEntityByID(pkt.entityId);
                if (entity instanceof PlayerEntity) {
                    final Container container = ((PlayerEntity) entity).openContainer;
                    Int2ObjectMaps.fastForEach(pkt.map, entry -> {
                        final Slot slot = InsertGemPacket.Handler.validateAndGet(container, entry.getIntKey());
                        if (slot == null) return;
                        SocketsAPI.getSockets(slot.getStack()).ifPresent(s -> s.getStackHandler().deserializeNBT(entry.getValue()));
                    });
                }
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
