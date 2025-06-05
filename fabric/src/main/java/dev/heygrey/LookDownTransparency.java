package dev.heygrey;

import dev.heygrey.config.LookDownTransparencyConfiguration;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

public class LookDownTransparency implements ClientModInitializer {
  private static KeyBinding toggleModEnabledKey;

  @Override
  public void onInitializeClient() {
    LookDownTransparencyConfiguration.init();
    toggleModEnabledKey =
        KeyBindingHelper.registerKeyBinding(
            new KeyBinding(
                "key.look-down-transparency.toggle_mod_enabled",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_UNKNOWN,
                "key.look-down-transparency"));
    ClientTickEvents.END_CLIENT_TICK.register(
        client -> {
          while (toggleModEnabledKey.wasPressed()) {
            LookDownTransparencyConfiguration.getInstance().modEnabled = !LookDownTransparencyConfiguration.getInstance().modEnabled;
            if (LookDownTransparencyConfiguration.getInstance().modEnabledAlert) {
              if (LookDownTransparencyConfiguration.getInstance().modEnabled) {
                client.player.sendMessage(Text.literal("Look Down Transparency Enabled"), true);
              } else {
                client.player.sendMessage(Text.literal("Look Down Transparency Disabled"), true);
              }
            }
          }
        });
  }
}
