package littleMaidMobX.tweaker;

import com.google.common.collect.Lists;
import net.minecraft.launchwrapper.IClassTransformer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LittleMaidMobXTransformer implements IClassTransformer {

    private static final Logger LOGGER = LogManager.getLogger(LittleMaidMobXTransformer.class);
    private static final String PKG = "mmmlibx/lib/multiModel/model/mc162/";
    private static final Map<String, String> TARGETS = new HashMap<String, String>() {
        {
            add("EquippedStabilizer");
            add("IModelBaseMMM");
            add("IModelCaps");
            add("ModelBase");
            add("ModelBaseDuo");
            add("ModelBaseNihil");
            add("ModelBaseSolo");
            add("ModelBox");
            add("ModelBoxBase");
            add("ModelCapsHelper");
            add("ModelLittleMaid_AC");
            add("ModelLittleMaid_Archetype");
            add("ModelLittleMaid_Orign");
            add("ModelLittleMaid_RX2");
            add("ModelLittleMaid_Aug");
            add("ModelLittleMaid_SR2");
            add("ModelLittleMaidBase");
            add("ModelMultiBase");
            add("ModelMultiMMMBase");
            add("ModelPlate");
            add("ModelRenderer");
            add("ModelStabilizerBase");
        }

        private void add(String name) {
            put("MMM_" + name, PKG + name);
        }
    };
    private static final List<String> IGNORES = Lists.newArrayList(
            "modchu.model",
            "modchu.lib",
            "net.minecraft.src.mod_Modchu_ModchuLib",
            "modchu.pflm",
            "modchu.pflmf");

    private boolean dirty;

    private String searchAndReplace(String text) {
        for (Map.Entry<String, String> target : TARGETS.entrySet()) {
            if (text.contains(target.getKey())) {
                String result = text.replace(target.getKey(), target.getValue());
                LOGGER.info("Found old MultiModel code, replace {} to {}", text, result);
//				Debug("%d Hit and Replace: %s -> %s", debugOut, pText, result);
                dirty = true;
                return result;
            }
        }
        return text;
    }
    /**
     * バイナリを解析して旧MMMLibのクラスを置き換える。
     * @param name
     * @param transformedName
     * @param basicClass
     * @return
     */
    private byte[] replacer(String name, String transformedName, byte[] basicClass) {
        ClassReader reader = new ClassReader(basicClass);
        final String superName = reader.getSuperName();
        final boolean replaceSuper = TARGETS.containsKey(superName);

        // どのクラスがMMMLibのクラスを使っているかわからないので、全クラスチェックする。当然重い。
        // (親クラスだけでなく、引数や戻り値だけ使っている可能性もある)

        dirty = false;

        // 親クラスの置き換え
        ClassNode node = new ClassNode();
        reader.accept(node, 0);
        node.superName = searchAndReplace(node.superName);
        if(replaceSuper) {
            LOGGER.info("Load old MultiModel, {} extends {} -> {}", name, superName, node.superName);
        }

        // フィールドの置き換え
        for (FieldNode field : node.fields) {
            field.desc = searchAndReplace(field.desc);
        }

        // メソッドの置き換え
        for (MethodNode method : node.methods) {
            method.desc = searchAndReplace(method.desc);

            if(method.localVariables != null)
            {
                for(LocalVariableNode lvn : method.localVariables)
                {
                    if(lvn.desc != null) lvn.desc = searchAndReplace(lvn.desc);
                    if(lvn.name != null) lvn.name = searchAndReplace(lvn.name);
                    if(lvn.signature != null) lvn.signature = searchAndReplace(lvn.signature);
                }
            }

            AbstractInsnNode instruction = method.instructions.getFirst();
            while(instruction != null) {
                if (instruction instanceof FieldInsnNode) {	//4
                    ((FieldInsnNode)instruction).desc = searchAndReplace(((FieldInsnNode)instruction).desc);
                    ((FieldInsnNode)instruction).name = searchAndReplace(((FieldInsnNode)instruction).name);
                    ((FieldInsnNode)instruction).owner = searchAndReplace(((FieldInsnNode)instruction).owner);
                } else if (instruction instanceof InvokeDynamicInsnNode) {	//6
                    ((InvokeDynamicInsnNode)instruction).desc = searchAndReplace(((InvokeDynamicInsnNode)instruction).desc);
                    ((InvokeDynamicInsnNode)instruction).name = searchAndReplace(((InvokeDynamicInsnNode)instruction).name);
                } else if (instruction instanceof MethodInsnNode) {	//5
                    ((MethodInsnNode)instruction).desc = searchAndReplace(((MethodInsnNode)instruction).desc);
                    ((MethodInsnNode)instruction).name = searchAndReplace(((MethodInsnNode)instruction).name);
                    ((MethodInsnNode)instruction).owner = searchAndReplace(((MethodInsnNode)instruction).owner);
                } else if (instruction instanceof MultiANewArrayInsnNode) {	//13
                    ((MultiANewArrayInsnNode)instruction).desc = searchAndReplace(((MultiANewArrayInsnNode)instruction).desc);
                } else if (instruction instanceof TypeInsnNode) {	//3
                    ((TypeInsnNode)instruction).desc = searchAndReplace(((TypeInsnNode)instruction).desc);
                }
                instruction = instruction.getNext();
            }
        }

        // バイナリコードの書き出し
        if (this.dirty) {
            ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES | ClassWriter.COMPUTE_MAXS);
            node.accept(writer);
            byte[] replacedClass = writer.toByteArray();
            LOGGER.info("Replace: {}", name);
            return replacedClass;
        } else {
            return basicClass;
        }
    }
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        for(String header : IGNORES){
            if(name.startsWith(header)) {
                LOGGER.info("Ignored Transform class {}", name);
                return basicClass;
            }
        }
        if (basicClass != null) {
            return replacer(name, transformedName, basicClass);
        }
        return null;
    }
}
