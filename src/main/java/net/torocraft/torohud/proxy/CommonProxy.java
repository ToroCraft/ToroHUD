package net.torocraft.torohud.proxy;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.torocraft.torohud.network.MessageEntityStatsRequest;
import net.torocraft.torohud.network.MessageEntityStatsResponse;
import net.torocraft.torohud.network.MessageLivingHurt;

public class CommonProxy {

  public void preInit(FMLPreInitializationEvent e) {

  }

  public void init(FMLInitializationEvent e) {
    int packetId = 0;
    MessageLivingHurt.init(packetId++);
    MessageEntityStatsRequest.init(packetId++);
    MessageEntityStatsResponse.init(packetId++);
  }

  public void postInit(FMLPostInitializationEvent e) {

  }

  public void displayDamageDealt(EntityLivingBase entity, float damage) {

  }

  public void updateEntityInCrosshairs() {

  }

  public EntityPlayer getPlayer() {
    return null;
  }

}
