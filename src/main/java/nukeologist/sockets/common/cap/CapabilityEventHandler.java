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

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import nukeologist.sockets.api.SocketsAPI;

import static nukeologist.sockets.Sockets.modLoc;

public enum CapabilityEventHandler {

    INSTANCE;

    @SubscribeEvent
    public void attachCap(AttachCapabilitiesEvent<ItemStack> event) {
        final ItemStack stack = event.getObject();
        if (stack.isEmpty()) return;
        final Item item = stack.getItem();
        if ((item instanceof TieredItem && ((TieredItem) item).getTier() == ItemTier.DIAMOND)
                || (item instanceof ArmorItem && ((ArmorItem) item).getArmorMaterial() == ArmorMaterial.DIAMOND))
            event.addCapability(modLoc("socket"), CapabilitySocketableItem.createProvider(stack));
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