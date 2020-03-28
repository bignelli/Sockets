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

package nukeologist.sockets.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.screen.inventory.ContainerScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import nukeologist.sockets.Sockets;
import nukeologist.sockets.api.SocketStackHandler;
import nukeologist.sockets.api.SocketsAPI;
import nukeologist.sockets.api.cap.IGem;
import nukeologist.sockets.common.container.SocketRemoverContainer;
import nukeologist.sockets.common.network.Network;
import nukeologist.sockets.common.network.RemoveSocketPacket;
import nukeologist.sockets.common.tileentity.SocketRemoverTileEntity;
import nukeologist.sockets.common.util.StringTranslations;

public class SocketRemoverScreen extends ContainerScreen<SocketRemoverContainer> {

    private static final ResourceLocation TEXTURE = Sockets.modLoc("textures/gui/socket_remover.png");

    public SocketRemoverScreen(SocketRemoverContainer screenContainer, PlayerInventory inv, ITextComponent titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        this.renderBackground();
        super.render(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void init() {
        super.init();
        this.addButton(new Button(this.guiLeft + 20, this.guiTop + 10, 64, 20, I18n.format(StringTranslations.SOCKET_REMOVER_BUTTON), this::requestRemoval));
    }

    private void requestRemoval(final Button button) {
        Network.CHANNEL.sendToServer(new RemoveSocketPacket(this.container.pos.getX(), this.container.pos.getY(), this.container.pos.getZ()));
        final ItemStack stack = container.getSlot(0).getStack();
        if (stack.isEmpty()) return;
        SocketsAPI.getSockets(stack).ifPresent(socket -> {
            final SocketStackHandler handler = socket.getStackHandler();
            final IGem[] gems = new IGem[handler.getSlots()];
            for (int i = 0; i < handler.getSlots(); i++) {
                gems[i] = SocketsAPI.getGem(handler.getStackInSlot(i)).orElse(null);
            }
            final SocketRemoverTileEntity te = this.container.getTile();
            if (SocketRemoverTileEntity.allGemsAccept(gems, socket, this.minecraft.player) && te != null && this.minecraft.player.isCreative() || te.getTotalXp(gems, socket) <= this.minecraft.player.experienceLevel) {
                final int slots = handler.getSlots();
                for (int i = 1; i < 5 && i - 1 < slots; i++) {
                    final ItemStack gem = handler.getStackInSlot(i - 1);
                    if (!gem.isEmpty()) {
                        handler.setStackInSlot(i - 1, ItemStack.EMPTY);
                        SocketsAPI.getGem(gem).ifPresent(g -> g.unequipped(socket, this.minecraft.player));
                    }
                }
            }
        });
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        RenderSystem.color4f(1f, 1f, 1f, 1f);
        this.minecraft.getTextureManager().bindTexture(TEXTURE);
        final int x = (this.width - this.xSize) / 2;
        final int y = (this.height - this.ySize) / 2;
        this.blit(x, y, 0, 0, this.xSize, this.ySize);
    }

    @Override   //Mostly copied from Anvil Screen
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        final SocketRemoverTileEntity te = this.container.getTile();
        final int xp = te == null ? 0 : te.getTotalXp();
        if (xp <= 0) return;
        final String s = I18n.format("container.repair.cost", xp);
        int k = this.xSize - 8 - this.font.getStringWidth(s) - 2;
        int j = 8453920;
        if (minecraft.player != null && !minecraft.player.isCreative() && minecraft.player.experienceLevel < xp) {
            j = 16736352;
        }
        fill(k - 2, 67, this.xSize - 8, 79, 1325400064);
        this.font.drawStringWithShadow(s, (float)k, 69.0F, j);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
