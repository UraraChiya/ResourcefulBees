package com.teamresourceful.resourcefulbees.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.teamresourceful.resourcefulbees.common.tileentity.HoneyTankTileEntity;
import com.teamresourceful.resourcefulbees.common.utils.CubeModel;
import com.teamresourceful.resourcefulbees.common.utils.RenderCuboid;
import net.minecraft.client.renderer.Atlases;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.fluids.FluidStack;
import org.jetbrains.annotations.NotNull;

public class RenderHoneyTank extends TileEntityRenderer<HoneyTankTileEntity> {

    public RenderHoneyTank(TileEntityRendererDispatcher renderer) {
        super(renderer);
    }

    @Override
    public void render(HoneyTankTileEntity tile, float partialTick, @NotNull MatrixStack matrix, @NotNull IRenderTypeBuffer renderer, int light, int overlayLight) {
        if (tile.getLevel() == null) return;
        FluidStack stack = tile.getFluidTank().getFluid();
        if (!stack.isEmpty()) {
            int level = tile.getFluidLevel();
            int color = stack.getFluid().getAttributes().getColor();
            ResourceLocation stillTexture = stack.getFluid().getAttributes().getStillTexture();
            IVertexBuilder builder = renderer.getBuffer(Atlases.translucentCullBlockSheet());
            Vector3f start = new Vector3f(0.1875f, 0.0625f, 0.1875f);
            Vector3f end = new Vector3f(0.8125f, 0.0625f + ((float) level / 100.0F) * 0.875f, 0.8125f);
            CubeModel model = new CubeModel(start, end);
            model.setTextures(stillTexture);
            RenderCuboid.INSTANCE.renderCube(model, matrix, builder, color, light, overlayLight);
        }
    }
}