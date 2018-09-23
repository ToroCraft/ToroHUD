package net.torocraft.torohud;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.torocraft.torohud.proxy.CommonProxy;

@Mod(modid = ToroHUD.MODID, name = ToroHUD.MODNAME, version = ToroHUD.VERSION)

public class ToroHUD {

  public static final String MODID = "torohud";
  public static final String VERSION = "1.12.2-2";
  public static final String MODNAME = "ToroHUD";

  @SidedProxy(clientSide = "net.torocraft.torohud.proxy.ClientProxy", serverSide = "net.torocraft.torohud.proxy.ServerProxy")
  public static CommonProxy PROXY;

  @Instance(value = ToroHUD.MODID)
  public static ToroHUD INSTANCE;

  public static SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel(MODID);

  public static MinecraftServer SERVER;

  @EventHandler
  public void preInit(FMLPreInitializationEvent e) {
    PROXY.preInit(e);

    //ConfigurationHandler.init(e.getSuggestedConfigurationFile());
    //MinecraftForge.EVENT_BUS.register(new ConfigurationHandler());
  }

  @EventHandler
  public void init(FMLInitializationEvent e) {
    PROXY.init(e);
  }

  @EventHandler
  public void postInit(FMLPostInitializationEvent e) {
    PROXY.postInit(e);
  }

  @EventHandler
  public void serverLoad(FMLServerStartingEvent e) {
    SERVER = e.getServer();
  }

}
