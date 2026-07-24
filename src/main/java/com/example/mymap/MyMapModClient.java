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

    @id86240433 (@Override)
    public void onInitializeClient() {
        MinecraftClient mcInstance = MinecraftClient.getInstance();
        MapStorage.init(mcInstance.runDirectory);

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.world != null) {
                if (client.player.age % 20 == 0) {
                    int pX = client.player.getBlockX();
                    int pZ = client.player.getBlockZ();

                    // Записываем зону 7х7 вокруг ног игрока
                    for (int dx = -3; dx <= 3; dx++) {
                        for (int dz = -3; dz <= 3; dz++) {
                            int targetX = pX + dx;
                            int targetZ = pZ + dz;

                            BlockPos topPos = client.world.getTopPosition(Heightmap.Type.WORLD_SURFACE, new BlockPos(targetX, 64, targetZ));
                            
                            // Исправлено: точный вызов цвета по маппингам 1.20.1
                            int colorValue = client.world.getBlockState(topPos).getMapColor(client.world, topPos).color;
                            
                            MapStorage.saveBlockColor(targetX, targetZ, colorValue);
                        }
                    }
                }

                if (openMapKey.wasPressed()) {
                    client.setScreen(new FullscreenPixelMapScreen());
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
