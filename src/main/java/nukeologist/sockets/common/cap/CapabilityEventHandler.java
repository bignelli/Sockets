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

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.*;
import net.minecraft.util.LazyValue;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.common.config.Config;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.regex.Pattern;

import static nukeologist.sockets.Sockets.modLoc;
import static nukeologist.sockets.Sockets.CORE;

public enum CapabilityEventHandler {

    INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger();
    private static final LazyValue<Object2IntMap<String>> capItems = new LazyValue<>(CapabilityEventHandler::getCaps);

    @SubscribeEvent
    public void attachCap(AttachCapabilitiesEvent<ItemStack> event) {
        final ItemStack stack = event.getObject();
        if (stack.isEmpty()) return;
        if (stack.getMaxStackSize() != 1) return;
        final Item item = stack.getItem();
        final int value = capItems.getValue().getInt(item.getRegistryName().toString());
        if (value != 0) {
            event.addCapability(modLoc("socket"), CapabilitySocketableItem.createProvider(stack, value));
        } else if (item instanceof TieredItem && Config.SERVER.enableAllTools.get()) {
            event.addCapability(modLoc("socket"), CapabilitySocketableItem.createProvider(stack, 1));
        } else if (item instanceof ArmorItem && Config.SERVER.enableAllArmor.get()) {
            event.addCapability(modLoc("socket"), CapabilitySocketableItem.createProvider(stack, 1));
        }
    }

    private static Object2IntMap<String> getCaps() {
        final Object2IntMap<String> map = new Object2IntOpenHashMap<>();
        final List<? extends String> vals = Config.SERVER.socketsItems.get();
        final Pattern pattern = Pattern.compile("=");
        for (final String str : vals) {
            final String[] pair = pattern.split(str);
            int value;
            try {
                value = Integer.parseInt(pair[1]);
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                value = 1;
                LOGGER.warn(CORE, "Failed to parse config value for \"{}\", setting its number of sockets to 1", pair[0]);
                LOGGER.error(CORE, "Cause: ", e);
            }
            map.putIfAbsent(pair[0], Math.max(Math.min(4, value), 1));
        }
        return map;
    }

    @SubscribeEvent
    public void gemTick(LivingEvent.LivingUpdateEvent event) {
        final LivingEntity entity = event.getEntityLiving();
        entity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(h -> tickGems(h, entity));
    }

    private void tickGems(final IItemHandler inv, LivingEntity entity) {
        for (int i = 0; i < inv.getSlots(); i++) {
            final ItemStack stack = inv.getStackInSlot(i);
            if (stack.isEmpty()) continue;
            SocketsAPI.getSockets(stack).ifPresent(item -> item.getStackHandler().forEach(gem -> gem.socketTick(item, entity)));
        }
    }

    //CAPABILITY SYNCING

    private void addListeners(ServerPlayerEntity player, Container container) {
        container.addListener(new CapabilityContainerListener(player));
    }

    @SubscribeEvent
    public void playerLoggedIn(final PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            final ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            addListeners(player, player.container);
        }
    }

    @SubscribeEvent
    public void playerClone(final PlayerEvent.Clone event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            final ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            addListeners(player, player.container);
        }
    }

    @SubscribeEvent
    public void containerOpen(final PlayerContainerEvent.Open event) {
        if (event.getPlayer() instanceof ServerPlayerEntity) {
            final ServerPlayerEntity player = (ServerPlayerEntity) event.getPlayer();
            addListeners(player, event.getContainer());
        }
    }
}
