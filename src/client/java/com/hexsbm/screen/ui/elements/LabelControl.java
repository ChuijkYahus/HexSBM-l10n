package com.hexsbm.screen.ui.elements;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class LabelControl extends ConfigControl {
    private final Text text;
    private final int color;

    private static final int DEFAULT_TEXT_COLOR = 0xFFFFFF;
    private static final int TEXT_X_OFFSET = 10;
    private static final int LABEL_HEIGHT = 10;

    public LabelControl(Text text, int color) {
        super(TEXT_X_OFFSET, LABEL_HEIGHT);
        this.text = text;
        this.color = color;
    }

    public LabelControl(Text text) {
        this(text, DEFAULT_TEXT_COLOR);
    }

    public LabelControl(String text, int color) {
        this(Text.literal(text), color);
    }

    public LabelControl(String text) {
        this(Text.literal(text), DEFAULT_TEXT_COLOR);
    }


    @Override
    public void render(DrawContext ctx, TextRenderer textRenderer, int mx, int my, int panelX, int scrollY) {
        if (!visible) return;
        int yScreen = this.y - scrollY;
        ctx.drawText(textRenderer, text, panelX + this.x, yScreen, color, false);
    }

    @Override
    public boolean mouseClicked(int mx, int my, int panelX, TextRenderer textRenderer, int scrollY) { return false; }

    @Override public boolean isEditing() { return false; }
}