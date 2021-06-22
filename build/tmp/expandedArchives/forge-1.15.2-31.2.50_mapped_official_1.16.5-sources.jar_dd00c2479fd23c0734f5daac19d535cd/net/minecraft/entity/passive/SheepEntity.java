package net.minecraft.entity.passive;

import com.google.common.collect.Maps;
import java.util.Arrays;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.AgeableEntity;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ILivingEntityData;
import net.minecraft.entity.Pose;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.BreedGoal;
import net.minecraft.entity.ai.goal.EatGrassGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.LookAtGoal;
import net.minecraft.entity.ai.goal.LookRandomlyGoal;
import net.minecraft.entity.ai.goal.PanicGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.TemptGoal;
import net.minecraft.entity.ai.goal.WaterAvoidingRandomWalkingGoal;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.DyeColor;
import net.minecraft.item.DyeItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootTables;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SheepEntity extends AnimalEntity implements net.minecraftforge.common.IShearable {
   private static final DataParameter<Byte> DATA_WOOL_ID = EntityDataManager.defineId(SheepEntity.class, DataSerializers.BYTE);
   private static final Map<DyeColor, IItemProvider> ITEM_BY_DYE = Util.make(Maps.newEnumMap(DyeColor.class), (p_203402_0_) -> {
      p_203402_0_.put(DyeColor.WHITE, Blocks.WHITE_WOOL);
      p_203402_0_.put(DyeColor.ORANGE, Blocks.ORANGE_WOOL);
      p_203402_0_.put(DyeColor.MAGENTA, Blocks.MAGENTA_WOOL);
      p_203402_0_.put(DyeColor.LIGHT_BLUE, Blocks.LIGHT_BLUE_WOOL);
      p_203402_0_.put(DyeColor.YELLOW, Blocks.YELLOW_WOOL);
      p_203402_0_.put(DyeColor.LIME, Blocks.LIME_WOOL);
      p_203402_0_.put(DyeColor.PINK, Blocks.PINK_WOOL);
      p_203402_0_.put(DyeColor.GRAY, Blocks.GRAY_WOOL);
      p_203402_0_.put(DyeColor.LIGHT_GRAY, Blocks.LIGHT_GRAY_WOOL);
      p_203402_0_.put(DyeColor.CYAN, Blocks.CYAN_WOOL);
      p_203402_0_.put(DyeColor.PURPLE, Blocks.PURPLE_WOOL);
      p_203402_0_.put(DyeColor.BLUE, Blocks.BLUE_WOOL);
      p_203402_0_.put(DyeColor.BROWN, Blocks.BROWN_WOOL);
      p_203402_0_.put(DyeColor.GREEN, Blocks.GREEN_WOOL);
      p_203402_0_.put(DyeColor.RED, Blocks.RED_WOOL);
      p_203402_0_.put(DyeColor.BLACK, Blocks.BLACK_WOOL);
   });
   private static final Map<DyeColor, float[]> COLORARRAY_BY_COLOR = Maps.newEnumMap(Arrays.stream(DyeColor.values()).collect(Collectors.toMap((DyeColor p_200204_0_) -> {
      return p_200204_0_;
   }, SheepEntity::createSheepColor)));
   private int eatAnimationTick;
   private EatGrassGoal eatBlockGoal;

   private static float[] createSheepColor(DyeColor p_192020_0_) {
      if (p_192020_0_ == DyeColor.WHITE) {
         return new float[]{0.9019608F, 0.9019608F, 0.9019608F};
      } else {
         float[] afloat = p_192020_0_.getTextureDiffuseColors();
         float f = 0.75F;
         return new float[]{afloat[0] * 0.75F, afloat[1] * 0.75F, afloat[2] * 0.75F};
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static float[] getColorArray(DyeColor p_175513_0_) {
      return COLORARRAY_BY_COLOR.get(p_175513_0_);
   }

   public SheepEntity(EntityType<? extends SheepEntity> p_i50245_1_, World p_i50245_2_) {
      super(p_i50245_1_, p_i50245_2_);
   }

   protected void registerGoals() {
      this.eatBlockGoal = new EatGrassGoal(this);
      this.goalSelector.addGoal(0, new SwimGoal(this));
      this.goalSelector.addGoal(1, new PanicGoal(this, 1.25D));
      this.goalSelector.addGoal(2, new BreedGoal(this, 1.0D));
      this.goalSelector.addGoal(3, new TemptGoal(this, 1.1D, Ingredient.of(Items.WHEAT), false));
      this.goalSelector.addGoal(4, new FollowParentGoal(this, 1.1D));
      this.goalSelector.addGoal(5, this.eatBlockGoal);
      this.goalSelector.addGoal(6, new WaterAvoidingRandomWalkingGoal(this, 1.0D));
      this.goalSelector.addGoal(7, new LookAtGoal(this, PlayerEntity.class, 6.0F));
      this.goalSelector.addGoal(8, new LookRandomlyGoal(this));
   }

   protected void customServerAiStep() {
      this.eatAnimationTick = this.eatBlockGoal.getEatAnimationTick();
      super.customServerAiStep();
   }

   public void aiStep() {
      if (this.level.isClientSide) {
         this.eatAnimationTick = Math.max(0, this.eatAnimationTick - 1);
      }

      super.aiStep();
   }

   protected void func_110147_ax() {
      super.func_110147_ax();
      this.getAttribute(SharedMonsterAttributes.field_111267_a).setBaseValue(8.0D);
      this.getAttribute(SharedMonsterAttributes.field_111263_d).setBaseValue((double)0.23F);
   }

   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_WOOL_ID, (byte)0);
   }

   public ResourceLocation getDefaultLootTable() {
      if (this.isSheared()) {
         return this.getType().getDefaultLootTable();
      } else {
         switch(this.getColor()) {
         case WHITE:
         default:
            return LootTables.SHEEP_WHITE;
         case ORANGE:
            return LootTables.SHEEP_ORANGE;
         case MAGENTA:
            return LootTables.SHEEP_MAGENTA;
         case LIGHT_BLUE:
            return LootTables.SHEEP_LIGHT_BLUE;
         case YELLOW:
            return LootTables.SHEEP_YELLOW;
         case LIME:
            return LootTables.SHEEP_LIME;
         case PINK:
            return LootTables.SHEEP_PINK;
         case GRAY:
            return LootTables.SHEEP_GRAY;
         case LIGHT_GRAY:
            return LootTables.SHEEP_LIGHT_GRAY;
         case CYAN:
            return LootTables.SHEEP_CYAN;
         case PURPLE:
            return LootTables.SHEEP_PURPLE;
         case BLUE:
            return LootTables.SHEEP_BLUE;
         case BROWN:
            return LootTables.SHEEP_BROWN;
         case GREEN:
            return LootTables.SHEEP_GREEN;
         case RED:
            return LootTables.SHEEP_RED;
         case BLACK:
            return LootTables.SHEEP_BLACK;
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      if (p_70103_1_ == 10) {
         this.eatAnimationTick = 40;
      } else {
         super.handleEntityEvent(p_70103_1_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getHeadEatPositionScale(float p_70894_1_) {
      if (this.eatAnimationTick <= 0) {
         return 0.0F;
      } else if (this.eatAnimationTick >= 4 && this.eatAnimationTick <= 36) {
         return 1.0F;
      } else {
         return this.eatAnimationTick < 4 ? ((float)this.eatAnimationTick - p_70894_1_) / 4.0F : -((float)(this.eatAnimationTick - 40) - p_70894_1_) / 4.0F;
      }
   }

   @OnlyIn(Dist.CLIENT)
   public float getHeadEatAngleScale(float p_70890_1_) {
      if (this.eatAnimationTick > 4 && this.eatAnimationTick <= 36) {
         float f = ((float)(this.eatAnimationTick - 4) - p_70890_1_) / 32.0F;
         return ((float)Math.PI / 5F) + 0.21991149F * MathHelper.sin(f * 28.7F);
      } else {
         return this.eatAnimationTick > 0 ? ((float)Math.PI / 5F) : this.xRot * ((float)Math.PI / 180F);
      }
   }

   public boolean func_184645_a(PlayerEntity p_184645_1_, Hand p_184645_2_) {
      ItemStack itemstack = p_184645_1_.getItemInHand(p_184645_2_);
      if (false && itemstack.getItem() == Items.SHEARS && !this.isSheared() && !this.isBaby()) { //Forge: Moved to onSheared
         this.func_213612_dV();
         if (!this.level.isClientSide) {
            itemstack.hurtAndBreak(1, p_184645_1_, (p_213613_1_) -> {
               p_213613_1_.broadcastBreakEvent(p_184645_2_);
            });
         }

         return true;
      } else {
         return super.func_184645_a(p_184645_1_, p_184645_2_);
      }
   }

   @Deprecated //Forge: Use Shearable interface
   public void func_213612_dV() {
      if (!this.level.isClientSide) {
         this.setSheared(true);
         int i = 1 + this.random.nextInt(3);

         for(int j = 0; j < i; ++j) {
            ItemEntity itementity = this.spawnAtLocation(ITEM_BY_DYE.get(this.getColor()), 1);
            if (itementity != null) {
               itementity.setDeltaMovement(itementity.getDeltaMovement().add((double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F), (double)(this.random.nextFloat() * 0.05F), (double)((this.random.nextFloat() - this.random.nextFloat()) * 0.1F)));
            }
         }
      }

      this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
   }

   public void addAdditionalSaveData(CompoundNBT p_213281_1_) {
      super.addAdditionalSaveData(p_213281_1_);
      p_213281_1_.putBoolean("Sheared", this.isSheared());
      p_213281_1_.putByte("Color", (byte)this.getColor().getId());
   }

   public void readAdditionalSaveData(CompoundNBT p_70037_1_) {
      super.readAdditionalSaveData(p_70037_1_);
      this.setSheared(p_70037_1_.getBoolean("Sheared"));
      this.setColor(DyeColor.byId(p_70037_1_.getByte("Color")));
   }

   protected SoundEvent getAmbientSound() {
      return SoundEvents.SHEEP_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource p_184601_1_) {
      return SoundEvents.SHEEP_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.SHEEP_DEATH;
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      this.playSound(SoundEvents.SHEEP_STEP, 0.15F, 1.0F);
   }

   public DyeColor getColor() {
      return DyeColor.byId(this.entityData.get(DATA_WOOL_ID) & 15);
   }

   public void setColor(DyeColor p_175512_1_) {
      byte b0 = this.entityData.get(DATA_WOOL_ID);
      this.entityData.set(DATA_WOOL_ID, (byte)(b0 & 240 | p_175512_1_.getId() & 15));
   }

   public boolean isSheared() {
      return (this.entityData.get(DATA_WOOL_ID) & 16) != 0;
   }

   public void setSheared(boolean p_70893_1_) {
      byte b0 = this.entityData.get(DATA_WOOL_ID);
      if (p_70893_1_) {
         this.entityData.set(DATA_WOOL_ID, (byte)(b0 | 16));
      } else {
         this.entityData.set(DATA_WOOL_ID, (byte)(b0 & -17));
      }

   }

   public static DyeColor getRandomSheepColor(Random p_175510_0_) {
      int i = p_175510_0_.nextInt(100);
      if (i < 5) {
         return DyeColor.BLACK;
      } else if (i < 10) {
         return DyeColor.GRAY;
      } else if (i < 15) {
         return DyeColor.LIGHT_GRAY;
      } else if (i < 18) {
         return DyeColor.BROWN;
      } else {
         return p_175510_0_.nextInt(500) == 0 ? DyeColor.PINK : DyeColor.WHITE;
      }
   }

   public SheepEntity func_90011_a(AgeableEntity p_90011_1_) {
      SheepEntity sheepentity = (SheepEntity)p_90011_1_;
      SheepEntity sheepentity1 = EntityType.SHEEP.create(this.level);
      sheepentity1.setColor(this.getOffspringColor(this, sheepentity));
      return sheepentity1;
   }

   public void ate() {
      this.setSheared(false);
      if (this.isBaby()) {
         this.ageUp(60);
      }

   }

   @Nullable
   public ILivingEntityData finalizeSpawn(IWorld p_213386_1_, DifficultyInstance p_213386_2_, SpawnReason p_213386_3_, @Nullable ILivingEntityData p_213386_4_, @Nullable CompoundNBT p_213386_5_) {
      this.setColor(getRandomSheepColor(p_213386_1_.getRandom()));
      return super.finalizeSpawn(p_213386_1_, p_213386_2_, p_213386_3_, p_213386_4_, p_213386_5_);
   }

   private DyeColor getOffspringColor(AnimalEntity p_175511_1_, AnimalEntity p_175511_2_) {
      DyeColor dyecolor = ((SheepEntity)p_175511_1_).getColor();
      DyeColor dyecolor1 = ((SheepEntity)p_175511_2_).getColor();
      CraftingInventory craftinginventory = makeContainer(dyecolor, dyecolor1);
      return this.level.getRecipeManager().getRecipeFor(IRecipeType.CRAFTING, craftinginventory, this.level).map((p_213614_1_) -> {
         return p_213614_1_.assemble(craftinginventory);
      }).map(ItemStack::getItem).filter(DyeItem.class::isInstance).map(DyeItem.class::cast).map(DyeItem::getDyeColor).orElseGet(() -> {
         return this.level.random.nextBoolean() ? dyecolor : dyecolor1;
      });
   }

   private static CraftingInventory makeContainer(DyeColor p_213611_0_, DyeColor p_213611_1_) {
      CraftingInventory craftinginventory = new CraftingInventory(new Container((ContainerType)null, -1) {
         public boolean stillValid(PlayerEntity p_75145_1_) {
            return false;
         }
      }, 2, 1);
      craftinginventory.setItem(0, new ItemStack(DyeItem.byColor(p_213611_0_)));
      craftinginventory.setItem(1, new ItemStack(DyeItem.byColor(p_213611_1_)));
      return craftinginventory;
   }

   protected float getStandingEyeHeight(Pose p_213348_1_, EntitySize p_213348_2_) {
      return 0.95F * p_213348_2_.height;
   }

   @Override
   public boolean isShearable(ItemStack item, net.minecraft.world.IWorldReader world, BlockPos pos) {
      return !this.isSheared() && !this.isBaby();
   }

   @Override
   public java.util.List<ItemStack> onSheared(ItemStack item, net.minecraft.world.IWorld world, BlockPos pos, int fortune) {
      java.util.List<ItemStack> ret = new java.util.ArrayList<>();
      if (!this.level.isClientSide) {
         this.setSheared(true);
         int i = 1 + this.random.nextInt(3);

         for(int j = 0; j < i; ++j) {
            ret.add(new ItemStack(ITEM_BY_DYE.get(this.getColor())));
         }
      }
      this.playSound(SoundEvents.SHEEP_SHEAR, 1.0F, 1.0F);
      return ret;
   }
}
