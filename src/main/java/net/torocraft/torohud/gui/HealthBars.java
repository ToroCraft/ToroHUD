package net.torocraft.torohud.gui;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.torocraft.torohud.ToroHUD;
import net.torocraft.torohud.config.ConfigurationHandler;
import net.torocraft.torohud.display.AbstractEntityDisplay;
import org.lwjgl.opengl.GL11;

@Mod.EventBusSubscriber(Side.CLIENT)
public class HealthBars {

  private static final ResourceLocation GUI_BARS_TEXTURES = new ResourceLocation(ToroHUD.MODID, "textures/gui/bars.png");

  private static final float HEALTH_INDICATOR_DELAY = 45;
  private static final float HEALTH_ANIMATION_SPEED = 0.1f;

  private static final int GREEN = 0x00FF00FF;
  private static final int YELLOW = 0xFFFF00FF;
  private static final int WHITE = 0xFFFFFFFF;
  private static final int RED = 0xFF0000FF;
  private static final int DARK_GRAY = 0x505050FF;
  private static final float zLevel = 0;

  private static final int DISPLAY_BAR_RADIUS = 60;
  private static final int DISPLAY_BAR_DIAMETER = DISPLAY_BAR_RADIUS * 2;

  @SubscribeEvent
  public static void onRenderWorldLast(RenderWorldLastEvent event) {
    if (ConfigurationHandler.showBarsAboveEntities) {
      Minecraft mc = Minecraft.getMinecraft();
      Entity viewer = mc.getRenderViewEntity();
      BlockPos pos = new BlockPos(viewer).subtract(new Vec3i(DISPLAY_BAR_RADIUS, DISPLAY_BAR_RADIUS, DISPLAY_BAR_RADIUS));
      AxisAlignedBB box = new AxisAlignedBB(pos).expand(DISPLAY_BAR_DIAMETER, DISPLAY_BAR_DIAMETER, DISPLAY_BAR_DIAMETER);
      List<EntityLivingBase> entities = mc.world.getEntitiesWithinAABB(EntityLivingBase.class, box);
      entities.forEach(e -> HealthBars.drawEntityHealthBarInWorld(e, event.getPartialTicks()));
    }
  }

  private static class State {

    private float previousHealth;
    private float previousHealthDelay;
  }

  private static final Map<Integer, State> states = new HashMap<>();


  @SubscribeEvent
  public static void cleanup(WorldTickEvent event) {
    World world = event.world;
    if (world.getTotalWorldTime() % 500 == 0) {
      states.entrySet().removeIf(e -> stateExpired(world, e.getKey(), e.getValue()));
    }
  }

  private static boolean stateExpired(World world, int id, State state) {
    if (state == null) {
      return true;
    }

    Entity entity = world.getEntityByID(id);

    if (entity == null) {
      return true;
    }

    if (!world.isBlockLoaded(new BlockPos(entity))) {
      return true;
    }

    return entity.isDead;
  }

  private static State getState(Entity entity) {
    int id = entity.getEntityId();
    State state = states.get(id);
    if (state == null) {
      state = new State();
      states.put(id, state);
    }
    return state;
  }

  public static void drawEntityHealthBarInWorld(EntityLivingBase entity, float partialTicks) {
    double x = entity.lastTickPosX + ((entity.posX - entity.lastTickPosX) * partialTicks);
    double y = entity.lastTickPosY + ((entity.posY - entity.lastTickPosY) * partialTicks);
    double z = entity.lastTickPosZ + ((entity.posZ - entity.lastTickPosZ) * partialTicks);
    drawEntityHealthBar(entity, x, y, z, null);
  }

  public static void drawEntityHealthBarInGui(Gui gui, EntityLivingBase entity, int x, int y) {
    drawEntityHealthBar(entity, x, y, 0, gui);
  }

  private static void drawEntityHealthBar(EntityLivingBase entity, double x, double y, double z, Gui gui) {
    State state = getState(entity);

    if (state.previousHealth == entity.getHealth() || state.previousHealth == -1) {
      state.previousHealthDelay = HEALTH_INDICATOR_DELAY;
      state.previousHealth = entity.getHealth();
    } else if (state.previousHealthDelay > 0) {
      state.previousHealthDelay--;
    } else if (state.previousHealthDelay < 1 && state.previousHealth > entity.getHealth()) {
      state.previousHealth -= HEALTH_ANIMATION_SPEED;
    } else {
      state.previousHealth = entity.getHealth();
      state.previousHealthDelay = HEALTH_INDICATOR_DELAY;
    }

    int color = determineColor(entity);
    float percent = entity.getHealth() / entity.getMaxHealth();
    float percent2 = state.previousHealth / entity.getMaxHealth();
    int zOffset = 0;

    drawBarInWorld(entity, gui, x, y, z, 1, DARK_GRAY, zOffset++);
    drawBarInWorld(entity, gui, x, y, z, percent2, WHITE, zOffset++);
    drawBarInWorld(entity, gui, x, y, z, percent, color, zOffset);
  }

  private static final float verticalMargin = 0.35f;
  private static final double fullSize = 40;
  private static final double halfSize = fullSize / 2;
  private static final float scale = 0.03f;


  private static void drawBarInWorld(EntityLivingBase entity, Gui gui, double x, double y, double z, float percent, int color, int zOffset) {

    float c = 0.00390625f;
    int u = 0;
    int v = 6 * 5 * 2 + 5;
    int uw = MathHelper.ceil(92 * percent);
    int vh = 5;

    double size = percent * fullSize;
    double h = 3;

    float r = (color >> 24 & 255) / 255.0F;
    float g = (color >> 16 & 255) / 255.0F;
    float b = (color >> 8 & 255) / 255.0F;
    float a = (color & 255) / 255.0F;

    Minecraft.getMinecraft().renderEngine.bindTexture(GUI_BARS_TEXTURES);
    GlStateManager.color(r, g, b, a);

    if (gui != null) {
      gui.drawTexturedModalRect((int) x, (int) y, u, v, uw, vh);
      return;
    }

    RenderManager renderManager = Minecraft.getMinecraft().getRenderManager();

    double relX = x - renderManager.viewerPosX;
    double relY = y - renderManager.viewerPosY + entity.height + verticalMargin;
    double relZ = z - renderManager.viewerPosZ;

    GlStateManager.pushMatrix();
    GlStateManager.translate(relX, relY, relZ);

    GL11.glNormal3f(0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(-renderManager.playerViewY, 0.0F, 1.0F, 0.0F);
    GlStateManager.rotate(renderManager.playerViewX, 1.0F, 0.0F, 0.0F);
    GlStateManager.translate(0, 0, -zOffset * 0.001);

    GlStateManager.scale(-scale, -scale, scale);
    boolean lighting = GL11.glGetBoolean(GL11.GL_LIGHTING);
    GlStateManager.disableLighting();
    GlStateManager.enableBlend();
    GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
    Tessellator tessellator = Tessellator.getInstance();
    BufferBuilder buffer = tessellator.getBuffer();

    buffer.begin(7, DefaultVertexFormats.POSITION_TEX);

    buffer.pos(-halfSize, 0, 0.0D).tex(u * c, v * c).endVertex();
    buffer.pos(-halfSize, h, 0.0D).tex(u * c, (v + vh) * c).endVertex();
    buffer.pos(-halfSize + size, h, 0.0D).tex((u + uw) * c, (v + vh) * c).endVertex();
    buffer.pos(-halfSize + size, 0, 0.0D).tex(((u + uw) * c), v * c).endVertex();

    tessellator.draw();

    GlStateManager.disableBlend();
    GlStateManager.enableDepth();
    GlStateManager.depthMask(true);
    if (lighting) {
      GlStateManager.enableLighting();
    }
    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
    GlStateManager.popMatrix();
  }

  private static int determineColor(EntityLivingBase entity) {
    switch (AbstractEntityDisplay.determineRelation(entity)) {
      case FOE:
        return RED;
      case FRIEND:
        return GREEN;
      default:
        return RED;
    }
  }

}
