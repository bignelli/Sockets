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

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.CraftingResultSlot;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.api.cap.IGem;
import nukeologist.sockets.api.cap.ISocketableItem;

import java.util.List;
import java.util.function.Supplier;

public class InsertGemPacket {

    private final int slot;

    public InsertGemPacket(final int slot) {
        this.slot = slot;
    }

    public static void encode(InsertGemPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.slot);
    }

    public static InsertGemPacket decode(PacketBuffer buf) {
        return new InsertGemPacket(buf.readInt());
    }

    public static class Handler {

        public static void handle(final InsertGemPacket pkt, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                final ServerPlayerEntity sender = ctx.get().getSender();
                if (sender == null || sender.openContainer == null) return;
                final Slot slot = validateAndGet(sender.openContainer, pkt.slot);
                if (slot == null || slot instanceof CraftingResultSlot) return;
                final ItemStack gemStack = sender.inventory.getItemStack();
                if (gemStack.isEmpty()) return;
                final ItemStack socketStack = slot.getStack();
                if (socketStack.isEmpty()) return;
                final LazyOptional<IGem> gem = SocketsAPI.getGem(gemStack);
                if (!gem.isPresent()) return;
                final LazyOptional<ISocketableItem> socket = SocketsAPI.getSockets(socketStack);

                //Finally after validation:
                socket.ifPresent(s -> gem.ifPresent(g -> {
                    if (!g.canEquipOn(s, sender)) return;
                    if (!s.accepts(g)) return;
                    final ItemStack copy = gemStack.split(1);
                    if (ItemHandlerHelper.insertItem(s.getStackHandler(), copy, false).isEmpty()) {
                        SocketsAPI.getGem(copy).ifPresent(gg -> gg.equipped(s, sender));
                        slot.inventory.markDirty();
                    } else {
                        gemStack.grow(1);
                    }
                }));
            });
            ctx.get().setPacketHandled(true);
        }

        public static Slot validateAndGet(Container container, int slot) {
            final List<Slot> slots = container.inventorySlots;
            final int size = slots.size();
            if (slot < size && slot >= 0) {
                return slots.get(slot);
            }
            return null;
        }
    }
}
