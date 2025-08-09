package dev.heygrey;

import dev.heygrey.config.LookDownTransparencyConfiguration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class LookDownTransparency implements ClientModInitializer {
  private static KeyBinding toggleTransparencyKey;

  @Override
  public void onInitializeClient() {
    LookDownTransparencyConfiguration.init();
    LookDownTransparencyConfiguration ldtConfiguration =
        LookDownTransparencyConfiguration.getInstance();

    toggleTransparencyKey =
        KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.look-down-transparency.toggle_transparency",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "key.look-down-transparency"));

    ClientTickEvents.END_CLIENT_TICK.register(
        client -> {
          while (toggleTransparencyKey.wasPressed()) {
            ldtConfiguration.transparency = !ldtConfiguration.transparency;
          }
        });
  }
}
