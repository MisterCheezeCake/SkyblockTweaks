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
package wtf.cheeze.sbt.hud.screen;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.ScreenRect;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.tooltip.HoveredTooltipPositioner;
import net.minecraft.client.gui.tooltip.Tooltip;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.ClickableWidget;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.HUD;
import wtf.cheeze.sbt.hud.bounds.Bounds;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.utils.CheezePair;
import wtf.cheeze.sbt.utils.Predicates;
import wtf.cheeze.sbt.utils.render.RenderUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static wtf.cheeze.sbt.hud.screen.EditorPopup.POPUP_Z;

public class HudScreen extends Screen {

    private final List<HUD> huds;
    private final Screen parent;
    @Nullable
    private HUD selectedElement = null;
    @Nullable
    private HUD hoveredElement = null;
    @Nullable
    private HUD selectedViaTheKeyboard = null;
    private ButtonWidget resetModeButton;
    private ButtonWidget cancelButton;
    private ButtonWidget resetButton;
    private Mode mode = Mode.DRAG;
    private int resetModeIndex = 0;
    private float offsetX = 0;
    private float offsetY = 0;
    private boolean textToggledOff = false;
    @Nullable
    private EditorPopup popup = null;

    public HudScreen(Text title, ArrayList<HUD> huds, Screen parent) {
        super(title);
        this.huds = huds.stream().filter(it -> it.shouldRender(true)).toList();
        this.parent = parent;
    }

    private static boolean macOS() {
        return System.getProperty("os.name").equalsIgnoreCase("mac os x");
    }

    public static boolean clickInBounds(HUD hud, double mouseX, double mouseY) {
        return clickInBounds(hud.getCurrentBounds(), mouseX, mouseY);
    }

    public static boolean clickInBounds(Bounds bounds, double mouseX, double mouseY) {
        return mouseX >= bounds.x && mouseX <= bounds.x + bounds.width && mouseY >= bounds.y && mouseY <= bounds.y + bounds.height;
    }

    private static float getMoveAmount() {
        return 2.0f / MinecraftClient.getInstance().getWindow().getScaledWidth();
    }

    private static void moveHorizontal(HUD hud, float amount) {
        var bounds = hud.getCurrentBoundsRelative();
        hud.updatePosition(bounds.x + amount, bounds.y);
    }

    private static void moveVertical(HUD hud, float amount) {
        var bounds = hud.getCurrentBoundsRelative();
        hud.updatePosition(bounds.x, bounds.y + amount);
    }

    @Override
    protected void init() {
        super.init();
        MinecraftClient mc = MinecraftClient.getInstance();
        var centerX = (mc.getWindow().getScaledWidth() / 2) - 50;

        cancelButton = new MatrixConsumingButton(Text.literal("Exit"), button -> {
            this.selectedElement = null;
            setMode(Mode.DRAG);
        }, centerX - 25, 95, 150, 20, (matrixStack -> matrixStack.translate(0, 0, 100)));

        resetButton = new MatrixConsumingButton(Text.literal("Reset"), button -> {
            this.huds.get(this.resetModeIndex).updatePosition(0.1f, 0.f);

        }, centerX - 25, 65, 150, 20, (matrixStack -> matrixStack.translate(0, 0, 100)));
        resetModeButton = new MatrixConsumingButton(this.huds.getFirst().getName().primaryName(), button -> {
            this.resetModeIndex++;
            if (this.resetModeIndex >= this.huds.size()) {
                this.resetModeIndex = 0;
            }
            resetModeButton.setMessage(this.huds.get(this.resetModeIndex).getName().primaryName());

        }, centerX - 25, 35, 150, 20, (matrixStack -> matrixStack.translate(0, 0, 100)));

        setMode(Mode.DRAG);
        this.addDrawableChild(cancelButton);
        this.addDrawableChild(resetButton);
        this.addDrawableChild(resetModeButton);

    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        drawInstructions(context);

        HUD hovered = null;
        for (HUD hud : huds) {
            boolean inBounds = clickInBounds(hud, mouseX, mouseY);
            hud.render(context, true, inBounds);
            if (inBounds) {
                hovered = hud;
                if (hasShiftDown()) {
                    this.setTooltip(Tooltip.of(hud.getName().primaryName()), HoveredTooltipPositioner.INSTANCE, false);
                }
            }
        }
        hoveredElement = hovered;
        if (hovered == null) {
            this.clearTooltip();
        }
    }

    @Override
    public void close() {
        MinecraftClient.getInstance().setScreen(parent);
        SBTConfig.HANDLER.save();
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        boolean b = super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        if (this.mode != Mode.DRAG) return b;
        if (selectedElement != null) {
            selectedElement.updatePosition(HUD.getRelativeX(mouseX - offsetX), HUD.getRelativeY(mouseY - offsetY));
        }
        return b;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        boolean b = super.mouseClicked(mouseX, mouseY, button);
        selectedViaTheKeyboard = null;
        for (HUD hud : huds) {
            if (clickInBounds(hud, mouseX, mouseY)) {
//                if (hasControlDown() || this.mode == Mode.TEXT) {
//                    enableTextMode(hud);
//                    return b;
//                }
                if (this.mode == Mode.DRAG) {
                    selectedElement = hud;
                    updateOffset(hud, mouseX, mouseY);
                }
                if (hasControlDown() || (this.mode == Mode.TEXT && !clickInBounds(popup.getBounds(), mouseX, mouseY))) {
                    this.addPopup(hud.getName().name(EditorPopup.WIDTH), (int) mouseX, (int) mouseY, getPopupWidgets(hud));
                    return b;
                }
            }
        }
        if (this.popup != null && !clickInBounds(this.popup.getBounds(), mouseX, mouseY)) {
            this.popup.remove();
            this.popup = null;
            this.setMode(Mode.DRAG);
        }
        return b;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean x = super.mouseReleased(mouseX, mouseY, button);
        if (this.mode == Mode.DRAG) selectedElement = null;
        return x;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontal, double vertical) {
        boolean b = super.mouseScrolled(mouseX, mouseY, horizontal, vertical);
        if (hoveredElement != null) {
            hoveredElement.updateScale(hoveredElement.getCurrentBoundsRelative().scale + (float) vertical / 10);
        }
        return b;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

        if (hasAltDown() && hasControlDown()) {
            textToggledOff = !textToggledOff;
        }

        if (keyCode == GLFW.GLFW_KEY_R && hasControlDown()) {
            setMode(Mode.RESET);
        }
        if (this.mode == Mode.DRAG) {
            if (this.hoveredElement != null) {
                switch (keyCode) {
                    case GLFW.GLFW_KEY_UP -> {
                        moveVertical(hoveredElement, -getMoveAmount());
                        selectedViaTheKeyboard = hoveredElement;
                    }
                    case GLFW.GLFW_KEY_DOWN -> {
                        moveVertical(hoveredElement, getMoveAmount());
                        selectedViaTheKeyboard = hoveredElement;
                    }
                    case GLFW.GLFW_KEY_LEFT -> {
                        moveHorizontal(hoveredElement, -getMoveAmount());
                        selectedViaTheKeyboard = hoveredElement;
                    }
                    case GLFW.GLFW_KEY_RIGHT -> {
                        moveHorizontal(hoveredElement, getMoveAmount());
                        moveHorizontal(hoveredElement, getMoveAmount());
                        selectedViaTheKeyboard = hoveredElement;
                    }
                    case GLFW.GLFW_KEY_EQUAL -> {
                        hoveredElement.updateScale(hoveredElement.getCurrentBoundsRelative().scale + 0.1f);
                        selectedViaTheKeyboard = hoveredElement;
                    }
                    case GLFW.GLFW_KEY_MINUS -> {
                        hoveredElement.updateScale(hoveredElement.getCurrentBoundsRelative().scale - 0.1f);
                        selectedViaTheKeyboard = hoveredElement;
                    }
                }
            } else if (this.selectedViaTheKeyboard != null) {
                switch (keyCode) {
                    case GLFW.GLFW_KEY_UP -> moveVertical(selectedViaTheKeyboard, -getMoveAmount());
                    case GLFW.GLFW_KEY_DOWN -> moveVertical(selectedViaTheKeyboard, getMoveAmount());
                    case GLFW.GLFW_KEY_LEFT -> moveHorizontal(selectedViaTheKeyboard, -getMoveAmount());
                    case GLFW.GLFW_KEY_RIGHT -> moveHorizontal(selectedViaTheKeyboard, getMoveAmount());
                    case GLFW.GLFW_KEY_EQUAL ->
                            selectedViaTheKeyboard.updateScale(selectedViaTheKeyboard.getCurrentBoundsRelative().scale + 0.1f);
                    case GLFW.GLFW_KEY_MINUS ->
                            selectedViaTheKeyboard.updateScale(selectedViaTheKeyboard.getCurrentBoundsRelative().scale - 0.1f);
                }
            }
        }
        if (keyCode == GLFW.GLFW_KEY_ESCAPE) {
            if (this.popup != null) {
                this.popup.remove();
                this.popup = null;
                this.setMode(Mode.DRAG);
                return true;
            }
            if (this.mode == Mode.RESET) {
                setMode(Mode.DRAG);
                return true;
            }
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    private void setMode(Mode newMode) {
        this.mode = newMode;
        if (newMode == Mode.DRAG) {
            cancelButton.visible = false;
            resetButton.visible = false;
            resetModeButton.visible = false;
        } else if (newMode == Mode.TEXT) {
            cancelButton.visible = false;
            resetButton.visible = false;
            resetModeButton.visible = false;
            selectedElement = null;
        } else if (newMode == Mode.RESET) {
            cancelButton.visible = true;
            resetButton.visible = true;
            resetModeButton.visible = true;
        }
    }

    private boolean shouldShowText() {
        if (hasAltDown()) return false;
        return !textToggledOff;
    }

    private void drawInstructions(DrawContext context) {
        var centerX = client.getWindow().getScaledWidth() / 2;
        context.getMatrices().push();
        context.getMatrices().translate(0, 0, 100);
        if (shouldShowText() && this.mode == Mode.DRAG) {
            context.drawCenteredTextWithShadow(client.textRenderer, "Drag or use the arrow keys to move items", centerX, 5, 0xFFFFFF);
            context.drawCenteredTextWithShadow(client.textRenderer, "Scroll or use the + and - keys to scale items", centerX, 15, 0xFFFFFF);
            context.drawCenteredTextWithShadow(client.textRenderer, "Press shift and hover to see the name of the item", centerX, 25, 0xFFFFFF);
            context.drawCenteredTextWithShadow(client.textRenderer, (macOS() ? "Command" : "Control") + " click for text mode/to edit anchor points", centerX, 35, 0xFFFFFF);
            context.drawCenteredTextWithShadow(client.textRenderer, (macOS() ? "Command" : "Control") + "+ R to enter Reset Mode", centerX, 45, 0xFFFFFF);
            context.drawCenteredTextWithShadow(client.textRenderer, "Hold " + (macOS() ? "option" : "alt") + " to hide this text or " + (macOS() ? "Cmd+Opt" : "Ctrl+Alt") + " to toggle it", centerX, 55, 0xFFFFFF);
        } else if (shouldShowText() && this.mode == Mode.TEXT) {
            context.drawCenteredTextWithShadow(client.textRenderer, "You are now in exact positioning mode", centerX, 5, 0xFFFFFF);
            context.drawCenteredTextWithShadow(client.textRenderer, "Enter the x and y positions in the text fields below", centerX, 15, 0xFFFFFF);
            context.drawCenteredTextWithShadow(client.textRenderer, "The number is relative, so 0 is fully up/left and 1 is fully down/right", centerX, 25, 0xFFFFFF);
            context.drawCenteredTextWithShadow(client.textRenderer, "Press the anchor button to change where the hud anchors itself", centerX, 35, 0xFFFFFF);
            context.drawCenteredTextWithShadow(client.textRenderer, "Hold " + (macOS() ? "option" : "alt") + " to hide this text or " + (macOS() ? "Cmd+Opt" : "Ctrl+Alt") + " to toggle it", centerX, 45, 0xFFFFFF);
        } else if (this.mode == Mode.RESET) {
            context.drawCenteredTextWithShadow(client.textRenderer, "You are now in reset mode", centerX, 5, 0xFFFFFF);
            context.drawCenteredTextWithShadow(client.textRenderer, "Press the reset button to reset the selected item to the default position", centerX, 15, 0xFFFFFF);
            context.drawCenteredTextWithShadow(client.textRenderer, "Press the first button to cycle between elements", centerX, 25, 0xFFFFFF);
        }
        context.getMatrices().pop();
    }

    private void updateOffset(HUD hud, double mouseX, double mouseY) {
        var bounds = hud.getCurrentBounds();
        var anchor = hud.INFO.getAnchorPoint.get();
        if (anchor == AnchorPoint.LEFT) {
            offsetX = (float) (mouseX - bounds.x);
        } else if (anchor == AnchorPoint.CENTER) {
            offsetX = (float) (mouseX - bounds.x - bounds.width / 2);
        } else if (anchor == AnchorPoint.RIGHT) {
            offsetX = (float) (mouseX - bounds.x - bounds.width);
        }
        offsetY = (float) (mouseY - bounds.y);
    }

    private List<CheezePair<String, ? extends ClickableWidget>> getPopupWidgets(HUD hud) {
        var x = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 70, 15, Text.literal("")) {
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                context.getMatrices().push();
                context.getMatrices().translate(0, 0, POPUP_Z);
                super.renderWidget(context, mouseX, mouseY, delta);
                context.getMatrices().pop();
            }
        };
        var y = new TextFieldWidget(MinecraftClient.getInstance().textRenderer, 0, 0, 70, 15, Text.literal("")) {
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                context.getMatrices().push();
                context.getMatrices().translate(0, 0, POPUP_Z);
                super.renderWidget(context, mouseX, mouseY, delta);
                context.getMatrices().pop();
            }
        };
        x.setTextPredicate(Predicates.ZERO_TO_ONE);
        y.setTextPredicate(Predicates.ZERO_TO_ONE);
        x.setChangedListener(s -> {
            if (s.isEmpty()) return;
            hud.updatePosition(Float.parseFloat(s), hud.getCurrentBoundsRelative().y);
        });
        y.setChangedListener(s -> {
            if (s.isEmpty()) return;
            hud.updatePosition(hud.getCurrentBoundsRelative().x, Float.parseFloat(s));
        });
        var bounds = hud.getCurrentBoundsRelative();
        x.setText("" + bounds.x);
        y.setText("" + bounds.y);

        SkyblockTweaks.LOGGER.info("X text: " + x.getText() + " Y text: " + y.getText());


        var scale = new DecimalSlider(0, 0, 0, 0, Text.literal(hud.INFO.getScale.get().toString().formatted("%.1f")), hud.INFO.getScale.get() / 3.0, 0.1, 3.0, 0.1, (val) -> hud.INFO.setScale.accept((float) (double) val)) {
            @Override
            public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta) {
                context.getMatrices().push();
                context.getMatrices().translate(0, 0, POPUP_Z);
                super.renderWidget(context, mouseX, mouseY, delta);
                context.getMatrices().pop();
            }
        };

        var anchor = new MatrixConsumingButton(Text.literal(hud.INFO.getAnchorPoint.get().name()), button -> {
            var anchorPoint = hud.INFO.getAnchorPoint.get();
            if (anchorPoint == AnchorPoint.LEFT) {
                hud.INFO.setAnchorPoint.accept(AnchorPoint.CENTER);
            } else if (anchorPoint == AnchorPoint.CENTER) {
                hud.INFO.setAnchorPoint.accept(AnchorPoint.RIGHT);
            } else if (anchorPoint == AnchorPoint.RIGHT) {
                hud.INFO.setAnchorPoint.accept(AnchorPoint.LEFT);
            }
            button.setMessage(Text.literal(hud.INFO.getAnchorPoint.get().name()));
        }, (matrixStack -> matrixStack.translate(0, 0, POPUP_Z)));

        if (!hud.supportsNonLeftAnchors) anchor.active = false;

        return List.of(new CheezePair<>("X", x), new CheezePair<>("Y", y), new CheezePair<>("Scale", scale), new CheezePair<>("Anchor", anchor)

        );


    }

    public void addPopup(Text title, int desiredX, int desiredY, List<CheezePair<String, ? extends ClickableWidget>> widgets) {
        this.setMode(Mode.TEXT);
        ScreenRect bounds;
        int i = 0;
        while (i < 1E6) { // prevent infinite loops if something goes wrong
            bounds = new ScreenRect(desiredX, desiredY, EditorPopup.WIDTH, EditorPopup.HEIGHT);
            RenderUtils.BreachResult breachResult = RenderUtils.isOffscreen(bounds);
            if (breachResult.fullyOnscreen()) {
                break;
            }
            if (breachResult.left()) {
                desiredX++;
            } else if (breachResult.right()) {
                desiredX--;
            }
            if (breachResult.top()) {
                desiredY++;
            } else if (breachResult.bottom()) {
                desiredY--;
            }
            i++;
        }
        if (this.popup != null) {
            this.popup.remove();
        }
        this.popup = new EditorPopup(this, desiredX, desiredY, title, widgets);

    }

    private enum Mode {
        DRAG, TEXT, RESET
    }

}
