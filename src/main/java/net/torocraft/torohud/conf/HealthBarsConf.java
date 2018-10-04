package net.torocraft.torohud.conf;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.torocraft.torohud.ToroHUD;
import net.torocraft.torohud.conf.ParticlesConf.Color;

@Config(modid = ToroHUD.MODID, name = ToroHUD.MODID + "/healthBars")
@Config.LangKey("healthBars.config.title")
public class HealthBarsConf {

  public enum Mode {NONE, WHEN_HOLDING_WEAPON, ALWAYS, WHEN_HURT, WHEN_HURT_TEMP}

  public enum NumberType {NONE, LAST, CUMULATIVE}

  @Name("Show Bars Above Entities")
  public static Mode showBarsAboveEntities = Mode.WHEN_HOLDING_WEAPON;

  @Name("Damage Number Type")
  public static NumberType numberType = NumberType.LAST;

  @Name("Additional Weapons")
  @Comment("When using WHEN_HOLDING_WEAPON to show entity bars, more items can be added here to be treated as weapons.")
  public static String[] additionalWeaponItems = {};

}
