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
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraft.world.storage.loot.conditions.ILootCondition;
import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.common.loot.LootModifier;
import nukeologist.sockets.common.config.Config;
import nukeologist.sockets.common.registry.SocketsItems;

import javax.annotation.Nonnull;
import java.util.List;

public class MobModifier extends LootModifier {

    private final ResourceLocation entityLoc;

    public MobModifier(ILootCondition[] conditionsIn, ResourceLocation entityLoc) {
        super(conditionsIn);
        this.entityLoc = entityLoc;
    }

    @Nonnull
    @Override
    protected List<ItemStack> doApply(List<ItemStack> generatedLoot, LootContext context) {
        final Entity entity = context.get(LootParameters.THIS_ENTITY);
        if (entity != null && entityLoc.equals(entity.getType().getRegistryName()) && context.getRandom().nextInt(100) < Config.SERVER.chargefulChance.get()) {
            generatedLoot.add(new ItemStack(SocketsItems.CHARGEFUL_GEM.get()));
        }
        return generatedLoot;
    }

    public static class Serializer extends GlobalLootModifierSerializer<MobModifier> {

        @Override
        public MobModifier read(ResourceLocation location, JsonObject object, ILootCondition[] ailootcondition) {
            final ResourceLocation rl = new ResourceLocation(JSONUtils.getString(object, "entity_type"));
            return new MobModifier(ailootcondition, rl);
        }
    }
}
