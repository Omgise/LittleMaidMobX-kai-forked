package supplysugarmachine.block;

import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ChatComponentTranslation;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.util.ForgeDirection;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import supplysugarmachine.tileentity.TileEntitySupplySugar;

public class BlockSupplySugar extends BlockContainer {
    public IIcon updownIcon;
    public IIcon sidedIcon;
    public IIcon markerIcon;

    public BlockSupplySugar() {
        super(Material.rock);
        this.setCreativeTab(CreativeTabs.tabTools);
        this.setBlockName("supplySugarMachine");/*システム名の設定*/
        //setBlockTextureName("freedommod:sugarsupplymachine_horizontal");/*ブロックのテクスチャの指定(複数指定の場合は消してください)*/
        this.setHardness(5.0f);/*硬さ*/
        this.setHarvestLevel("pickaxe", 1);/*回収するのに必要なツール*/
        this.setResistance(100000.0f);/*爆破耐性*/
        this.setStepSound(Block.soundTypeStone);/*ブロックの上を歩いた時の音*/
        /*setBlockUnbreakable();*//*ブロックを破壊不可に設定*/
        /*setTickRandomly(true);*//*ブロックのtick処理をランダムに。デフォルトfalse*/
        /*disableStats();*//*ブロックの統計情報を保存しない*/
        this.setLightOpacity(0);/*ブロックの透過係数。デフォルト０（不透過）*/
        this.setLightLevel(1.0f);/*明るさ 1.0F = 15*/
        this.setBlockBounds(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);/*当たり判定*/
    }

    private void getChestPositionWithNBT(EntityPlayer player, int x, int y, int z) {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setString("author", player.getDisplayName());
        nbt.setString("title", "position");
        NBTTagList bookTag = new NBTTagList();
        bookTag.appendTag(new NBTTagString(x + "," + y + "," + z));
        nbt.setTag("pages", bookTag);
        ItemStack writtenBook = new ItemStack(Items.written_book, 1);
        writtenBook.setTagCompound(nbt);
        player.inventory.addItemStackToInventory(writtenBook);
        //EntityItem eItem = new EntityItem(player.worldObj, player.posX, player.posY, player.posZ, writtenBook);
        //player.worldObj.spawnEntityInWorld(eItem);
    }

    @Override
    public int getRenderType() {
        return 0;
        //return LMM_LittleMaidMobX.SupplySugar_RenderID;
    }

    @Override
    public boolean renderAsNormalBlock() {
        return false;
    }

    @Override
    public int damageDropped(int meta) {
        return meta;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerBlockIcons(IIconRegister iconRegister) {
        super.registerBlockIcons(iconRegister);
        updownIcon = iconRegister.registerIcon("supplysugarmachine:supplysugarmachine_vertical");
        sidedIcon = iconRegister.registerIcon("supplysugarmachine:supplysugarmachine_horizontal");
        markerIcon = iconRegister.registerIcon("supplysugarmachine:supplysugarmachine_marker");
    }

    /*
     * 面によって利用するアイコンを変更するメソッド.
     * 引数のsideはブロックの上下東西南北(0~5の整数), metaはブロックのメタデータ.
     * 上下東西南北を0~5で表すのはわかりづらいので, ここではForgeDirectionで定義されるEnum定数を利用している.
     */
    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIcon(int side, int meta) {
        if ((side == ForgeDirection.EAST.ordinal()) || (side == ForgeDirection.NORTH.ordinal()) ||
                (side == ForgeDirection.SOUTH.ordinal()) || (side == ForgeDirection.WEST.ordinal())) {
            return sidedIcon;
        }
        return updownIcon;
    }

    @Override
    public void onBlockPlacedBy(World world, int x, int y, int z, EntityLivingBase entity, ItemStack stack) {
        if (!world.isRemote) {
            TileEntitySupplySugar tile = (TileEntitySupplySugar) world.getTileEntity(x, y, z);
            if (tile == null) {
                tile = new TileEntitySupplySugar();
            }
            if (stack.hasTagCompound()) {
                NBTTagCompound nbt = stack.getTagCompound();
                tile.setSugarSize(nbt.getInteger("sugar"));
            } else {
                tile.setSugarSize(1);
            }
        }
    }

    @Override
    public int quantityDropped(Random random) {
        return 0;
    }

    @Override
    public void breakBlock(World world, int x, int y, int z, Block p_149749_5_, int p_149749_6_) {
        if (!world.isRemote) {
            TileEntitySupplySugar tile = (TileEntitySupplySugar) world.getTileEntity(x, y, z);
            ItemStack items = new ItemStack(this, 1);
            NBTTagCompound nbt = new NBTTagCompound();
            nbt.setInteger("sugar", tile.getSugarSize());
            items.setTagCompound(nbt);
            EntityItem eItem = new EntityItem(world, x, y, z, items);
            world.spawnEntityInWorld(eItem);
        }
    }

    @Override
    public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float posX, float posY, float posZ) {
        //ブロックを右クリックした際の動作
        boolean isCorrectSugar = false;
        ItemStack stack = player.inventory.getCurrentItem();
        TileEntity tile = world.getTileEntity(x, y, z);
        if (stack != null) {
            if (stack.getItem() == Items.book) {
                getChestPositionWithNBT(player, x, y, z);
                stack.stackSize = stack.stackSize - 1;
            } else if (stack.getItem() == Items.name_tag) {
                ((TileEntitySupplySugar) tile).customName = stack.getDisplayName();
            } else if (stack.getItem() == Items.written_book) {
                NBTTagCompound nbt = stack.getTagCompound();
                String title = nbt.getString("title");
                if (title != null && title.equals("position")) {
                    NBTTagList bookTag = (NBTTagList) nbt.getTag("pages");
                    //player.addChatMessage(new ChatComponentText("bookTag: "+bookTag));
                    boolean isWritten = false;
                    for (int i = 0; i < bookTag.tagCount(); i++) {
                        String[] split = bookTag.getStringTagAt(i).split(",");
                        if (x == Integer.parseInt(split[0]) &&
                                y == Integer.parseInt(split[1]) &&
                                z == Integer.parseInt(split[2])) {
                            isWritten = true;
                            break;
                        }
                    }
                    if (!isWritten) {
                        bookTag.appendTag(new NBTTagString(x + "," + y + "," + z));
                        nbt.setTag("pages", bookTag);
                        stack.setTagCompound(nbt);
                    }
                }
            } else {
                isCorrectSugar = true;
            }
        }else {
            isCorrectSugar = true;
        }

        if (isCorrectSugar) {
            TileEntitySupplySugar supplySugar = (TileEntitySupplySugar) tile;
            int count = 0;
            for (int i = 0; i < player.inventory.getSizeInventory(); i++) {
                ItemStack itemStack = player.inventory.getStackInSlot(i);
                if ((itemStack != null) && (itemStack.getItem() == Items.sugar)) {
                    count += itemStack.stackSize;
                    player.inventory.setInventorySlotContents(i, null);
                }
            }
            supplySugar.addSugarSize(count);
            int size = supplySugar.getSugarSize();
            if (!world.isRemote) {
                player.addChatMessage(new ChatComponentTranslation("tile.supplySugarMachine.supply_sugar_remaining", size));
                //player.addChatMessage(new ChatComponentText("砂糖の数： " + size));
            }
            if ((size > 0) && (supplySugar.outputEmptySugarMessage)) {
                supplySugar.outputEmptySugarMessage = false;
            }
            player.inventory.markDirty();
        }
        return true;
    }

    @Override
    public boolean hasTileEntity(int metadata) {
        return true;
    }

    @Override
    public boolean canProvidePower() {
        return true;
    }

    @Override
    public int isProvidingWeakPower(IBlockAccess access, int x, int y, int z, int side) {
        TileEntity tile = access.getTileEntity(x, y, z);
        if (tile instanceof TileEntitySupplySugar) {
            int count = ((TileEntitySupplySugar) tile).getSugarSize();
            return count / 320;
        }
        return 0;
    }

    @Override
    public TileEntity createNewTileEntity(World world, int metadata) {
        return new TileEntitySupplySugar();
    }
}
