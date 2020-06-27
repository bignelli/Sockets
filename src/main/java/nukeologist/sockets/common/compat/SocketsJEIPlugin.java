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

package nukeologist.sockets.common.compat;

//import mezz.jei.api.IModPlugin;
//import mezz.jei.api.JeiPlugin;
//import mezz.jei.api.constants.VanillaTypes;
//import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import nukeologist.sockets.Sockets;
import nukeologist.sockets.common.registry.SocketsItems;
import nukeologist.sockets.common.util.StringTranslations;

//@JeiPlugin
public final class SocketsJEIPlugin {//implements IModPlugin {

    private static final ResourceLocation ID = Sockets.modLoc("jeiplugin");

    //@Override
    //public void registerRecipes(IRecipeRegistration registry) {
    //    registry.addIngredientInfo(new ItemStack(SocketsItems.DIAMOND_GEM.get()), VanillaTypes.ITEM, StringTranslations.DIAMOND_GEM_JEI);
    //    registry.addIngredientInfo(new ItemStack(SocketsItems.EMERALD_GEM.get()), VanillaTypes.ITEM, StringTranslations.EMERALD_GEM_JEI);
    //    registry.addIngredientInfo(new ItemStack(SocketsItems.ENCHANTFUL_GEM.get()), VanillaTypes.ITEM, StringTranslations.ENCHANTFUL_GEM_JEI);
    //    registry.addIngredientInfo(new ItemStack(SocketsItems.LAZULI_GEM.get()), VanillaTypes.ITEM, StringTranslations.LAZULI_GEM_JEI);
    //    registry.addIngredientInfo(new ItemStack(SocketsItems.RUBY_GEM.get()), VanillaTypes.ITEM, StringTranslations.RUBY_GEM_JEI);
    //    registry.addIngredientInfo(new ItemStack(SocketsItems.SAPPHIRE_GEM.get()), VanillaTypes.ITEM, StringTranslations.SAPPHIRE_GEM_JEI);
    //    registry.addIngredientInfo(new ItemStack(SocketsItems.CHARGEFUL_GEM.get()), VanillaTypes.ITEM, StringTranslations.CHARGEFUL_GEM_JEI);
    //}
    //
    //@Override
    //public ResourceLocation getPluginUid() {
    //    return ID;
    //}
}
