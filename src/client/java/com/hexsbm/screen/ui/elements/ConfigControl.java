package com.hexsbm.screen.ui.elements;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import java.util.function.BooleanSupplier;

public abstract class ConfigControl {
    public int x;
    public int y;
    public int height;
    public boolean visible = true;
    protected BooleanSupplier visibilityCondition = () -> true;

    public ConfigControl(int x, int height) {
        this.x = x;
        this.height = height;
    }

    public abstract void render(DrawContext ctx, TextRenderer textRenderer, int mx, int my, int panelX, int scrollY);
    public abstract boolean mouseClicked(int mx, int my, int panelX, TextRenderer textRenderer, int scrollY);

    public boolean mouseScrolled(int mx, int my, double amount, int panelX) { return false; }
    public void finishEditing() {}
    public boolean isEditing() { return false; }
    public boolean keyPressed(int keyCode, int scanCode) { return false; }

    public void setY(int y) {
        this.y = y;
    }

    public int getHeight() {
        return this.visible ? this.height : 0;
    }

    public void setVisibilityCondition(BooleanSupplier condition) {
        this.visibilityCondition = condition;
    }

    public void updateVisibility() {
        this.visible = this.visibilityCondition.getAsBoolean();
    }
}