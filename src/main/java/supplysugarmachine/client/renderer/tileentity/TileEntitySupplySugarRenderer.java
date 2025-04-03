package supplysugarmachine.client.renderer.tileentity;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import supplysugarmachine.SupplySugarMachine;
import supplysugarmachine.tileentity.TileEntitySupplySugar;

@SideOnly(Side.CLIENT)
public class TileEntitySupplySugarRenderer extends TileEntitySpecialRenderer {
	private final ResourceLocation texture = new ResourceLocation(SupplySugarMachine.MOD_ID,"textures/entity/supplysugarmachine/supplysugarmachine_marker.png");
	private final Minecraft mc = Minecraft.getMinecraft();
	double updown = 0.0;
	boolean isUp = true;
	double rot = 0.0;

	public TileEntitySupplySugarRenderer() {}

	public void renderText(TileEntity tile, double x, double y, double z, float scale) {
		if (!((TileEntitySupplySugar)tile).customName.isEmpty()) {
			GL11.glPushMatrix();
			GL11.glTranslatef((float) x + 0.5f, (float) y + 1.2f, (float) z + 0.5f);
			GL11.glRotated(-mc.thePlayer.rotationYawHead, 0, 1, 0);
			GL11.glRotated(mc.thePlayer.rotationPitch, 1, 0, 0);
			GL11.glScalef(scale, scale, scale);
			FontRenderer font = this.func_147498_b();
			String signText = ((TileEntitySupplySugar)tile).customName;
			font.drawString(signText, -font.getStringWidth(signText) / 2, -5, 0);
			GL11.glPopMatrix();
		}
	}

	@Override
	public void renderTileEntityAt(TileEntity tile, double x, double y, double z, float partialTick) {
		renderText(tile, x, y, z, -0.05f);
		if (((TileEntitySupplySugar)tile).getSugarSize() == 0) {
			Tessellator tessellator = Tessellator.instance;
			final float FACE_XZ_NORMAL = 0.8944f;
			final float FACE_Y_NORMAL  = 0.4472f;

			GL11.glPushMatrix();
			GL11.glEnable(GL12.GL_RESCALE_NORMAL);
			GL11.glEnable(GL11.GL_BLEND);
			GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
			GL11.glTranslated(x + 0.5D, y + 1.5D + updown, z + 0.5D);
			GL11.glColor4f(1.0F, 0.0F, 0.0F, 0.5F);
			GL11.glRotated(rot, 0, 1, 0);
			this.bindTexture(texture);
			//tessellator.setColorRGBA_F(1.0F, 0.0F, 0.0F, 0.5F);
			//tessellator.setColorOpaque_F(0.5F, 0.5F, 0.5F);
			tessellator.startDrawing(GL11.GL_TRIANGLES);
			tessellator.setNormal(0.0F, FACE_Y_NORMAL, FACE_XZ_NORMAL);
			tessellator.addVertex(0.5D, 1.0D, 0.5D);
			tessellator.addVertex(-0.5D, 1.0D, 0.5D);
			tessellator.addVertex(0.0D, 0.0D, 0.0D);
			tessellator.draw();

			tessellator.startDrawing(GL11.GL_TRIANGLES);
			tessellator.setNormal(-FACE_XZ_NORMAL, FACE_Y_NORMAL, 0.0F);
			tessellator.addVertex(-0.5D, 1.0D, 0.5D);
			tessellator.addVertex(-0.5D, 1.0D, -0.5D);
			tessellator.addVertex(0.0D, 0.0D, 0.0D);
			tessellator.draw();

			tessellator.startDrawing(GL11.GL_TRIANGLES);
			tessellator.setNormal(0.0F, FACE_Y_NORMAL, -FACE_XZ_NORMAL);
			tessellator.addVertex(-0.5D, 1.0D, -0.5D);
			tessellator.addVertex(0.5D, 1.0D, -0.5D);
			tessellator.addVertex(0.0D, 0.0D, 0.0D);
			tessellator.draw();

			tessellator.startDrawing(GL11.GL_TRIANGLES);
			tessellator.setNormal(FACE_XZ_NORMAL, FACE_Y_NORMAL, 0.0F);
			tessellator.addVertex(0.5D, 1.0D, -0.5D);
			tessellator.addVertex(0.5D, 1.0D, 0.5D);
			tessellator.addVertex(0.0D, 0.0D, 0.0D);
			tessellator.draw();

			tessellator.startDrawing(GL11.GL_QUADS);
			tessellator.setNormal(0.0F, 1.0F, 0.0F);
			tessellator.addVertex(0.5D, 1.0D, -0.5D);
			tessellator.addVertex(-0.5D, 1.0D, -0.5D);
			tessellator.addVertex(-0.5D, 1.0D, 0.5D);
			tessellator.addVertex(0.5D, 1.0D, 0.5D);
			tessellator.draw();

			GL11.glPopMatrix();

			rot += 1.0;
			if (updown > 0.25) {
				isUp = false;
			}
			else if (updown < -0.25) {
				isUp = true;
			}
			if (isUp) {
				updown += 0.005;
			}
			else {
				updown -= 0.005;
			}
		}
	}
}
