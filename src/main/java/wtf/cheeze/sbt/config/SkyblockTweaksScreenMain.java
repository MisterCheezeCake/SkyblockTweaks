/*
 * Copyright (C) 2024 MisterCheezeCake
 *
 * This file is part of SkyblockTweaks.
 *
 * SkyblockTweaks is free software: you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation, either
 * version 3 of the License, or (at your option) any later version.
 *
 * SkyblockTweaks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with SkyblockTweaks. If not, see <https://www.gnu.org/licenses/>.
 */
package wtf.cheeze.sbt.config;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.ConfirmLinkScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.hud.HudManager;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.render.RenderUtils;
import wtf.cheeze.sbt.hud.screen.HudScreen;

public class SkyblockTweaksScreenMain extends Screen {
    public static final ResourceLocation ICON = ResourceLocation.fromNamespaceAndPath("skyblocktweaks", "icon.png");
    private final Screen parent;

    private Button hudButton;
    public SkyblockTweaksScreenMain(Screen parent) {
        super(Component.literal("SkyBlockTweaks"));
        this.parent = parent;
    }
    @Override
    @SuppressWarnings("MagicNumber")
    public void init() {
        Minecraft mc = Minecraft.getInstance();
        var centerx = mc.getWindow().getGuiScaledWidth() / 2;
        var leftColumn = centerx - 100;
        var rightColumn = centerx + 5;
        Button configButton = Button.builder(Component.literal("Open Config"), button -> {
            mc.execute(() -> mc.setScreen(SBTConfig.getScreen(this)));
        }).bounds(centerx - 100, 55, 200, 20).build();
        hudButton = Button.builder(Component.literal("Edit HUD Positions"), button -> {
            mc.execute(() -> mc.setScreen(new HudScreen(Component.literal("SkyBlockTweaks"), HudManager.HUDS, this)));
        }).bounds(centerx - 100, 85, 200, 20).build();
        Button modrinthButton = Button.builder(Component.literal("Modrinth"), button -> {
            ConfirmLinkScreen.confirmLinkNow(this, "https://modrinth.com/mod/sbt", true);
        }).bounds(leftColumn, 115, 95, 20).build();
        Button githubButton = Button.builder(Component.literal("GitHub"), button -> {
            ConfirmLinkScreen.confirmLinkNow(this, "https://github.com/MisterCheezeCake/SkyblockTweaks", true);
        }).bounds(rightColumn, 115, 95, 20).build();
        Button discordButton = Button.builder(Component.literal("Discord"), button -> {
            ConfirmLinkScreen.confirmLinkNow(this, "https://discord.gg/YH3hw926hz", true);
        }).bounds(leftColumn, 145, 95, 20).build();
        Button legalButton = Button.builder(Component.literal("Legal"), button -> {
            ConfirmLinkScreen.confirmLinkNow(this, "https://github.com/MisterCheezeCake/SkyblockTweaks/blob/main/OPENSOURCE.md", true);
        }).bounds(rightColumn, 145, 95, 20).build();
        Button closeButton = Button.builder(Component.literal("Close"), button -> {
            mc.execute(() -> mc.setScreen(parent));
        }).bounds(centerx - 100, 175, 200, 20).build();
        this.addRenderableWidget(configButton);
        this.addRenderableWidget(hudButton);
        this.addRenderableWidget(modrinthButton);
        this.addRenderableWidget(githubButton);
        this.addRenderableWidget(discordButton);
        this.addRenderableWidget(legalButton);
        this.addRenderableWidget(closeButton);
        if (Minecraft.getInstance().level == null) {
            hudButton.active = false;
            hudButton.setTooltip(Tooltip.create(Component.literal("Join a world/server to edit HUD Positions")));
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        Minecraft mc = Minecraft.getInstance();
        var centerX = mc.getWindow().getGuiScaledWidth() / 2;
        super.render(guiGraphics, mouseX, mouseY, delta);
        RenderUtils.drawCenteredText(guiGraphics, Component.literal("SkyblockTweaks"), centerX, 3, Colors.SBT_GREEN, true, 2.5f);
        RenderUtils.drawCenteredText(guiGraphics, Component.literal("v" + SkyblockTweaks.VERSION.getVersionString()), centerX, 25, Colors.WHITE, true);
        RenderUtils.drawCenteredText(guiGraphics, Component.literal("By MisterCheezeCake"), centerX, 36, Colors.RED, true);
    }
}
