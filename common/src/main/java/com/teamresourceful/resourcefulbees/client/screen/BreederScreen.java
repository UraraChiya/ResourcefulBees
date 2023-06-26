package com.teamresourceful.resourcefulbees.client.screen;

import com.teamresourceful.resourcefulbees.common.lib.constants.BreederConstants;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.menus.BreederMenu;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

@Environment(EnvType.CLIENT)
public class BreederScreen extends AbstractContainerScreen<BreederMenu> {

    private static final ResourceLocation BACKGROUND = new ResourceLocation(ModConstants.MOD_ID, "textures/gui/apiary/apiary_breeder_gui.png");

    public BreederScreen(BreederMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
        preInit();
    }

    protected void preInit(){
        this.imageWidth = 198;
        this.imageHeight = 148 + BreederConstants.NUM_OF_BREEDERS * 20;
        this.inventoryLabelX = 30;
        this.inventoryLabelY = 95;
        this.titleLabelX = 30;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(graphics);
        super.render(graphics, mouseX, mouseY, partialTicks);
        this.renderTooltip(graphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(@NotNull GuiGraphics graphics, float partialTicks, int mouseX, int mouseY) {
        graphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, 198, 188);
        int y = this.topPos + 21;
        for (int i = 0; i < BreederConstants.NUM_OF_BREEDERS; i++) {
            int width = (int)(((float)menu.times.get(i) / menu.endTimes.get(i)) * 118);
            graphics.blit(BACKGROUND, this.leftPos+51, y, 0, 246, width, 10);
            y+= 20;
        }
    }
}
