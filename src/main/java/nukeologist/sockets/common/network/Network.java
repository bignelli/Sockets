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

package nukeologist.sockets.common.network;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import nukeologist.sockets.Sockets;
import nukeologist.sockets.api.SocketsAPI;

import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class Network {

    public static final SimpleChannel CHANNEL = NetworkRegistry.ChannelBuilder
            .named(Sockets.modLoc("main"))
            .networkProtocolVersion(NetworkVersion::getNetProtocol)
            .clientAcceptedVersions(NetworkVersion.clientAcceptedVersions)
            .serverAcceptedVersions(NetworkVersion.serverAcceptedVersions)
            .simpleChannel();

    private static int index;

    //When making a new packet, always bump major to avoid problems
    public static void register() {
        message(InsertGemPacket.class, InsertGemPacket::encode, InsertGemPacket::decode, InsertGemPacket.Handler::handle);
    }

    private static <C> void message(final Class<C> clazz, final BiConsumer<C, PacketBuffer> encoder, final Function<PacketBuffer, C> decoder, final BiConsumer<C, Supplier<NetworkEvent.Context>> consumer) {
        CHANNEL.registerMessage(index++, clazz, encoder, decoder, consumer);
    }

    private static class NetworkVersion {
        private static final String netProtocol;
        private static final Predicate<String> clientAcceptedVersions, serverAcceptedVersions;

        static {
            netProtocol = SocketsAPI.NETWORK_VERSION;
            int major = parseMajor(netProtocol);
            int minor = parseMinor(netProtocol);
            int patch = parsePatch(netProtocol);

            clientAcceptedVersions = s -> parseMajor(s) == major && parsePatch(s) >= patch;
            serverAcceptedVersions = s -> parseMajor(s) == major && parseMinor(s) >= minor;
        }

        private static String getNetProtocol() {
            return netProtocol;
        }

        private static int parseMajor(String version) {
            final StringBuilder builder = new StringBuilder();
            final char[] ver = version.toCharArray();
            for (int i = 0; i < ver.length; i++) {
                if (ver[i] == '.') return Integer.parseInt(builder.toString());
                builder.append(ver[i]);
            }
            return -1;
        }

        private static int parseMinor(String version) {
            final StringBuilder builder = new StringBuilder();
            final char[] ver = version.toCharArray();
            int i;
            for (i = 0; i < ver.length; i++) {
                if (ver[i] == '.') break;
            }
            i++;
            for (int j = i; j < ver.length; j++) {
                if (ver[j] == '.') return Integer.parseInt(builder.toString());
                builder.append(ver[j]);
            }
            return -1;
        }

        private static int parsePatch(String version) {
            final StringBuilder builder = new StringBuilder();
            final char[] ver = version.toCharArray();
            int i;
            boolean found = false;
            for (i = 0; i < ver.length; i++) {
                if (ver[i] == '.') {
                    if (found) break;
                    found = true;
                }
            }
            i++;
            for (int j = i; j < ver.length; j++) {
                if (ver[j] == '.') return Integer.parseInt(builder.toString());
                builder.append(ver[j]);
            }
            return -1;
        }
    }
}
