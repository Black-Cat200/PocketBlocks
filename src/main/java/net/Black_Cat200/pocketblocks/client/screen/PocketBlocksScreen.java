package net.Black_Cat200.pocketblocks.client.screen;

import net.Black_Cat200.pocketblocks.client.screen.PocketBlocksScreen;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class PocketBlocksScreen extends Screen {
    public PocketBlocksScreen(Component title) {
        super(title);
    }

    @Override
    protected void init() {
        super.init();
        // Здесь позже добавим кнопки сброса или настроек, если понадобятся
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        // Рисуем дефолтный темный фон Майнкрафта за экраном игры
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);

        // Отрисовка текста заголовка по центру экрана
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 20, 0xFFFFFF);

        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        // Игра не будет ставиться на паузу в одиночном мире при открытии Тетриса
        return false;
    }
}