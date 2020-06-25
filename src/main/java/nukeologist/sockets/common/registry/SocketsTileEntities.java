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

package nukeologist.sockets.common.registry;

import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.common.tileentity.SocketRemoverTileEntity;

public final class SocketsTileEntities {

    private SocketsTileEntities() {
    }

    public static final DeferredRegister<TileEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, SocketsAPI.ID);

    public static final RegistryObject<TileEntityType<SocketRemoverTileEntity>> SOCKET_REMOVER = TILES.register("socket_remover", () -> TileEntityType.Builder.create(SocketRemoverTileEntity::new, SocketsBlocks.SOCKET_REMOVER.get()).build(null));
}
