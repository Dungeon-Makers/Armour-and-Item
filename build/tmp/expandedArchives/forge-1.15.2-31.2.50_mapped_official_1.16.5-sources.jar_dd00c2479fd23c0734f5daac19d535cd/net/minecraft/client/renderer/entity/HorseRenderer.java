package net.minecraft.client.renderer.entity;

import com.google.common.collect.Maps;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.layers.LeatherHorseArmorLayer;
import net.minecraft.client.renderer.entity.model.HorseModel;
import net.minecraft.client.renderer.texture.LayeredTexture;
import net.minecraft.entity.passive.horse.HorseEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public final class HorseRenderer extends AbstractHorseRenderer<HorseEntity, HorseModel<HorseEntity>> {
   private static final Map<String, ResourceLocation> field_110852_a = Maps.newHashMap();

   public HorseRenderer(EntityRendererManager p_i47205_1_) {
      super(p_i47205_1_, new HorseModel<>(0.0F), 1.1F);
      this.addLayer(new LeatherHorseArmorLayer(this));
   }

   public ResourceLocation getTextureLocation(HorseEntity p_110775_1_) {
      String s = p_110775_1_.func_110264_co();
      ResourceLocation resourcelocation = field_110852_a.get(s);
      if (resourcelocation == null) {
         resourcelocation = new ResourceLocation(s);
         Minecraft.getInstance().getTextureManager().register(resourcelocation, new LayeredTexture(p_110775_1_.func_110212_cp()));
         field_110852_a.put(s, resourcelocation);
      }

      return resourcelocation;
   }
}