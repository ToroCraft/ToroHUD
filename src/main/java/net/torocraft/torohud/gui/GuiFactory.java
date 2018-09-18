package net.torocraft.torohud.gui;

import java.util.Set;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.IModGuiFactory;
import net.torocraft.torohud.config.GuiConfigToroHealth;

public class GuiFactory implements IModGuiFactory {

  @Override
  public void initialize(Minecraft minecraftInstance) {

  }

  @Override
  public boolean hasConfigGui() {
    return true;
  }

  @Override
  public GuiScreen createConfigGui(GuiScreen parentScreen) {
    return new GuiConfigToroHealth(parentScreen);
  }

  @Override
  public Set<RuntimeOptionCategoryElement> runtimeGuiCategories() {
    return null;
  }

}
