package littleMaidMobX.client.gui;

import cpw.mods.fml.client.config.DummyConfigElement;
import cpw.mods.fml.client.config.GuiConfig;
import cpw.mods.fml.client.config.IConfigElement;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import littleMaidMobX.LittleMaidMobX;
import littleMaidMobX.config.LittleMaidConfig;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;

import java.util.ArrayList;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiLittleMaidMobXConfig extends GuiConfig {
    public GuiLittleMaidMobXConfig(GuiScreen parentScreen) {
        super(parentScreen, getConfigElements(), LittleMaidMobX.MOD_ID, false, false, "Little Maid Mob Settings");
    }

    private static List<IConfigElement> getConfigElements()
    {
        List<IConfigElement> list = new ArrayList<>();

        list.add(categoryElement(LittleMaidConfig.CATEGORY_CLIENT, "Client", "littlemaidmob.configgui.ctgy.client"));
        list.add(categoryElement(LittleMaidConfig.CATEGORY_MAIDS, "Maids", "littlemaidmob.configgui.ctgy.maids"));
        list.add(categoryElement(LittleMaidConfig.CATEGORY_SPAWNING, "Spawning", "littlemaidmob.configgui.ctgy.spawning"));
        list.add(categoryElement(LittleMaidConfig.CATEGORY_ITEMS, "Items", "littlemaidmob.configgui.ctgy.items"));
        //list.add(categoryElement(LittleMaidConfig.CATEGORY_MMMLIB, "MMMLib", "littlemaidmob.configgui.ctgy.mmmlib"));
        list.add(categoryElement(LittleMaidConfig.CATEGORY_DEBUG, "Debugging", "littlemaidmob.configgui.ctgy.debug"));

        return list;
    }

    /** Creates a button linking to another screen where all options of the category are available */
    private static IConfigElement<?> categoryElement(String category, String name, String tooltip_key)
    {
        return new DummyConfigElement.DummyCategoryElement<>(name, tooltip_key, new ConfigElement<>(LittleMaidConfig.configuration().getCategory(category)).getChildElements());
    }
}
