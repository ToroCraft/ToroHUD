package net.torocraft.torohud.display;

import net.minecraft.entity.EntityLivingBase;

public interface IDisplay {

  void setEntity(EntityLivingBase entity);

  void setPosition(int x, int y);

  void draw();
}