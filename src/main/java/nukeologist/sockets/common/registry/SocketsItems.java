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

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.*;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.api.cap.ISocketableItem;
import nukeologist.sockets.common.item.EnchantfulGemItem;
import nukeologist.sockets.common.item.GemItem;

import java.util.Comparator;
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

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, SocketsAPI.ID);

    //If there is "_gem" in the registry name, it will be automagically added to a sockets:gems tag.
    public static final RegistryObject<Item> DIAMOND_GEM = ITEMS.register("diamond_gem", () -> new GemItem(defaultGem()));
    public static final RegistryObject<Item> SOCKET_REMOVER = ITEMS.register("socket_remover", () -> new BlockItem(SocketsBlocks.SOCKET_REMOVER.get(), defaultGroup()));
    public static final RegistryObject<Item> EMERALD_GEM = ITEMS.register("emerald_gem", () -> new GemItem(defaultGem()));
    public static final RegistryObject<Item> ENCHANTFUL_GEM = ITEMS.register("enchantful_gem", () -> new EnchantfulGemItem(defaultGroup().maxStackSize(1).rarity(Rarity.EPIC)));
    public static final RegistryObject<Item> LAZULI_GEM = ITEMS.register("lazuli_gem", () -> new GemItem(defaultGem()).onEquip(i -> i.getHolder().getOrCreateTag().putBoolean("socketsFortune", true)).onUnequip(SocketsItems::handleUnequipLazuli));
    public static final RegistryObject<Item> RUBY = ITEMS.register("ruby", () -> new Item(defaultGroup()));
    public static final RegistryObject<Item> SAPPHIRE = ITEMS.register("sapphire", () -> new Item(defaultGroup()));
    public static final RegistryObject<Item> RUBY_GEM = ITEMS.register("ruby_gem", () -> new GemItem(defaultGem()).onEquip(i -> i.getHolder().getOrCreateTag().putBoolean("socketsSmelt", true)).onUnequip(SocketsItems::handleUnequipRuby));
    public static final RegistryObject<Item> SAPPHIRE_GEM = ITEMS.register("sapphire_gem", () -> new GemItem(defaultGem()));
    public static final RegistryObject<Item> RUBY_ORE = ITEMS.register("ruby_ore", () -> new BlockItem(SocketsBlocks.RUBY_ORE.get(), defaultGroup()));
    public static final RegistryObject<Item> SAPPHIRE_ORE = ITEMS.register("sapphire_ore", () -> new BlockItem(SocketsBlocks.SAPPHIRE_ORE.get(), defaultGroup()));
    public static final RegistryObject<Item> RUBY_BLOCK = ITEMS.register("ruby_block", () -> new BlockItem(SocketsBlocks.RUBY_BLOCK.get(), defaultGroup()));
    public static final RegistryObject<Item> SAPPHIRE_BLOCK = ITEMS.register("sapphire_block", () -> new BlockItem(SocketsBlocks.SAPPHIRE_BLOCK.get(), defaultGroup()));
    public static final RegistryObject<Item> CHARGEFUL_GEM = ITEMS.register("chargeful_gem", () -> new GemItem(defaultGem()));

    private static Item.Properties defaultGroup() {
        return new Item.Properties().group(SOCKETS_GROUP);
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

    private static final EquipmentSlotType[] WEAPONS = new EquipmentSlotType[]{EquipmentSlotType.MAINHAND, EquipmentSlotType.OFFHAND};

    //Handles the loot level for the Sapphire Gem
    public static void handleLootLevel(final LootingLevelEvent event) {
        int level = event.getLootingLevel();
        if (level >= 3) return;
        final DamageSource source = event.getDamageSource();
        if (source.getTrueSource() instanceof LivingEntity) {
            final LivingEntity killer = (LivingEntity) source.getTrueSource();
            for (final EquipmentSlotType type : WEAPONS) {
                final long equipLevel = countGems(killer, SAPPHIRE_GEM.get(), type);
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

    private static final EquipmentSlotType[] ARMOR = new EquipmentSlotType[]{EquipmentSlotType.CHEST, EquipmentSlotType.LEGS, EquipmentSlotType.FEET, EquipmentSlotType.HEAD};

    //Diamond Gem and Chargeful Gem
    public static void onHurtEvent(final LivingHurtEvent event) {
        for (final EquipmentSlotType type : ARMOR) {
            final long equipLevel = countGems(event.getEntityLiving(), DIAMOND_GEM.get(), type);
            event.setAmount(event.getAmount() - equipLevel);
        }
        final Entity source = event.getSource().getTrueSource();
        if (!(source instanceof LivingEntity)) return;
        final LivingEntity entity = (LivingEntity) source;
        final long diamonds = countGems(entity, DIAMOND_GEM.get(), EquipmentSlotType.MAINHAND);
        event.setAmount(event.getAmount() + diamonds);
        final long chargefuls = countGems(entity, CHARGEFUL_GEM.get(), EquipmentSlotType.MAINHAND);
        final World world = entity.getEntityWorld();
        if (chargefuls > 0 && !world.isRemote) {
            final LivingEntity victim = event.getEntityLiving();
            final LightningBoltEntity bolt = new LightningBoltEntity(world, victim.getPosX(), victim.getPosY(), victim.getPosZ(), false);
            bolt.setCaster((ServerPlayerEntity) entity);
            ((ServerWorld) world).addLightningBolt(bolt);
        }
    }

    //Emerald Gem
    public static void onBreakEvent(final BlockEvent.BreakEvent event) {
        if (event.isCanceled()) return;
        if (event.getExpToDrop() <= 0) return;
        final long xp = countGems(event.getPlayer(), EMERALD_GEM.get(), EquipmentSlotType.MAINHAND);
        event.setExpToDrop(event.getExpToDrop() + (int) xp);
    }

    private static long countGems(final LivingEntity entity, final Item gem, final EquipmentSlotType type) {
        return SocketsAPI.getSockets(entity.getItemStackFromSlot(type))
                .map(ISocketableItem::getStackHandler)
                .map(i -> IntStream.range(0, i.getSlots())
                        .mapToObj(i::getStackInSlot)
                        .filter(stack -> stack.getItem() == gem).count()).orElse(0L);
    }
}
