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

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.LanguageProvider;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.common.registry.SocketsItems;

public class EnglishProvider extends LanguageProvider {

    public EnglishProvider(DataGenerator gen) {
        super(gen, SocketsAPI.ID, "en_us");
    }

    @Override
    protected void addTranslations() {
        add(SocketsItems.DIAMOND_GEM.get(), "Diamond Gem");
    }
}
