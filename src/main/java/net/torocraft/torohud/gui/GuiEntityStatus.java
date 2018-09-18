package net.torocraft.torohud.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.torohud.ToroHUD;
import net.torocraft.torohud.config.ConfigurationHandler;
import net.torocraft.torohud.display.BarDisplay;
import net.torocraft.torohud.display.EntityDisplay;
import net.torocraft.torohud.display.HeartsDisplay;
import net.torocraft.torohud.display.NumericDisplay;
import net.torocraft.torohud.display.PotionDisplay;
import net.torocraft.torohud.display.IDisplay;

public class GuiEntityStatus extends Gui {

  private static final int PADDING_FROM_EDGE = 3;
  private static final ResourceLocation SKIN_BASIC = new ResourceLocation(ToroHUD.MODID, "textures/gui/default_skin_basic.png");
  private static final ResourceLocation SKIN_HEAVY = new ResourceLocation(ToroHUD.MODID, "textures/gui/default_skin_heavy.png");

  private final Minecraft mc;
  private final IDisplay entityDisplay;
  private final IDisplay potionDisplay;
  private final IDisplay heartsDisplay;
  private final IDisplay numericDisplay;
  private final IDisplay barDisplay;
  int screenX = PADDING_FROM_EDGE;
  int screenY = PADDING_FROM_EDGE;
  int displayHeight;
  int displayWidth;
  int x, y;
  private EntityLivingBase entity;
  private int age = 0;
  private boolean showHealthBar = false;

  public GuiEntityStatus() {
    this(Minecraft.getMinecraft());
  }

  public GuiEntityStatus(Minecraft mc) {
    this.mc = mc;
    entityDisplay = new EntityDisplay(mc);
    heartsDisplay = new HeartsDisplay(mc, this);
    numericDisplay = new NumericDisplay(mc, this);
    potionDisplay = new PotionDisplay(mc, this);
    barDisplay = new BarDisplay(mc, this);

    entityDisplay.setPosition(50, 50);
    heartsDisplay.setPosition(25, 150);
    potionDisplay.setPosition(25, 160);
    numericDisplay.setPosition(130, 150);
    barDisplay.setPosition(25, 200);
  }

  @SubscribeEvent
  public void drawHealthBar(RenderGameOverlayEvent.Pre event) {
    if (!showHealthBar || event.getType() != ElementType.CHAT) {
      return;
    }
    updateGuiAge();
    updatePositions();
    drawSkin();
    draw();
  }

  private void drawSkin() {
    if (ConfigurationHandler.skin.equals("NONE")) {
      return;
    }

    if (ConfigurationHandler.skin.equals("HEAVY")) {
      mc.getTextureManager().bindTexture(SKIN_HEAVY);
    } else {
      mc.getTextureManager().bindTexture(SKIN_BASIC);
    }

    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    Gui.drawModalRectWithCustomSizedTexture(screenX - 10, screenY - 10, 0.0f, 0.0f, 160, 60, 160, 60);
  }

  private void updateGuiAge() {
    String entityStatusDisplay = ConfigurationHandler.entityStatusDisplay;
    age = age + 15;
    if (age > ConfigurationHandler.hideDelay || entityStatusDisplay.equals("OFF")) {
      hideHealthBar();
    }
  }

  private void updatePositions() {
    adjustForDisplayPositionSetting();

    x = screenX;
    y = screenY;

    if (ConfigurationHandler.showEntityModel) {
      entityDisplay.setPosition(x, y);
      x += 40;
    }

    if (ConfigurationHandler.statusDisplayPosition.contains("BOTTOM")) {
      y += 6;
    }

    numericDisplay.setPosition(x, y);
    barDisplay.setPosition(x, y);
    heartsDisplay.setPosition(x, y);
    potionDisplay.setPosition(x, y);
  }

  private void draw() {
    if (ConfigurationHandler.showEntityModel) {
      entityDisplay.draw();
    }

    if ("NUMERIC".equals(ConfigurationHandler.entityStatusDisplay)) {
      numericDisplay.draw();
    } else if ("BAR".equals(ConfigurationHandler.entityStatusDisplay)) {
      barDisplay.draw();
    } else if ("HEARTS".equals(ConfigurationHandler.entityStatusDisplay)) {
      heartsDisplay.draw();
    }

    potionDisplay.draw();
  }

  private void adjustForDisplayPositionSetting() {

    if (ConfigurationHandler.showEntityModel) {
      displayHeight = 40;
      displayWidth = 140;
    } else {
      displayHeight = 32;
      displayWidth = 100;
    }

    ScaledResolution viewport = new ScaledResolution(mc);
    String displayPosition = ConfigurationHandler.statusDisplayPosition;

    int sh = viewport.getScaledHeight();
    int sw = viewport.getScaledWidth();

    if (displayPosition.contains("TOP") || displayPosition.equals("CUSTOM")) {
      screenY = PADDING_FROM_EDGE;
    }

    if (displayPosition.contains("BOTTOM")) {
      screenY = sh - displayHeight - PADDING_FROM_EDGE;
    }

    if (displayPosition.contains("LEFT") || displayPosition.equals("CUSTOM")) {
      screenX = PADDING_FROM_EDGE;
    }

    if (displayPosition.contains("RIGHT")) {
      screenX = sw - displayWidth - PADDING_FROM_EDGE;
    }

    if (displayPosition.contains("CENTER")) {
      screenX = (sw - displayWidth) / 2;
    }

    screenX += ConfigurationHandler.statusDisplayX;
    screenY += ConfigurationHandler.statusDisplayY;
  }

  private void showHealthBar() {
    showHealthBar = true;
  }

  private void hideHealthBar() {
    showHealthBar = false;
  }

  public void setEntity(EntityLivingBase entityToTrack) {
    showHealthBar();
    age = 0;
    if (entity != null && entity.getUniqueID().equals(entityToTrack.getUniqueID())) {
      return;
    }
    entity = entityToTrack;
    entityDisplay.setEntity(entity);
    heartsDisplay.setEntity(entity);
    numericDisplay.setEntity(entity);
    barDisplay.setEntity(entity);
    potionDisplay.setEntity(entity);
  }
}
