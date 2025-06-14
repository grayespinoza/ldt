package dev.heygrey.config;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;

@Config(name = "look-down-transparency")
public class LookDownTransparencyConfiguration implements ConfigData {
  @ConfigEntry.Category("Client")
  @ConfigEntry.Gui.Tooltip
  public boolean modEnabled = true;

  @ConfigEntry.Category("Client")
  @ConfigEntry.Gui.Tooltip
  public boolean modEnabledAlert = true;

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
    AutoConfig.register(LookDownTransparencyConfiguration.class, Toml4jConfigSerializer::new);
  }

  public static LookDownTransparencyConfiguration getInstance() {
    return AutoConfig.getConfigHolder(LookDownTransparencyConfiguration.class).getConfig();
  }
}
