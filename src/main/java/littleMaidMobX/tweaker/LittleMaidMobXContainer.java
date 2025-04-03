package littleMaidMobX.tweaker;

import com.google.common.eventbus.EventBus;
import cpw.mods.fml.common.DummyModContainer;
import cpw.mods.fml.common.LoadController;
import cpw.mods.fml.common.ModMetadata;

import java.util.Collections;

public class LittleMaidMobXContainer extends DummyModContainer {
    public LittleMaidMobXContainer() {
        super(new ModMetadata());
        ModMetadata metadata = getMetadata();

        metadata.modId		= "OldModelLoader Compatibility";
        metadata.name		= "OldModelLoader Compatibility";
        metadata.version	= "1.0";
        metadata.authorList	= Collections.singletonList("MMM");
        metadata.description	= "Old little maid MultiModel compatibility";//"The MultiModel before 1.6.2 is read.";
        metadata.url			= "";
        metadata.credits		= "";
        setEnabledState(true);
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        bus.register(this);
        return true;
    }

    @Override
    public Class<?> getCustomResourcePackClass() {
        // 古いリソースを読み込むためのリソースパック
        return LittleMaidMobXResourcePack.class;
    }
}
