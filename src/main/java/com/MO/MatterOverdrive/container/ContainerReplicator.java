package com.MO.MatterOverdrive.container;

import com.MO.MatterOverdrive.container.slot.SlotEnergy;
import com.MO.MatterOverdrive.container.slot.SlotRemoveOnly;
import com.MO.MatterOverdrive.container.slot.SlotShielding;
import com.MO.MatterOverdrive.util.MOContainerHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.ForgeDirection;
import cofh.lib.util.helpers.EnergyHelper;

import com.MO.MatterOverdrive.container.slot.SlotDatabase;
import com.MO.MatterOverdrive.tile.TileEntityMachineReplicator;
import com.MO.MatterOverdrive.util.MatterHelper;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ContainerReplicator extends ContainerMachine<TileEntityMachineReplicator>
{
	private int lastReplicateProgress;

	public ContainerReplicator(InventoryPlayer inventory,TileEntityMachineReplicator tileentity)
	{
		super(inventory, tileentity);
	}

	@Override
	public void init(InventoryPlayer inventory)
	{
		this.addSlotToContainer(new SlotRemoveOnly(machine,machine.OUTPUT_SLOT_ID,70,52));
		this.addSlotToContainer(new SlotRemoveOnly(machine,machine.SECOUND_OUTPUT_SLOT_ID,96,52));
		this.addSlotToContainer(new SlotDatabase(machine, machine.DATABASE_SLOT_ID,8,52));
		this.addSlotToContainer(new SlotShielding(machine, this.machine.SHIELDING_SLOT_ID,8,79));
		this.addSlotToContainer(new SlotEnergy(machine, this.machine.getEnergySlotID(),8,106));

		super.init(inventory);
		MOContainerHelper.AddPlayerSlots(inventory, this,45,89,true,true);
	}

	@Override
	public void addCraftingToCrafters(ICrafting icrafting)
	{
		super.addCraftingToCrafters(icrafting);
		icrafting.sendProgressBarUpdate(this, 0, this.machine.replicateProgress);
	}

	@Override
	public void detectAndSendChanges()
	{
		super.detectAndSendChanges();
		for(int i = 0;i < this.crafters.size();i++)
		{
			ICrafting icrafting = (ICrafting)this.crafters.get(i);
			
			if(this.lastReplicateProgress != this.machine.replicateProgress)
			{
				icrafting.sendProgressBarUpdate(this, 0, this.machine.replicateProgress);
			}

			this.lastReplicateProgress = this.machine.replicateProgress;
		}
	}
	
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int slot,int newValue)
	{
		if(slot == 0)
			this.machine.replicateProgress = newValue;
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) 
	{
		return true;
	}
}
