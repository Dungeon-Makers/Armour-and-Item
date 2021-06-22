package net.minecraft.item;

import com.google.common.collect.Multimap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class SwordItem extends TieredItem {
   private final float attackDamage;
   private final float field_200895_b;

   public SwordItem(IItemTier p_i48460_1_, int p_i48460_2_, float p_i48460_3_, Item.Properties p_i48460_4_) {
      super(p_i48460_1_, p_i48460_4_);
      this.field_200895_b = p_i48460_3_;
      this.attackDamage = (float)p_i48460_2_ + p_i48460_1_.getAttackDamageBonus();
   }

   public float getDamage() {
      return this.attackDamage;
   }

   public boolean canAttackBlock(BlockState p_195938_1_, World p_195938_2_, BlockPos p_195938_3_, PlayerEntity p_195938_4_) {
      return !p_195938_4_.isCreative();
   }

   public float getDestroySpeed(ItemStack p_150893_1_, BlockState p_150893_2_) {
      Block block = p_150893_2_.getBlock();
      if (block == Blocks.COBWEB) {
         return 15.0F;
      } else {
         Material material = p_150893_2_.getMaterial();
         return material != Material.PLANT && material != Material.REPLACEABLE_PLANT && material != Material.CORAL && !p_150893_2_.is(BlockTags.LEAVES) && material != Material.VEGETABLE ? 1.0F : 1.5F;
      }
   }

   public boolean hurtEnemy(ItemStack p_77644_1_, LivingEntity p_77644_2_, LivingEntity p_77644_3_) {
      p_77644_1_.hurtAndBreak(1, p_77644_3_, (p_220045_0_) -> {
         p_220045_0_.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
      });
      return true;
   }

   public boolean mineBlock(ItemStack p_179218_1_, World p_179218_2_, BlockState p_179218_3_, BlockPos p_179218_4_, LivingEntity p_179218_5_) {
      if (p_179218_3_.getDestroySpeed(p_179218_2_, p_179218_4_) != 0.0F) {
         p_179218_1_.hurtAndBreak(2, p_179218_5_, (p_220044_0_) -> {
            p_220044_0_.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
         });
      }

      return true;
   }

   public boolean isCorrectToolForDrops(BlockState p_150897_1_) {
      return p_150897_1_.getBlock() == Blocks.COBWEB;
   }

   public Multimap<String, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType p_111205_1_) {
      Multimap<String, AttributeModifier> multimap = super.getDefaultAttributeModifiers(p_111205_1_);
      if (p_111205_1_ == EquipmentSlotType.MAINHAND) {
         multimap.put(SharedMonsterAttributes.field_111264_e.func_111108_a(), new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", (double)this.attackDamage, AttributeModifier.Operation.ADDITION));
         multimap.put(SharedMonsterAttributes.field_188790_f.func_111108_a(), new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)this.field_200895_b, AttributeModifier.Operation.ADDITION));
      }

      return multimap;
   }
}