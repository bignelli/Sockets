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

import net.minecraftforge.common.loot.GlobalLootModifierSerializer;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.common.loot.DungeonModifier;
import nukeologist.sockets.common.loot.FortuneModifier;
import nukeologist.sockets.common.loot.MobModifier;
import nukeologist.sockets.common.loot.SmeltingModifier;

public final class SocketsLootModifiers {

    private SocketsLootModifiers() {
    }

    public static final DeferredRegister<GlobalLootModifierSerializer<?>> MODIFIERS = DeferredRegister.create(ForgeRegistries.LOOT_MODIFIER_SERIALIZERS, SocketsAPI.ID);

    public static final RegistryObject<GlobalLootModifierSerializer<?>> SMELT = MODIFIERS.register("smelting", SmeltingModifier.Serializer::new);
    public static final RegistryObject<GlobalLootModifierSerializer<?>> FORTUNE = MODIFIERS.register("fortune", FortuneModifier.Serializer::new);
    public static final RegistryObject<GlobalLootModifierSerializer<?>> ENCHANTFUL = MODIFIERS.register("enchantful", DungeonModifier.Serializer::new);
    public static final RegistryObject<GlobalLootModifierSerializer<?>> CHARGEFUL = MODIFIERS.register("chargeful", MobModifier.Serializer::new);
}
