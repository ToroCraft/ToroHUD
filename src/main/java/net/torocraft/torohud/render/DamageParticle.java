package net.torocraft.torohud.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.torohud.conf.ParticlesConf;
import net.torocraft.torohud.conf.ParticlesConf.Color;
import org.lwjgl.opengl.GL11;


@SideOnly(Side.CLIENT)
public class DamageParticle extends Particle {

  protected static final float GRAVITY = 0.1F;
  protected static final float SIZE = 3.0F;
  protected static final int LIFESPAN = 12;
  protected static final double BOUNCE_STRENGTH = 1.5F;

  protected String text;
  protected boolean grow = true;
  protected float scale = 1.0F;
  private int damage;

  public static void displayParticle(Entity entity, int damage) {
    if (!ParticlesConf.showDamageParticles) {
      return;
    }
    if (damage == 0) {
      return;
    }
    World world = entity.world;
    double motionX = world.rand.nextGaussian() * 0.02;
    double motionY = 0.5f;
    double motionZ = world.rand.nextGaussian() * 0.02;
    Particle damageIndicator = new DamageParticle(damage, world, entity.posX, entity.posY + entity.height, entity.posZ, motionX, motionY,
        motionZ);
    Minecraft.getMinecraft().effectRenderer.addEffect(damageIndicator);
  }


  public DamageParticle(int damage, World world, double parX, double parY, double parZ, double parMotionX, double parMotionY, double parMotionZ) {
    super(world, parX, parY, parZ, parMotionX, parMotionY, parMotionZ);
    particleTextureJitterX = 0.0F;
    particleTextureJitterY = 0.0F;
    particleGravity = GRAVITY;
    particleScale = SIZE;
    particleMaxAge = LIFESPAN;
    this.damage = damage;
    this.text = Integer.toString(Math.abs(damage));
  }

  protected DamageParticle(World worldIn, double posXIn, double posYIn, double posZIn) {
    this(0, worldIn, posXIn, posYIn, posZIn, 0, 0, 0);
  }

  @Override
  public void renderParticle(final BufferBuilder renderer, final Entity entity, final float x, final float y,
      final float z, final float dX, final float dY, final float dZ) {
    float rotationYaw = (-Minecraft.getMinecraft().player.rotationYaw);
    float rotationPitch = Minecraft.getMinecraft().player.rotationPitch;

    final float locX = ((float) (this.prevPosX + (this.posX - this.prevPosX) * x - interpPosX));
    final float locY = ((float) (this.prevPosY + (this.posY - this.prevPosY) * y - interpPosY));
    final float locZ = ((float) (this.prevPosZ + (this.posZ - this.prevPosZ) * z - interpPosZ));

    GL11.glPushMatrix();
    if (ParticlesConf.visibleThroughWalls) {
      GL11.glDepthFunc(519);
    } else {
      GL11.glDepthFunc(515);
    }
    GL11.glTranslatef(locX, locY, locZ);
    GL11.glRotatef(rotationYaw, 0.0F, 1.0F, 0.0F);
    GL11.glRotatef(rotationPitch, 1.0F, 0.0F, 0.0F);

    GL11.glScalef(-1.0F, -1.0F, 1.0F);
    GL11.glScaled(this.particleScale * 0.008D, this.particleScale * 0.008D, this.particleScale * 0.008D);
    GL11.glScaled(this.scale, this.scale, this.scale);

    OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, 240.0F, 0.003662109F);
    GL11.glEnable(3553);
    GL11.glDisable(3042);
    GL11.glDepthMask(true);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glEnable(3553);
    GL11.glEnable(2929);
    GL11.glDisable(2896);
    GL11.glBlendFunc(770, 771);
    GL11.glEnable(3042);
    GL11.glEnable(3008);
    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);

    Color color = ParticlesConf.damageColor;
    if (damage < 0) {
      color = ParticlesConf.healColor;
    }

    final FontRenderer fontRenderer = Minecraft.getMinecraft().fontRenderer;
    fontRenderer.drawStringWithShadow(this.text,
        -MathHelper.floor(fontRenderer.getStringWidth(this.text) / 2.0F) + 1, -MathHelper.floor(fontRenderer.FONT_HEIGHT / 2.0F) + 1, mapColor(color));

    GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
    GL11.glDepthFunc(515);

    GL11.glPopMatrix();
    if (this.grow) {
      this.particleScale *= 1.08F;
      if (this.particleScale > SIZE * 3.0D) {
        this.grow = false;
      }
    } else {
      this.particleScale *= 0.96F;
    }
  }

  public int getFXLayer() {
    return 3;
  }

  private static int mapColor(Color color) {
    switch (color) {
      case RED:
        return 0xff0000;
      case GREEN:
        return 0x00ff00;
      case BLUE:
        return 0x0000ff;
      case YELLOW:
        return 0xffff00;
      case ORANGE:
        return 0xffa500;
      case BLACK:
        return 0x000000;
      case PURPLE:
        return 0x960096;
      default:
        return 0xffffff;
    }
  }

}
