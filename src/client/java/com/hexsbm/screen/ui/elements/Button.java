package com.hexsbm.screen.ui.elements;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class Button extends ConfigControl {
    private final Text label;
    private final int color;
    private final Runnable action;

    private boolean isPressed = false;

    private static final int BUTTON_WIDTH = 90;
    private static final int BUTTON_HEIGHT = 16;
    private static final int BACKGROUND_COLOR = 0xFF333333;
    private static final int HOVER_BACKGROUND_COLOR = 0xFF555555;
    private static final int PRESSED_BACKGROUND_COLOR = 0xFF111111;
    private static final int BORDER_COLOR = 0xFF666666;
    private static final int TEXT_Y_OFFSET = 1;

    public Button(int x, Text label, int color, Runnable action) {
        super(x, BUTTON_HEIGHT);
        this.label = label;
        this.color = color;
        this.action = action;
    }

    @Override
    public void render(DrawContext ctx, TextRenderer textRenderer, int mx, int my, int panelX, int scrollY) {
        if (!visible) return;
        int yScreen = this.y - scrollY;
        int sx = panelX + this.x;

        boolean isHovered = mx >= sx && mx <= sx + BUTTON_WIDTH && my >= yScreen && my <= yScreen + BUTTON_HEIGHT;

        int currentBackgroundColor = BACKGROUND_COLOR;
        if (isPressed) {
            currentBackgroundColor = PRESSED_BACKGROUND_COLOR;
            isPressed = false;
        } else if (isHovered) {
            currentBackgroundColor = HOVER_BACKGROUND_COLOR;
        }

        ctx.fill(sx, yScreen, sx + BUTTON_WIDTH, yScreen + BUTTON_HEIGHT, currentBackgroundColor);
        ctx.drawBorder(sx, yScreen, BUTTON_WIDTH, BUTTON_HEIGHT, BORDER_COLOR);

        int textX = sx + (BUTTON_WIDTH - textRenderer.getWidth(label)) / 2;
        int textY = yScreen + (BUTTON_HEIGHT - textRenderer.fontHeight) / 2 + TEXT_Y_OFFSET;
        ctx.drawText(textRenderer, label, textX, textY, color, false);
    }

    @Override
    public boolean mouseClicked(int mx, int my, int panelX, TextRenderer textRenderer, int scrollY) {
        if (!visible) return false;
        int yScreen = this.y - scrollY;
        int sx = panelX + this.x;
        if (mx >= sx && mx <= sx + BUTTON_WIDTH && my >= yScreen && my <= yScreen + BUTTON_HEIGHT) {
            isPressed = true;
            action.run();
            return true;
        }
        return false;
    }

    @Override public boolean isEditing() { return false; }
}