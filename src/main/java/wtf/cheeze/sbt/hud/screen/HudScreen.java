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

import dev.isxander.yacl3.gui.utils.KeyUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.navigation.ScreenRectangle;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.glfw.GLFW;
import wtf.cheeze.sbt.SkyblockTweaks;
import wtf.cheeze.sbt.config.SBTConfig;
import wtf.cheeze.sbt.hud.HUD;
import wtf.cheeze.sbt.hud.bounds.Bounds;
import wtf.cheeze.sbt.hud.utils.AnchorPoint;
import wtf.cheeze.sbt.utils.CheezePair;
import wtf.cheeze.sbt.utils.MultiUtils;
import wtf.cheeze.sbt.utils.errors.ErrorHandler;
import wtf.cheeze.sbt.utils.errors.ErrorLevel;
import wtf.cheeze.sbt.utils.render.ScreenListener;
import wtf.cheeze.sbt.utils.text.Predicates;
import wtf.cheeze.sbt.utils.render.Colors;
import wtf.cheeze.sbt.utils.render.RenderUtils;

//?if >1.21.8 {

import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.input.MouseButtonEvent;


//?}

import java.util.ArrayList;
import java.util.List;

public class HudScreen extends Screen {
    private static final float RELATIVE_MOVE_AMOUNT = 2.0f;
    private final List<HUD> huds;
    private final Screen parent;
    @Nullable
    private HUD selectedElement = null;
    @Nullable
    private HUD hoveredElement = null;
    @Nullable
    private HUD selectedViaTheKeyboard = null;
    private Button resetModeButton;
    private Button cancelButton;
    private Button resetButton;
    private Mode mode = Mode.DRAG;
    private int resetModeIndex = 0;
    private float offsetX = 0;
    private float offsetY = 0;
    private boolean textToggledOff = false;
    @Nullable
    private EditorPopup popup = null;

    public HudScreen(Component title, ArrayList<HUD> huds, Screen parent) {
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
        return RELATIVE_MOVE_AMOUNT / Minecraft.getInstance().getWindow().getGuiScaledWidth();
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
    @SuppressWarnings("MagicNumber")
    protected void init() {
        super.init();
        Minecraft mc = Minecraft.getInstance();
        var centerX = (mc.getWindow().getGuiScaledWidth() / 2) - 50;

        cancelButton = new ConstructableButton(Component.literal("Exit"), button -> {
            this.selectedElement = null;
            setMode(Mode.DRAG);
        }, centerX - 25, 95, 150, 20);

        resetButton = new ConstructableButton(Component.literal("Reset"), button -> this.huds.get(this.resetModeIndex).updatePosition(0.1f, 0.f), centerX - 25, 65, 150, 20);
        resetModeButton = new ConstructableButton(this.huds.getFirst().getName().primaryName(), button -> {
            this.resetModeIndex++;
            if (this.resetModeIndex >= this.huds.size()) {
                this.resetModeIndex = 0;
            }
            resetModeButton.setMessage(this.huds.get(this.resetModeIndex).getName().primaryName());

        }, centerX - 25, 35, 150, 20);

        setMode(Mode.DRAG);
        this.addRenderableWidget(cancelButton);
        this.addRenderableWidget(resetButton);
        this.addRenderableWidget(resetModeButton);
        //this.addWidget(new EventListener());
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float delta) {
        super.render(guiGraphics, mouseX, mouseY, delta);

        HUD hovered = null;
        boolean drawnTooltip = false;
        for (HUD hud : huds) {
            boolean inBounds = clickInBounds(hud, mouseX, mouseY);
            try {
                hud.render(guiGraphics, true, inBounds);
            } catch (Exception e) {
                ErrorHandler.handle(e, "Failed to render " + hud.getName().pName() + "in HudScreen", ErrorLevel.WARNING);
            }
            if (inBounds) {
                hovered = hud;
                if (MultiUtils.shiftDown()) {
//                    context.drawTooltip(hud.getName().primaryName(),);
                    if (!drawnTooltip) guiGraphics.setTooltipForNextFrame(Minecraft.getInstance().font, hud.getName().primaryName(), mouseX, mouseY );
                    drawnTooltip = true;
                    //this.setTooltip(Tooltip.of(hud.getName().primaryName()), HoveredTooltipPositioner.INSTANCE.getPosition(), false);
                }
            }
        }
        hoveredElement = hovered;
//        if (hovered == null) {
//            this.clearTooltip();
//        }

        drawInstructions(guiGraphics);
        if (this.popup != null) {
            this.popup.render(guiGraphics, mouseX, mouseY, delta);
        }
    }

    @Override
    public void onClose() {
        Minecraft.getInstance().setScreen(parent);
        SBTConfig.save();
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
    //? if >1.21.8 {
    public boolean keyPressed(KeyEvent event) {
    int keyCode = event.key();
    
    //?} else {
    /*public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

    *///?}
        if (MultiUtils.altDown() && MultiUtils.controlDown()) {
            textToggledOff = !textToggledOff;
        }

        if (keyCode == GLFW.GLFW_KEY_R && MultiUtils.controlDown()) {
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
        return
                //?if >1.21.8 {
                super.keyPressed(event);
                //?} else {
        /*super.keyPressed(keyCode, scanCode, modifiers);

        *///?}
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
        if (MultiUtils.altDown()) return false;
        return !textToggledOff;
    }

    private void drawInstructions(GuiGraphics guiGraphics) {
        var centerX = minecraft.getWindow().getGuiScaledWidth() / 2;
        if (shouldShowText() && this.mode == Mode.DRAG) {
            guiGraphics.drawCenteredString(minecraft.font, "Drag or use the arrow keys to move items", centerX, 5, Colors.WHITE);
            guiGraphics.drawCenteredString(minecraft.font, "Scroll or use the + and - keys to scale items", centerX, 15, Colors.WHITE);
            guiGraphics.drawCenteredString(minecraft.font, "Press shift and hover to see the name of the item", centerX, 25, Colors.WHITE);
            guiGraphics.drawCenteredString(minecraft.font, (macOS() ? "Command" : "Control") + " click for text mode/to edit anchor points", centerX, 35, Colors.WHITE);
            guiGraphics.drawCenteredString(minecraft.font, (macOS() ? "Command" : "Control") + "+ R to enter Reset Mode", centerX, 45, Colors.WHITE);
            guiGraphics.drawCenteredString(minecraft.font, "Hold " + (macOS() ? "option" : "alt") + " to hide this text or " + (macOS() ? "Cmd+Opt" : "Ctrl+Alt") + " to toggle it", centerX, 55, Colors.WHITE);
        } else if (shouldShowText() && this.mode == Mode.TEXT) {
            guiGraphics.drawCenteredString(minecraft.font, "You are now in exact positioning mode", centerX, 5, Colors.WHITE);
            guiGraphics.drawCenteredString(minecraft.font, "Enter the x and y positions in the text fields below", centerX, 15, Colors.WHITE);
            guiGraphics.drawCenteredString(minecraft.font, "The number is relative, so 0 is fully up/left and 1 is fully down/right", centerX, 25, Colors.WHITE);
            guiGraphics.drawCenteredString(minecraft.font, "Press the anchor button to change where the hud anchors itself", centerX, 35, Colors.WHITE);
            guiGraphics.drawCenteredString(minecraft.font, "Hold " + (macOS() ? "option" : "alt") + " to hide this text or " + (macOS() ? "Cmd+Opt" : "Ctrl+Alt") + " to toggle it", centerX, 45, Colors.WHITE);
        } else if (this.mode == Mode.RESET) {
            guiGraphics.drawCenteredString(minecraft.font, "You are now in reset mode", centerX, 5, Colors.WHITE);
            guiGraphics.drawCenteredString(minecraft.font, "Press the reset button to reset the selected item to the default position", centerX, 15, Colors.WHITE);
            guiGraphics.drawCenteredString(minecraft.font, "Press the first button to cycle between elements", centerX, 25, Colors.WHITE);
        }
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

    private List<CheezePair<String, ? extends AbstractWidget>> getPopupWidgets(HUD hud) {
        var x = new EditBox(Minecraft.getInstance().font, 0, 0, 70, 15, Component.literal(""));
        var y = new EditBox(Minecraft.getInstance().font, 0, 0, 70, 15, Component.literal(""));
        x.setFilter(Predicates.ZERO_TO_ONE);
        y.setFilter(Predicates.ZERO_TO_ONE);
        x.setResponder(s -> {
            if (s.isEmpty()) return;
            hud.updatePosition(Float.parseFloat(s), hud.getCurrentBoundsRelative().y);
        });
        y.setResponder(s -> {
            if (s.isEmpty()) return;
            hud.updatePosition(hud.getCurrentBoundsRelative().x, Float.parseFloat(s));
        });
        var bounds = hud.getCurrentBoundsRelative();
        x.setValue("" + bounds.x);
        y.setValue("" + bounds.y);

        SkyblockTweaks.LOGGER.info("X text: " + x.getValue() + " Y text: " + y.getValue());


        var scale = new DecimalSlider(0, 0, 0, 0, Component.literal(hud.INFO.getScale.get().toString().formatted("%.1f")), hud.INFO.getScale.get() / 3.0, 0.1, 3.0, 0.1, (val) -> hud.INFO.setScale.accept((float) (double) val));
        var anchor = new ConstructableButton(Component.literal(hud.INFO.getAnchorPoint.get().name()), button -> {
            var anchorPoint = hud.INFO.getAnchorPoint.get();
            if (anchorPoint == AnchorPoint.LEFT) {
                hud.INFO.setAnchorPoint.accept(AnchorPoint.CENTER);
            } else if (anchorPoint == AnchorPoint.CENTER) {
                hud.INFO.setAnchorPoint.accept(AnchorPoint.RIGHT);
            } else if (anchorPoint == AnchorPoint.RIGHT) {
                hud.INFO.setAnchorPoint.accept(AnchorPoint.LEFT);
            }
            button.setMessage(Component.literal(hud.INFO.getAnchorPoint.get().name()));
        });

        if (!hud.supportsNonLeftAnchors) anchor.active = false;

        return List.of(new CheezePair<>("X", x), new CheezePair<>("Y", y), new CheezePair<>("Scale", scale), new CheezePair<>("Anchor", anchor)

        );


    }

    //TODO: Popup button is blinking
    public void addPopup(Component title, int desiredX, int desiredY, List<CheezePair<String, ? extends AbstractWidget>> widgets) {
        this.setMode(Mode.TEXT);
        ScreenRectangle bounds;
        int i = 0;
        while (i < 1E6) { // prevent infinite loops if something goes wrong
            bounds = new ScreenRectangle(desiredX, desiredY, EditorPopup.WIDTH, EditorPopup.HEIGHT);
            RenderUtils.BreachResult breachResult = RenderUtils.isOffscreen(bounds);
            if (breachResult.breachesAll()) {
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


    @Override
    //?if > 1.21.8 {
    public boolean mouseReleased(MouseButtonEvent event) {
        if (this.mode == Mode.DRAG) selectedElement = null;
        return super.mouseReleased(event);
    }
     
    //?} else {
    /*public boolean mouseReleased(double mouseX, double mouseY, int button) {
        boolean x = super.mouseReleased(mouseX, mouseY, button);
        if (this.mode == Mode.DRAG) selectedElement = null;
        return x;
    }
    *///?}



    @Override
    //?if >1.21.8 {
    
    public boolean mouseClicked(MouseButtonEvent event, boolean isDoubleClick) {
        double x = event.x();
        double y = event.y();
     //?} else {
     /*public boolean mouseClicked(double x, double y, int button) {
     *///?}
        this.selectedViaTheKeyboard = null;
        for (HUD hud : huds) {
            if (clickInBounds(hud, x, y)) {
                if (this.mode == Mode.DRAG) {
                    selectedElement = hud;
                    updateOffset(hud, x, y);
                }
                if (MultiUtils.controlDown() || (this.mode == Mode.TEXT && !clickInBounds(popup.getBounds(), x, y))) {
                    this.addPopup(hud.getName().name(EditorPopup.WIDTH), (int) x, (int) y, getPopupWidgets(hud));
                    return
                            //? if >1.21.8 {
                            super.mouseClicked(event, isDoubleClick);
                            //?} else {
                            /*super.mouseClicked(x, y, button);
                            *///?}
                }
            }
        }
        if (this.popup != null && !clickInBounds(this.popup.getBounds(), x, y)) {
            this.popup.remove();
            this.popup = null;
            this.setMode(Mode.DRAG);
        }
        return
                //? if >1.21.8 {
                super.mouseClicked(event, isDoubleClick);
                 //?} else {
                /*super.mouseClicked(x, y, button);
        *///?}
    }

    @Override
    //?if >1.21.8 {
    public boolean mouseDragged(MouseButtonEvent event, double mouseXIgnore, double mouseYIgnore) {
        double x = event.x();
        double y = event.y();
        //?} else {
    /*public boolean mouseDragged(double x, double y, int button, double deltaX, double deltaY) {
        *///?}
        // TODO: What are these parameters?
        if (this.mode != Mode.DRAG) return
                //?if >1.21.8 {
                 super.mouseDragged(event, mouseXIgnore, mouseYIgnore);
                //?} else {
                /*super.mouseDragged(x, y, button, deltaX, deltaY);
                *///?}
        if (this.selectedElement != null) {
            this.selectedElement.updatePosition(HUD.getRelativeX(x - this.offsetX), HUD.getRelativeY(y - this.offsetY));
        }

        return
                //?if >1.21.8 {
                 super.mouseDragged(event, mouseXIgnore, mouseYIgnore);
                //?} else {
                /*super.mouseDragged(x, y, button, deltaX, deltaY);
                  *///?}
    }

}





