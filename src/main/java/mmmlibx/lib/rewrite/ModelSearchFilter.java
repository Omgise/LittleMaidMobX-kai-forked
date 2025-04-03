package mmmlibx.lib.rewrite;

public class ModelSearchFilter {

    public final String name;
    public final String texturePath;
    public final String className;

    public ModelSearchFilter(String name, String texturePath, String className){
        this.name = name;
        this.texturePath = texturePath;
        this.className = className;
    }

    public String[] toArray(){
        return new String[]{this.name, this.texturePath, this.className};
    }
}
