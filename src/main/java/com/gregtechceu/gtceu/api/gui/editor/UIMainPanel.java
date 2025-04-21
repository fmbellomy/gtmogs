package com.gregtechceu.gtceu.api.gui.editor;

import com.gregtechceu.gtceu.api.gui.GuiTextures;

import com.lowdragmc.lowdraglib.gui.editor.ui.Editor;
import com.lowdragmc.lowdraglib.gui.editor.ui.MainPanel;
import com.lowdragmc.lowdraglib.gui.texture.TextTexture;
import com.lowdragmc.lowdraglib.gui.widget.WidgetGroup;

public class UIMainPanel extends MainPanel {

    final String description;

    public UIMainPanel(Editor editor, WidgetGroup root, String description) {
        super(editor, root);
        this.setBackground((graphics, mouseX, mouseY, x, y, width, height) -> {
            if (description != null) {
                new TextTexture(description).scale(2.0f).draw(graphics, mouseX, mouseY, x, y,
                        width - editor.getConfigPanel().getSize().getWidth(), height);
            }
            var border = 4;
            var background = GuiTextures.BACKGROUND;
            var position = root.getPosition();
            var size = root.getSize();
            var w = Math.max(size.width + border * 2, 172);
            var h = Math.max(size.height + border * 2, 86);
            background.draw(graphics, mouseX, mouseY,
                    position.x - (w - size.width) / 2f,
                    position.y - (h - size.height) / 2f,
                    w, h);
        });
        this.description = description;
    }
}
