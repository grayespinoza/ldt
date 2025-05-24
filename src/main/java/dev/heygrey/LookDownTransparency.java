package dev.heygrey;

import dev.heygrey.config.Configuration;
import net.fabricmc.api.ClientModInitializer;

public class LookDownTransparency implements ClientModInitializer {
  @Override
  public void onInitializeClient() {
    Configuration.init();
  }
}
