package mmmlibx.lib;

import java.io.File;

import littleMaidMobX.client.resources.OldZipTexturesLoader;

public class MMM_ProxyClient extends MMM_ProxyCommon
{
	public boolean isClient()
	{
		return true;
	}

	@Override
	public void addTextureToOldZipLoader(String name, File file) {
		OldZipTexturesLoader.KEYS.put(name, file);
	}
}
