package com.MO.MatterOverdrive.items;

import com.MO.MatterOverdrive.Reference;
import com.MO.MatterOverdrive.api.weapon.IWeaponModule;
import com.MO.MatterOverdrive.items.includes.MOBaseItem;
import com.MO.MatterOverdrive.util.MOStringHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Simeon on 4/15/2015.
 */
public class WeaponModuleBarrel extends MOBaseItem implements IWeaponModule
{

    public static final String[] names = {"damage"};
    private IIcon[] icons;

    public WeaponModuleBarrel(String name)
    {
        super(name);

        this.setHasSubtypes(true);
        this.setMaxDamage(0);
        this.setMaxStackSize(16);
    }

    @Override
    public boolean hasDetails(ItemStack itemStack)
    {
        return true;
    }

    @Override
    public void addDetails(ItemStack itemstack, EntityPlayer player, List infos)
    {
        super.addDetails(itemstack, player, infos);
        Map<Integer,Double> stats = (Map<Integer,Double>)getValue(itemstack);
        for (final Map.Entry<Integer, Double> entry : stats.entrySet())
        {
            infos.add(MOStringHelper.weaponStatToInfo(entry.getKey(), entry.getValue()));
        }
    }

    @Override
    public int getSlot(ItemStack itemStack)
    {
        return Reference.MODULE_BARREL;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void getSubItems(Item item, CreativeTabs creativeTabs, List list)
    {
        for (int i = 0; i < names.length;i++)
        {
            list.add(new ItemStack(item, 1, i));
        }
    }

    @Override
    @SideOnly(Side.CLIENT)
    public IIcon getIconFromDamage(int damage)
    {
        if (damage >= 0 && damage < icons.length)
        {
            return icons[damage];
        }
        return null;
    }

    @Override
    public String getUnlocalizedName(ItemStack itemStack)
    {
        int damage = itemStack.getItemDamage();
        return this.getUnlocalizedName() + "." + names[damage];
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void registerIcons(IIconRegister iconRegister)
    {
        icons = new IIcon[names.length];

        for (int i = 0; i < names.length;i++)
        {
            icons[i] = iconRegister.registerIcon(Reference.MOD_ID + ":barrel_" + names[i]);
        }
    }



    @Override
    public Object getValue(ItemStack itemStack)
    {
        int damage = itemStack.getItemDamage();
        Map<Integer,Double> stats = new HashMap<Integer, Double>();
        switch (damage)
        {
            case 0:
                stats.put(Reference.WS_DAMAGE,1.5);
                stats.put(Reference.WS_AMMO,0.5);
                stats.put(Reference.WS_EFFECT,0.5);
                break;
        }
        return stats;
    }
}
