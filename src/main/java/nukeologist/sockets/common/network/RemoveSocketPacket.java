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
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkEvent;
import nukeologist.sockets.common.tileentity.SocketRemoverTileEntity;

import java.util.function.Supplier;

public class RemoveSocketPacket {

    private final BlockPos pos;

    //We send 3 ints to possibly make it compatible with Open Cubic Chunks in the future.
    public RemoveSocketPacket(final int x, final int y, final int z) {
        this.pos = new BlockPos(x, y, z);
    }

    public static void encode(RemoveSocketPacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.pos.getX());
        buf.writeInt(pkt.pos.getY());
        buf.writeInt(pkt.pos.getZ());
    }

    public static RemoveSocketPacket decode(PacketBuffer buf) {
        return new RemoveSocketPacket(buf.readInt(), buf.readInt(), buf.readInt());
    }

    public static class Handler {

        public static void handle(final RemoveSocketPacket pkt, Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> {
                final ServerPlayerEntity player = ctx.get().getSender();
                final BlockPos pos = pkt.pos;
                final TileEntity te = player.world.getTileEntity(pos);
                if (te instanceof SocketRemoverTileEntity) ((SocketRemoverTileEntity) te).tryAndRemove(player);
            });
        }
    }
}
