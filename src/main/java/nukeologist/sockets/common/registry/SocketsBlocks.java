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

import net.minecraft.block.Block;
import net.minecraft.block.OreBlock;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.common.block.GemOreBlock;
import nukeologist.sockets.common.block.SocketRemoverBlock;

import java.util.Random;
import java.util.function.ToIntFunction;

public final class SocketsBlocks {

    private SocketsBlocks() {
    }

    //Blocks in here shall be known to the loot table provider.
    public static final DeferredRegister<Block> BLOCKS = new DeferredRegister<>(ForgeRegistries.BLOCKS, SocketsAPI.ID);

    public static final RegistryObject<Block> SOCKET_REMOVER = BLOCKS.register("socket_remover", () -> new SocketRemoverBlock(Block.Properties.create(Material.IRON)));
    public static final RegistryObject<Block> RUBY_ORE = BLOCKS.register("ruby_ore", () -> new GemOreBlock(Block.Properties.create(Material.ROCK)).exp(SocketsBlocks::xp));
    public static final RegistryObject<Block> SAPPHIRE_ORE = BLOCKS.register("sapphire_ore", () -> new GemOreBlock(Block.Properties.create(Material.ROCK)).exp(SocketsBlocks::xp));

    //Used by the ore blocks
    private static final int xp(final Random random) {
        return MathHelper.nextInt(random, 2, 5);
    }
}
