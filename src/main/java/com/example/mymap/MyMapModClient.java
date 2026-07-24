package com.example.mymap;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;
import org.lwjgl.glfw.GLFW;

public class MyMapModClient implements ClientModInitializer {
    public static KeyBinding openMapKey;

    @Override
    public void onInitializeClient() {
        MinecraftClient mcInstance = MinecraftClient.getInstance();
        MapStorage.init(mcInstance.runDirectory);

        ClientTickEvents.END_CLIENT_TICK.register(gameClient -> {
            if (gameClient.player != null && gameClient.world != null) {
                try {
                    int pX = gameClient.player.getBlockX();
                    int pZ = gameClient.player.getBlockZ();

                    // Сканируем зону 7х7 вокруг ног игрока
                    for (int dx = -3; dx <= 3; dx++) {
                        for (int dz = -3; dz <= 3; dz++) {
                            int targetX = pX + dx;
                            int targetZ = pZ + dz;

                            try {
                                // Находим самый верхний блок
                                BlockPos topPos = gameClient.world.getTopPosition(
                                    Heightmap.Type.WORLD_SURFACE, 
                                    new BlockPos(targetX, 64, targetZ)
                                );

                                var blockState = gameClient.world.getBlockState(topPos);
                                var mapColor = blockState.getMapColor(gameClient.world, topPos);
                                
                                if (mapColor != null) {
                                    int colorValue = mapColor.color;
                                    MapStorage.saveBlockColor(targetX, targetZ, colorValue);
                                }
                            } catch (Exception e) {
                                // Игнорируем ошибки для отдельных блоков
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (openMapKey != null && openMapKey.wasPressed()) {
                    gameClient.setScreen(new FullscreenPixelMapScreen());
                }
            }
        });

        openMapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.footprintmap.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.footprintmap"
        ));
    }
}
