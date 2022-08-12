package com.teamresourceful.resourcefulbees.client.utils;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.teamresourceful.resourcefulbees.ResourcefulBees;
import com.teamresourceful.resourcefulbees.common.entity.passive.CustomBeeEntity;
import com.teamresourceful.resourcefulbees.common.lib.constants.ModConstants;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.FontResourceManagerAccessor;
import com.teamresourceful.resourcefulbees.common.mixin.accessors.MinecraftAccessor;
import com.teamresourceful.resourcefulbees.common.utils.Bound;
import com.teamresourceful.resourcefulbees.common.utils.Vec2i;
import com.teamresourceful.resourcefullib.client.CloseablePoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.font.FontSet;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public final class RenderUtils {

    public static final int FONT_COLOR_1 = 0xffc9c9c9;
    public static final int FONT_COLOR_2 = 0xff2cafc7;
    private static final FontSet FONT_8 =  ((FontResourceManagerAccessor) ((MinecraftAccessor) Minecraft.getInstance()).getFontManager()).getFontSets().get(new ResourceLocation(ResourcefulBees.MOD_ID, "jetbrains_mono_8"));
    public static final Font TERMINAL_FONT_8 = new Font(resourceLocation -> FONT_8);
    private static final FontSet FONT_12 =  ((FontResourceManagerAccessor) ((MinecraftAccessor) Minecraft.getInstance()).getFontManager()).getFontSets().get(new ResourceLocation(ResourcefulBees.MOD_ID, "jetbrains_mono_12"));
    public static final Font TERMINAL_FONT_12 = new Font(resourceLocation -> FONT_12);

    private RenderUtils() {
        throw new IllegalStateException(ModConstants.UTILITY_CLASS);
    }

    public static void renderEntity(PoseStack stack, Entity entity, float x, float y, float rotation, float renderScale) {
        float scaledSize;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player != null) entity.tickCount = mc.player.tickCount;
        if (entity instanceof CustomBeeEntity customBee) {
            scaledSize = 20 / customBee.getRenderData().sizeModifier();
        } else {
            scaledSize = 20 / (Math.max(entity.getBbWidth(), entity.getBbHeight()));
        }
        if (mc.player != null) {
            try (var ignored = new CloseablePoseStack(stack)) {
                stack.translate(10, 20 * renderScale, 0.5);
                stack.translate(x, y, 1);
                stack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
                stack.translate(0, 0, 100);
                stack.scale(-(scaledSize * renderScale), (scaledSize * renderScale), 30);
                stack.mulPose(Vector3f.YP.rotationDegrees(rotation));
                EntityRenderDispatcher entityRenderer = mc.getEntityRenderDispatcher();
                MultiBufferSource.BufferSource buffer = mc.renderBuffers().bufferSource();
                entityRenderer.render(entity, 0, 0, 0.0D, mc.getFrameTime(), 1, stack, buffer, LightTexture.FULL_BRIGHT);
                buffer.endBatch();
            }
        }
    }

    public static void drawFluid(PoseStack matrix, int height, int width, FluidStack fluidStack, int x, int y, int blitOffset) {
        Minecraft mc = Minecraft.getInstance();
        bindTexture(InventoryMenu.BLOCK_ATLAS);
        IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStack.getFluid());
        TextureAtlasSprite sprite = mc.getTextureAtlas(InventoryMenu.BLOCK_ATLAS).apply(props.getStillTexture(fluidStack));
        int remainder = height % 16;
        int splits = (height - remainder) / 16;
        if (remainder != 0) splits++;
        int fluidColor = props.getTintColor(fluidStack);

        RenderSystem.setShaderColor(((fluidColor >> 16) & 0xFF)/ 255.0F, ((fluidColor >> 8) & 0xFF)/ 255.0F, (fluidColor & 0xFF)/ 255.0F,  ((fluidColor >> 24) & 0xFF)/ 255.0F);
        for (int i = 0; i < splits; i++)
            GuiComponent.blit(matrix,x, y + (i * 16), blitOffset, width, i+1 == splits && remainder != 0 ? remainder : 16, sprite);
    }

    public static void bindTexture(ResourceLocation location) {
        com.teamresourceful.resourcefullib.client.utils.RenderUtils.bindTexture(location);
    }

    public static void drawCenteredString(Font font, PoseStack stack, Component component, int x, int y, int color, boolean shadow) {
        if (shadow) {
            Gui.drawCenteredString(stack, font, component, x, y, color);
        } else {
            x = x - (font.width(component) / 2);
            font.draw(stack, component, x, y, color);
        }
    }

    public static Bound getScissorBounds(Minecraft minecraft, PoseStack stack, int x, int y, int width, int height) {

        float guiScale = (float)minecraft.getWindow().getGuiScale();
        Vec2i translation = getTranslation(stack);
        float translationX = translation.x() * guiScale;
        float translationY = translation.z() * guiScale;
        return new Bound((int) (translationX+ x * guiScale), (int) (Minecraft.getInstance().getWindow().getHeight() - y * guiScale - translationY - height * guiScale), (int) (width * guiScale), (int) (height * guiScale));
    }

    public static Vec2i getTranslation(PoseStack stack) {
        Matrix4f pose = stack.last().pose();
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
        pose.store(floatBuffer);
        return new Vec2i((int)floatBuffer.get(12), (int)floatBuffer.get(13));
    }

    public static void renderItem(PoseStack stack, ItemStack item, int x, int y) {
        Vec2i translation = getTranslation(stack);
        Minecraft.getInstance().getItemRenderer().renderGuiItem(item, translation.x() + x, translation.z() + y);
    }
}