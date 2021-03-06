package com.MO.MatterOverdrive.items;

import java.util.List;

import cofh.lib.util.TimeTracker;
import com.MO.MatterOverdrive.Reference;
import com.MO.MatterOverdrive.sound.MachineSound;
import com.MO.MatterOverdrive.util.MOPhysicsHelper;
import com.MO.MatterOverdrive.util.MOStringHelper;
import com.mojang.realmsclient.gui.ChatFormatting;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.SoundManager;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.IIcon;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import cofh.lib.util.helpers.MathHelper;

import com.MO.MatterOverdrive.api.matter.IMatterDatabase;
import com.MO.MatterOverdrive.gui.GuiMatterScanner;
import com.MO.MatterOverdrive.handler.SoundHandler;
import com.MO.MatterOverdrive.items.includes.MOBaseItem;
import com.MO.MatterOverdrive.util.MatterDatabaseHelper;
import com.MO.MatterOverdrive.util.MatterHelper;
import org.lwjgl.input.Keyboard;
import scala.collection.parallel.ParIterableLike;

public class MatterScanner extends MOBaseItem
{
	public static final String SELECTED_TAG_NAME = "lastSelected";
	public static final String PAGE_TAG_NAME = "page";
	public static final String PANEL_OPEN_TAG_NAME = "panelOpen";
	public static final int PROGRESS_PER_ITEM = 10;
	public static final int SCAN_TIME = 40;
	@SideOnly(Side.CLIENT)
	public static MachineSound scanningSound;
	public static IIcon offline_icon;

	public MatterScanner(String name)
	{
		super(name);
	}

	/**
	 * Returns the icon index of the stack given as argument.
	 */
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconIndex(ItemStack itemStack)
	{
		if (itemStack.hasTagCompound() && Minecraft.getMinecraft().theWorld != null)
		{
			IMatterDatabase database = MatterScanner.getLink(Minecraft.getMinecraft().theWorld,itemStack);
			if (database != null)
			{
				return this.itemIcon;
			}
		}
		return offline_icon;
	}

	@Override
	public void addInformation(ItemStack itemstack, EntityPlayer player, List infos, boolean p_77624_4_)
	{
		if(hasDetails(itemstack))
		{
			if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT) || Keyboard.isKeyDown(Keyboard.KEY_RSHIFT))
			{
				addDetails(itemstack,player,infos);
			}
			else
			{
				infos.add(MOStringHelper.MORE_INFO);
				infos.add("Press '"+ EnumChatFormatting.YELLOW + "C" + EnumChatFormatting.GRAY + "' to open GUI");
			}
		}
	}

	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister iconRegister)
	{
		this.itemIcon = iconRegister.registerIcon(this.getIconString());
		offline_icon = iconRegister.registerIcon(Reference.MOD_ID + ":" + "matter_scanner_offline");
	}

	@Override
	public void addDetails(ItemStack itemstack, EntityPlayer player, List infos)
	{
		IMatterDatabase database = getLink(player.worldObj, itemstack);

		if (database != null)
		{
			if (itemstack.hasTagCompound()) {
				infos.add(ChatFormatting.GREEN + "Online");
			}

			NBTTagCompound lastSelected = getSelectedAsNBT(itemstack);
			if(lastSelected != null) {
				NBTTagCompound lastItem = database.getItemAsNBT(ItemStack.loadItemStackFromNBT(lastSelected));
				if (lastItem != null)
				{
					infos.add("Progress: " + lastItem.getByte(MatterDatabaseHelper.PROGRESS_TAG_NAME) + " / " + 100 + " %");
					infos.add("Selected: " + MatterDatabaseHelper.GetItemStackFromNBT(lastItem).getDisplayName());
				}
			}
		} else {
			infos.add(ChatFormatting.RED + "Offline");
		}
	}

	@Override
	public boolean hasDetails(ItemStack itemStack)
	{
		return true;
	}

	public static IMatterDatabase getLink(World world,ItemStack scanner)
	{
		if(scanner != null && scanner.getItem() instanceof MatterScanner)
		{
			if(scanner.hasTagCompound())
			{
				if(scanner.getTagCompound().getBoolean("isLinked"))
				{
					int x = scanner.getTagCompound().getInteger("link_x");
					int y = scanner.getTagCompound().getInteger("link_y");
					int z = scanner.getTagCompound().getInteger("link_z");

					TileEntity e = world.getTileEntity(x, y, z);
					if (e instanceof IMatterDatabase)
						return (IMatterDatabase) e;
					else
						unLink(world,scanner);
				}
			}
		}
		return null;
	}

	public static void unLink(World world,ItemStack scanner)
	{
		if(scanner.hasTagCompound())
		{
			scanner.getTagCompound().setBoolean("isLinked", false);
		}
	}

	public static void link(World world, int xCoord, int yCoord, int zCoord,ItemStack scanner)
	{
		if(scanner.getItem() instanceof MatterScanner)
		{
			((MatterScanner)scanner.getItem()).TagCompountCheck(scanner);
		}

		if(scanner.hasTagCompound())
		{
			scanner.getTagCompound().setBoolean("isLinked",true);
			scanner.getTagCompound().setInteger("link_x", xCoord);
			scanner.getTagCompound().setInteger("link_y", yCoord);
			scanner.getTagCompound().setInteger("link_z", zCoord);
		}
	}

	public int getItemStackLimit(ItemStack item)
	{
		return 1;
	}



	private void increaseScanProgress(ItemStack item)
	{
		if(item.hasTagCompound())
		{
			int lastScanProgress = item.getTagCompound().getInteger(MatterDatabaseHelper.PROGRESS_TAG_NAME);
			item.getTagCompound().setInteger(MatterDatabaseHelper.PROGRESS_TAG_NAME, MathHelper.clampI(lastScanProgress + 1, 0, 1));
		}
	}

	private int getScanProgress(ItemStack item)
	{
		if(item.hasTagCompound())
		{
			return item.getTagCompound().getInteger(MatterDatabaseHelper.PROGRESS_TAG_NAME);
		}

		return 0;
	}

	private void resetScanProgress(ItemStack item)
	{
		if(item.hasTagCompound())
		{
			item.getTagCompound().setInteger(MatterDatabaseHelper.PROGRESS_TAG_NAME,0);
		}
	}

	private boolean HarvestBlock(ItemStack scanner,EntityPlayer player,World world,int x,int y,int z)
	{
		if(!world.isRemote)
		{
			ItemStack item = MatterDatabaseHelper.GetItemStackFromWorld(world, x, y, z);
			return world.func_147480_a(x, y, z, false);
		}

		return false;
	}

	public static void setSelected(ItemStack scanner,ItemStack itemStack)
	{
		if(scanner.hasTagCompound())
		{
			NBTTagCompound tagCompound = new NBTTagCompound();
			itemStack.writeToNBT(tagCompound);
			setSelected(scanner, tagCompound);
		}
	}

	public static void setSelected(ItemStack scanner,NBTTagCompound tagCompound)
	{
		if(scanner.hasTagCompound())
		{
			scanner.getTagCompound().setTag(SELECTED_TAG_NAME, tagCompound);
		}
	}

	public static NBTTagCompound getSelectedAsNBT(ItemStack scanner)
	{
		if(scanner.hasTagCompound() && scanner.getTagCompound().hasKey(SELECTED_TAG_NAME))
		{
			return scanner.getTagCompound().getCompoundTag(SELECTED_TAG_NAME);
		}
		return null;
	}

	public static ItemStack getSelectedAsItem(ItemStack scanner)
	{
		if(scanner.hasTagCompound())
		{
			return ItemStack.loadItemStackFromNBT(scanner.getTagCompound().getCompoundTag(SELECTED_TAG_NAME));
		}
		return null;
	}

	
	@Override
	public void InitTagCompount(ItemStack stack)
	{
        MatterDatabaseHelper.InitTagCompound(stack);
	}

	@SideOnly(Side.CLIENT)
    public static void DisplayGuiScreen()
    {
		if(MatterHelper.isMatterScanner(Minecraft.getMinecraft().thePlayer.getHeldItem()))
		{
			Minecraft.getMinecraft().displayGuiScreen(new GuiMatterScanner(Minecraft.getMinecraft().thePlayer.getHeldItem(),Minecraft.getMinecraft().thePlayer.inventory.currentItem));
			return;
		}

		for (int i = 0; i < Minecraft.getMinecraft().thePlayer.inventory.getSizeInventory(); i++)
		{
			if (MatterHelper.isMatterScanner(Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(i)))
			{
				Minecraft.getMinecraft().displayGuiScreen(new GuiMatterScanner(Minecraft.getMinecraft().thePlayer.inventory.getStackInSlot(i),i));
				return;
			}
		}
    }

	public static int getLastPage(ItemStack scanner)
	{
		if(scanner.hasTagCompound())
		{
			return scanner.getTagCompound().getInteger(PAGE_TAG_NAME);
		}
		return 0;
	}

	public static void setLastPage(ItemStack scanner,int page)
	{
		if(scanner.hasTagCompound())
		{
			scanner.getTagCompound().setInteger(PAGE_TAG_NAME, page);
		}
	}

	/**
	 * returns the action that specifies what animation to play when the items is being used
	 */
	public EnumAction getItemUseAction(ItemStack p_77661_1_)
	{
		return EnumAction.block;
	}

	/**
	 * How long it takes to use or consume an item
	 */
	public int getMaxItemUseDuration(ItemStack p_77626_1_)
	{
		return SCAN_TIME;
	}

	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer entityplayer)
	{
		if(this.getMovingObjectPositionFromPlayer(world,entityplayer,true) != null)
		{
			if (world.isRemote)
				playSound(entityplayer.posX, entityplayer.posY, entityplayer.posZ);

			entityplayer.setItemInUse(itemstack, getMaxItemUseDuration(itemstack));
		}
		return itemstack;
	}

	@Override
	public void onUpdate(ItemStack itemStack,World world, Entity entity, int p_77663_4_, boolean p_77663_5_)
	{
		super.onUpdate(itemStack,world,entity,p_77663_4_,p_77663_5_);

		if (world.isRemote) {
			if (entity instanceof EntityPlayer) {
				EntityPlayer player = (EntityPlayer) entity;
				if (player.isUsingItem() && player.getItemInUse() == itemStack)
				{

				} else
				{
					stopScanSounds();
				}
			}
		}
	}

	public ItemStack onEaten(ItemStack scanner, World world, EntityPlayer player)
	{
		if(world.isRemote)
		{
			stopScanSounds();
			return scanner;
		}

		MovingObjectPosition position = this.getMovingObjectPositionFromPlayer(player.worldObj,player,true);
		if (position != null) {
			if (!world.isRemote) {
				int x = position.blockX;
				int y = position.blockY;
				int z = position.blockZ;
				ItemStack worldItem = MatterDatabaseHelper.GetItemStackFromWorld(world, x, y, z);

				//finished scanning
				Scan(world, scanner, player, worldItem, x, y, z);
			}
		}
		return scanner;
	}

	@Override
	public void onUsingTick(ItemStack scanner, EntityPlayer player, int count)
	{
		MovingObjectPosition position = this.getMovingObjectPositionFromPlayer(player.worldObj,player,true);
		if (position != null) {

			if (!player.worldObj.isRemote) {
				int x = position.blockX;
				int y = position.blockY;
				int z = position.blockZ;
				ItemStack lastSelected = getSelectedAsItem(scanner);
				ItemStack worldItem = MatterDatabaseHelper.GetItemStackFromWorld(player.worldObj, x, y, z);

				if (!MatterDatabaseHelper.areEqual(lastSelected, worldItem))
				{
					setSelected(scanner, worldItem);
					player.stopUsingItem();
					if (player.worldObj.isRemote)
					{
						stopScanSounds();
					}
				}
			}
		}
		else
		{
			if (player.worldObj.isRemote)
			{
				stopScanSounds();
				player.stopUsingItem();
			}
		}
	}

	@SideOnly(Side.CLIENT)
	private void playSound(double x,double y,double z)
	{
		if(scanningSound == null)
		{
			scanningSound = new MachineSound(new ResourceLocation(Reference.MOD_ID + ":" +"scanner_scanning"),(float)x,(float)y,(float)z,0.6f,1);
			Minecraft.getMinecraft().getSoundHandler().playSound(scanningSound);
		}
	}

	public void onPlayerStoppedUsing(ItemStack scanner, World world, EntityPlayer player, int count)
	{
		if (world.isRemote)
			stopScanSounds();
	}

	@SideOnly(Side.CLIENT)
	private void stopScanSounds()
	{
		if(scanningSound != null)
		{
			scanningSound.stopPlaying();
			scanningSound = null;
		}
	}

	public boolean Scan(World world,ItemStack scanner,EntityPlayer player,ItemStack worldBlock,int x,int y,int z)
	{
		this.TagCompountCheck(scanner);

		IMatterDatabase database = getLink(world, scanner);

		if (database != null)
		{
			resetScanProgress(scanner);

			if (MatterDatabaseHelper.increaseProgress(database, worldBlock, PROGRESS_PER_ITEM)) {
				//scan successful
				SoundHandler.PlaySoundAt(world, "scanner_success", player);
				return HarvestBlock(scanner, player, world, x, y, z);
			}
			else
			{
				//scan fail
				SoundHandler.PlaySoundAt(world, "scanner_fail", player);
				return false;
			}
		}

		return false;
	}
}
