package com.MO.MatterOverdrive.data.inventory;

import com.MO.MatterOverdrive.Reference;
import com.MO.MatterOverdrive.container.slot.SlotDatabase;
import com.MO.MatterOverdrive.util.MatterHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

/**
 * Created by Simeon on 3/16/2015.
 */
public class DatabaseSlot extends Slot
{
    protected static final ResourceLocation Icon = new ResourceLocation(Reference.PATH_GUI + "items/scanner.png");

    public DatabaseSlot(boolean isMainSlot) {
        super(isMainSlot);
    }

    @Override
    public boolean isValidForSlot(ItemStack itemStack)
    {
        return MatterHelper.isMatterScanner(itemStack);
    }

    @Override
    public ResourceLocation getTexture()
    {
        return Icon;
    }

    @Override
    boolean isEqual(net.minecraft.inventory.Slot slot)
    {
        return slot instanceof SlotDatabase;
    }
}
