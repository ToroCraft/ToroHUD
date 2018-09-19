package net.torocraft.torohud.proxy;

import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItemFrame;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.torocraft.torohud.ToroHUD;
import net.torocraft.torohud.config.ConfigurationHandler;
import net.torocraft.torohud.gui.GuiEntityStatus;
import net.torocraft.torohud.network.MessageEntityStatsRequest;
import net.torocraft.torohud.render.DamageParticle;

public class ClientProxy extends CommonProxy {

  private GuiEntityStatus entityStatusGUI;
  private Minecraft mc = Minecraft.getMinecraft();
  private Entity pointedEntity;
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
  public void setEntityInCrosshairs() {
    RayTraceResult r = getMouseOver(1.0f);
    if (r != null && RayTraceResult.Type.ENTITY.equals(r.typeOfHit)) {
      if (r.entityHit instanceof EntityLivingBase) {
        requestEntityUpdate(r.entityHit);
        entityStatusGUI.setEntity((EntityLivingBase) r.entityHit);
      }
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

  @Nullable
  @SideOnly(Side.CLIENT)
  public RayTraceResult rayTrace(Entity e, double blockReachDistance, float partialTicks) {
    Vec3d vec3d = e.getPositionEyes(partialTicks);
    Vec3d vec3d1 = e.getLook(partialTicks);
    Vec3d vec3d2 = vec3d.addVector(vec3d1.x * blockReachDistance, vec3d1.y * blockReachDistance, vec3d1.z * blockReachDistance);
    return mc.world.rayTraceBlocks(vec3d, vec3d2, false, true, true);
  }

  public RayTraceResult getMouseOver(float partialTicks) {
    RayTraceResult objectMouseOver;
    Entity observer = this.mc.getRenderViewEntity();

    if (observer == null || this.mc.world == null) {
      return null;
    }

    this.mc.pointedEntity = null;
    double reachDistance = 50;

    objectMouseOver = rayTrace(observer, reachDistance, partialTicks);

    Vec3d observerPositionEyes = observer.getPositionEyes(partialTicks);

    double distance = reachDistance;

    if (objectMouseOver != null) {
      distance = objectMouseOver.hitVec.distanceTo(observerPositionEyes);
    }

    Vec3d lookVector = observer.getLook(partialTicks);
    Vec3d lookVectorFromEyePosition = observerPositionEyes.addVector(lookVector.x * reachDistance, lookVector.y * reachDistance,
        lookVector.z * reachDistance);
    this.pointedEntity = null;
    Vec3d hitVector = null;
    List<Entity> list = this.mc.world.getEntitiesInAABBexcluding(observer,
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
          this.pointedEntity = entity1;
          hitVector = raytraceresult == null ? observerPositionEyes : raytraceresult.hitVec;
          d2 = 0.0D;
        }
      } else if (raytraceresult != null) {
        double d3 = observerPositionEyes.distanceTo(raytraceresult.hitVec);

        if (d3 < d2 || d2 == 0.0D) {
          if (entity1.getLowestRidingEntity() == observer.getLowestRidingEntity() && !observer.canRiderInteract()) {
            if (d2 == 0.0D) {
              this.pointedEntity = entity1;
              hitVector = raytraceresult.hitVec;
            }
          } else {
            this.pointedEntity = entity1;
            hitVector = raytraceresult.hitVec;
            d2 = d3;
          }
        }
      }
    }

    objectMouseOver = new RayTraceResult(this.pointedEntity, hitVector);

    if (this.pointedEntity instanceof EntityLivingBase || this.pointedEntity instanceof EntityItemFrame) {
      this.mc.pointedEntity = this.pointedEntity;
    }

    return objectMouseOver;
  }

  @Override
  public EntityPlayer getPlayer() {
    return Minecraft.getMinecraft().player;
  }


}