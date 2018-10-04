package net.torocraft.torohud.util;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.torocraft.torohud.conf.HealthBarGuiConf;
import org.apache.commons.lang3.ArrayUtils;

public class EntityUtil {

  public static boolean whiteListedEntity(Entity entity) {
    return !ArrayUtils.contains(HealthBarGuiConf.entityBlacklist, getEntityStringId(entity));
  }

  public static String getEntityStringId(Entity entity) {
    try {
      return EntityRegistry.getEntry(entity.getClass()).getRegistryName().toString();
    } catch (Exception e) {
      return "unknown:unknown";
    }
  }
}
