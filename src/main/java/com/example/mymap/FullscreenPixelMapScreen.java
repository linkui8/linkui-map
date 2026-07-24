package com.example.mymap;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class FullscreenPixelMapScreen extends Screen {
    public FullscreenPixelMapScreen() {
        super(Text.literal("Карта следов"));
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        this.renderBackground(context);

        MinecraftClient mc = MinecraftClient.getInstance();
        if (mc == null || mc.player == null || mc.world == null) return;

        int centerX = this.width / 2;
        int centerY = this.height / 2;

        int playerX = mc.player.getBlockX();
        int playerZ = mc.player.getBlockZ();

        int pixelSize = 4;
        int range = 40;

        // Рисуем заголовок
        context.drawText(mc.textRenderer, Text.literal("Карта следов"), 
                        centerX - 50, 10, 0xFFFFFFFF, false);

        for (int dx = -range; dx <= range; dx++) {
            for (int dz = -range; dz <= range; dz++) {
                int currentX = playerX + dx;
                int currentZ = playerZ + dz;

                Integer color = MapStorage.getColor(currentX, currentZ);
                if (color != null) {
                    int drawX = centerX + (dx * pixelSize);
                    int drawY = centerY + (dz * pixelSize);
                    int finalHexColor = 0xFF000000 | color;

                    context.fill(drawX, drawY, drawX + pixelSize, drawY + pixelSize, finalHexColor);
                }
            }
        }

        // Белая точка игрока в центре
        context.fill(centerX, centerY, centerX + pixelSize, centerY + pixelSize, 0xFFFFFFFF);
        
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean shouldPauseGame() {
        return false;
    }
}
