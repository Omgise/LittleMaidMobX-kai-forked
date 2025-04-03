package mmmlibx.lib;

import littleMaidMobX.entity.EntityLittleMaid;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntityFurnace;

public class ItemHelper {
	public static boolean isCake(Item item){
		if (item == null) return false;
		return item == Items.cake;
	}

	public static boolean isCake(ItemStack pItemstack){
		if (pItemstack == null) return false;
		return isCake(pItemstack.getItem());
	}

	public static boolean isSugar(Item item){
		if (item == null) return false;
		return item == Items.sugar;
	}
	
	public static boolean isSugar(ItemStack pItemstack){
		if (pItemstack == null) return false;
		return isSugar(pItemstack.getItem());
	}

	public static boolean hasSugar(EntityLittleMaid maid){
		boolean flag = false;
		for(ItemStack stack: maid.maidInventory.mainInventory){
			if(stack == null) continue;
			if(isSugar(stack.getItem())){
				flag = true;
				break;
			}
		}
		return flag;
	}
	
	public static int getFoodAmount(ItemStack pItemstack) {
		if (pItemstack == null) {
			return -1;
		}
		if (pItemstack.getItem() instanceof ItemFood) {
			return ((ItemFood) pItemstack.getItem()).func_150905_g(pItemstack);
		}
		return -1;
	}
	
	public static boolean isItemBurned(ItemStack pItemstack) {
		return ((pItemstack != null) &&
				TileEntityFurnace.getItemBurnTime(pItemstack) > 0);
	}

	public static boolean isItemSmelting(ItemStack pItemstack) {
		return ((pItemstack != null) && MMM_Helper.getSmeltingResult(pItemstack) != null);
	}

	public static boolean isItemExplord(ItemStack stack) {
		if (stack == null)
			return false;
		Item item = stack.getItem();
		return item instanceof ItemBlock && Block.getBlockFromItem(item).getMaterial() == Material.tnt;
	}
}
