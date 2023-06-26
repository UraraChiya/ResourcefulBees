package com.teamresourceful.resourcefulbees.client.screen.locator;

import com.teamresourceful.resourcefulbees.client.util.ClientRenderUtils;
import com.teamresourceful.resourcefulbees.common.entities.CustomBeeEntityType;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ObjectSelectionList;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

public class BeeLocatorEntry extends ObjectSelectionList.Entry<BeeLocatorEntry> {

    private final Consumer<BeeLocatorEntry> selector;
    private final Entity displayEntity;
    private final Component displayName;

    public BeeLocatorEntry(Consumer<BeeLocatorEntry> selector, @NotNull Entity displayEntity, @NotNull Component displayName) {
        this.selector = selector;
        this.displayEntity = displayEntity;
        this.displayName = displayName;
    }

    public Component getDisplayName() {
        return displayName;
    }

    public String getType() {
        return displayEntity.getType() instanceof CustomBeeEntityType<?> customBeeEntityType ? customBeeEntityType.getBeeType() : null;
    }

    @Override
    public @NotNull Component getNarration() {
        return displayName;
    }

    @Override
    public void render(@NotNull GuiGraphics graphics, int id, int top, int left, int width, int height, int mouseX, int mouseY, boolean hovered, float partialTick) {
        Minecraft instance = Minecraft.getInstance();
        Font font = instance.font;
        graphics.drawString(font, displayName, left + 30, top + 5, 10526880);
        try (var ignored = new CloseablePoseStack(graphics)) {
            ClientRenderUtils.renderEntity(graphics, this.displayEntity, left + 5, top + 5, 45F, 1f);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        this.selector.accept(this);
        return false;
    }
}
