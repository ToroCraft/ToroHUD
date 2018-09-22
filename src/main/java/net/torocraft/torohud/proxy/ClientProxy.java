package net.torocraft.torohud.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.torohud.ToroHUD;
import net.torocraft.torohud.config.ConfigurationHandler;
import net.torocraft.torohud.gui.GuiEntityStatus;
import net.torocraft.torohud.network.MessageEntityStatsRequest;
import net.torocraft.torohud.render.DamageParticle;
import net.torocraft.torohud.util.RayTrace;

public class ClientProxy extends CommonProxy {

  private GuiEntityStatus entityStatusGUI;

  private int entityIdInCrosshairs = 0;
  private int refreshCooldown = 0;

  @Override
  public void preInit(FMLPreInitializationEvent e) {
    super.preInit(e);
    entityStatusGUI = new GuiEntityStatus();
  }

  @Override
  public void postInit(FMLPostInitializationEvent e) {
    super.postInit(e);
    MinecraftForge.EVENT_BUS.register(entityStatusGUI);
  }

  @Override
  public void displayDamageDealt(EntityLivingBase entity, float damage) {
    if (!entity.world.isRemote) {
      return;
    }

    if (!ConfigurationHandler.showDamageParticles) {
      return;
    }

    displayParticle(entity, Math.round(damage));
  }

  private void displayParticle(Entity entity, int damage) {
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

  @Override
  public void updateEntityInCrosshairs() {
    EntityLivingBase entity = RayTrace.getEntityInCrosshairs();
    if (entity != null) {
      requestEntityUpdate(entity);
      entityStatusGUI.setEntity(entity);
    }
  }

  public void requestEntityUpdate(Entity entity) {
    int id = entity.getEntityId();
    if (id != entityIdInCrosshairs) {
      refreshCooldown = 0;
      entityIdInCrosshairs = id;
    }
    if (refreshCooldown < 1) {
      ToroHUD.NETWORK.sendToServer(new MessageEntityStatsRequest(id));
      refreshCooldown = 50;
    }
    refreshCooldown--;
  }

  @Override
  public EntityPlayer getPlayer() {
    return Minecraft.getMinecraft().player;
  }


}