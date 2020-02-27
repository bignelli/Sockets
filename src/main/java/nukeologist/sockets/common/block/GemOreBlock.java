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

package nukeologist.sockets.common.block;

import net.minecraft.block.OreBlock;

import java.util.Random;
import java.util.function.ToIntFunction;

public class GemOreBlock extends OreBlock {

    private ToIntFunction<Random> func;

    public GemOreBlock(Properties properties) {
        super(properties);
    }

    public GemOreBlock exp(final ToIntFunction<Random> func) {
        this.func = func;
        return this;
    }

    @Override
    protected int getExperience(Random random) {
        return func == null ? super.getExperience(random) : func.applyAsInt(random);
    }

}
