package mmmlibx.lib.rewrite;

import cpw.mods.fml.relauncher.FMLInjectionData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;

public class RewritedFileManager {

    private static final Logger LOGGER = LogManager.getLogger();

    public static final RewritedFileManager INSTANCE = new RewritedFileManager();
    public final File fileDir;

    public final Map<String, List<File>> searchedFiles = new HashMap<>();

    private RewritedFileManager(){
        this.fileDir = new File(((File)FMLInjectionData.data()[6]).getAbsoluteFile(), "littleMaidMobX");
    }

    public static List<File> getAllFiles(ClassLoader loader){
        List<File> foundFiles = new ArrayList<>();
        //TODO Fix can't load in development
        if (loader instanceof URLClassLoader) {
            for (URL lurl : ((URLClassLoader)loader).getURLs()) {
                try {
                    String ls = lurl.toString();
                    if (ls.endsWith("/bin/") || ls.contains("/out/production/") || ls.contains("/mods/")) {
                        foundFiles.add(new File(lurl.toURI()));
                        LOGGER.info("URLClassLoader File added, file url: {}", lurl.toString());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        File[] fileList = INSTANCE.fileDir.listFiles();
        if (INSTANCE.fileDir.exists() && fileList != null){
            foundFiles.addAll(Arrays.asList(fileList));
        }
        return foundFiles;
    }


    public static void searchFile(String id, String filter){
        File mods = new File(((File)FMLInjectionData.data()[6]).getAbsoluteFile(), "mods");
        List<File> files = INSTANCE.searchedFiles.computeIfAbsent(id, s -> new ArrayList<>());
        if (INSTANCE.fileDir.isDirectory()){
            files.addAll(Arrays.asList(INSTANCE.fileDir.listFiles()));
        }
        if (mods.isDirectory()){
            files.addAll(Arrays.asList(mods.listFiles()));
        }
        if (!files.isEmpty()){
            try {
                files.stream().filter(file -> file.getName().contains(filter))
                        .forEach(file -> {
                            if (file.getName().endsWith(".zip") || file.getName().endsWith(".jar")) {
                                files.add(file);
                                LOGGER.info("File added: {}", file.toString());
                            } else if (file.isDirectory()) {
                                files.add(file);
                                LOGGER.info("Directory added: {}", file.toString());
                            }
                        });
            }catch (Exception e){
                //TODO always caught ConcurrentModificationException, requires fix
                LOGGER.warn("Caught error", e);
            }
        }
    }
}
