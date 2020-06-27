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
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.conditions.ILootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import nukeologist.sockets.common.config.Config;
import nukeologist.sockets.common.registry.SocketsItems;

import java.util.List;

public class DungeonModifier extends LootModifier {

    public DungeonModifier(ILootCondition[] conditionsIn) {
        super(conditionsIn);
    }

    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        if (context.getRandom().nextInt(100) < Config.SERVER.dungeonEnchantfulChance.get()) {
            generatedLoot.add(new ItemStack(SocketsItems.ENCHANTFUL_GEM.get()));
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<DungeonModifier> {

        @Override
        public DungeonModifier read(ResourceLocation location, JsonObject object, ILootCondition[] conditions) {
            return new DungeonModifier(conditions);
        }
    }
}
