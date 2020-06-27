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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
import net.minecraft.util.ResourceLocation;
import nukeologist.sockets.api.SocketsAPI;
import org.apache.commons.lang3.text.translate.JavaUnicodeEscaper;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

/**
 * Utility class to datagen the loot modifiers. May change if forge ever adds a default one.
 * WIP atm it's hardcoded
 */
@SuppressWarnings("deprecation")
public class LootModifierProvider implements IDataProvider {

    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    private final DataGenerator gen;
    private final String modid;
    private GlobalLootModifiers modifiers;

    public LootModifierProvider(DataGenerator gen, String modid) {
        this.gen = gen;
        this.modid = modid;
    }

    public LootModifierProvider(DataGenerator gen) {
        this(gen, SocketsAPI.ID);
    }

    protected void addLootModifiers() {
        modifiers = GlobalLootModifiers.of(modLoc("smelting").toString(), modLoc("fortune").toString(), modLoc("enchantful").toString(), modLoc("chargeful").toString());
    }

    protected ResourceLocation modLoc(final String path) {
        return new ResourceLocation(modid, path);
    }

    @Override
    public void act(DirectoryCache cache) throws IOException {
        addLootModifiers();
        if (modifiers != null) save(cache, modifiers, this.gen.getOutputFolder().resolve("data/forge/loot_modifiers/global_loot_modifiers.json"));
        save(cache, new ModifierTest("sockets:smelting", "{socketsSmelt:1b}", "minecraft:match_tool"), this.gen.getOutputFolder().resolve("data/" + modid + "/loot_modifiers/smelting.json"));
        save(cache, new ModifierTest("sockets:fortune", "{socketsFortune:1b}", "minecraft:match_tool"), this.gen.getOutputFolder().resolve("data/" + modid + "/loot_modifiers/fortune.json"));
        save(cache, new ModifierTest2("sockets:enchantful", "minecraft:chest", "minecraft:location_check"), this.gen.getOutputFolder().resolve("data/" + modid + "/loot_modifiers/enchantful.json"));
        save(cache, new ModifierTest3("sockets:chargeful",  "minecraft:elder_guardian"), this.gen.getOutputFolder().resolve("data/" + modid + "/loot_modifiers/chargeful.json"));
    }

    /*Copy from LanguageProvider */
    private void save(DirectoryCache cache, Object object, Path target) throws IOException {
        String data = GSON.toJson(object);
        data = JavaUnicodeEscaper.outsideOf(0, 0x7f).translate(data); // Escape unicode after the fact so that it's not double escaped by GSON
        String hash = IDataProvider.HASH_FUNCTION.hashUnencodedChars(data).toString();
        if (!Objects.equals(cache.getPreviousHash(target), hash) || !Files.exists(target)) {
            Files.createDirectories(target.getParent());

            try (BufferedWriter bufferedwriter = Files.newBufferedWriter(target)) {
                bufferedwriter.write(data);
            }
        }

        cache.recordHash(target, hash);
    }
    /*End copy*/

    @Override
    public String getName() {
        return "Loot Modifiers : " + modid;
    }

    protected static class GlobalLootModifiers {
        private boolean replace = false;
        private List<String> entries = new ArrayList<>();

        protected static GlobalLootModifiers of(String... locs) {
            final GlobalLootModifiers mod = new GlobalLootModifiers();
            Collections.addAll(mod.entries, locs);
            return mod;
        }
    }

    //TEMPORARY, WIP
    protected static class ModifierTest {

        private ModifierTest(String type, String nbt, String condition) {
            this.type = type;
            this.conditions = Collections.singletonList(new Condition(nbt, condition));
        }

        private String type;
        private List<Condition> conditions;

        private static class Condition {

            private Condition(String nbt, String condition) {
                predicate = new Predicate(nbt);
                this.condition = condition;
            }
            private String condition;
            private Predicate predicate;
        }

        private static class Predicate {

            private Predicate(String nbt) {
                this.nbt = nbt;
            }
            private final String nbt;
        }
    }

    protected static class ModifierTest2 {

        private ModifierTest2(String type, String block, String condition) {
            this.type = type;
            this.conditions = Collections.singletonList(new ModifierTest2.Condition(block, condition));
        }

        private String type;
        private List<ModifierTest2.Condition> conditions;

        private static class Condition {

            private Condition(String block, String condition) {
                this.condition = condition;
                this.predicate = new Predicate(block);
            }
            private String condition;
            private ModifierTest2.Predicate predicate;

        }

        private static class Predicate {

            private Dummy block;
            private Predicate(String block) {
                this.block = new Dummy(block);
            }
        }

        private static class Dummy {
            private String block;
            private Dummy(String block) {
                this.block = block;
            }
        }

    }

    protected static class ModifierTest3 {

        private ModifierTest3(String type, String id) {
            this.type = type;
            this.conditions = Collections.emptyList();
            this.entity_type = id;
        }

        private String type;
        private List<ModifierTest3.Condition> conditions;
        private String entity_type;

        private static class Condition {

            private Condition(String condition, String id) {
                this.condition = condition;
                this.entity = id;
            }
            private String condition;
            private String entity;

        }

    }
}
