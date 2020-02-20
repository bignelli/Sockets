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

package nukeologist.sockets;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DeferredWorkQueue;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.common.cap.CapabilityEventHandler;
import nukeologist.sockets.common.cap.CapabilityGemItem;
import nukeologist.sockets.common.cap.CapabilitySocketableItem;
import nukeologist.sockets.common.network.Network;
import nukeologist.sockets.common.registry.SocketsBlocks;
import nukeologist.sockets.common.registry.SocketsContainers;
import nukeologist.sockets.common.registry.SocketsItems;
import nukeologist.sockets.common.registry.SocketsTileEntities;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.MarkerManager;

@Mod(SocketsAPI.ID)
public final class Sockets {

    private static final Logger LOGGER = LogManager.getLogger();
    private static final Marker CORE = MarkerManager.getMarker("SOCKETS");

    public Sockets() {
        LOGGER.info(CORE, "Sockets mod initializing!");
        final IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        modBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.register(CapabilityEventHandler.INSTANCE);

        //Registry
        SocketsItems.ITEMS.register(modBus);
        SocketsBlocks.BLOCKS.register(modBus);
        SocketsTileEntities.TILES.register(modBus);
        SocketsContainers.CONTAINERS.register(modBus);
        LOGGER.info(CORE, "Subscribed all event handlers.");
    }

    @SuppressWarnings("deprecation")
    private void commonSetup(final FMLCommonSetupEvent event) {
        CapabilitySocketableItem.register();
        CapabilityGemItem.register();
        DeferredWorkQueue.runLater(Network::register);
        LOGGER.info(CORE, "Registered Capabilities and Network");
    }

    public static ResourceLocation modLoc(final String path) {
        return new ResourceLocation(SocketsAPI.ID, path);
    }
}
