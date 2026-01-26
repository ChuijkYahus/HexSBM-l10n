package com.hexsbm.screen.ui.elements;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;

import java.util.List;

public class RowControl extends ConfigControl {
    private final List<ConfigControl> children;

    public RowControl(List<ConfigControl> children) {
        super(0, 0);
        this.children = children;

        int maxHeight = 0;
        for (ConfigControl child : children) {
            if (child.height > maxHeight) {
                maxHeight = child.height;
            }
        }
        this.height = maxHeight;
    }

    @Override
    public void render(DrawContext ctx, TextRenderer textRenderer, int mx, int my, int panelX, int scrollY) {
        if (!visible) return;
        for (ConfigControl child : children) {
            child.y = this.y; 
            child.render(ctx, textRenderer, mx, my, panelX, scrollY);
        }
    }

    @Override
    public boolean mouseClicked(int mx, int my, int panelX, TextRenderer textRenderer, int scrollY) {
        if (!visible) return false;
        for (ConfigControl child : children) {
            child.y = this.y;
            if (child.mouseClicked(mx, my, panelX, textRenderer, scrollY)) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void finishEditing() {
        for (ConfigControl child : children) {
            child.finishEditing();
        }
    }

    @Override
    public boolean isEditing() {
        return children.stream().anyMatch(ConfigControl::isEditing);
    }
    
    @Override
    public boolean keyPressed(int keyCode, int scanCode) {
        for (ConfigControl child : children) {
            if (child.keyPressed(keyCode, scanCode)) {
                return true;
            }
        }
        return false;
    }
}
