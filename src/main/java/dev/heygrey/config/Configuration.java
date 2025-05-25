package dev.heygrey.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

@Config(name = "look-down-transparency")
public class Configuration implements ConfigData {
  @ConfigEntry.Category("Client")
  @ConfigEntry.Gui.Tooltip
  @ConfigEntry.BoundedDiscrete(min = 0, max = 77)
  public int terminalTransparency = 33;

  @ConfigEntry.Category("Client")
  @ConfigEntry.Gui.Tooltip
  @ConfigEntry.BoundedDiscrete(min = 0, max = 77)
  public int initiatingAngle = 66;

  @ConfigEntry.Category("Client")
  @ConfigEntry.Gui.Tooltip
  public boolean affectsAllPlayers = true;

  @ConfigEntry.Category("Client")
  @ConfigEntry.Gui.Tooltip
  public boolean affectsArmor = true;

  @ConfigEntry.Category("Client")
  @ConfigEntry.Gui.Tooltip
  public boolean affectsFirstPerson = true;

  @ConfigEntry.Category("Client")
  @ConfigEntry.Gui.Tooltip
  public boolean affectsThirdPerson = true;

  public static void init() {
    AutoConfig.register(Configuration.class, Toml4jConfigSerializer::new);
  }

  public static Configuration getInstance() {
    return AutoConfig.getConfigHolder(Configuration.class).getConfig();
  }
}
