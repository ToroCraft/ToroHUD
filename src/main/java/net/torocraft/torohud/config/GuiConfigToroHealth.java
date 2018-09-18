package net.torocraft.torohud.config;

import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.torocraft.torohud.ToroHUD;

public class GuiConfigToroHealth extends GuiConfig {

  public GuiConfigToroHealth(GuiScreen parent) {
    super(parent, new ConfigElement(ConfigurationHandler.config.getCategory(Configuration.CATEGORY_CLIENT)).getChildElements(),
        ToroHUD.MODID,
        false,
        false,
        "ToroHUD");
    titleLine2 = ConfigurationHandler.config.getConfigFile().getAbsolutePath();
  }
}
