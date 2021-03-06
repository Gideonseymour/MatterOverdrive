package com.MO.MatterOverdrive.container;

import com.MO.MatterOverdrive.container.slot.MOSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;

/**
 * Created by Simeon on 3/16/2015.
 */
public abstract class MOBaseContainer extends Container
{
    public Slot addSlotToContainer(Slot p_75146_1_)
    {
        return super.addSlotToContainer(p_75146_1_);
    }

    public MOSlot getSlotAt(int id)
    {
        return  (MOSlot)inventorySlots.get(id);
    }
}
