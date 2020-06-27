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

package nukeologist.sockets.client.datagen;

import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.ITag;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.crafting.conditions.IConditionBuilder;
import nukeologist.sockets.common.registry.SocketsBlocks;
import nukeologist.sockets.common.registry.SocketsItems;
import nukeologist.sockets.common.registry.SocketsTags;

import java.util.function.Consumer;
import java.util.function.Supplier;

public class Recipes extends RecipeProvider implements IConditionBuilder {

    public Recipes(DataGenerator generatorIn) {
        super(generatorIn);
    }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer) {
        compactBlock(SocketsBlocks.RUBY_BLOCK, SocketsTags.Items.GEMS_RUBY, SocketsTags.Items.STORAGE_BLOCKS_RUBY, SocketsItems.RUBY, "has_ruby", "has_ruby_block", consumer);
        compactBlock(SocketsBlocks.SAPPHIRE_BLOCK, SocketsTags.Items.GEMS_SAPPHIRE, SocketsTags.Items.STORAGE_BLOCKS_SAPPHIRE, SocketsItems.SAPPHIRE, "has_sapphire", "has_sapphire_block", consumer);

        defaultGem(SocketsItems.DIAMOND_GEM, Tags.Items.STORAGE_BLOCKS_DIAMOND, Tags.Items.GEMS_DIAMOND, "has_diamond", consumer);
        defaultGem(SocketsItems.EMERALD_GEM, Tags.Items.STORAGE_BLOCKS_EMERALD, Tags.Items.GEMS_EMERALD, "has_emerald", consumer);

        defaultGem(SocketsItems.LAZULI_GEM, Tags.Items.STORAGE_BLOCKS_LAPIS, Tags.Items.GEMS_LAPIS, "has_lapis", consumer);
        defaultGem(SocketsItems.RUBY_GEM, SocketsTags.Items.STORAGE_BLOCKS_RUBY, SocketsTags.Items.GEMS_RUBY, "has_ruby", consumer);
        defaultGem(SocketsItems.SAPPHIRE_GEM, SocketsTags.Items.STORAGE_BLOCKS_SAPPHIRE, SocketsTags.Items.GEMS_SAPPHIRE, "has_sapphire", consumer);

        ShapedRecipeBuilder.shapedRecipe(SocketsBlocks.SOCKET_REMOVER.get()).key('#', SocketsTags.Items.GEMS).key('@', Items.ANVIL).patternLine("###").patternLine("#@#").patternLine("###").addCriterion("has_anvil", hasItem(Items.ANVIL)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(SocketsItems.CHARGEFUL_GEM.get()).addIngredient(SocketsItems.ENCHANTFUL_GEM.get()).addIngredient(Items.TRIDENT).addCriterion("has_enchantful", hasItem(SocketsItems.ENCHANTFUL_GEM.get())).build(consumer);
    }

    private void compactBlock(final Supplier<Block> block, final ITag.INamedTag<Item> itemTag, final ITag.INamedTag<Item> blockTag, final Supplier<Item> reverse, final String itemCriterion, final String blockCriterion, final Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(block.get()).key('#', itemTag).patternLine("###").patternLine("###").patternLine("###").addCriterion(itemCriterion, hasItem(itemTag)).build(consumer);
        ShapelessRecipeBuilder.shapelessRecipe(reverse.get(), 9).addIngredient(blockTag).addCriterion(blockCriterion, hasItem(blockTag)).build(consumer);
    }

    private void defaultGem(final Supplier<Item> gem, final ITag.INamedTag<Item> blockTag, final ITag.INamedTag<Item> gemTag, final String criterion, final Consumer<IFinishedRecipe> consumer) {
        ShapedRecipeBuilder.shapedRecipe(gem.get()).key('#', blockTag).key('@', gemTag).patternLine("#@#").patternLine("@#@").patternLine("#@#").addCriterion(criterion, hasItem(gemTag)).build(consumer);
    }
}
