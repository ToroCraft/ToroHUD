package net.torocraft.torohud.util;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.torohud.conf.HealthBarGuiConf;

@SideOnly(Side.CLIENT)
public class RayTrace {

  private static Minecraft mc = Minecraft.getMinecraft();

  public static EntityLivingBase getEntityInCrosshairs() {
    RayTraceResult r = getMouseOver();
    if (r != null && RayTraceResult.Type.ENTITY.equals(r.typeOfHit)) {
      if (r.entityHit instanceof EntityLivingBase) {
        return (EntityLivingBase) r.entityHit;
      }
    }
    return null;
  }

  @Nullable
  private static RayTraceResult rayTrace(Entity e, double blockReachDistance, float partialTicks) {
    Vec3d vec3d = e.getPositionEyes(partialTicks);
    Vec3d vec3d1 = e.getLook(partialTicks);
    Vec3d vec3d2 = vec3d.addVector(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
    return mc.world.rayTraceBlocks(vec3d, vec3d2, false, true, true);
  }

  private static RayTraceResult getMouseOver() {
    Entity pointedEntity;
    float partialTicks = 1f;

    RayTraceResult objectMouseOver;
    Entity observer = mc.getRenderViewEntity();

    if (observer == null || mc.world == null) {
      return null;
    }

    mc.pointedEntity = null;
    double reachDistance = HealthBarGuiConf.distance;

    objectMouseOver = rayTrace(observer, reachDistance, partialTicks);

    Vec3d observerPositionEyes = observer.getPositionEyes(partialTicks);

    double distance = reachDistance;

    if (objectMouseOver != null) {
      distance = objectMouseOver.hitVec.distanceTo(observerPositionEyes);
    }

    Vec3d lookVector = observer.getLook(partialTicks);
    Vec3d lookVectorFromEyePosition = observerPositionEyes.addVector(lookVector.x * reachDistance, lookVector.y * reachDistance,
        lookVector.z * reachDistance);

    pointedEntity = null;
    Vec3d hitVector = null;

    List<Entity> list = mc.world.getEntitiesInAABBexcluding(observer,
        observer.getEntityBoundingBox()
            .expand(lookVector.x * reachDistance, lookVector.y * reachDistance, lookVector.z * reachDistance)
            .expand(1.0D, 1.0D, 1.0D),
        EntitySelectors.NOT_SPECTATING);
    double d2 = distance;

    for (Entity entity1 : list) {
      AxisAlignedBB axisalignedbb = entity1.getEntityBoundingBox().grow((double) entity1.getCollisionBorderSize());
      RayTraceResult raytraceresult = axisalignedbb.calculateIntercept(observerPositionEyes, lookVectorFromEyePosition);

      if (axisalignedbb.contains(observerPositionEyes)) {
        if (d2 >= 0.0D) {
          pointedEntity = entity1;
          hitVector = raytraceresult == null ? observerPositionEyes : raytraceresult.hitVec;
          d2 = 0.0D;
        }
      } else if (raytraceresult != null) {
        double d3 = observerPositionEyes.distanceTo(raytraceresult.hitVec);

        if (d3 < d2 || d2 == 0.0D) {
          if (entity1.getLowestRidingEntity() == observer.getLowestRidingEntity() && !observer.canRiderInteract()) {
            if (d2 == 0.0D) {
              pointedEntity = entity1;
              hitVector = raytraceresult.hitVec;
            }
          } else {
            pointedEntity = entity1;
            hitVector = raytraceresult.hitVec;
            d2 = d3;
          }
        }
      }
    }

    objectMouseOver = new RayTraceResult(pointedEntity, hitVector);

    if (pointedEntity instanceof EntityLivingBase || pointedEntity instanceof EntityItemFrame) {
      mc.pointedEntity = pointedEntity;
    }

    return objectMouseOver;
  }

}
