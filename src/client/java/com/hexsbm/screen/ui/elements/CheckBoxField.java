package com.hexsbm.screen.ui.elements;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

import java.util.function.BooleanSupplier;
import java.util.function.Consumer;

public class CheckBoxField extends ConfigControl {
    private final Text label;
    private final BooleanSupplier getter;
    private final Consumer<Boolean> setter;

    private static final int BOX_SIZE = 10;
    private static final int BOX_Y_OFFSET = 1;
    private static final int TEXT_CHECKBOX_SPACING = 5;
    private static final int CHECKMARK_PADDING = 2;
    private static final int CHECKBOX_HEIGHT = 12;

    private static final int BACKGROUND_COLOR = 0xFF333333;
    private static final int HOVER_BACKGROUND_COLOR = 0xFF555555;
    private static final int BORDER_COLOR = 0xFF666666;
    private static final int CHECKMARK_COLOR = 0xFFFFFFFF;

    public CheckBoxField(int x, Text label, BooleanSupplier getter, Consumer<Boolean> setter) {
        super(x, CHECKBOX_HEIGHT);
        this.label = label;
        this.getter = getter;
        this.setter = setter;
    }

    @Override
    public void render(DrawContext ctx, TextRenderer textRenderer, int mx, int my, int panelX, int scrollY) {
        if (!visible) return;
        int yScreen = this.y - scrollY;
        boolean value = getter.getAsBoolean();
        int sx = panelX + this.x;

        Text fullLabel = Text.empty().append(label).append(":");
        ctx.drawText(textRenderer, fullLabel, sx, yScreen + BOX_Y_OFFSET + (BOX_SIZE - textRenderer.fontHeight) / 2, 0xFFFFFF, false);

        int checkboxX = sx + textRenderer.getWidth(fullLabel) + TEXT_CHECKBOX_SPACING;
        int checkboxY = yScreen + BOX_Y_OFFSET;

        boolean isHovered = mx >= checkboxX && mx <= checkboxX + BOX_SIZE && my >= checkboxY && my <= checkboxY + BOX_SIZE;

        int bgColor = isHovered ? HOVER_BACKGROUND_COLOR : BACKGROUND_COLOR;
        ctx.fill(checkboxX, checkboxY, checkboxX + BOX_SIZE, checkboxY + BOX_SIZE, bgColor);
        ctx.drawBorder(checkboxX, checkboxY, BOX_SIZE, BOX_SIZE, BORDER_COLOR);

        if (value) {
            ctx.fill(checkboxX + CHECKMARK_PADDING, checkboxY + CHECKMARK_PADDING, checkboxX + BOX_SIZE - CHECKMARK_PADDING, checkboxY + BOX_SIZE - CHECKMARK_PADDING, CHECKMARK_COLOR);
        }
    }

    @Override
    public boolean mouseClicked(int mx, int my, int panelX, TextRenderer textRenderer, int scrollY) {
        if (!visible) return false;
        int yScreen = this.y - scrollY;
        int sx = panelX + this.x;

        Text fullLabel = Text.empty().append(label).append(":");
        int checkboxX = sx + textRenderer.getWidth(fullLabel) + TEXT_CHECKBOX_SPACING;
        int checkboxY = yScreen + BOX_Y_OFFSET;

        if (mx >= checkboxX && mx <= checkboxX + BOX_SIZE && my >= checkboxY && my <= checkboxY + BOX_SIZE) {
            setter.accept(!getter.getAsBoolean());
            return true;
        }
        return false;
    }

    @Override public boolean isEditing() { return false; }
}