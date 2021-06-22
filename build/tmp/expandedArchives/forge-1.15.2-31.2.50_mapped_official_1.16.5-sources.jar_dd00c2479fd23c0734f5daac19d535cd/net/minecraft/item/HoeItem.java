package net.minecraft.item;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class HoeItem extends TieredItem {
   private final float field_185072_b;
   protected static final Map<Block, BlockState> TILLABLES = Maps.newHashMap(ImmutableMap.of(Blocks.GRASS_BLOCK, Blocks.FARMLAND.defaultBlockState(), Blocks.GRASS_PATH, Blocks.FARMLAND.defaultBlockState(), Blocks.DIRT, Blocks.FARMLAND.defaultBlockState(), Blocks.COARSE_DIRT, Blocks.DIRT.defaultBlockState()));

   public HoeItem(IItemTier p_i48488_1_, float p_i48488_2_, Item.Properties p_i48488_3_) {
      super(p_i48488_1_, p_i48488_3_);
      this.field_185072_b = p_i48488_2_;
   }

   public ActionResultType useOn(ItemUseContext p_195939_1_) {
      World world = p_195939_1_.getLevel();
      BlockPos blockpos = p_195939_1_.getClickedPos();
      int hook = net.minecraftforge.event.ForgeEventFactory.onHoeUse(p_195939_1_);
      if (hook != 0) return hook > 0 ? ActionResultType.SUCCESS : ActionResultType.FAIL;
      if (p_195939_1_.getClickedFace() != Direction.DOWN && world.isEmptyBlock(blockpos.above())) {
         BlockState blockstate = TILLABLES.get(world.getBlockState(blockpos).getBlock());
         if (blockstate != null) {
            PlayerEntity playerentity = p_195939_1_.getPlayer();
            world.playSound(playerentity, blockpos, SoundEvents.HOE_TILL, SoundCategory.BLOCKS, 1.0F, 1.0F);
            if (!world.isClientSide) {
               world.setBlock(blockpos, blockstate, 11);
               if (playerentity != null) {
                  p_195939_1_.getItemInHand().hurtAndBreak(1, playerentity, (p_220043_1_) -> {
                     p_220043_1_.broadcastBreakEvent(p_195939_1_.getHand());
                  });
               }
            }

            return ActionResultType.SUCCESS;
         }
      }

      return ActionResultType.PASS;
   }

   public boolean hurtEnemy(ItemStack p_77644_1_, LivingEntity p_77644_2_, LivingEntity p_77644_3_) {
      p_77644_1_.hurtAndBreak(1, p_77644_3_, (p_220042_0_) -> {
         p_220042_0_.broadcastBreakEvent(EquipmentSlotType.MAINHAND);
      });
      return true;
   }

   public Multimap<String, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlotType p_111205_1_) {
      Multimap<String, AttributeModifier> multimap = super.getDefaultAttributeModifiers(p_111205_1_);
      if (p_111205_1_ == EquipmentSlotType.MAINHAND) {
         multimap.put(SharedMonsterAttributes.field_111264_e.func_111108_a(), new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 0.0D, AttributeModifier.Operation.ADDITION));
         multimap.put(SharedMonsterAttributes.field_188790_f.func_111108_a(), new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", (double)this.field_185072_b, AttributeModifier.Operation.ADDITION));
      }

      return multimap;
   }
}
