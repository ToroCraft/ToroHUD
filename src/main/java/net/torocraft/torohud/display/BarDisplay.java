package net.torocraft.torohud.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.torocraft.torohud.gui.HealthBars;
import net.torocraft.torohud.conf.HealthBarGuiConf;

public class BarDisplay extends AbstractEntityDisplay implements IDisplay {

  private static final ResourceLocation ICON_TEXTURES = new ResourceLocation("textures/gui/icons.png");

  private static final int BAR_WIDTH = 92;

  private final Minecraft mc;
  private final Gui gui;
  private int y;
  private int barX;
  private int barY;

  public BarDisplay(Minecraft mc, Gui gui) {
    this.mc = mc;
    this.gui = gui;
  }

  @Override
  public void setPosition(int x, int y) {
    this.y = y;
    barX = x + 4;
    barY = y + 12;
  }

  @Override
  public void draw() {
    if (entity == null) {
      return;
    }
    render();
  }

  public void render() {
    String name = getEntityName();
    String health = (int) Math.ceil(entity.getHealth()) + "/" + (int) entity.getMaxHealth();

    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

    GlStateManager.pushMatrix();
    GlStateManager.scale(HealthBarGuiConf.healthScale, HealthBarGuiConf.healthScale, HealthBarGuiConf.healthScale);
    HealthBars.drawEntityHealthBarInGui(gui, entity, barX + HealthBarGuiConf.healthXOffset, barY + HealthBarGuiConf.healthXOffset);
    GlStateManager.popMatrix();

    if(!HealthBarGuiConf.hideName)
    {
      GlStateManager.pushMatrix();
      GlStateManager.scale(HealthBarGuiConf.nameScale, HealthBarGuiConf.nameScale, HealthBarGuiConf.nameScale);
      mc.fontRenderer.drawStringWithShadow(name, barX + HealthBarGuiConf.nameXOffset, y + 2 + HealthBarGuiConf.nameYOffset, 16777215);
      GlStateManager.popMatrix();
      
      barX += mc.fontRenderer.getStringWidth(name) + 5 + HealthBarGuiConf.nameXOffset;
    }

    if(!HealthBarGuiConf.hideHealthNo)
    {
      renderHeartIcon(barX + HealthBarGuiConf.healthNoXOffset, y + 1 + HealthBarGuiConf.healthNoYOffset);
      barX += 10 + HealthBarGuiConf.healthNoXOffset;

      mc.fontRenderer.drawStringWithShadow(health, barX, y + 2, 0xe0e0e0);
      barX += mc.fontRenderer.getStringWidth(health) + 5;
    }

    if(!HealthBarGuiConf.hideArmor) 
    { 
      renderArmorIcon(barX + HealthBarGuiConf.armorXOffset, y + 1 + HealthBarGuiConf.armorYOffset);
      barX += 10 + HealthBarGuiConf.armorXOffset;

      mc.fontRenderer.drawStringWithShadow(entity.getTotalArmorValue() + "", barX, y + 2, 0xe0e0e0);
    }
  }

  private void renderArmorIcon(int x, int y) {
    mc.getTextureManager().bindTexture(ICON_TEXTURES);
    gui.drawTexturedModalRect(x, y, 34, 9, 9, 9);
  }

  private void renderHeartIcon(int x, int y) {
    mc.getTextureManager().bindTexture(ICON_TEXTURES);
    gui.drawTexturedModalRect(x, y, 16 + 36, 0, 9, 9);
  }

}