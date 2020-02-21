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

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import nukeologist.sockets.api.SocketStackHandler;
import nukeologist.sockets.api.cap.IGem;
import nukeologist.sockets.api.cap.ISocketableItem;
import nukeologist.sockets.common.cap.CapabilityGemItem;

import javax.annotation.Nullable;

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
        });
    }

    @Override
    public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
        return false;
    }

    @Override
    public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
        return true;
    }

    private boolean containsThis(final SocketStackHandler handler) {
        for (int i = 0; i < handler.getSlots(); i++) {
            if (handler.getStackInSlot(i).getItem() == this)
                return true;
        }
        return false;
    }
}
