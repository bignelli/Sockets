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

package nukeologist.sockets.common.loot;

import com.google.gson.JsonObject;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameterSets;
import net.minecraft.loot.LootParameters;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.api.cap.ISocketableItem;
import nukeologist.sockets.common.registry.SocketsItems;

import javax.annotation.Nonnull;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

public class FortuneModifier extends LootModifier {

    public FortuneModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        final ItemStack tool = context.get(LootParameters.TOOL);
        //this conflicts with the smelt enchant. Maybe make it conflict in the loot condition JSON?
        if (tool.getOrCreateTag().getBoolean("socketsSmelt"))
            return generatedLoot;
        //silk touch does not conform with abundance
        final Map<Enchantment, Integer> enchants = EnchantmentHelper.getEnchantments(tool);
        if (enchants.containsKey(Enchantments.SILK_TOUCH))
            return generatedLoot;
        final boolean already = enchants.containsKey(Enchantments.FORTUNE);
        final ItemStack fakeTool = tool.copy();
        fakeTool.getOrCreateTag().putBoolean("socketsFortune", false); //Makes sure we do not recursion-call this again
        final long level = SocketsAPI.getSockets(fakeTool)
                .map(ISocketableItem::getStackHandler)
                .map(i -> IntStream.range(0, i.getSlots())
                        .mapToObj(i::getStackInSlot)
                        .filter(stack -> stack.getItem() == SocketsItems.LAZULI_GEM.get()).count()).orElse(0L);
        if (level == 0) return generatedLoot;
        final int bonus = already ? enchants.get(Enchantments.FORTUNE) : 0;
        fakeTool.addEnchantment(Enchantments.FORTUNE, (int) Math.min(3, level + bonus));
        final LootContext.Builder builder = new LootContext.Builder(context);
        builder.withParameter(LootParameters.TOOL, fakeTool);
        final LootContext ctx = builder.build(LootParameterSets.BLOCK);
        final LootTable loottable = context.getWorld().getServer().getLootTableManager().getLootTableFromLocation(context.get(LootParameters.BLOCK_STATE).getBlock().getLootTable());
        return loottable.generate(ctx);
    }

    public static class Serializer extends GlobalLootModifierSerializer<FortuneModifier> {

        @Override
        public FortuneModifier read(ResourceLocation location, JsonObject object, ILootCondition[] conditions) {
            return new FortuneModifier(conditions);
        }
    }
}
