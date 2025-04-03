package littleMaidMobX.client.resources;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipFile;

import net.minecraft.client.resources.IResourcePack;
import net.minecraft.client.resources.data.IMetadataSection;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.util.ResourceLocation;

import com.google.common.collect.ImmutableSet;

public class OldZipTexturesLoader implements IResourcePack {

	public static final Map<String, File> KEYS = new HashMap<>();

	@Override
	public InputStream getInputStream(ResourceLocation location) throws IOException {
		if(resourceExists(location)){
			String key = location.getResourcePath();
			if(key.startsWith("/")) key = key.substring(1);
			File zipFile = KEYS.get(key);
			@SuppressWarnings("resource")
			ZipFile file = new ZipFile(zipFile);
            return file.getInputStream(file.getEntry(key));
		}
		return null;
	}

	@Override
	public BufferedImage getPackImage() {
		return null;
	}

	@Override
	public IMetadataSection getPackMetadata(IMetadataSerializer serializer, String key) {
		return null;
	}

	@Override
	public String getPackName() {
		return "OldTexturesLoader";
	}

	@Override
	public Set<String> getResourceDomains() {
		return ImmutableSet.of("mmmlibx");
	}

	@Override
	public boolean resourceExists(ResourceLocation location) {
		String key = location.getResourcePath();
		if(key.startsWith("/")) key = key.substring(1);
		return KEYS.containsKey(key);
	}

}
