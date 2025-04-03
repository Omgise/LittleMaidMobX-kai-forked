package zabuton.proxy;

import cpw.mods.fml.client.registry.RenderingRegistry;
import zabuton.entity.EntityZabuton;
import zabuton.client.renderer.entity.RenderZabuton;

public class ClientProxy extends CommonProxy {
    public void registerRenderer() {
        RenderingRegistry.registerEntityRenderingHandler(EntityZabuton.class, new RenderZabuton());
    }
}
