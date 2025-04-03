package supplysugarmachine.proxy;

import cpw.mods.fml.common.registry.GameRegistry;
import supplysugarmachine.tileentity.TileEntitySupplySugar;

public class CommonProxy {
	public void registerTileEntity() {
		GameRegistry.registerTileEntity(TileEntitySupplySugar.class, "SupplySugarMachine");
	}
}
