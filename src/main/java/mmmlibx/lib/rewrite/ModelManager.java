package mmmlibx.lib.rewrite;

import littleMaidMobX.client.resources.OldZipTexturesLoader;
import mmmlibx.lib.MMMLib;
import mmmlibx.lib.MMM_Helper;
import mmmlibx.lib.MMM_TextureBox;
import mmmlibx.lib.multiModel.model.mc162.ModelMultiBase;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ModelManager {

    private static final Logger LOGGER = LogManager.getLogger();
    public static final ModelManager INSTANCE = new ModelManager();

    public static final int tx_oldwild = 0x10; //16;
    public static final int tx_oldarmor1 = 0x11; //17;
    public static final int tx_oldarmor2 = 0x12; //18;
    public static final int tx_oldeye = 0x13; //19;
    public static final int tx_gui = 0x20; //32;
    public static final int tx_wild = 0x30; //48;
    public static final int tx_armor1 = 0x40; //64;
    public static final int tx_armor2 = 0x50; //80;
    public static final int tx_eye = 0x60; //96;
    public static final int tx_eyecontract = 0x60; //96;
    public static final int tx_eyewild = 0x70; //112;
    public static final int tx_armor1light = 0x80; //128;
    public static final int tx_armor2light = 0x90; //144;

    private static final String[] FILE_NAMES = {
            "mob_littlemaid0.png", "mob_littlemaid1.png",
            "mob_littlemaid2.png", "mob_littlemaid3.png",
            "mob_littlemaid4.png", "mob_littlemaid5.png",
            "mob_littlemaid6.png", "mob_littlemaid7.png",
            "mob_littlemaid8.png", "mob_littlemaid9.png",
            "mob_littlemaida.png", "mob_littlemaidb.png",
            "mob_littlemaidc.png", "mob_littlemaidd.png",
            "mob_littlemaide.png", "mob_littlemaidf.png",
            "mob_littlemaidw.png",
            "mob_littlemaid_a00.png", "mob_littlemaid_a01.png"
    };
    public final List<ModelSearchFilter> searchFilters = new ArrayList<>();

    public final Map<String, ModelHolder> models = new HashMap<>();
    public final List<MMM_TextureBox> textures = new ArrayList<>();

    private ModelManager() {
        this.searchFilters.add(new ModelSearchFilter("mmmlibx", "/assets/minecraft/textures/entity/ModelMulti/", "ModelMulti_"));
        this.searchFilters.add(new ModelSearchFilter("mmmlibx", "/assets/minecraft/textures/entity/littleMaid/", "ModelMulti_"));
        this.searchFilters.add(new ModelSearchFilter("mmmlibx", "/assets/minecraft/textures/entity/littleMaid/", "ModelLittleMaid_"));
        this.searchFilters.add(new ModelSearchFilter("mmmlibx", "/mob/ModelMulti/", "ModelMulti_"));
        this.searchFilters.add(new ModelSearchFilter("mmmlibx", "/mob/littleMaid/", "ModelLittleMaid_"));
    }

    protected boolean loadModelFromDirectory(File baseFile, File file, ModelSearchFilter filter) {
        // modsフォルダに突っ込んであるものも検索、再帰で。
        if (baseFile == null || file == null) {
            return false;
        }
        try {
            File[] files = file.listFiles();
            if (files != null) {
                for (File modelFile : files) {
                    if (modelFile.isDirectory()) {
                        return loadModelFromDirectory(baseFile, modelFile, filter);
                    } else {
                        if (modelFile.getName().endsWith(".class")) {
                            String path = MMM_Helper.getRelativePathSimple(baseFile, modelFile);
                            if (path != null) {
                                //addModelClass(path, filter);
                            }
                        } else {
                            String replacedPath = modelFile.getPath().replace('\\', '/');
                            int i = replacedPath.indexOf(filter.texturePath);
                            if (replacedPath.contains(filter.texturePath)) {
                                // 対象はテクスチャディレクトリ
                                OldZipTexturesLoader.keys.put(replacedPath.substring(i), file);
                                addModelTexture(replacedPath.substring(i), filter);
                            }
                        }
                    }
                }
                return true;
            }
            return false;
        } catch (Exception e) {
            MMMLib.Debug("addTextureDebug-Exception.");
            return false;
        }
    }

    private boolean loadModelFromZip(File file, ModelSearchFilter filter) {
        if (file == null || file.isDirectory()) {
            return false;
        }
        try {
            FileInputStream fis = new FileInputStream(file);
            ZipInputStream zis = new ZipInputStream(fis);
            ZipEntry entry;
            do {
                entry = zis.getNextEntry();
                if (entry == null) {
                    break;
                }
                if (!entry.isDirectory()) {
                    if (entry.getName().endsWith(".class")) {
                        //addModelClass(entry.getName(), pSearch);
                    } else {
                        MMMLib.proxy.addTextureToOldZipLoader(entry.getName(), file);
                        addModelTexture(entry.getName(), filter);
                    }
                }
            } while (true);
            zis.close();
            fis.close();
            return true;
        } catch (Exception exception) {
            MMMLib.Debug("addTextureZip-Exception.");
            return false;
        }
    }


    private void addModelTexture(String path, ModelSearchFilter filter) {
        // パッケージにテクスチャを登録
        if (!path.startsWith("/")) {
            path = "/" + path;
        }
        if (path.startsWith(filter.texturePath)) {
            int i = path.lastIndexOf("/");
            if (filter.texturePath.length() < i) {
                String textureName = path.substring(filter.texturePath.length(), i);
                textureName = textureName.replace('/', '.');
                String fn = path.substring(i);
                int modelIndex = getModelIndex(fn);
                if (modelIndex > -1) {
                    if (modelIndex == tx_oldarmor1) {
                        modelIndex = tx_armor1;
                    }
                    if (modelIndex == tx_oldarmor2) {
                        modelIndex = tx_armor2;
                    }
                    if (modelIndex == tx_oldwild) {
                        modelIndex = tx_wild + 12;
                    }
                    MMM_TextureBox texture = getTextureBox(textureName);
                    if (texture == null) {
                        texture = new MMM_TextureBox(textureName, filter.toArray());
                        this.textures.add(texture);
                        //MMMLib.Debug("getTextureName-append-texturePack-%s", pn);
                    }
                    texture.addTexture(modelIndex, path);
                }
            }
        }

    }


    public void addModelClass(String modelName, Class<? extends ModelMultiBase> model) {
        try {
            Constructor<? extends ModelMultiBase> constructor = model.getConstructor(float.class);
            ModelMultiBase skin = constructor.newInstance(0.0f);
            float[] armorModelsSize = skin.getArmorModelsSize();
            ModelMultiBase inner = constructor.newInstance(armorModelsSize[0]);
            ModelMultiBase outer = constructor.newInstance(armorModelsSize[1]);
            this.models.put(modelName.toLowerCase(), new ModelHolder(skin, inner, outer));
        } catch (Exception e) {
            LOGGER.warn("Failed to load MultiModel class", e);
        }
    }

    public MMM_TextureBox getTextureBox(String pName) {
        for (MMM_TextureBox textureBox : this.textures) {
            if (textureBox.textureName.equals(pName)) {
                return textureBox;
            }
        }
        return null;
    }

    protected int getModelIndex(String name) {
        // 名前からインデックスを取り出す
        for (int i = 0; i < FILE_NAMES.length; i++) {
            if (name.endsWith(FILE_NAMES[i])) {
                return i;
            }
        }
        Pattern p = Pattern.compile("_([0-9a-f]+).png");
        Matcher m = p.matcher(name);
        if (m.find()) {
            return Integer.decode("0x" + m.group(1));
        }
        return -1;
    }

    public static class ModelHolder {

        public final ModelMultiBase skin;
        public final ModelMultiBase inner;
        public final ModelMultiBase outer;

        public ModelHolder(ModelMultiBase skin, ModelMultiBase inner, ModelMultiBase outer) {
            this.skin = skin;
            this.inner = inner;
            this.outer = outer;
        }
    }
}
