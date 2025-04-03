package supplysugarmachine.proxy;

import cpw.mods.fml.client.registry.ClientRegistry;
import supplysugarmachine.client.renderer.tileentity.TileEntitySupplySugarRenderer;
import supplysugarmachine.tileentity.TileEntitySupplySugar;

@SuppressWarnings("unused")
public class ClientProxy extends CommonProxy {

    @Override
    public void registerTileEntity() {
        ClientRegistry.registerTileEntity(TileEntitySupplySugar.class, "SupplySugarMachine", new TileEntitySupplySugarRenderer());
    }
}
