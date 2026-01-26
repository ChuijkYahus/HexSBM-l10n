package com.hexsbm.screen.ui;

import com.hexsbm.config.HexSBMConfig;

public class ColorScheme {

    private static final int ALPHA_MASK = 0xFF000000;
    private static final float MAX_RGB_VALUE = 255f;

    private final int pigmentColor;
    private final HexSBMConfig config;
    private final HexSBMConfig.HighlightPages highlightMode;

    public ColorScheme(int pigmentColor, HexSBMConfig config, HexSBMConfig.HighlightPages highlightMode) {
        this.pigmentColor = pigmentColor;
        this.config = config;
        this.highlightMode = highlightMode;
    }

    public int getOuterInnerColor(boolean cur, boolean hover, boolean hasSpell) {
        int alpha = cur ? config.activeAlpha : hover ? config.hoverAlpha : config.inactiveAlpha;
        int rgb = getRgbForState(cur, hover, false, false, hasSpell);
        return mkColor(alpha, rgb);
    }

    public int getOuterOuterColor(boolean cur, boolean hover, boolean hasSpell) {
        int alpha = cur ? config.activeAlpha : hover ? config.hoverAlpha : config.inactiveAlpha;
        int rgb = getRgbForState(cur, hover, false, true, hasSpell);
        return mkColor(alpha, rgb);
    }

    public int getInnerInnerColor(boolean cur, boolean hover, boolean hasSpell) {
        int alpha = cur ? config.activeAlpha : hover ? config.hoverAlpha : config.inactiveAlpha;
        int rgb = getRgbForState(cur, hover, true, false, hasSpell);
        return mkColor(alpha, rgb);
    }

    public int getInnerOuterColor(boolean cur, boolean hover, boolean hasSpell) {
        int alpha = cur ? config.activeAlpha : hover ? config.hoverAlpha : config.inactiveAlpha;
        int rgb = getRgbForState(cur, hover, true, true, hasSpell);
        return mkColor(alpha, rgb);
    }

    private int getRgbForState(boolean cur, boolean hover, boolean isInner, boolean isOuterEdge, boolean hasSpell) {
        int effectiveBaseColor;

        if (highlightMode == HexSBMConfig.HighlightPages.WITH_SPELL && !hasSpell) {
            if (config.isUsePigmentForEmptySector()) {
                effectiveBaseColor = lighten(this.pigmentColor, 0.2f);
            } else {
                effectiveBaseColor = (config.getEmptySectorColorR() << 16) | (config.getEmptySectorColorG() << 8) | config.getEmptySectorColorB();
            }
        } else {
            effectiveBaseColor = this.pigmentColor;
        }

        if (config.disableGradient) {
            if (cur) {
                return lighten(effectiveBaseColor, config.outerActiveLighten);
            } else if (hover) {
                return lighten(effectiveBaseColor, config.outerHoverLighten);
            } else {
                return lighten(effectiveBaseColor, config.outerInactiveLighten);
            }
        } else {
            if (cur) {
                return lighten(effectiveBaseColor, isInner ? config.innerActiveLighten : config.outerActiveLighten);
            } else if (hover) {
                return lighten(effectiveBaseColor, isInner ? config.innerHoverLighten : config.outerHoverLighten);
            } else {
                if ((isInner && !isOuterEdge) || (!isInner && isOuterEdge)) {
                    return lighten(effectiveBaseColor, isInner ? config.innerInactiveLighten : config.outerInactiveLighten);
                }
                else {
                    return darken(effectiveBaseColor, isInner ? config.innerInactiveDarken : config.outerInactiveDarken);
                }
            }
        }
    }

    // --- Вспомогательные методы ---

    private int mkColor(int alpha, int rgb) {
        return (alpha << 24) | (rgb & 0x00FFFFFF);
    }

    private int lighten(int color, float f) {
        float r = Math.min(1, ((color >> 16) & 0xFF) / MAX_RGB_VALUE + f);
        float g = Math.min(1, ((color >> 8) & 0xFF) / MAX_RGB_VALUE + f);
        float b = Math.min(1, (color & 0xFF) / MAX_RGB_VALUE + f);
        return (color & ALPHA_MASK) | ((int)(r * MAX_RGB_VALUE) << 16) | ((int)(g * MAX_RGB_VALUE) << 8) | (int)(b * MAX_RGB_VALUE);
    }

    private int darken(int color, float f) {
        float r = Math.max(0, ((color >> 16) & 0xFF) / MAX_RGB_VALUE - f);
        float g = Math.max(0, ((color >> 8) & 0xFF) / MAX_RGB_VALUE - f);
        float b = Math.max(0, (color & 0xFF) / MAX_RGB_VALUE - f);
        return (color & ALPHA_MASK) | ((int)(r * MAX_RGB_VALUE) << 16) | ((int)(g * MAX_RGB_VALUE) << 8) | (int)(b * MAX_RGB_VALUE);
    }
}