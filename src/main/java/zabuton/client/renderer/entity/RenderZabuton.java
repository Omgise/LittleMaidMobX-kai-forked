package zabuton.client.renderer.entity;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import zabuton.Zabuton;
import zabuton.client.model.ModelZabuton;
import zabuton.entity.EntityZabuton;

public class RenderZabuton extends Render {

    protected final ModelBase model;
    protected static final ResourceLocation[] TEXTURES = new ResourceLocation[]{
            new ResourceLocation(Zabuton.MOD_ID, "textures/entity/zabuton_f.png"),
            new ResourceLocation(Zabuton.MOD_ID, "textures/entity/zabuton_e.png"),
            new ResourceLocation(Zabuton.MOD_ID, "textures/entity/zabuton_d.png"),
            new ResourceLocation(Zabuton.MOD_ID, "textures/entity/zabuton_c.png"),
            new ResourceLocation(Zabuton.MOD_ID, "textures/entity/zabuton_b.png"),
            new ResourceLocation(Zabuton.MOD_ID, "textures/entity/zabuton_a.png"),
            new ResourceLocation(Zabuton.MOD_ID, "textures/entity/zabuton_9.png"),
            new ResourceLocation(Zabuton.MOD_ID, "textures/entity/zabuton_8.png"),
            new ResourceLocation(Zabuton.MOD_ID, "textures/entity/zabuton_7.png"),
            new ResourceLocation(Zabuton.MOD_ID, "textures/entity/zabuton_6.png"),
            new ResourceLocation(Zabuton.MOD_ID, "textures/entity/zabuton_5.png"),
            new ResourceLocation(Zabuton.MOD_ID, "textures/entity/zabuton_4.png"),
            new ResourceLocation(Zabuton.MOD_ID, "textures/entity/zabuton_3.png"),
            new ResourceLocation(Zabuton.MOD_ID, "textures/entity/zabuton_2.png"),
            new ResourceLocation(Zabuton.MOD_ID, "textures/entity/zabuton_1.png"),
            new ResourceLocation(Zabuton.MOD_ID, "textures/entity/zabuton_0.png")
    };

    public RenderZabuton() {
        shadowSize = 0.0F;
        this.model = new ModelZabuton();
    }

    public void doRenderZabuton(EntityZabuton zabuton, double x, double y, double z, float yaw) {
        if (zabuton.color >= 0 && zabuton.color < 16) {
            shadowSize = 0.5F;
            // レンダリング実装
            // レンダリング
            GL11.glPushMatrix();
            GL11.glTranslatef((float) x, (float) y, (float) z);
            GL11.glRotatef(180F - yaw, 0.0F, 1.0F, 0.0F);
            bindEntityTexture(zabuton);
            GL11.glScalef(-1F, -1F, 1.0F);
            model.render(zabuton, 0.0F, 0.0F, -0.1F, 0.0F, 0.0F, 0.0625F);
            GL11.glPopMatrix();
        } else {
            // Entityがスポーン後、サーバから色情報を取得するまで描画しない。どの色で描画すればいいかわからないため
            shadowSize = 0.0F;
        }
    }

    @Override
    public void doRender(Entity entity, double x, double y, double z, float yaw, float partialTick) {
        doRenderZabuton((EntityZabuton) entity, x, y, z, yaw);
    }

    @Override
    protected ResourceLocation getEntityTexture(Entity entity) {
        return TEXTURES[((EntityZabuton) entity).color & 0x0F];
    }

}