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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.api.cap.ISocketableItem;
import nukeologist.sockets.common.item.EnchantfulGemItem;
import nukeologist.sockets.common.item.GemItem;

import java.util.Comparator;
import java.util.UUID;
import java.util.stream.IntStream;

public final class SocketsItems {

    private SocketsItems() {
    }

    public static final ItemGroup SOCKETS_GROUP = new ItemGroup("sockets") {
        @Override
        public ItemStack createIcon() {
            return new ItemStack(DIAMOND_GEM.get());
        }

        @Override
        public void fill(NonNullList<ItemStack> items) {
            super.fill(items);
            items.sort(Comparator.comparing(s -> s.getItem() instanceof BlockItem ? ("block" + ((BlockItem) s.getItem()).getBlock().getRegistryName().toString()) : s.getItem().getRegistryName().toString()));
        }
    };

    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, SocketsAPI.ID);

    //If there is "_gem" in the registry name, it will be automagically added to a sockets:gems tag.
    public static final RegistryObject<Item> DIAMOND_GEM = ITEMS.register("diamond_gem", () -> new GemItem(defaultGem()).attributes(SocketsItems::getDiamondModifier));
    public static final RegistryObject<Item> SOCKET_REMOVER = ITEMS.register("socket_remover", () -> new BlockItem(SocketsBlocks.SOCKET_REMOVER.get(), defaultGroup()));
    public static final RegistryObject<Item> EMERALD_GEM = ITEMS.register("emerald_gem", () -> new GemItem(defaultGem()).attributes(SocketsItems::getEmeraldModifier));
    public static final RegistryObject<Item> ENCHANTFUL_GEM = ITEMS.register("enchantful_gem", () -> new EnchantfulGemItem(defaultGroup().maxStackSize(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> LAZULI_GEM = ITEMS.register("lazuli_gem", () -> new GemItem(defaultGem()).onEquip(i -> i.getHolder().getOrCreateTag().putBoolean("socketsFortune", true)).onUnequip(SocketsItems::handleUnequipLazuli));
    public static final RegistryObject<Item> RUBY = ITEMS.register("ruby", () -> new Item(defaultGroup()));
    public static final RegistryObject<Item> SAPPHIRE = ITEMS.register("sapphire", () -> new Item(defaultGroup()));
    public static final RegistryObject<Item> RUBY_GEM = ITEMS.register("ruby_gem", () -> new GemItem(defaultGem()).onEquip(i -> i.getHolder().getOrCreateTag().putBoolean("socketsSmelt", true)).onUnequip(SocketsItems::handleUnequipRuby));
    public static final RegistryObject<Item> SAPPHIRE_GEM = ITEMS.register("sapphire_gem", () -> new GemItem(defaultGem()));
    public static final RegistryObject<Item> RUBY_ORE = ITEMS.register("ruby_ore", () -> new BlockItem(SocketsBlocks.RUBY_ORE.get(), defaultGroup()));
    public static final RegistryObject<Item> SAPPHIRE_ORE = ITEMS.register("sapphire_ore", () -> new BlockItem(SocketsBlocks.SAPPHIRE_ORE.get(), defaultGroup()));

    private static Item.Properties defaultGroup() {
        return new Item.Properties()
                .group(SOCKETS_GROUP);
    }

    private static Item.Properties defaultGem() {
        return defaultGroup().rarity(Rarity.RARE);
    }

    //Lazuli
    private static void handleUnequipLazuli(final ISocketableItem item) {
        if (!item.getStackHandler().hasItem(LAZULI_GEM.get())) {
            item.getHolder().getOrCreateTag().putBoolean("socketsFortune", false);
        }
    }

    //Ruby
    private static void handleUnequipRuby(final ISocketableItem item) {
        if (!item.getStackHandler().hasItem(RUBY_GEM.get())) {
            item.getHolder().getOrCreateTag().putBoolean("socketsSmelt", false);
        }
    }

    //Handles the loot level for the Sapphire Gem
    public static void handleLootLevel(final LootingLevelEvent event) {
        int level = event.getLootingLevel();
        if (level >= 3) return;
        final DamageSource source = event.getDamageSource();
        if (source.getTrueSource() instanceof LivingEntity) {
            final LivingEntity killer = (LivingEntity) source.getTrueSource();
            final EquipmentSlotType[] vals = new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND};
            for (EquipmentSlotType type : vals) {
                final long equipLevel = SocketsAPI.getSockets(killer.getItemStackFromSlot(type))
                        .map(ISocketableItem::getStackHandler)
                        .map(i -> IntStream.range(0, i.getSlots())
                                .mapToObj(i::getStackInSlot)
                                .filter(stack -> stack.getItem() == SAPPHIRE_GEM.get()).count()).orElse(0L);
                if (level + equipLevel >= 3) {
                    event.setLootingLevel(3);
                    return;
                } else {
                    level += equipLevel;
                }
            }
            event.setLootingLevel(level);
        }
    }

    //Diamond
    private static final UUID DIAMOND_DAMAGE_UUID = UUID.fromString("bc2ccb92-af05-4f91-970d-d9e44d211aa3");
    private static final UUID DIAMOND_PROTECTION_UUID = UUID.fromString("830d4884-b1fa-44a8-aeea-b6dab4c16acb");
    private static final AttributeModifier DIAMOND_DAMAGE = new AttributeModifier(DIAMOND_DAMAGE_UUID, "sockets_diamond_dmg", 1D, AttributeModifier.Operation.ADDITION);
    private static final AttributeModifier DIAMOND_PROTECTION = new AttributeModifier(DIAMOND_PROTECTION_UUID, "sockets_diamond_prt", 1D, AttributeModifier.Operation.ADDITION);

    private static Multimap<String, AttributeModifier> getDiamondModifier(EquipmentSlotType type) {
        final Multimap<String, AttributeModifier> map = HashMultimap.create();
        switch (type) {
            case MAINHAND:
            case OFFHAND:
                map.put("generic.attackDamage", DIAMOND_DAMAGE);
                break;
            case FEET:
            case HEAD:
            case LEGS:
            case CHEST:
                map.put("generic.armorToughness", DIAMOND_PROTECTION);
                break;
        }
        return map;
    }

    //Emerald
    private static final UUID EMERALD_LUCK_UUID = UUID.fromString("697acf81-603e-4fce-8db0-bda4bcfa9994");
    private static final AttributeModifier EMERALD_LUCK = new AttributeModifier(EMERALD_LUCK_UUID, "sockets_emerald_luck", 100D, AttributeModifier.Operation.ADDITION);

    private static Multimap<String, AttributeModifier> getEmeraldModifier(EquipmentSlotType type) {
        final Multimap<String, AttributeModifier> map = HashMultimap.create();
        map.put("generic.luck", EMERALD_LUCK);
        return map;
    }
}
