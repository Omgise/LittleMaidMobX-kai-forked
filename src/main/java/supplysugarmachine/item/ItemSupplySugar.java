package supplysugarmachine.item;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.StatCollector;

public class ItemSupplySugar extends ItemBlock {

	public ItemSupplySugar(Block block) {
		super(block);
		maxStackSize = 64;
	}

	@Override
    public void addInformation(ItemStack itemstack, EntityPlayer player, List list, boolean advancedItemTooltips) {
    	NBTTagCompound nbt =  itemstack.getTagCompound();
    	if (itemstack.hasTagCompound()) {
    		long count = nbt.getLong("sugar");
			list.add(StatCollector.translateToLocalFormatted("tile.supplySugarMachine.supply_sugar_remaining", count));
    	}
    }
}
