package zabuton.dispenser;

import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.dispenser.IPosition;
import net.minecraft.entity.IProjectile;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import zabuton.entity.EntityZabuton;

public class BehaviorZabutonDispense extends BehaviorProjectileDispense {

    private ItemStack stack;

    @Override
    public ItemStack dispenseStack(IBlockSource blockSource, ItemStack stack) {
        // 色を識別するためにItemStackを確保
        this.stack = stack;
        return super.dispenseStack(blockSource, stack);
    }

    @Override
    protected IProjectile getProjectileEntity(World world, IPosition position) {
        return new EntityZabuton(world, position.getX(), position.getY(), position.getZ(), (byte) stack.getItemDamage());
    }
}
