package com.modularwarfare.api;


import com.modularwarfare.client.fpp.basic.renderers.RenderGunStatic;
import com.modularwarfare.client.fpp.enhanced.renderers.RenderGunEnhanced;
import net.minecraft.client.model.ModelBiped;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public abstract class RenderHandSleeveEventEnhanced extends Event{
    public final RenderGunEnhanced renderGunEnhanced;
    public final EnumHandSide side;
    public final ModelBiped modelplayer;


    public RenderHandSleeveEventEnhanced(RenderGunEnhanced render, EnumHandSide side, ModelBiped modelplayer) {
        this.renderGunEnhanced = render;
        this.side = side;
        this.modelplayer = modelplayer;
    }


    @Cancelable
    public static class Pre extends RenderHandSleeveEventEnhanced {

        public Pre(RenderGunEnhanced render, EnumHandSide side, ModelBiped modelplayer) {
            super(render, side, modelplayer);
            // TODO Auto-generated constructor stub
        }


    }

    public static  class Post extends RenderHandSleeveEventEnhanced {

        public Post(RenderGunEnhanced render, EnumHandSide side, ModelBiped modelplayer) {
            super(render, side, modelplayer);
            // TODO Auto-generated constructor stub
        }


    }
}