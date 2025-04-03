package mmmlibx.lib.rewrite;

import java.io.File;
import java.util.List;
import java.util.Map;

public abstract class BaseManager {

    public abstract String prefix();

    protected void load(){
        for (Map.Entry<String, List<File>> le : RewritedFileManager.INSTANCE.searchedFiles.entrySet()) {
            for (File lf : le.getValue()) {
                loadFile(lf);
            }
        }
    }

    private void loadFile(File file) {
        if (file.exists() && file.canRead()) {
            if (file.isDirectory()) {
                loadDirectory(file);
            } else {
                //decodeZip(file);
            }
        }
    }

    private void loadDirectory(File dirFile){
        File[] listFiles = dirFile.listFiles();
        if (listFiles != null) {
            for (File file : listFiles) {
                if (file.isFile()){
                    String name = file.getName();
                    if (name.contains(prefix()) && name.endsWith(".class")){

                    }
                }
            }
        }
    }

    private void loadClass(String name){
        if (name == null){
            return;
        }

    }
}
