package dev.heygrey;

import dev.heygrey.config.LookDownTransparencyConfiguration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

public class LookDownTransparency implements ClientModInitializer {
  private static KeyMapping toggleTransparencyKey;

  @Override
  public void onInitializeClient() {
    LookDownTransparencyConfiguration.init();
    LookDownTransparencyConfiguration ldtConfiguration =
        LookDownTransparencyConfiguration.getInstance();

    toggleTransparencyKey =
        KeyBindingHelper.registerKeyBinding(
            new KeyMapping(
                "key.look-down-transparency.toggle_transparency",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "key.look-down-transparency"));

    ClientTickEvents.END_CLIENT_TICK.register(
        client -> {
          while (toggleTransparencyKey.consumeClick()) {
            ldtConfiguration.transparency = !ldtConfiguration.transparency;
          }
        });
  }
}
