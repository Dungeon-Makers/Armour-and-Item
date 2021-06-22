package net.minecraft.client.renderer;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RenderHelper {
   public static void turnBackOn() {
      RenderSystem.enableLighting();
      RenderSystem.enableColorMaterial();
      RenderSystem.colorMaterial(1032, 5634);
   }

   public static void turnOff() {
      RenderSystem.disableLighting();
      RenderSystem.disableColorMaterial();
   }

   public static void setupLevel(Matrix4f p_227781_0_) {
      RenderSystem.setupLevelDiffuseLighting(p_227781_0_);
   }

   public static void setupForFlatItems() {
      RenderSystem.setupGuiFlatDiffuseLighting();
   }

   public static void setupFor3DItems() {
      RenderSystem.setupGui3DDiffuseLighting();
   }
}