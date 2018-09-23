package net.torocraft.torohud.proxy;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.torohud.ToroHUD;
import net.torocraft.torohud.gui.GuiEntityStatus;
import net.torocraft.torohud.network.MessageEntityStatsRequest;
import net.torocraft.torohud.render.DamageParticle;
import net.torocraft.torohud.util.EntityUtil;
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
    DamageParticle.displayParticle(entity, Math.round(damage));
  }

  @Override
  public void updateEntityInCrosshairs() {
    EntityLivingBase entity = RayTrace.getEntityInCrosshairs();
    if (entity != null && EntityUtil.whiteListedEntity(entity)) {
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