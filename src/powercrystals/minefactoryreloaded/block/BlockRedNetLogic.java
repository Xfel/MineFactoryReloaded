package powercrystals.minefactoryreloaded.block;

import powercrystals.minefactoryreloaded.MineFactoryReloadedCore;
import powercrystals.minefactoryreloaded.api.rednet.IConnectableRedNet;
import powercrystals.minefactoryreloaded.api.rednet.RedNetConnectionType;
import powercrystals.minefactoryreloaded.core.MFRUtil;
import powercrystals.minefactoryreloaded.gui.MFRCreativeTab;
import powercrystals.minefactoryreloaded.item.ItemLogicUpgradeCard;
import powercrystals.minefactoryreloaded.tile.TileEntityRedNetLogic;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.common.ForgeDirection;

public class BlockRedNetLogic extends BlockContainer implements IConnectableRedNet
{
	private int[] _sideRemap = new int[] { 3, 1, 2, 0 };
	
	public BlockRedNetLogic(int id)
	{
		super(id, Material.clay);
		setUnlocalizedName("mfr.rednet.logic");
		setHardness(0.8F);
		setCreativeTab(MFRCreativeTab.tab);
	}
	
	@Override
	public void onBlockPlacedBy(World world, int x, int y, int z, EntityLiving entity, ItemStack stack)
	{
		if(entity == null)
		{
			return;
		}
		TileEntity te = world.getBlockTileEntity(x, y, z);
		if(te instanceof TileEntityRedNetLogic)
		{
			int facing = MathHelper.floor_double((double)((entity.rotationYaw * 4F) / 360F) + 0.5D) & 3;
			if(facing == 0)
			{
				world.setBlockMetadataWithNotify(x, y, z, 3, 3);
			}
			else if(facing == 1)
			{
				world.setBlockMetadataWithNotify(x, y, z, 0, 3);
			}
			else if(facing == 2)
			{
				world.setBlockMetadataWithNotify(x, y, z, 1, 3);
			}
			else if(facing == 3)
			{
				world.setBlockMetadataWithNotify(x, y, z, 2, 3);
			}
		}
	}

	@Override
	public TileEntity createNewTileEntity(World world)
	{
		return new TileEntityRedNetLogic();
	}

	@Override
	public RedNetConnectionType getConnectionType(World world, int x, int y, int z, ForgeDirection side)
	{
		TileEntityRedNetLogic logic = (TileEntityRedNetLogic)world.getBlockTileEntity(x, y, z);
		if(logic != null && side.ordinal() > 1 && side.ordinal() < 6)
		{
			if(world.getBlockMetadata(x, y, z) == _sideRemap[side.ordinal() - 2])
			{
				return RedNetConnectionType.None;
			}
		}
		return RedNetConnectionType.CableAll;
	}

	@Override
	public int getOutputValue(World world, int x, int y, int z, ForgeDirection side, int subnet)
	{
		TileEntityRedNetLogic logic = (TileEntityRedNetLogic)world.getBlockTileEntity(x, y, z);
		if(logic != null)
		{
			return logic.getOutputValue(side, subnet);
		}
		else
		{
			return 0;
		}
	}

	@Override
	public int[] getOutputValues(World world, int x, int y, int z, ForgeDirection side)
	{
		TileEntityRedNetLogic logic = (TileEntityRedNetLogic)world.getBlockTileEntity(x, y, z);
		if(logic != null)
		{
			return logic.getOutputValues(side);
		}
		else
		{
			return new int[16];
		}
	}

	@Override
	public void onInputsChanged(World world, int x, int y, int z, ForgeDirection side, int[] inputValues)
	{
		TileEntityRedNetLogic logic = (TileEntityRedNetLogic)world.getBlockTileEntity(x, y, z);
		if(logic != null)
		{
			logic.onInputsChanged(side, inputValues);
		}
	}

	@Override
	public void onInputChanged(World world, int x, int y, int z, ForgeDirection side, int inputValue)
	{
	}
	
	@Override
	public boolean onBlockActivated(World world, int x, int y, int z, EntityPlayer player, int side, float xOffset, float yOffset, float zOffset)
	{
		if(MFRUtil.isHoldingWrench(player))
		{
			int nextMeta = world.getBlockMetadata(x, y, z) + 1;
			if(nextMeta > 3)
			{
				nextMeta = 0;
			}
			world.setBlockMetadataWithNotify(x, y, z, nextMeta, 3);
		}
		else if(MFRUtil.isHolding(player, ItemLogicUpgradeCard.class))
		{
			TileEntityRedNetLogic logic = (TileEntityRedNetLogic)world.getBlockTileEntity(x, y, z);
			if(logic != null)
			{
				if(logic.insertUpgrade(player.inventory.getCurrentItem().getItemDamage() + 1));
				{
					if(!player.capabilities.isCreativeMode)
					{
					player.inventory.setInventorySlotContents(player.inventory.currentItem, null);
					}
					return true;
				}
			}
			return false;
		}
		else
		{
			player.openGui(MineFactoryReloadedCore.instance(), 0, world, x, y, z);
		}
		return true;
	}
	
	@Override
	public int getRenderType()
	{
		return MineFactoryReloadedCore.renderIdRedNetLogic;
	}
	
	@Override
	public boolean isOpaqueCube()
	{
		return false;
	}
	
	@Override
	public boolean isBlockNormalCube(World world, int x, int y, int z)
	{
		return false;
	}
	
	@Override
	public boolean isBlockSolidOnSide(World world, int x, int y, int z, ForgeDirection side)
	{
		return true;
	}
}