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
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import net.minecraftforge.items.ItemHandlerHelper;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Copied and modified from a Forge Test. Used by the Ruby Gem.
 * https://github.com/MinecraftForge/MinecraftForge/blob/1.15.x/src/test/java/net/minecraftforge/debug/gameplay/loot/GlobalLootModifiersTest.java
 */
public class SmeltingModifier extends LootModifier {

    public SmeltingModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    private Optional<FurnaceRecipe> optionalFurnaceRecipe = Optional.empty();
    private FurnaceRecipe recipe;
    private ItemStack failed = ItemStack.EMPTY;

    @Nonnull
    @Override //Changed such that it caches the last recipe
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        final List<ItemStack> ret = new ArrayList<>();
        final RecipeManager manager = context.getWorld().getRecipeManager();
        final World world = context.getWorld();
        for (final ItemStack stack : generatedLoot) {
            if (ItemStack.areItemsEqual(stack, failed)) {
                ret.add(stack);
                continue;
            }
            final Inventory dummy = new Inventory(stack);
            if (!optionalFurnaceRecipe.isPresent()) {
                optionalFurnaceRecipe = manager.getRecipe(IRecipeType.SMELTING, dummy, world);
                recipe = optionalFurnaceRecipe.orElse(null);
            } else {
                if (!recipe.matches(dummy, world)) {
                    optionalFurnaceRecipe = manager.getRecipe(IRecipeType.SMELTING, dummy, world);
                }
                recipe = optionalFurnaceRecipe.orElse(null);
            }
            if (recipe != null) {
                final ItemStack out = recipe.getRecipeOutput();
                if (!out.isEmpty()) {
                    ret.add(ItemHandlerHelper.copyStackWithSize(out, stack.getCount() * out.getCount()));
                } else {
                    if (!ItemStack.areItemsEqual(stack, failed))
                        failed = stack.copy();
                    ret.add(stack);
                }
            } else {
                if (!ItemStack.areItemsEqual(stack, failed))
                    failed = stack.copy();
                ret.add(stack);
            }
        }
        return ret;
    }

    public static class Serializer extends GlobalLootModifierSerializer<SmeltingModifier> {

        @Override
        public SmeltingModifier read(ResourceLocation name, JsonObject json, ILootCondition[] conditionsIn) {
            return new SmeltingModifier(conditionsIn);
        }
    }
}
