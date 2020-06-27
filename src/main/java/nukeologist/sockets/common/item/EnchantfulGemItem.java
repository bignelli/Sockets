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

package nukeologist.sockets.common.item;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import nukeologist.sockets.api.SocketStackHandler;
import nukeologist.sockets.api.cap.IGem;
import nukeologist.sockets.api.cap.ISocketableItem;
import nukeologist.sockets.common.cap.CapabilityGemItem;
import nukeologist.sockets.common.util.StringTranslations;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EnchantfulGemItem extends Item {

    public EnchantfulGemItem(Properties properties) {
        super(properties);
    }

    @Override
    public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundNBT nbt) {
        return CapabilityGemItem.createProvider(new IGem() {

            @Override
            public boolean canEquipOn(ISocketableItem item, LivingEntity entity) {
                return !containsThis(item.getStackHandler());
            }

            @Override
            public void equipped(ISocketableItem item, LivingEntity entity) {
                final ItemStack holder = item.getHolder();
                if (holder.isEmpty()) return;
                EnchantmentHelper.getEnchantments(stack).forEach(holder::addEnchantment); //maybe kinda OP?
            }

            @Override
            public void unequipped(ISocketableItem item, LivingEntity entity) {
                final ItemStack holder = item.getHolder();
                if (holder.isEmpty()) return;
                final CompoundNBT tag = holder.getOrCreateTag();
                if (tag.contains("Enchantments", 9)) {
                    ListNBT listnbt = tag.getList("Enchantments", 10);
                    final List<String> enchants = new ArrayList<>();
                    listnbt.removeIf(n -> n instanceof CompoundNBT && isContainedEnchantment((CompoundNBT) n, stack, enchants));
                }
            }

            @Override
            public List<ITextComponent> getExtraTooltip() {
                return Collections.singletonList(new TranslationTextComponent(StringTranslations.ENCHANTFUL_EXTRA_TOOLTIP).func_240699_a_(TextFormatting.GREEN));
            }
        });
    }

    @Override
    @OnlyIn(Dist.CLIENT) //Avoid OnlyIn as much as possible, though this method is an exception.
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        tooltip.add(new TranslationTextComponent(StringTranslations.ENCHANTFUL_TOOLTIP).func_240699_a_(TextFormatting.GREEN));
    }

    private boolean isContainedEnchantment(final CompoundNBT nbt, final ItemStack stack, final List<String> ench) {
        final Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(stack);
        for (Map.Entry<Enchantment, Integer> entry : enchants.entrySet()) {
            final String id = nbt.getString("id");
            if (nbt.contains("id") && id.equals(entry.getKey().getRegistryName().toString()) && !ench.contains(id)) {
                ench.add(id);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return true;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return true;
    }

    @Override
    public int getItemEnchantability() {
        return 10;
    }

    private boolean containsThis(final SocketStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.getStackInSlot(i).getItem() == this)
                return true;
        }
        return false;
    }
}
