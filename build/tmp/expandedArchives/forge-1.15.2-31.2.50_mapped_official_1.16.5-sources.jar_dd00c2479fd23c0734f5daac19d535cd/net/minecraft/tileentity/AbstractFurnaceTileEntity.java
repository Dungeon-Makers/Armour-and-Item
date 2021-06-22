package net.minecraft.tileentity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.block.AbstractFurnaceBlock;
import net.minecraft.block.Blocks;
import net.minecraft.entity.item.ExperienceOrbEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.IRecipeHolder;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.Direction;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;

public abstract class AbstractFurnaceTileEntity extends LockableTileEntity implements ISidedInventory, IRecipeHolder, IRecipeHelperPopulator, ITickableTileEntity {
   private static final int[] SLOTS_FOR_UP = new int[]{0};
   private static final int[] SLOTS_FOR_DOWN = new int[]{2, 1};
   private static final int[] SLOTS_FOR_SIDES = new int[]{1};
   protected NonNullList<ItemStack> items = NonNullList.withSize(3, ItemStack.EMPTY);
   private int litTime;
   private int litDuration;
   private int cookingProgress;
   private int cookingTotalTime;
   protected final IIntArray dataAccess = new IIntArray() {
      public int get(int p_221476_1_) {
         switch(p_221476_1_) {
         case 0:
            return AbstractFurnaceTileEntity.this.litTime;
         case 1:
            return AbstractFurnaceTileEntity.this.litDuration;
         case 2:
            return AbstractFurnaceTileEntity.this.cookingProgress;
         case 3:
            return AbstractFurnaceTileEntity.this.cookingTotalTime;
         default:
            return 0;
         }
      }

      public void set(int p_221477_1_, int p_221477_2_) {
         switch(p_221477_1_) {
         case 0:
            AbstractFurnaceTileEntity.this.litTime = p_221477_2_;
            break;
         case 1:
            AbstractFurnaceTileEntity.this.litDuration = p_221477_2_;
            break;
         case 2:
            AbstractFurnaceTileEntity.this.cookingProgress = p_221477_2_;
            break;
         case 3:
            AbstractFurnaceTileEntity.this.cookingTotalTime = p_221477_2_;
         }

      }

      public int getCount() {
         return 4;
      }
   };
   private final Map<ResourceLocation, Integer> recipesUsed = Maps.newHashMap();
   protected final IRecipeType<? extends AbstractCookingRecipe> recipeType;

   protected AbstractFurnaceTileEntity(TileEntityType<?> p_i49964_1_, IRecipeType<? extends AbstractCookingRecipe> p_i49964_2_) {
      super(p_i49964_1_);
      this.recipeType = p_i49964_2_;
   }

   @Deprecated //Forge - get burn times by calling ForgeHooks#getBurnTime(ItemStack)
   public static Map<Item, Integer> getFuel() {
      Map<Item, Integer> map = Maps.newLinkedHashMap();
      add(map, Items.LAVA_BUCKET, 20000);
      add(map, Blocks.COAL_BLOCK, 16000);
      add(map, Items.BLAZE_ROD, 2400);
      add(map, Items.COAL, 1600);
      add(map, Items.CHARCOAL, 1600);
      add(map, ItemTags.LOGS, 300);
      add(map, ItemTags.PLANKS, 300);
      add(map, ItemTags.WOODEN_STAIRS, 300);
      add(map, ItemTags.WOODEN_SLABS, 150);
      add(map, ItemTags.WOODEN_TRAPDOORS, 300);
      add(map, ItemTags.WOODEN_PRESSURE_PLATES, 300);
      add(map, net.minecraftforge.common.Tags.Items.FENCES_WOODEN, 300);
      add(map, net.minecraftforge.common.Tags.Items.FENCE_GATES_WOODEN, 300);
      add(map, Blocks.NOTE_BLOCK, 300);
      add(map, Blocks.BOOKSHELF, 300);
      add(map, Blocks.LECTERN, 300);
      add(map, Blocks.JUKEBOX, 300);
      add(map, Blocks.CHEST, 300);
      add(map, Blocks.TRAPPED_CHEST, 300);
      add(map, Blocks.CRAFTING_TABLE, 300);
      add(map, Blocks.DAYLIGHT_DETECTOR, 300);
      add(map, ItemTags.BANNERS, 300);
      add(map, Items.BOW, 300);
      add(map, Items.FISHING_ROD, 300);
      add(map, Blocks.LADDER, 300);
      add(map, ItemTags.SIGNS, 200);
      add(map, Items.WOODEN_SHOVEL, 200);
      add(map, Items.WOODEN_SWORD, 200);
      add(map, Items.WOODEN_HOE, 200);
      add(map, Items.WOODEN_AXE, 200);
      add(map, Items.WOODEN_PICKAXE, 200);
      add(map, ItemTags.WOODEN_DOORS, 200);
      add(map, ItemTags.BOATS, 1200);
      add(map, ItemTags.WOOL, 100);
      add(map, ItemTags.WOODEN_BUTTONS, 100);
      add(map, Items.STICK, 100);
      add(map, ItemTags.SAPLINGS, 100);
      add(map, Items.BOWL, 100);
      add(map, ItemTags.CARPETS, 67);
      add(map, Blocks.DRIED_KELP_BLOCK, 4001);
      add(map, Items.CROSSBOW, 300);
      add(map, Blocks.BAMBOO, 50);
      add(map, Blocks.DEAD_BUSH, 100);
      add(map, Blocks.SCAFFOLDING, 400);
      add(map, Blocks.LOOM, 300);
      add(map, Blocks.BARREL, 300);
      add(map, Blocks.CARTOGRAPHY_TABLE, 300);
      add(map, Blocks.FLETCHING_TABLE, 300);
      add(map, Blocks.SMITHING_TABLE, 300);
      add(map, Blocks.COMPOSTER, 300);
      return map;
   }

   private static void add(Map<Item, Integer> p_213992_0_, Tag<Item> p_213992_1_, int p_213992_2_) {
      for(Item item : p_213992_1_.func_199885_a()) {
         p_213992_0_.put(item, p_213992_2_);
      }

   }

   private static void add(Map<Item, Integer> p_213996_0_, IItemProvider p_213996_1_, int p_213996_2_) {
      p_213996_0_.put(p_213996_1_.asItem(), p_213996_2_);
   }

   private boolean isLit() {
      return this.litTime > 0;
   }

   public void func_145839_a(CompoundNBT p_145839_1_) {
      super.func_145839_a(p_145839_1_);
      this.items = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
      ItemStackHelper.loadAllItems(p_145839_1_, this.items);
      this.litTime = p_145839_1_.getInt("BurnTime");
      this.cookingProgress = p_145839_1_.getInt("CookTime");
      this.cookingTotalTime = p_145839_1_.getInt("CookTimeTotal");
      this.litDuration = this.getBurnDuration(this.items.get(1));
      int i = p_145839_1_.getShort("RecipesUsedSize");

      for(int j = 0; j < i; ++j) {
         ResourceLocation resourcelocation = new ResourceLocation(p_145839_1_.getString("RecipeLocation" + j));
         int k = p_145839_1_.getInt("RecipeAmount" + j);
         this.recipesUsed.put(resourcelocation, k);
      }

   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      p_189515_1_.putInt("BurnTime", this.litTime);
      p_189515_1_.putInt("CookTime", this.cookingProgress);
      p_189515_1_.putInt("CookTimeTotal", this.cookingTotalTime);
      ItemStackHelper.saveAllItems(p_189515_1_, this.items);
      p_189515_1_.putShort("RecipesUsedSize", (short)this.recipesUsed.size());
      int i = 0;

      for(Entry<ResourceLocation, Integer> entry : this.recipesUsed.entrySet()) {
         p_189515_1_.putString("RecipeLocation" + i, entry.getKey().toString());
         p_189515_1_.putInt("RecipeAmount" + i, entry.getValue());
         ++i;
      }

      return p_189515_1_;
   }

   public void tick() {
      boolean flag = this.isLit();
      boolean flag1 = false;
      if (this.isLit()) {
         --this.litTime;
      }

      if (!this.level.isClientSide) {
         ItemStack itemstack = this.items.get(1);
         if (this.isLit() || !itemstack.isEmpty() && !this.items.get(0).isEmpty()) {
            IRecipe<?> irecipe = this.level.getRecipeManager().getRecipeFor((IRecipeType<AbstractCookingRecipe>)this.recipeType, this, this.level).orElse(null);
            if (!this.isLit() && this.canBurn(irecipe)) {
               this.litTime = this.getBurnDuration(itemstack);
               this.litDuration = this.litTime;
               if (this.isLit()) {
                  flag1 = true;
                  if (itemstack.hasContainerItem())
                      this.items.set(1, itemstack.getContainerItem());
                  else
                  if (!itemstack.isEmpty()) {
                     Item item = itemstack.getItem();
                     itemstack.shrink(1);
                     if (itemstack.isEmpty()) {
                        this.items.set(1, itemstack.getContainerItem());
                     }
                  }
               }
            }

            if (this.isLit() && this.canBurn(irecipe)) {
               ++this.cookingProgress;
               if (this.cookingProgress == this.cookingTotalTime) {
                  this.cookingProgress = 0;
                  this.cookingTotalTime = this.getTotalCookTime();
                  this.burn(irecipe);
                  flag1 = true;
               }
            } else {
               this.cookingProgress = 0;
            }
         } else if (!this.isLit() && this.cookingProgress > 0) {
            this.cookingProgress = MathHelper.clamp(this.cookingProgress - 2, 0, this.cookingTotalTime);
         }

         if (flag != this.isLit()) {
            flag1 = true;
            this.level.setBlock(this.worldPosition, this.level.getBlockState(this.worldPosition).setValue(AbstractFurnaceBlock.LIT, Boolean.valueOf(this.isLit())), 3);
         }
      }

      if (flag1) {
         this.setChanged();
      }

   }

   protected boolean canBurn(@Nullable IRecipe<?> p_214008_1_) {
      if (!this.items.get(0).isEmpty() && p_214008_1_ != null) {
         ItemStack itemstack = p_214008_1_.getResultItem();
         if (itemstack.isEmpty()) {
            return false;
         } else {
            ItemStack itemstack1 = this.items.get(2);
            if (itemstack1.isEmpty()) {
               return true;
            } else if (!itemstack1.sameItem(itemstack)) {
               return false;
            } else if (itemstack1.getCount() + itemstack.getCount() <= this.getMaxStackSize() && itemstack1.getCount() + itemstack.getCount() <= itemstack1.getMaxStackSize()) { // Forge fix: make furnace respect stack sizes in furnace recipes
               return true;
            } else {
               return itemstack1.getCount() + itemstack.getCount() <= itemstack.getMaxStackSize(); // Forge fix: make furnace respect stack sizes in furnace recipes
            }
         }
      } else {
         return false;
      }
   }

   private void burn(@Nullable IRecipe<?> p_214007_1_) {
      if (p_214007_1_ != null && this.canBurn(p_214007_1_)) {
         ItemStack itemstack = this.items.get(0);
         ItemStack itemstack1 = p_214007_1_.getResultItem();
         ItemStack itemstack2 = this.items.get(2);
         if (itemstack2.isEmpty()) {
            this.items.set(2, itemstack1.copy());
         } else if (itemstack2.getItem() == itemstack1.getItem()) {
            itemstack2.grow(itemstack1.getCount());
         }

         if (!this.level.isClientSide) {
            this.setRecipeUsed(p_214007_1_);
         }

         if (itemstack.getItem() == Blocks.WET_SPONGE.asItem() && !this.items.get(1).isEmpty() && this.items.get(1).getItem() == Items.BUCKET) {
            this.items.set(1, new ItemStack(Items.WATER_BUCKET));
         }

         itemstack.shrink(1);
      }
   }

   protected int getBurnDuration(ItemStack p_213997_1_) {
      if (p_213997_1_.isEmpty()) {
         return 0;
      } else {
         Item item = p_213997_1_.getItem();
         return net.minecraftforge.common.ForgeHooks.getBurnTime(p_213997_1_);
      }
   }

   protected int getTotalCookTime() {
      return this.level.getRecipeManager().getRecipeFor((IRecipeType<AbstractCookingRecipe>)this.recipeType, this, this.level).map(AbstractCookingRecipe::getCookingTime).orElse(200);
   }

   public static boolean isFuel(ItemStack p_213991_0_) {
      return net.minecraftforge.common.ForgeHooks.getBurnTime(p_213991_0_) > 0;
   }

   public int[] getSlotsForFace(Direction p_180463_1_) {
      if (p_180463_1_ == Direction.DOWN) {
         return SLOTS_FOR_DOWN;
      } else {
         return p_180463_1_ == Direction.UP ? SLOTS_FOR_UP : SLOTS_FOR_SIDES;
      }
   }

   public boolean canPlaceItemThroughFace(int p_180462_1_, ItemStack p_180462_2_, @Nullable Direction p_180462_3_) {
      return this.canPlaceItem(p_180462_1_, p_180462_2_);
   }

   public boolean canTakeItemThroughFace(int p_180461_1_, ItemStack p_180461_2_, Direction p_180461_3_) {
      if (p_180461_3_ == Direction.DOWN && p_180461_1_ == 1) {
         Item item = p_180461_2_.getItem();
         if (item != Items.WATER_BUCKET && item != Items.BUCKET) {
            return false;
         }
      }

      return true;
   }

   public int getContainerSize() {
      return this.items.size();
   }

   public boolean isEmpty() {
      for(ItemStack itemstack : this.items) {
         if (!itemstack.isEmpty()) {
            return false;
         }
      }

      return true;
   }

   public ItemStack getItem(int p_70301_1_) {
      return this.items.get(p_70301_1_);
   }

   public ItemStack removeItem(int p_70298_1_, int p_70298_2_) {
      return ItemStackHelper.removeItem(this.items, p_70298_1_, p_70298_2_);
   }

   public ItemStack removeItemNoUpdate(int p_70304_1_) {
      return ItemStackHelper.takeItem(this.items, p_70304_1_);
   }

   public void setItem(int p_70299_1_, ItemStack p_70299_2_) {
      ItemStack itemstack = this.items.get(p_70299_1_);
      boolean flag = !p_70299_2_.isEmpty() && p_70299_2_.sameItem(itemstack) && ItemStack.tagMatches(p_70299_2_, itemstack);
      this.items.set(p_70299_1_, p_70299_2_);
      if (p_70299_2_.getCount() > this.getMaxStackSize()) {
         p_70299_2_.setCount(this.getMaxStackSize());
      }

      if (p_70299_1_ == 0 && !flag) {
         this.cookingTotalTime = this.getTotalCookTime();
         this.cookingProgress = 0;
         this.setChanged();
      }

   }

   public boolean stillValid(PlayerEntity p_70300_1_) {
      if (this.level.getBlockEntity(this.worldPosition) != this) {
         return false;
      } else {
         return p_70300_1_.distanceToSqr((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D) <= 64.0D;
      }
   }

   public boolean canPlaceItem(int p_94041_1_, ItemStack p_94041_2_) {
      if (p_94041_1_ == 2) {
         return false;
      } else if (p_94041_1_ != 1) {
         return true;
      } else {
         ItemStack itemstack = this.items.get(1);
         return isFuel(p_94041_2_) || p_94041_2_.getItem() == Items.BUCKET && itemstack.getItem() != Items.BUCKET;
      }
   }

   public void clearContent() {
      this.items.clear();
   }

   public void setRecipeUsed(@Nullable IRecipe<?> p_193056_1_) {
      if (p_193056_1_ != null) {
         this.recipesUsed.compute(p_193056_1_.getId(), (p_214004_0_, p_214004_1_) -> {
            return 1 + (p_214004_1_ == null ? 0 : p_214004_1_);
         });
      }

   }

   @Nullable
   public IRecipe<?> getRecipeUsed() {
      return null;
   }

   public void awardUsedRecipes(PlayerEntity p_201560_1_) {
   }

   public void func_213995_d(PlayerEntity p_213995_1_) {
      List<IRecipe<?>> list = Lists.newArrayList();

      for(Entry<ResourceLocation, Integer> entry : this.recipesUsed.entrySet()) {
         p_213995_1_.level.getRecipeManager().byKey(entry.getKey()).ifPresent((p_213993_3_) -> {
            list.add(p_213993_3_);
            func_214003_a(p_213995_1_, entry.getValue(), ((AbstractCookingRecipe)p_213993_3_).getExperience());
         });
      }

      p_213995_1_.awardRecipes(list);
      this.recipesUsed.clear();
   }

   private static void func_214003_a(PlayerEntity p_214003_0_, int p_214003_1_, float p_214003_2_) {
      if (p_214003_2_ == 0.0F) {
         p_214003_1_ = 0;
      } else if (p_214003_2_ < 1.0F) {
         int i = MathHelper.floor((float)p_214003_1_ * p_214003_2_);
         if (i < MathHelper.ceil((float)p_214003_1_ * p_214003_2_) && Math.random() < (double)((float)p_214003_1_ * p_214003_2_ - (float)i)) {
            ++i;
         }

         p_214003_1_ = i;
      }

      while(p_214003_1_ > 0) {
         int j = ExperienceOrbEntity.getExperienceValue(p_214003_1_);
         p_214003_1_ -= j;
         p_214003_0_.level.addFreshEntity(new ExperienceOrbEntity(p_214003_0_.level, p_214003_0_.getX(), p_214003_0_.getY() + 0.5D, p_214003_0_.getZ() + 0.5D, j));
      }

   }

   public void fillStackedContents(RecipeItemHelper p_194018_1_) {
      for(ItemStack itemstack : this.items) {
         p_194018_1_.accountStack(itemstack);
      }

   }

   net.minecraftforge.common.util.LazyOptional<? extends net.minecraftforge.items.IItemHandler>[] handlers =
           net.minecraftforge.items.wrapper.SidedInvWrapper.create(this, Direction.UP, Direction.DOWN, Direction.NORTH);

   @Override
   public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable Direction facing) {
      if (!this.remove && facing != null && capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
         if (facing == Direction.UP)
            return handlers[0].cast();
         else if (facing == Direction.DOWN)
            return handlers[1].cast();
         else
            return handlers[2].cast();
      }
      return super.getCapability(capability, facing);
   }

   @Override
   public void setRemoved() {
      super.setRemoved();
      for (int x = 0; x < handlers.length; x++)
        handlers[x].invalidate();
   }
}
