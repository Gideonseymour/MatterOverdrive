package com.MO.MatterOverdrive.init;

import com.MO.MatterOverdrive.Reference;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.util.IIcon;
import net.minecraftforge.client.event.TextureStitchEvent;

/**
 * Created by Simeon on 3/17/2015.
 */
public class MatterOverdriveIcons
{
    public static IIcon Base;
    public static IIcon Vent;
    public static IIcon Matter_tank_full;
    public static IIcon Matter_tank_empty;
    public static IIcon Transperant;

    public static IIcon particle_steam;

    @SubscribeEvent
    public void registerTextures(TextureStitchEvent event)
    {
        System.out.println("Stiching textures");

        switch (event.map.getTextureType())
        {
            case 0:
                initBlockIcons(event.map);
                break;
            case 1:

        }
    }

    private void initBlockIcons(IIconRegister r)
    {
        Vent = register(r, "vent");
        Base = register(r, "base");
        Matter_tank_empty = register(r, "tank_empty");
        Matter_tank_full = register(r, "tank_full");
        Transperant = register(r, "transperant");

    }

    private void initParticleIcons(IIconRegister r)
    {
        particle_steam = register(r,"particle_steam");

    }

    private void initItems(IIconRegister r)
    {
        register(r, "vent");
        register(r, "base");
        register(r, "tank_empty");
        register(r, "tank_full");
    }

    public static IIcon register(IIconRegister register,String name)
    {
        return register.registerIcon(Reference.MOD_ID + ":" + name);
    }
}
