package net.torocraft.torohud.conf;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.torocraft.torohud.ToroHUD;
import net.torocraft.torohud.conf.HealthBarsConf.NumberType;

@Config(modid = ToroHUD.MODID, name = ToroHUD.MODID + "/healthBarGui")
@Config.LangKey("healthBarGui.config.title")
public class HealthBarGuiConf {

  public enum GuiAnchor {TOP_LEFT, TOP_CENTER, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT}

  public enum Skin {NONE, BASIC}

  @Name("Gui Scale")
  public static float scale = 1f;

  @Name("Distance")
  public static float distance = 60f;

  @Name("Show 3D Model of Entity")
  public static boolean showEntityModel = true; // config.getBoolean("Show 3D Model of Entity", Configuration.CATEGORY_CLIENT, true, "Shows a 3D model of the entity being targeted");

  @Name("Disable GUI")
  public static boolean disableGui = false;

  @Name("X Offset")
  public static int xOffset = 0;

  @Name("Y Offset")
  public static int yOffset = 0;

  @Name("GUI Position")
  public static GuiAnchor guiPosition = GuiAnchor.TOP_LEFT;

  @Name("Hide Delay")
  @Comment("Delays hiding the dialog for the given number of milliseconds")
  @RangeInt(min = 50, max = 5000)
  public static int hideDelay = 300;

  @Name("Background Skin Selection")
  public static Skin skin = Skin.BASIC;

  @Name("Entity Black List")
  public static String[] entityBlacklist = {};

  @Name("Damage Number Type")
  public static NumberType numberType = NumberType.LAST;

}
