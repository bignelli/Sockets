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

package nukeologist.sockets.client;

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
import net.minecraftforge.client.event.GuiContainerEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.items.ItemHandlerHelper;
import nukeologist.sockets.Sockets;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.api.cap.IGem;
import nukeologist.sockets.api.cap.ISocketableItem;
import nukeologist.sockets.common.network.InsertGemPacket;
import nukeologist.sockets.common.network.Network;

import java.util.List;

public enum ClientForgeEventHandler {

    INSTANCE;

    private static final ResourceLocation SOCKET = Sockets.modLoc("textures/gui/socket.png");

    @SubscribeEvent
    public void onContainerForeground(GuiContainerEvent.DrawForeground event) {
        final ContainerScreen screen = event.getGuiContainer();
        final ItemStack holdingStack = screen.getMinecraft().player.inventory.getItemStack();
        if (holdingStack.isEmpty()) return;
        if (SocketsAPI.getGem(holdingStack).isPresent()) drawSockets(screen);
    }

    //TODO figure out what the int mousebutton means, and cancel if it doesn't match left click ?
    @SubscribeEvent //fires before IGuiEventListener#mouseReleased is handled. Cancel to bypass it.
    public void onMouseReleased(GuiScreenEvent.MouseReleasedEvent.Pre event) {
        if (checkEvent(event)) return;

        //If it got here, then we can proceed with insertion.
        final ItemStack holdingStack = event.getGui().getMinecraft().player.inventory.getItemStack();
        final ItemStack copyStack = holdingStack.copy();
        copyStack.setCount(1);
        final Slot slot = ((ContainerScreen) event.getGui()).getSlotUnderMouse();
        final LazyOptional<ISocketableItem> socket = SocketsAPI.getSockets(slot.getStack());
        final LazyOptional<IGem> gem = SocketsAPI.getGem(holdingStack);
        final boolean accepts = gem.map(g -> socket.map(s -> s.accepts(g)).orElse(false)).orElse(false);
        final boolean canInsert = gem.map(g -> socket.map(s -> ItemHandlerHelper.insertItem(s.getStackHandler(), copyStack, true).isEmpty()).orElse(false)).orElse(false);
        if (accepts && canInsert) {
            event.setCanceled(true);
            Network.CHANNEL.sendToServer(new InsertGemPacket(getSlotNumber((ContainerScreen) event.getGui(), slot)));
            //This is next part is to auto sync the client. If the info sent to the server was false, then this will just deceive the client.
            final ItemStack copy = holdingStack.split(1);
            socket.ifPresent(s -> {
                if (!ItemHandlerHelper.insertItem(s.getStackHandler(), copy, false).isEmpty()) {
                    holdingStack.grow(1); //should never reach this line.
                }
            });
        }
    }

    //Creative screen has a finicky container (OnlyIn client!)
    private int getSlotNumber(final ContainerScreen screen, final Slot slot) {
        return screen instanceof CreativeScreen ? slot.slotNumber - (screen.getContainer()).inventorySlots.size() + 9 + 36 : slot.slotNumber;
    }

    private boolean checkEvent(GuiScreenEvent.MouseInputEvent event) {
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
    public void onTooltipEvent(ItemTooltipEvent event) {
        final ItemStack stack = event.getItemStack();
        if (stack.isEmpty()) return;
        if (!SocketsAPI.getSockets(stack).isPresent()) return;
        final List<ITextComponent> list = event.getToolTip();
        SocketsAPI.getSockets(stack).map(ISocketableItem::getStackHandler)
                .ifPresent(h -> h.forEach(gem -> list.addAll(gem.getExtraTooltip())));
    }

    private void drawSockets(ContainerScreen screen) {
        final List<Slot> slots = screen.getContainer().inventorySlots;
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        RenderSystem.disableDepthTest();
        for (final Slot slot : slots) {
            final ItemStack stack = slot.getStack();
            if (stack.isEmpty()) continue;
            final LazyOptional<ISocketableItem> socket = SocketsAPI.getSockets(stack);
            socket.ifPresent(s -> {
                screen.getMinecraft().getTextureManager().bindTexture(SOCKET);
                //int x0, int y0, int z, float u0, float v0, int width, int height, int textureHeight, int textureWidth
                AbstractGui.blit(slot.xPos, slot.yPos, 300, 0, 0, 16, 16, 16, 16);
                final ItemStack test = s.getStackHandler().getStackInSlot(0);
                if (!test.isEmpty()) {
                    final ItemRenderer renderer = screen.getMinecraft().getItemRenderer();
                    RenderSystem.pushMatrix();
                    final float previous = renderer.zLevel;
                    renderer.zLevel = 200f;
                    final float factor = 0.8f; //maybe change this value as in config? Also 16 because that's the size of an item.
                    RenderSystem.translatef(slot.xPos + ((16 - 16 * factor) / 2), slot.yPos + ((16 - 16 * factor) / 2), 0F);
                    RenderSystem.scalef(factor, factor, 1f);
                    renderer.renderItemAndEffectIntoGUI(test, 0, 0);
                    renderer.zLevel = previous;
                    RenderSystem.popMatrix();
                }
            });
        }
        RenderSystem.enableDepthTest();
    }
}
