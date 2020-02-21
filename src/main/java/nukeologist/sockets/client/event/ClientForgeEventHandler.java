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

package nukeologist.sockets.client.event;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.screen.inventory.CreativeScreen;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.items.ItemHandlerHelper;
import nukeologist.sockets.Sockets;
import nukeologist.sockets.api.SocketStackHandler;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.api.cap.IGem;
import nukeologist.sockets.api.cap.ISocketableItem;
import nukeologist.sockets.common.network.InsertGemPacket;
import nukeologist.sockets.common.network.Network;

import java.util.List;

@Mod.EventBusSubscriber(modid = SocketsAPI.ID, value = Dist.CLIENT)
public final class ClientForgeEventHandler {

    private static final ResourceLocation SOCKET = Sockets.modLoc("textures/gui/socket.png");

    @SubscribeEvent
    public static void onContainerForeground(GuiContainerEvent.DrawForeground event) {
        final ContainerScreen screen = event.getGuiContainer();
        final ItemStack holdingStack = screen.getMinecraft().player.inventory.getItemStack();
        if (holdingStack.isEmpty()) return;
        if (SocketsAPI.getGem(holdingStack).isPresent()) drawSockets(screen);
    }

    //TODO figure out what the int mousebutton means, and cancel if it doesn't match left click ?
    @SubscribeEvent //fires before IGuiEventListener#mouseReleased is handled. Cancel to bypass it.
    public static void onMouseReleased(GuiScreenEvent.MouseReleasedEvent.Pre event) {
        if (checkEvent(event)) return;

        //If it got here, then we can proceed with insertion.
        final Minecraft mc = event.getGui().getMinecraft();
        final ItemStack holdingStack = mc.player.inventory.getItemStack();
        final ItemStack copyStack = holdingStack.copy();
        copyStack.setCount(1);
        final Slot slot = ((ContainerScreen) event.getGui()).getSlotUnderMouse();
        final LazyOptional<ISocketableItem> socket = SocketsAPI.getSockets(slot.getStack());
        final LazyOptional<IGem> gem = SocketsAPI.getGem(holdingStack);
        final boolean accepts = gem.map(g -> socket.map(s -> s.accepts(g)).orElse(false)).orElse(false);
        final boolean canInsert = gem.map(g -> socket.map(s -> ItemHandlerHelper.insertItem(s.getStackHandler(), copyStack, true).isEmpty()).orElse(false)).orElse(false);
        if (accepts && canInsert) {
            if (!(event.getGui() instanceof CreativeScreen)) {
                //do not even bother sending if it is the creative menu, as the slotnumber is plain wrong on the server.
                Network.CHANNEL.sendToServer(new InsertGemPacket(slot.slotNumber));
            }
            event.setCanceled(true);
            //This is next part is to auto sync the client. If the info sent to the server was false, then this will just deceive the client.
            final ItemStack copy = holdingStack.split(1);
            socket.ifPresent(s -> {
                if (!ItemHandlerHelper.insertItem(s.getStackHandler(), copy, false).isEmpty()) {
                    holdingStack.grow(1); //should never reach this line.
                } else {
                    SocketsAPI.getGem(copy).ifPresent(g -> g.equipped(s, mc.player));
                }
            });
        }
    }

    private static boolean checkEvent(GuiScreenEvent.MouseInputEvent event) {
        if (!(event.getGui() instanceof ContainerScreen)) return true;
        final Minecraft mc = event.getGui().getMinecraft();
        if (mc.player == null) return true;
        final ItemStack holdingStack = mc.player.inventory.getItemStack();
        if (holdingStack.isEmpty()) return true;
        final ContainerScreen screen = (ContainerScreen) event.getGui();
        final Slot slot = screen.getSlotUnderMouse();
        if (slot == null) return true;
        final ItemStack slotStack = slot.getStack();
        if (slotStack.isEmpty()) return true;
        final LazyOptional<ISocketableItem> socket = SocketsAPI.getSockets(slotStack);
        if (!socket.isPresent()) return true;
        final LazyOptional<IGem> gem = SocketsAPI.getGem(holdingStack);
        if (!gem.isPresent()) return true;
        return false;
    }

    @SubscribeEvent
    public static void onTooltipEvent(ItemTooltipEvent event) {
        final ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        if (!SocketsAPI.getSockets(stack).isPresent()) return;
        final List<ITextComponent> list = event.getToolTip();
        SocketsAPI.getSockets(stack).map(ISocketableItem::getStackHandler)
                .ifPresent(h -> h.forEach(gem -> list.addAll(gem.getExtraTooltip())));
    }

    private static void drawSockets(ContainerScreen screen) {
        final List<Slot> slots = screen.getContainer().inventorySlots;
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.disableDepthTest();
        for (final Slot slot : slots) {
            final ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;
            final LazyOptional<ISocketableItem> socket = SocketsAPI.getSockets(stack);
            socket.ifPresent(s -> {
                screen.getMinecraft().getTextureManager().bindTexture(SOCKET);
                final int size = s.getSlots();
                //int x0, int y0, int z, float u0, float v0, int width, int height, int textureHeight, int textureWidth
                AbstractGui.blit(slot.xPos, slot.yPos, 300, (size - 1) * 16, 0, 16, 16, 16, 64);
                renderSocketGems(screen.getMinecraft(), s, slot, 0.8f); //maybe change the factor value in config? Also 16 because that's the size of an item.
            });
        }
        RenderSystem.enableDepthTest();
    }

    //Yes, while hardcoded, more in the same slot would probably be a nightmare for the user. Hardcoded for the socket texture.
    private static void renderSocketGems(final Minecraft mc, final ISocketableItem item, final Slot slot, final float factor) {
        final ItemRenderer renderer = mc.getItemRenderer();
        final SocketStackHandler handler = item.getStackHandler();
        final int size = item.getSlots();
        final ItemStack first = handler.getStackInSlot(0);
        switch (size) {
            case 1:
                if (first.isEmpty()) return;
                renderSocketGem(renderer, first, slot.xPos, slot.yPos, factor);
                break;
            case 2:
                final ItemStack second = handler.getStackInSlot(1);
                if (!first.isEmpty()) renderSocketGem(renderer, first, slot.xPos - 3, slot.yPos + 3, factor);
                if (!second.isEmpty()) renderSocketGem(renderer, second, slot.xPos + 3, slot.yPos - 3, factor);
                break;
            case 3:
                final ItemStack secondd = handler.getStackInSlot(1); //big brain
                final ItemStack third = handler.getStackInSlot(2);
                if (!first.isEmpty()) renderSocketGem(renderer, first, slot.xPos - 4, slot.yPos - 4, factor);
                if (!secondd.isEmpty()) renderSocketGem(renderer, secondd, slot.xPos + 4, slot.yPos - 4, factor);
                if (!third.isEmpty()) renderSocketGem(renderer, third, slot.xPos, slot.yPos + 4, factor);
                break;
            case 4:
            default:
                final ItemStack seconddd = handler.getStackInSlot(1); //EPIC big brain
                final ItemStack thirdd = handler.getStackInSlot(2);
                final ItemStack fourth = handler.getStackInSlot(3);
                if (!first.isEmpty()) renderSocketGem(renderer, first, slot.xPos - 4, slot.yPos - 4, factor);
                if (!seconddd.isEmpty()) renderSocketGem(renderer, seconddd, slot.xPos + 4, slot.yPos - 4, factor);
                if (!thirdd.isEmpty()) renderSocketGem(renderer, thirdd, slot.xPos - 4, slot.yPos + 4, factor);
                if (!fourth.isEmpty()) renderSocketGem(renderer, fourth, slot.xPos + 4, slot.yPos + 4, factor);
                break;
        }
    }

    private static void renderSocketGem(final ItemRenderer renderer, final ItemStack stack, final int x, final int y, final float factor) {
        RenderSystem.pushMatrix();
        final float previous = renderer.zLevel;
        renderer.zLevel = 200f;
        RenderSystem.translatef(x + ((16 - 16 * factor) / 2), y + ((16 - 16 * factor) / 2), 0F);
        RenderSystem.scalef(factor, factor, 1f);
        renderer.renderItemAndEffectIntoGUI(stack, 0, 0);
        renderer.zLevel = previous;
        RenderSystem.popMatrix();
    }
}
