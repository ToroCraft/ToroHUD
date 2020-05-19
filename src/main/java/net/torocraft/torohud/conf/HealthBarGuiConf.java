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

  // Health Config
  @Name("Hide Health Number")
  @Comment("Hide the health number")
  public static boolean hideHealthNo = false;

  @Name("Health Number X Offset")
  public static int healthNoXOffset = 0;

  @Name("Health Number Y Offset")
  public static int healthNoYOffset = 0;

  @Name("Health Bar X Offset")
  public static int healthXOffset = 0;

  @Name("Health Bar Y Offset")
  public static int healthYOffset = 0;

  @Name("Health Bar Scale")
  public static float healthScale = 1f;

  // Nameplate Config
  @Name("Hide Nameplate")
  public static boolean hideName = false;

  @Name("Nameplate X Offset")
  public static int nameXOffset = 0;

  @Name("Nameplate Y Offset")
  public static int nameYOffset = 0;

  @Name("Nameplate Scale")
  public static float nameScale = 1f;

  // Armor Config
  @Name("Hide Armor")
  public static boolean hideArmor = false;

  @Name("Armor X Offset")
  public static int armorXOffset = 0;

  @Name("Armor Y Offset")
  public static int armorYOffset = 0;

  // Potion Config
  @Name("Potion X Offset")
  public static int potionXOffset = 0;

  @Name("Potion Y Offset")
  public static int potionYOffset = 0;


  // Overall GUI Config
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
