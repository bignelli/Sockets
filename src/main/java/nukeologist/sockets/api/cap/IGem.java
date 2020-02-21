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

package nukeologist.sockets.api.cap;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.ITextComponent;

import java.util.Collections;
import java.util.List;

/**
 * Items which can be put inside other items' sockets should expose this interface as a
 * capability.
 */
public interface IGem {

    /**
     * Determines if this ItemStack can be equipped in a socket. Called on client and server.
     *
     * @param item   item with the socket
     * @param entity entity with this in the inventory
     * @return true if it can be equipped, false otherwise.
     */
    default boolean canEquipOn(ISocketableItem item, LivingEntity entity) {
        return true;
    }

    /**
     * Determines if this ItemStack can be unequipped from a socket.
     *
     * @param item   item with the socket
     * @param entity entity with this in the inventory
     * @return true if can be unequipped, false otherwise.
     */
    default boolean canUnequipOn(ISocketableItem item, LivingEntity entity) {
        return true;
    }

    /**
     * Called when this ItemStack was equipped on a socket, on client and server.
     * By default, plays a sound.
     *
     * @param item   item with the socket.
     * @param entity entity with this in the inventory
     * @see IGem#playEquipSound(ISocketableItem, LivingEntity)
     */
    default void equipped(ISocketableItem item, LivingEntity entity) {
        this.playEquipSound(item, entity);
    }

    /**
     * Called when this ItemStack was unequipped on a socket.
     *
     * @param item   item with the socket.
     * @param entity entity with this in the inventory
     */
    default void unequipped(ISocketableItem item, LivingEntity entity) {

    }

    /**
     * Called every tick while the ItemStack is on a socket. Do NOT modify
     * the {@link nukeologist.sockets.api.SocketStackHandler} inventory.
     *
     * @param item   item with the socket.
     * @param entity entity with this in the inventory
     */
    default void socketTick(ISocketableItem item, LivingEntity entity) {

    }

    /**
     * A map of AttributeModifier used by the socket when this gem enters it, similar to the item version.
     * Called before {@link IGem#equipped(ISocketableItem, LivingEntity)} and ONLY on the server.
     *
     * @param item          item with the socket
     * @param equipmentSlot slot which applies the modifier
     * @return a map of the attribute modifiers
     */
    default Multimap<String, AttributeModifier> getGemAttributeModifiers(ISocketableItem item, EquipmentSlotType equipmentSlot) {
        return HashMultimap.create();
    }

    /**
     * Called on the client to give extra tooltip information when hovering an
     * {@link ISocketableItem} with this inside it.
     *
     * @return extra tooltip information
     */
    default List<ITextComponent> getExtraTooltip() {
        return Collections.emptyList();
    }

    /**
     * Plays a sound when equipped (called by {@link IGem#equipped(ISocketableItem, LivingEntity)})
     * by default.
     *
     * @param item   item with the socket.
     * @param entity entity with this in the inventory
     */
    default void playEquipSound(ISocketableItem item, LivingEntity entity) {
        final PlayerEntity player = entity.world.isRemote() && entity instanceof PlayerEntity ? (PlayerEntity) entity : null;
        entity.world.playSound(player, entity.getPosition(),
                SoundEvents.ITEM_ARMOR_EQUIP_DIAMOND,
                SoundCategory.NEUTRAL, 1.0f, 1.0f);
    }
}
