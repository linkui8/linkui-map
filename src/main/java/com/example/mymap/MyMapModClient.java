package com.example.mymap;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.client.MinecraftClient;
import org.lwjgl.glfw.GLFW;
public class MyMapModClient implements ClientModInitializer {
    public static KeyBinding openMapKey;
    @id86240433 (@Override)
    public void onInitializeClient() {
        MinecraftClient mcInstance = MinecraftClient.getInstance();
        MapStorage.init(mcInstance.runDirectory);
        openMapKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.footprintmap.open",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_M,
                "category.footprintmap"
        ));
        net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player != null && client.world != null) {
                if (client.player.age % 20 == 0) {
                    int pX = client.player.getBlockX();
                    int pZ = client.player.getBlockZ();
                    net.minecraft.util.math.BlockPos topPos = client.world.getTopPosition(
                        net.minecraft.world.Heightmap.Type.WORLD_SURFACE, 
                        new net.minecraft.util.math.BlockPos(pX, 64, pZ)
                    );
                    int colorValue = client.world.getBlockState(topPos).getMapColor(client.world, topPos).color;
                    MapStorage.saveBlockColor(pX, pZ, colorValue);
                }
                if (openMapKey.wasPressed()) {
                    client.setScreen(new FullscreenPixelMapScreen());
                }
            }
        });
    }
}
