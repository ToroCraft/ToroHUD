package net.torocraft.torohud.display;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.monster.EntitySlime;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;

public abstract class AbstractEntityDisplay implements IDisplay {

  protected EntityLivingBase entity;

  public static Relation determineRelation(Entity entity) {
    if (entity instanceof EntityMob) {
      return Relation.FOE;
    } else if (entity instanceof EntitySlime) {
      return Relation.FOE;
    } else if (entity instanceof EntityGhast) {
      return Relation.FOE;
    } else if (entity instanceof EntityAnimal) {
      return Relation.FRIEND;
    } else if (entity instanceof EntitySquid) {
      return Relation.FRIEND;
    } else if (entity instanceof EntityAmbientCreature) {
      return Relation.FRIEND;
    } else {
      return Relation.UNKNOWN;
    }
  }

  protected Relation determineRelation() {
   return determineRelation(entity);
  }

  @Override
  public void setEntity(EntityLivingBase entity) {
    this.entity = entity;
  }

  public String getEntityName() {
    if (entity == null) {
      return "";
    }
    return entity.getDisplayName().getFormattedText();
  }

  public enum Relation {
    FRIEND, FOE, UNKNOWN
  }
}