package net.torocraft.torohud.display;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.torocraft.torohud.ToroHUD;

public class BarDisplay extends AbstractEntityDisplay implements IDisplay {

  private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation(ToroHUD.MODID, "textures/gui/bars.png");

  private static final int BAR_WIDTH = 92;

  private final Minecraft mc;
  private final Gui gui;
  private int y;
  private int barX;
  private int barY;

  private static final float HEALTH_INDICATOR_DELAY = 45;
  private float previousHealth;
  private float previousHealthDelay;
  private int entityId;

  public BarDisplay(Minecraft mc, Gui gui) {
    this.mc = mc;
    this.gui = gui;
    resetBarState();
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
      //resetBarState();
      return;
    }
    if (entity.getEntityId() != entityId) {
      resetBarState();
      entityId = entity.getEntityId();
      return;
    }
    render();
  }

  public void render() {
    String name = getEntityName();
    String health = (int) Math.ceil(entity.getHealth()) + "/" + (int) entity.getMaxHealth();

    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    mc.getTextureManager().bindTexture(GUI_BARS_TEXTURES);
    renderHealthBar();
    mc.fontRenderer.drawStringWithShadow(name + "  " + health + "", barX, y + 2, 16777215);
  }

  private void resetBarState() {
    previousHealth = -1;
    previousHealthDelay = HEALTH_INDICATOR_DELAY;
  }

  private void renderHealthBar() {
    if (previousHealth == entity.getHealth() || previousHealth == -1) {
      previousHealthDelay = HEALTH_INDICATOR_DELAY;
      previousHealth = entity.getHealth();
    } else if (previousHealthDelay > 0) {
      previousHealthDelay--;
    } else if (previousHealthDelay < 1 && previousHealth > entity.getHealth()) {
      previousHealth -= 0.1f;
    } else {
      previousHealth = entity.getHealth();
      previousHealthDelay = HEALTH_INDICATOR_DELAY;
    }

    Color color = determineColor();
    float percent = entity.getHealth() / entity.getMaxHealth();
    float percent2 = previousHealth / entity.getMaxHealth();

    renderBarBacker(color);
    renderBar(Color.YELLOW, percent2);
    renderBar(color, percent);
  }


  private void renderBarBacker(Color color) {
    gui.drawTexturedModalRect(barX, barY, 0, color.ordinal() * 5 * 2, BAR_WIDTH, 5);
  }

  private void renderBar(Color color, float percent) {
    int healthWidth = (int) (percent * BAR_WIDTH);
    if (healthWidth > 0) {
      gui.drawTexturedModalRect(barX, barY, 0, color.ordinal() * 5 * 2 + 5, healthWidth, 5);
    }
  }

  private Color determineColor() {
    switch (determineRelation()) {
      case FOE:
        return Color.RED;
      case FRIEND:
        return Color.GREEN;
      default:
        return Color.WHITE;
    }
  }

  public enum Color {
    PINK, BLUE, RED, GREEN, YELLOW, PURPLE, WHITE;
  }

  public enum Relation {
    FRIEND, FOE, UNKNOWN
  }

}