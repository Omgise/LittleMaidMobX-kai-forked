package supplysugarmachine;

import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import supplysugarmachine.block.BlockSupplySugar;
import supplysugarmachine.item.ItemSupplySugar;
import supplysugarmachine.proxy.CommonProxy;

@Mod(
        modid = SupplySugarMachine.MOD_ID,
        name = "SupplySugarMachine",
        version = "1.7.10_1.0"
)
public class SupplySugarMachine {

    public static final String MOD_ID = "supplysugarmachine";
    @Instance(MOD_ID)
    public static SupplySugarMachine instance;

    public static BlockSupplySugar supplySugarBlock;

    @SidedProxy(
            modId = MOD_ID,
            clientSide = "supplysugarmachine.proxy.ClientProxy",
            serverSide = "supplysugarmachine.proxy.CommonProxy")
    public static CommonProxy proxy;

    @SuppressWarnings("unused")
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        //砂糖供給ブロックを追加
        supplySugarBlock = new BlockSupplySugar();
        GameRegistry.registerBlock(supplySugarBlock, ItemSupplySugar.class, "supply_sugar_machine");
        GameRegistry.addRecipe(new ItemStack(supplySugarBlock, 1, 1),
                "RRR",
                "RSR",
                "RRR",
                'R', Blocks.cobblestone,
                'S', Items.sugar);
    }

    @SuppressWarnings("unused")
    @EventHandler
    public void init(FMLInitializationEvent event) {
        //レンダーを追加
        proxy.registerTileEntity();
    }
}
