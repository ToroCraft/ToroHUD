package net.torocraft.torohud.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.torocraft.torohud.ToroHUD;
import net.torocraft.torohud.network.MessageLivingHurt;

@Mod.EventBusSubscriber()
public class Events {

  @SubscribeEvent
  public static void displayDamage(LivingHurtEvent event) {
    EntityLivingBase e = event.getEntityLiving();
    if (e.world.isRemote) {
      return;
    }
    TargetPoint around = new TargetPoint(e.dimension, e.posX, e.posY, e.posZ, 100);
    MessageLivingHurt message = new MessageLivingHurt(e.getEntityId(), event.getAmount());
    ToroHUD.NETWORK.sendToAllAround(message, around);
  }

  @SubscribeEvent
  public static void displayEntityStatus(RenderGameOverlayEvent.Pre event) {
    if (event.getType() != ElementType.CHAT) {
      return;
    }
    ToroHUD.PROXY.setEntityInCrosshairs();
  }

}
