package com.teamresourceful.resourcefulbees.centrifuge.client.components.controlpanels.terminal;

import com.mojang.blaze3d.vertex.PoseStack;
import com.teamresourceful.resourcefulbees.centrifuge.client.components.controlpanels.AbstractControlPanel;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.CentrifugeTerminalScreen;
import com.teamresourceful.resourcefulbees.centrifuge.client.screens.CentrifugeTextures;
import com.teamresourceful.resourcefulbees.client.utils.TextUtils;
import com.teamresourceful.resourcefulbees.common.lib.constants.translations.CentrifugeTranslations;
import com.teamresourceful.resourcefulbees.client.util.TextUtils;
import com.teamresourceful.resourcefulbees.common.lib.enums.ControlPanelTabs;
import com.teamresourceful.resourcefullib.client.utils.RenderUtils;
import org.jetbrains.annotations.NotNull;

public class TerminalControlPanel extends AbstractControlPanel {

    public TerminalControlPanel(int x, int y, CentrifugeTerminalScreen terminalScreen) {
        super(x, y, terminalScreen);
    }

    @Override
    protected void init() {
        createControlPanelDisplayTab(y+18, ControlPanelTabs.HOME);
        createControlPanelDisplayTab(y+32, ControlPanelTabs.INPUTS);
        createControlPanelDisplayTab(y+46, ControlPanelTabs.ITEM_OUTPUTS);
        createControlPanelDisplayTab(y+60, ControlPanelTabs.FLUID_OUTPUTS);
        createControlPanelDisplayTab(y+74, ControlPanelTabs.FILTERS);
    }

    @Override
    public void render(@NotNull PoseStack stack, int mouseX, int mouseY, float partialTicks) {
        RenderUtils.bindTexture(CentrifugeTextures.COMPONENTS);
        blit(stack, x, y, 75, 165, 75, 91);
        TextUtils.tf12DrawCenteredStringNoShadow(stack, CentrifugeTranslations.TERMINAL_TAB, x+37f, y+6.5f, TextUtils.FONT_COLOR_1);
        super.render(stack, mouseX, mouseY, partialTicks);
    }
}
