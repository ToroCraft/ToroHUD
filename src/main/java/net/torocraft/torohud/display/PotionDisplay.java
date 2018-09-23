package net.torocraft.torohud.display;

import com.google.common.collect.Ordering;
import java.util.Collection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.torocraft.torohud.network.MessageEntityStatsResponse;

public class PotionDisplay extends AbstractEntityDisplay implements IDisplay {

  public static final ResourceLocation INVENTORY_BACKGROUND = new ResourceLocation("textures/gui/container/inventory.png");
  private final Minecraft mc;
  private final Gui gui;
  private int x, originX = 100;
  private int y, originY = 100;

  public PotionDisplay(Minecraft mc, Gui gui) {
    this.mc = mc;
    this.gui = gui;
  }

  @Override
  public void setPosition(int x, int y) {
    originX = x;
    originY = y;
    resetToOrigin();
  }

  @Override
  public void draw() {
    if (entity == null) {
      return;
    }

    resetToOrigin();
    drawEffects();
  }

  private void resetToOrigin() {
    x = originX;
    y = originY;
  }

  private void drawName() {
    gui.drawString(mc.fontRenderer, getEntityName(), x, y, 0xFFFFFF);
    y += 10;
  }

  private static final int Y_OFFSET = 18;
  private static final int X_OFFSET = 3;

  private void drawEffects() {
    Collection<PotionEffect> potions = MessageEntityStatsResponse.POTIONS;

    if (potions == null || potions.size() < 1) {
      return;
    }

    int index = 0;
    int x = this.x + X_OFFSET;
    int y = this.y + Y_OFFSET;

    for (PotionEffect potion : Ordering.natural().sortedCopy(potions)) {

      int textureIndex = potion.getPotion().getStatusIconIndex();
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
      GlStateManager.disableLighting();
      mc.renderEngine.bindTexture(INVENTORY_BACKGROUND);
      gui.drawTexturedModalRect(x, y, textureIndex % 8 * 18, 198 + textureIndex / 8 * 18, 18, 18);

      String duration = Potion.getPotionDurationString(potion, 1.0F);
      mc.fontRenderer.drawStringWithShadow(duration, x, y + 18, 0xe0e0e0);

      mc.fontRenderer.drawStringWithShadow(getAmplifierText(potion), x, y, 0xc0c0c0);

      x += 24;
    }
  }

  private String getAmplifierText(PotionEffect potioneffect) {
    if (potioneffect.getAmplifier() == 1) {
      return I18n.format("enchantment.level.2");
    } else if (potioneffect.getAmplifier() == 2) {
      return I18n.format("enchantment.level.3");
    } else if (potioneffect.getAmplifier() == 3) {
      return I18n.format("enchantment.level.4");
    }
    return I18n.format("enchantment.level.1");
  }

}