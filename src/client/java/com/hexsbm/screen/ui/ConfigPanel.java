package com.hexsbm.screen.ui;

import com.hexsbm.config.ConfigManager;
import com.hexsbm.config.HexSBMConfig;
import com.hexsbm.screen.ui.elements.*;

import net.minecraft.client.font.TextRenderer;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.stream.Collectors;

public class ConfigPanel {
    private final List<ConfigControl> controls;
    private final HexSBMConfig config;
    private int scrollY = 0;
    private int contentHeight = 0;
    private static final int SPACING = 10;

    private record RingControlSet(Text radius1Label, java.util.function.IntSupplier radius1Getter, java.util.function.IntConsumer radius1Setter,
                                  Text radius2Label, java.util.function.IntSupplier radius2Getter, java.util.function.IntConsumer radius2Setter,
                                  java.util.function.IntSupplier iconOffsetGetter, java.util.function.IntConsumer iconOffsetSetter) {}

    private record GradientControlSet(java.util.function.IntSupplier activeLightenGetter, java.util.function.IntConsumer activeLightenSetter,
                                      java.util.function.IntSupplier hoverLightenGetter, java.util.function.IntConsumer hoverLightenSetter,
                                      java.util.function.IntSupplier inactiveLightenGetter, java.util.function.IntConsumer inactiveLightenSetter,
                                      java.util.function.IntSupplier inactiveDarkenGetter, java.util.function.IntConsumer inactiveDarkenSetter) {}

    public ConfigPanel(HexSBMConfig config) {
        this.config = config;
        this.controls = new ArrayList<>();

        // === Сброс (Верх) ===
        addResetSection();
        controls.add(new DividerControl(10));

        // === Поведение ===
        controls.add(new LabelControl(Text.translatable("hexsbm.ui.behavior"), 0xAAAAAA));
        controls.add(new CheckBoxField(10, Text.translatable("hexsbm.ui.tooltips"), config::isEnableTooltips, config::setEnableTooltips));
        controls.add(new CheckBoxField(10, Text.translatable("hexsbm.ui.close_on_miss_click"), config::isCloseOnBackgroundClick, config::setCloseOnBackgroundClick));
        controls.add(new CycleField(10, Text.translatable("hexsbm.ui.open_menu"), List.of(Text.translatable("hexsbm.ui.hold"), Text.translatable("hexsbm.ui.press")), config::getMenuOpenMode, config::setMenuOpenMode));
        controls.add(new DividerControl(10));

        // === Кольца ===
        addRingControls(Text.translatable("hexsbm.ui.spell_ring"),
                new RingControlSet(
                        Text.translatable("hexsbm.ui.outer_radius"), config::getOuterRingOuterRadius, config::setOuterRingOuterRadius,
                        Text.translatable("hexsbm.ui.inner_radius"), config::getOuterRingInnerRadius, config::setOuterRingInnerRadius,
                        config::getOuterIconRadiusOffset, v -> config.setOuterIconRadiusOffset(v)
                ));

        addRingControls(Text.translatable("hexsbm.ui.group_ring"),
                new RingControlSet(
                        Text.translatable("hexsbm.ui.inner_radius"), config::getInnerRingInnerRadius, config::setInnerRingInnerRadius,
                        Text.translatable("hexsbm.ui.outer_radius"), config::getInnerRingOuterRadius, config::setInnerRingOuterRadius,
                        config::getInnerIconRadiusOffset, v -> config.setInnerIconRadiusOffset(v)
                ));

        // === Цвет ===
        controls.add(new LabelControl(Text.translatable("hexsbm.ui.color"), 0xAAAAAA));
        controls.add(new CheckBoxField(10, Text.translatable("hexsbm.ui.auto_color"), config::isUsePigmentColor, config::setUsePigmentColor));

        var rField = new NumberField(100, Text.translatable("hexsbm.ui.r"), () -> (config.uiBaseColor >> 16) & 0xFF, v -> config.uiBaseColor = (config.uiBaseColor & 0xFF00FFFF) | ((v & 0xFF) << 16), false, true);
        rField.setVisibilityCondition(() -> !config.isUsePigmentColor());
        controls.add(rField);

        var gField = new NumberField(100, Text.translatable("hexsbm.ui.g"), () -> (config.uiBaseColor >> 8) & 0xFF, v -> config.uiBaseColor = (config.uiBaseColor & 0xFFFF00FF) | ((v & 0xFF) << 8), false, true);
        gField.setVisibilityCondition(() -> !config.isUsePigmentColor());
        controls.add(gField);

        var bField = new NumberField(100, Text.translatable("hexsbm.ui.b"), () -> config.uiBaseColor & 0xFF, v -> config.uiBaseColor = (config.uiBaseColor & 0xFFFFFF00) | (v & 0xFF), false, true);
        bField.setVisibilityCondition(() -> !config.isUsePigmentColor());
        controls.add(bField);
        
        controls.add(new DividerControl(10));

        // === Режим подсветки страниц ===
        List<Text> highlightPageOptions = Arrays.stream(HexSBMConfig.HighlightPages.values())
                .map(mode -> Text.translatable("hexsbm.ui.highlight_pages." + mode.name().toLowerCase()))
                .collect(Collectors.toList());
        controls.add(new CycleField(10, Text.translatable("hexsbm.ui.highlight_pages"), highlightPageOptions,
                () -> config.getHighlightPages().ordinal(),
                v -> config.setHighlightPages(HexSBMConfig.HighlightPages.values()[v])));

        // === Цвет пустых секторов ===
        var emptySectorLabel = new LabelControl(Text.translatable("hexsbm.ui.empty_sector_color"), 0xAAAAAA);
        emptySectorLabel.setVisibilityCondition(() -> config.getHighlightPages() == HexSBMConfig.HighlightPages.WITH_SPELL);
        controls.add(emptySectorLabel);

        var emptyAutoColorCheckbox = new CheckBoxField(10, Text.translatable("hexsbm.ui.auto_color"), config::isUsePigmentForEmptySector, config::setUsePigmentForEmptySector);
        emptyAutoColorCheckbox.setVisibilityCondition(() -> config.getHighlightPages() == HexSBMConfig.HighlightPages.WITH_SPELL);
        controls.add(emptyAutoColorCheckbox);

        var emptyRField = new NumberField(100, Text.translatable("hexsbm.ui.r"), config::getEmptySectorColorR, config::setEmptySectorColorR, false, true);
        emptyRField.setVisibilityCondition(() -> config.getHighlightPages() == HexSBMConfig.HighlightPages.WITH_SPELL && !config.isUsePigmentForEmptySector());
        controls.add(emptyRField);

        var emptyGField = new NumberField(100, Text.translatable("hexsbm.ui.g"), config::getEmptySectorColorG, config::setEmptySectorColorG, false, true);
        emptyGField.setVisibilityCondition(() -> config.getHighlightPages() == HexSBMConfig.HighlightPages.WITH_SPELL && !config.isUsePigmentForEmptySector());
        controls.add(emptyGField);

        var emptyBField = new NumberField(100, Text.translatable("hexsbm.ui.b"), config::getEmptySectorColorB, config::setEmptySectorColorB, false, true);
        emptyBField.setVisibilityCondition(() -> config.getHighlightPages() == HexSBMConfig.HighlightPages.WITH_SPELL && !config.isUsePigmentForEmptySector());
        controls.add(emptyBField);

        var emptyColorDivider = new DividerControl(10);
        controls.add(emptyColorDivider);
        
        // === Градиенты ===
        var gradientToggle = new CheckBoxField(10, Text.translatable("hexsbm.ui.gradient"), () -> !config.isDisableGradient(), val -> config.setDisableGradient(!val));
        controls.add(gradientToggle);
        
        addGradientControls(Text.translatable("hexsbm.ui.gradient_outer"),
                new GradientControlSet(
                        () -> (int)(config.outerActiveLighten * 100), v -> config.outerActiveLighten = v / 100f,
                        () -> (int)(config.outerHoverLighten * 100), v -> config.outerHoverLighten = v / 100f,
                        () -> (int)(config.outerInactiveLighten * 100), v -> config.outerInactiveLighten = v / 100f,
                        () -> (int)(config.outerInactiveDarken * 100), v -> config.outerInactiveDarken = v / 100f
                ), () -> !config.isDisableGradient());

        addGradientControls(Text.translatable("hexsbm.ui.gradient_inner"),
                new GradientControlSet(
                        () -> (int)(config.innerActiveLighten * 100), v -> config.innerActiveLighten = v / 100f,
                        () -> (int)(config.innerHoverLighten * 100), v -> config.innerHoverLighten = v / 100f,
                        () -> (int)(config.innerInactiveLighten * 100), v -> config.innerInactiveLighten = v / 100f,
                        () -> (int)(config.innerInactiveDarken * 100), v -> config.innerInactiveDarken = v / 100f
                ), () -> !config.isDisableGradient());
        
        controls.add(new DividerControl(10));

        // === Сброс (Низ) ===
        addResetSection();
    }
    
    private void updateLayout() {
        int currentY = 20;
        for (var control : controls) {
            control.updateVisibility();
            if (control.visible) {
                control.setY(currentY);
                int height = control.getHeight();
                currentY += height;
                if (height > 0) {
                    currentY += SPACING;
                }
            }
        }
        this.contentHeight = currentY;
    }

    private void addRingControls(Text title, RingControlSet rcs) {
        controls.add(new LabelControl(title, 0xAAAAAA));
        controls.add(new NumberField(100, rcs.radius1Label(), rcs.radius1Getter(), rcs.radius1Setter(), false));
        controls.add(new NumberField(100, rcs.radius2Label(), rcs.radius2Getter(), rcs.radius2Setter(), false));
        controls.add(new NumberField(100, Text.translatable("hexsbm.ui.icon_offset"), rcs.iconOffsetGetter(), rcs.iconOffsetSetter(), true));
        controls.add(new DividerControl(10));
    }

    private void addGradientControls(Text title, GradientControlSet gcs, java.util.function.BooleanSupplier visibility) {
        var label = new LabelControl(title, 0xAAAAAA);
        label.setVisibilityCondition(visibility);
        controls.add(label);

        var active = new NumberField(100, Text.translatable("hexsbm.ui.gradient_active_lighten"), gcs.activeLightenGetter(), gcs.activeLightenSetter(), false);
        active.setVisibilityCondition(visibility);
        controls.add(active);

        var hover = new NumberField(100, Text.translatable("hexsbm.ui.gradient_hover_lighten"), gcs.hoverLightenGetter(), gcs.hoverLightenSetter(), false);
        hover.setVisibilityCondition(visibility);
        controls.add(hover);

        var inactiveLighten = new NumberField(100, Text.translatable("hexsbm.ui.gradient_inactive_lighten"), gcs.inactiveLightenGetter(), gcs.inactiveLightenSetter(), false);
        inactiveLighten.setVisibilityCondition(visibility);
        controls.add(inactiveLighten);
        
        var inactiveDarken = new NumberField(100, Text.translatable("hexsbm.ui.gradient_inactive_darken"), gcs.inactiveDarkenGetter(), gcs.inactiveDarkenSetter(), false);
        inactiveDarken.setVisibilityCondition(visibility);
        controls.add(inactiveDarken);
    }

    private void addResetSection() {
        controls.add(new LabelControl(Text.translatable("hexsbm.ui.reset"), 0xAAAAAA));
        controls.add(new RowControl(List.of(
            new Button(15, Text.translatable("hexsbm.ui.save"), 0xFFFFFF, this::saveConfig),
            new Button(115, Text.translatable("hexsbm.ui.reset_changes"), 0xFFFFFF, this::resetChanges)
        )));
        controls.add(new Button(115, Text.translatable("hexsbm.ui.reset_all"), 0xFFFF0000, this::resetToDefaults));
    }

    public void render(DrawContext ctx, int px, TextRenderer textRenderer, int mx, int my) {
        updateLayout();
        
        int panelHeight = ctx.getScaledWindowHeight();
        int maxScroll = Math.max(0, this.contentHeight - panelHeight + 20);
        if (scrollY > maxScroll) {
            scrollY = maxScroll;
        }

        ctx.fill(px, 0, px + 220, panelHeight, 0xCC000000);
        ctx.fill(px, 0, px + 220, 1, 0xFFFFFFFF);
        ctx.fill(px, 0, px + 1, panelHeight, 0xFFFFFFFF);
        ctx.fill(px + 219, 0, px + 220, panelHeight, 0xFFFFFFFF);

        ctx.drawText(textRenderer, Text.translatable("hexsbm.ui.title"), px + 10, 5, 0xFFFFFFFF, false);

        ctx.enableScissor(px + 1, 20, px + 219, panelHeight);

        for (var control : controls) {
            control.render(ctx, textRenderer, mx, my, px, scrollY);
        }

        ctx.disableScissor();
    }

    public boolean mouseClicked(int mx, int my, int px, TextRenderer textRenderer) {
        if (mx <= px) return false;

        boolean wasEditing = controls.stream().anyMatch(ConfigControl::isEditing);
        boolean clickedOnControl = false;

        for (var control : controls) {
            if (control.mouseClicked(mx, my, px, textRenderer, scrollY)) {
                for (var c : controls) {
                    if (c != control) c.finishEditing();
                }
                clickedOnControl = true;
                break;
            }
        }

        if (clickedOnControl) return true;
        if (wasEditing) {
            controls.forEach(ConfigControl::finishEditing);
            return true;
        }

        return false;
    }

    public boolean keyPressed(int keyCode, int scanCode, int mods) {
        for (var control : controls) {
            if (control.isEditing()) {
                 if (control.keyPressed(keyCode, scanCode)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean mouseScrolled(int mx, int my, double amount, int px, int windowHeight) {
        if (mx <= px) return false;

        for (var control : controls) {
            if (control instanceof NumberField field) {
                if (field.isMouseOver(mx, my, px, scrollY)) {
                    return field.mouseScrolled(mx, my, amount, px);
                }
            }
        }

        int maxScroll = Math.max(0, this.contentHeight - windowHeight + 20);
        scrollY = MathHelper.clamp(scrollY - (int)(amount * 10), 0, maxScroll);
        return true;
    }

    public void close() {
        controls.forEach(ConfigControl::finishEditing);
    }

    private void saveConfig() {
        ConfigManager.saveConfig(this.config);
    }

    private void resetToDefaults() {
        this.config.copyFrom(new HexSBMConfig());
    }

    private void resetChanges() {
        this.config.copyFrom(ConfigManager.getSavedConfig());
    }
}