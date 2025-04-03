package supplysugarmachine.tileentity;

import java.util.List;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class TileEntitySupplySugar extends TileEntity implements ISidedInventory {
    private static final int STACK_LIMIT = 2000000000;
    public int sugarCount = 0;
    public boolean outputEmptySugarMessage = false;
    public String customName = "";

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);
        this.sugarCount = nbt.getInteger("sugar");
        String name = nbt.getString("name");
        if (!name.isEmpty()) {
            this.customName = name;
        }
    }

    @Override
    public void writeToNBT(NBTTagCompound nbt) {
        super.writeToNBT(nbt);
        nbt.setInteger("sugar", this.sugarCount);
        if (!this.customName.isEmpty()) {
            nbt.setString("name", this.customName);
        }
    }

    /*
     * パケットの送信・受信処理。
     * カスタムパケットは使わず、バニラのパケット送受信処理を使用。
     */
    @Override
    public Packet getDescriptionPacket() {
        NBTTagCompound nbt = new NBTTagCompound();
        this.writeToNBT(nbt);
        return new S35PacketUpdateTileEntity(this.xCoord, this.yCoord, this.zCoord, 1, nbt);
    }

    @Override
    public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity pkt) {
        NBTTagCompound nbt = pkt.func_148857_g();
        this.readFromNBT(nbt);
    }

    @SideOnly(Side.CLIENT)
    public int getMetadata() {
        return this.worldObj.getBlockMetadata(xCoord, yCoord, zCoord);
    }

    @Override
    public void markDirty() {
        List<EntityPlayer> list = this.worldObj.playerEntities;
        for (EntityPlayer player : list) {
            if ((player instanceof EntityPlayerMP)) {
                ((EntityPlayerMP) player).playerNetServerHandler.sendPacket(getDescriptionPacket());
            }
        }
    }

    public int getSugarSize() {
        return this.sugarCount;
    }

    public void addSugarSize(int size) {
        this.sugarCount = Math.min(this.sugarCount + size, STACK_LIMIT);
        markDirty();
    }

    public void setSugarSize(int size) {
        this.sugarCount = size;
    }

    @Override
    public int getSizeInventory() {
        return 1;
    }

    @Override
    public ItemStack getStackInSlot(int slot) {
		/*
		this.worldObj.getPlayerEntityByName("aoyanagiYuu").addChatMessage(new ChatComponentText("getStackInSlot: "+slot));
		int num = 0;
		if(this.SugarNumber > 64) {
			num = 64;
			//this.Sugar.stackSize -= 64;
		}
		else {
			num = (int)this.SugarNumber;
			//this.Sugar.stackSize = 0;
		}

		if (num == 0) {
			return null;
		}
		else {
			return new ItemStack(Items.sugar, num);
		}
		*/
        return null;
    }

    @Override
    public ItemStack decrStackSize(int slot, int dec) {
        if ((this.sugarCount == 0) || (slot == 1)) return null;
        int stackSize;
        if (this.sugarCount > 64) {
            stackSize = 64;
            this.sugarCount -= 64;
        } else {
            stackSize = this.sugarCount;
            this.sugarCount = 0;
        }
        markDirty();
        return new ItemStack(Items.sugar, stackSize);
    }

    @Override
    public ItemStack getStackInSlotOnClosing(int slot) {
        //this.worldObj.getPlayerEntityByName("aoyanagiYuu").addChatMessage(new ChatComponentText("getStackInSlotOnClosing: "+slot));
        return null;
    }

    @Override
    public void setInventorySlotContents(int slot, ItemStack setStack) {
        //this.worldObj.getPlayerEntityByName("aoyanagiYuu").addChatMessage(new ChatComponentText("setInventorySlotContents: "+slot));
        if ((setStack != null) && (setStack.getItem() == Items.sugar)) {
            this.addSugarSize(setStack.stackSize);
        }
    }

    @Override
    public String getInventoryName() {
        return "SugarSupplyMachine";
    }

    @Override
    public boolean hasCustomInventoryName() {
        return true;
    }

    @Override
    public int getInventoryStackLimit() {
        return STACK_LIMIT;
    }

    @Override
    public boolean isUseableByPlayer(EntityPlayer player) {
        //return worldObj.getTileEntity(xCoord, yCoord, zCoord) != this ? false : player.getDistanceSq(xCoord + 0.5D, yCoord + 0.5D, zCoord + 0.5D) <= 64.0D;
        return true;
    }

    @Override
    public void openInventory() {}

    @Override
    public void closeInventory() {}

    @SideOnly(Side.SERVER)
    public void renderMarker(int x, int y, int z) {
        //LMM_LittleMaidMobX.proxy.render.renderTileEntityAt(this, x, y, z, 1.0F);
        //SupplySugar_Render render = new SupplySugar_Render();
        //render.renderTileEntityAt(this, x, y, z, 1.0F);
    }

    @Override
    public boolean isItemValidForSlot(int slot, ItemStack stack) {
        if ((stack != null) && (stack.getItem() == Items.sugar) && (stack.stackSize > 0)) {
            stack.stackSize -= 1;
            this.addSugarSize(1);
            return true;
        }
        return false;
    }

    @Override
    public int[] getAccessibleSlotsFromSide(int side) {
        return new int[]{0, 1, 2};
    }

    @Override
    public boolean canInsertItem(int slot, ItemStack stack, int side) {
        return stack != null && stack.getItem() == Items.sugar;
        //return this.isItemValidForSlot(slot, stack);
    }

    @Override
    public boolean canExtractItem(int slot, ItemStack stack, int side) {
        return false;
    }
}
