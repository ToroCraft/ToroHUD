package net.torocraft.torohud.conf;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Name;
import net.torocraft.torohud.ToroHUD;

@Config(modid = ToroHUD.MODID, name = ToroHUD.MODID + "/particles")
@Config.LangKey("particles.config.title")
public class ParticlesConf {

  public enum Color {RED, GREEN, BLUE, YELLOW, ORANGE, BLACK, PURPLE}

  @Name("Show Damage Particles")
  public static boolean showDamageParticles = true;

  @Name("Heal Color")
  public static Color healColor = Color.GREEN;

  @Name("Damage Color")
  public static Color damageColor = Color.RED;

  @Name("Visible Throw Walls")
  public static boolean visibleThroughWalls = false;

}
