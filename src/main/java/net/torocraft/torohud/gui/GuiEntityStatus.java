package net.torocraft.torohud.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.torocraft.torohud.ToroHUD;
import net.torocraft.torohud.display.BarDisplay;
import net.torocraft.torohud.display.EntityDisplay;
import net.torocraft.torohud.display.IDisplay;
import net.torocraft.torohud.display.PotionDisplay;

@Config(modid = ToroHUD.MODID, name = "GUI Settings")
public class GuiEntityStatus extends Gui {

  //
//
//
  @Name("Disable GUI")
  public static boolean disableGui = false;// config.getString("Health Bar Display", Configuration.CATEGORY_CLIENT, "HEARTS", "Display Health Bars", new String[]{"BAR", "OFF"});

//  statusDisplayPosition = config
//      .getString("Health Bar Position", Configuration.CATEGORY_CLIENT, "TOP LEFT", "Location of Health Bar", new String[]{"TOP LEFT", "TOP CENTER", "TOP RIGHT", "BOTTOM LEFT", "BOTTOM RIGHT"});


  @Name("X Offset")
  public static int xOffset = 0; //config.getInt("Health Bar X", Configuration.CATEGORY_CLIENT, 0, -20000, 20000, "With CUSTOM position, sets X position of Health Bar");

  @Name("Y Offset")
  public static int yOffset = 0; // config.getInt("Health Bar Y", Configuration.CATEGORY_CLIENT, 0, -20000, 20000, "With CUSTOM position, sets Y position of Health Bar");


  @Name("GUI Position")
  public static GuiAnchor guiPosition = GuiAnchor.TOP_LEFT;

  public enum GuiAnchor {TOP_LEFT, TOP_CENTER, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT}

  @Name("Hide Delay")
  @Comment("Delays hiding the dialog for the given number of milliseconds")
  @RangeInt(min = 50, max = 5000)
  public static int hideDelay = 300; //config.getInt("Hide Delay", Configuration.CATEGORY_CLIENT, 400, 50, 5000, );

  public enum Skin {NONE, BASIC}

  @Name("Background Skin Selection")
  public static Skin skin = Skin.BASIC; // config.getString("Skin", Configuration.CATEGORY_CLIENT, "BASIC", "Background Skin Selection", new String[]{"NONE", "BASIC", "HEAVY"});

  private static final int PADDING_FROM_EDGE = 3;
  private static final ResourceLocation SKIN_BASIC = new ResourceLocation(ToroHUD.MODID, "textures/gui/default_skin_basic.png");
  private static final ResourceLocation SKIN_HEAVY = new ResourceLocation(ToroHUD.MODID, "textures/gui/default_skin_heavy.png");

  private final Minecraft mc;
  private final IDisplay entityDisplay;
  private final IDisplay potionDisplay;
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
    potionDisplay = new PotionDisplay(mc, this);
    barDisplay = new BarDisplay(mc, this);

    entityDisplay.setPosition(50, 50);
    potionDisplay.setPosition(25, 200);
    barDisplay.setPosition(25, 200);
  }

  @SubscribeEvent
  public void drawHealthBar(RenderGameOverlayEvent.Pre event) {
    if (disableGui || !showHealthBar || event.getType() != ElementType.CHAT) {
      return;
    }
    updateGuiAge();
    updatePositions();
    drawSkin();
    draw();
  }

  private void drawSkin() {
    if (skin.equals(Skin.NONE) || !EntityDisplay.showEntityModel) {
      return;
    }
    mc.getTextureManager().bindTexture(SKIN_BASIC);
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    Gui.drawModalRectWithCustomSizedTexture(screenX - 10, screenY - 10, 0.0f, 0.0f, 160, 60, 160, 60);
  }

  private void updateGuiAge() {
    age = age + 15;
    if (age > hideDelay) {
      hideHealthBar();
    }
  }

  private void updatePositions() {
    adjustForDisplayPositionSetting();

    x = screenX;
    y = screenY;

    if (EntityDisplay.showEntityModel) {
      entityDisplay.setPosition(x, y);
      x += 40;
    }

    if (guiPosition.equals(GuiAnchor.BOTTOM_LEFT) || guiPosition.equals(GuiAnchor.BOTTOM_RIGHT)) {
      y += 6;
    }

    barDisplay.setPosition(x, y);
    potionDisplay.setPosition(x, y);
  }

  private void draw() {
    if (EntityDisplay.showEntityModel) {
      entityDisplay.draw();
    }
    barDisplay.draw();
    potionDisplay.draw();
  }

  private void adjustForDisplayPositionSetting() {

    if (EntityDisplay.showEntityModel) {
      displayHeight = 40;
      displayWidth = 140;
    } else {
      displayHeight = 32;
      displayWidth = 100;
    }

    ScaledResolution viewport = new ScaledResolution(mc);
    String displayPosition = guiPosition.toString();

    int sh = viewport.getScaledHeight();
    int sw = viewport.getScaledWidth();

    if (displayPosition.contains("TOP")) {
      screenY = PADDING_FROM_EDGE;
    }

    if (displayPosition.contains("BOTTOM")) {
      screenY = sh - displayHeight - PADDING_FROM_EDGE;
    }

    if (displayPosition.contains("LEFT")) {
      screenX = PADDING_FROM_EDGE;
    }

    if (displayPosition.contains("RIGHT")) {
      screenX = sw - displayWidth - PADDING_FROM_EDGE;
    }

    if (displayPosition.contains("CENTER")) {
      screenX = (sw - displayWidth) / 2;
    }

    screenX += xOffset;
    screenY += yOffset;
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
    barDisplay.setEntity(entity);
    potionDisplay.setEntity(entity);
  }
}
