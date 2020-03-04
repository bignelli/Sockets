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
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.api.cap.IGem;

import java.util.function.Supplier;

/**
 * The Creative Screen/Container are special snowflakes
 */
public class CreativeInsertGemPacket {

    private final ItemStack stack;
    private final int slot;

    public CreativeInsertGemPacket(ItemStack stack, int slot) {
        this.stack = stack;
        this.slot = slot;
    }

    public static void encode(CreativeInsertGemPacket pkt, PacketBuffer buf) {
        buf.writeItemStack(pkt.stack, false);
        buf.writeInt(pkt.slot);
    }

    public static CreativeInsertGemPacket decode(PacketBuffer buf) {
        return new CreativeInsertGemPacket(buf.readItemStack(), buf.readInt());
    }

    public static class Handler {

        public static void handle(CreativeInsertGemPacket pkt, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                final ServerPlayerEntity player = ctx.get().getSender();
                if (!player.isCreative()) return;
                final int slot = pkt.slot;
                if (slot <= -1) return;
                player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                        .ifPresent(h -> {
                            if (slot >= h.getSlots()) return;
                            final ItemStack stack = pkt.stack;
                            final LazyOptional<IGem> gem = SocketsAPI.getGem(stack);
                            SocketsAPI.getSockets(h.getStackInSlot(slot)).ifPresent(s -> {
                                gem.ifPresent(g -> {
                                    if (!g.canEquipOn(s, player)) return;
                                    if (!s.accepts(g)) return;
                                    final ItemStack copy = stack.split(1);
                                    if (ItemHandlerHelper.insertItem(s.getStackHandler(), copy, false).isEmpty()) {
                                        SocketsAPI.getGem(copy).ifPresent(gg -> {
                                            gg.equipped(s, player);
                                        });
                                    }
                                });
                            });
                        });
            });
            ctx.get().setPacketHandled(true);
        }
    }
}
