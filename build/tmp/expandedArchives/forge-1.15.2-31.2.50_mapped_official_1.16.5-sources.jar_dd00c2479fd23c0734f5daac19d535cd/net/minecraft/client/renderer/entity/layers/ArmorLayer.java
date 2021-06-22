package net.minecraft.client.renderer.entity.layers;

import com.google.common.collect.Maps;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.DyeableArmorItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ArmorLayer<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> extends LayerRenderer<T, M> {
   protected final A innerModel;
   protected final A outerModel;
   private static final Map<String, ResourceLocation> ARMOR_LOCATION_CACHE = Maps.newHashMap();

   protected ArmorLayer(IEntityRenderer<T, M> p_i50951_1_, A p_i50951_2_, A p_i50951_3_) {
      super(p_i50951_1_);
      this.innerModel = p_i50951_2_;
      this.outerModel = p_i50951_3_;
   }

   public void render(MatrixStack p_225628_1_, IRenderTypeBuffer p_225628_2_, int p_225628_3_, T p_225628_4_, float p_225628_5_, float p_225628_6_, float p_225628_7_, float p_225628_8_, float p_225628_9_, float p_225628_10_) {
      this.func_229129_a_(p_225628_1_, p_225628_2_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_, EquipmentSlotType.CHEST, p_225628_3_);
      this.func_229129_a_(p_225628_1_, p_225628_2_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_, EquipmentSlotType.LEGS, p_225628_3_);
      this.func_229129_a_(p_225628_1_, p_225628_2_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_, EquipmentSlotType.FEET, p_225628_3_);
      this.func_229129_a_(p_225628_1_, p_225628_2_, p_225628_4_, p_225628_5_, p_225628_6_, p_225628_7_, p_225628_8_, p_225628_9_, p_225628_10_, EquipmentSlotType.HEAD, p_225628_3_);
   }

   private void func_229129_a_(MatrixStack p_229129_1_, IRenderTypeBuffer p_229129_2_, T p_229129_3_, float p_229129_4_, float p_229129_5_, float p_229129_6_, float p_229129_7_, float p_229129_8_, float p_229129_9_, EquipmentSlotType p_229129_10_, int p_229129_11_) {
      ItemStack itemstack = p_229129_3_.getItemBySlot(p_229129_10_);
      if (itemstack.getItem() instanceof ArmorItem) {
         ArmorItem armoritem = (ArmorItem)itemstack.getItem();
         if (armoritem.getSlot() == p_229129_10_) {
            A a = this.func_215337_a(p_229129_10_);
            a = getArmorModelHook(p_229129_3_, itemstack, p_229129_10_, a);
            ((BipedModel)this.getParentModel()).copyPropertiesTo(a);
            a.prepareMobModel(p_229129_3_, p_229129_4_, p_229129_5_, p_229129_6_);
            this.setPartVisibility(a, p_229129_10_);
            a.setupAnim(p_229129_3_, p_229129_4_, p_229129_5_, p_229129_7_, p_229129_8_, p_229129_9_);
            boolean flag = this.usesInnerModel(p_229129_10_);
            boolean flag1 = itemstack.hasFoil();
            if (armoritem instanceof net.minecraft.item.IDyeableArmorItem) { // Allow this for anything, not only cloth
               int i = ((net.minecraft.item.IDyeableArmorItem)armoritem).getColor(itemstack);
               float f = (float)(i >> 16 & 255) / 255.0F;
               float f1 = (float)(i >> 8 & 255) / 255.0F;
               float f2 = (float)(i & 255) / 255.0F;
               renderArmor(p_229129_1_, p_229129_2_, p_229129_11_, flag1, a, f, f1, f2, this.getArmorResource(p_229129_3_, itemstack, p_229129_10_, null));
               renderArmor(p_229129_1_, p_229129_2_, p_229129_11_, flag1, a, 1.0F, 1.0F, 1.0F, this.getArmorResource(p_229129_3_, itemstack, p_229129_10_, "overlay"));
            } else {
               renderArmor(p_229129_1_, p_229129_2_, p_229129_11_, flag1, a, 1.0F, 1.0F, 1.0F, this.getArmorResource(p_229129_3_, itemstack, p_229129_10_, null));
            }

         }
      }
   }

   private void func_229128_a_(MatrixStack p_229128_1_, IRenderTypeBuffer p_229128_2_, int p_229128_3_, ArmorItem p_229128_4_, boolean p_229128_5_, A p_229128_6_, boolean p_229128_7_, float p_229128_8_, float p_229128_9_, float p_229128_10_, @Nullable String p_229128_11_) {
      renderArmor(p_229128_1_, p_229128_2_, p_229128_3_, p_229128_5_, p_229128_6_, p_229128_8_, p_229128_9_, p_229128_10_, this.func_177178_a(p_229128_4_, p_229128_7_, p_229128_11_));
   }
   private void renderArmor(MatrixStack p_229128_1_, IRenderTypeBuffer p_229128_2_, int p_229128_3_, boolean p_229128_5_, A p_229128_6_, float p_229128_8_, float p_229128_9_, float p_229128_10_, ResourceLocation armorResource) {
      IVertexBuilder ivertexbuilder = ItemRenderer.getFoilBuffer(p_229128_2_, RenderType.entityCutoutNoCull(armorResource), false, p_229128_5_);
      p_229128_6_.renderToBuffer(p_229128_1_, ivertexbuilder, p_229128_3_, OverlayTexture.NO_OVERLAY, p_229128_8_, p_229128_9_, p_229128_10_, 1.0F);
   }

   public A func_215337_a(EquipmentSlotType p_215337_1_) {
      return (A)(this.usesInnerModel(p_215337_1_) ? this.innerModel : this.outerModel);
   }

   private boolean usesInnerModel(EquipmentSlotType p_188363_1_) {
      return p_188363_1_ == EquipmentSlotType.LEGS;
   }

   @Deprecated //Use the more sensitive version getArmorResource below
   private ResourceLocation func_177178_a(ArmorItem p_177178_1_, boolean p_177178_2_, @Nullable String p_177178_3_) {
      String s = "textures/models/armor/" + p_177178_1_.getMaterial().getName() + "_layer_" + (p_177178_2_ ? 2 : 1) + (p_177178_3_ == null ? "" : "_" + p_177178_3_) + ".png";
      return ARMOR_LOCATION_CACHE.computeIfAbsent(s, ResourceLocation::new);
   }

   protected abstract void setPartVisibility(A p_188359_1_, EquipmentSlotType p_188359_2_);

   protected abstract void func_177194_a(A p_177194_1_);


   /*=================================== FORGE START =========================================*/

   /**
    * Hook to allow item-sensitive armor model. for LayerBipedArmor.
    */
   protected A getArmorModelHook(T entity, ItemStack itemStack, EquipmentSlotType slot, A model) {
      return model;
   }

   /**
    * More generic ForgeHook version of the above function, it allows for Items to have more control over what texture they provide.
    *
    * @param entity Entity wearing the armor
    * @param stack ItemStack for the armor
    * @param slot Slot ID that the item is in
    * @param type Subtype, can be null or "overlay"
    * @return ResourceLocation pointing at the armor's texture
    */
   public ResourceLocation getArmorResource(net.minecraft.entity.Entity entity, ItemStack stack, EquipmentSlotType slot, @javax.annotation.Nullable String type) {
      ArmorItem item = (ArmorItem)stack.getItem();
      String texture = item.getMaterial().getName();
      String domain = "minecraft";
      int idx = texture.indexOf(':');
      if (idx != -1)
      {
         domain = texture.substring(0, idx);
         texture = texture.substring(idx + 1);
      }
      String s1 = String.format("%s:textures/models/armor/%s_layer_%d%s.png", domain, texture, (usesInnerModel(slot) ? 2 : 1), type == null ? "" : String.format("_%s", type));

      s1 = net.minecraftforge.client.ForgeHooksClient.getArmorTexture(entity, stack, s1, slot, type);
      ResourceLocation resourcelocation = ARMOR_LOCATION_CACHE.get(s1);

      if (resourcelocation == null)
      {
         resourcelocation = new ResourceLocation(s1);
         ARMOR_LOCATION_CACHE.put(s1, resourcelocation);
      }

      return resourcelocation;
   }
   /*=================================== FORGE END ===========================================*/
}
