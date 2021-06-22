package net.minecraft.client.renderer.entity.layers;

import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class BipedArmorLayer<T extends LivingEntity, M extends BipedModel<T>, A extends BipedModel<T>> extends ArmorLayer<T, M, A> {
   public BipedArmorLayer(IEntityRenderer<T, M> p_i50936_1_, A p_i50936_2_, A p_i50936_3_) {
      super(p_i50936_1_, p_i50936_2_, p_i50936_3_);
   }

   protected void setPartVisibility(A p_188359_1_, EquipmentSlotType p_188359_2_) {
      this.func_177194_a(p_188359_1_);
      switch(p_188359_2_) {
      case HEAD:
         p_188359_1_.head.visible = true;
         p_188359_1_.hat.visible = true;
         break;
      case CHEST:
         p_188359_1_.body.visible = true;
         p_188359_1_.rightArm.visible = true;
         p_188359_1_.leftArm.visible = true;
         break;
      case LEGS:
         p_188359_1_.body.visible = true;
         p_188359_1_.rightLeg.visible = true;
         p_188359_1_.leftLeg.visible = true;
         break;
      case FEET:
         p_188359_1_.rightLeg.visible = true;
         p_188359_1_.leftLeg.visible = true;
      }

   }

   protected void func_177194_a(A p_177194_1_) {
      p_177194_1_.setAllVisible(false);
   }
   
   @Override
   protected A getArmorModelHook(T entity, net.minecraft.item.ItemStack itemStack, EquipmentSlotType slot, A model) {
      return net.minecraftforge.client.ForgeHooksClient.getArmorModel(entity, itemStack, slot, model);
   }
}
