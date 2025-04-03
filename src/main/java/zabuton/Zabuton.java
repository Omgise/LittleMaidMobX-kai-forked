package zabuton;

import net.minecraft.block.BlockDispenser;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.EntityRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import zabuton.dispenser.BehaviorZabutonDispense;
import zabuton.entity.EntityZabuton;
import zabuton.item.ItemZabuton;
import zabuton.proxy.CommonProxy;

@Mod(modid = Zabuton.MOD_ID,
        name = Zabuton.MOD_ID,
        dependencies = "required-after:Forge@[10.12.2.1121,)")
public class Zabuton {
    public static final String MOD_ID = "zabuton";

    @SidedProxy(
            clientSide = "zabuton.proxy.ClientProxy",
            serverSide = "zabuton.proxy.CommonProxy")
    public static CommonProxy proxy;

    public static Item zabuton;

    @SuppressWarnings("unused")
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        zabuton = new ItemZabuton();
        zabuton.setUnlocalizedName(MOD_ID + ":zabuton");
        zabuton.setTextureName(MOD_ID + ":zabuton");
        zabuton.setCreativeTab(CreativeTabs.tabTransport);
        GameRegistry.registerItem(zabuton, "zabuton");
        for (int metadata = 0; metadata < 16; metadata++) {
            GameRegistry.addRecipe(new ItemStack(zabuton, 1, 15 - metadata), "s ", "##",
                    's', Items.string,
                    '#', new ItemStack(Blocks.wool, 1, metadata));
        }

        EntityRegistry.registerModEntity(EntityZabuton.class, "zabuton", 0, this, 80, 3, true);
        proxy.registerRenderer();

        BlockDispenser.dispenseBehaviorRegistry.putObject(zabuton, new BehaviorZabutonDispense());
    }
}
