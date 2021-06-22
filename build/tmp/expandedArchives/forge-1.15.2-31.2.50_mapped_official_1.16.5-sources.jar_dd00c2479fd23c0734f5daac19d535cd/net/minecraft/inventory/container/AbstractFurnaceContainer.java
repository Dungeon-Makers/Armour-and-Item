package net.minecraft.inventory.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IRecipeHelperPopulator;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.AbstractCookingRecipe;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.item.crafting.ServerRecipePlacerFurnace;
import net.minecraft.tileentity.AbstractFurnaceTileEntity;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class AbstractFurnaceContainer extends RecipeBookContainer<IInventory> {
   private final IInventory container;
   private final IIntArray data;
   protected final World level;
   private final IRecipeType<? extends AbstractCookingRecipe> recipeType;

   protected AbstractFurnaceContainer(ContainerType<?> p_i50103_1_, IRecipeType<? extends AbstractCookingRecipe> p_i50103_2_, int p_i50103_3_, PlayerInventory p_i50103_4_) {
      this(p_i50103_1_, p_i50103_2_, p_i50103_3_, p_i50103_4_, new Inventory(3), new IntArray(4));
   }

   protected AbstractFurnaceContainer(ContainerType<?> p_i50104_1_, IRecipeType<? extends AbstractCookingRecipe> p_i50104_2_, int p_i50104_3_, PlayerInventory p_i50104_4_, IInventory p_i50104_5_, IIntArray p_i50104_6_) {
      super(p_i50104_1_, p_i50104_3_);
      this.recipeType = p_i50104_2_;
      checkContainerSize(p_i50104_5_, 3);
      checkContainerDataCount(p_i50104_6_, 4);
      this.container = p_i50104_5_;
      this.data = p_i50104_6_;
      this.level = p_i50104_4_.player.level;
      this.addSlot(new Slot(p_i50104_5_, 0, 56, 17));
      this.addSlot(new FurnaceFuelSlot(this, p_i50104_5_, 1, 56, 53));
      this.addSlot(new FurnaceResultSlot(p_i50104_4_.player, p_i50104_5_, 2, 116, 35));

      for(int i = 0; i < 3; ++i) {
         for(int j = 0; j < 9; ++j) {
            this.addSlot(new Slot(p_i50104_4_, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
         }
      }

      for(int k = 0; k < 9; ++k) {
         this.addSlot(new Slot(p_i50104_4_, k, 8 + k * 18, 142));
      }

      this.addDataSlots(p_i50104_6_);
   }

   public void fillCraftSlotsStackedContents(RecipeItemHelper p_201771_1_) {
      if (this.container instanceof IRecipeHelperPopulator) {
         ((IRecipeHelperPopulator)this.container).fillStackedContents(p_201771_1_);
      }

   }

   public void clearCraftingContent() {
      this.container.clearContent();
   }

   public void handlePlacement(boolean p_217056_1_, IRecipe<?> p_217056_2_, ServerPlayerEntity p_217056_3_) {
      (new ServerRecipePlacerFurnace<>(this)).recipeClicked(p_217056_3_, (IRecipe<IInventory>)p_217056_2_, p_217056_1_);
   }

   public boolean recipeMatches(IRecipe<? super IInventory> p_201769_1_) {
      return p_201769_1_.matches(this.container, this.level);
   }

   public int getResultSlotIndex() {
      return 2;
   }

   public int getGridWidth() {
      return 1;
   }

   public int getGridHeight() {
      return 1;
   }

   @OnlyIn(Dist.CLIENT)
   public int getSize() {
      return 3;
   }

   public boolean stillValid(PlayerEntity p_75145_1_) {
      return this.container.stillValid(p_75145_1_);
   }

   public ItemStack quickMoveStack(PlayerEntity p_82846_1_, int p_82846_2_) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = this.slots.get(p_82846_2_);
      if (slot != null && slot.hasItem()) {
         ItemStack itemstack1 = slot.getItem();
         itemstack = itemstack1.copy();
         if (p_82846_2_ == 2) {
            if (!this.moveItemStackTo(itemstack1, 3, 39, true)) {
               return ItemStack.EMPTY;
            }

            slot.onQuickCraft(itemstack1, itemstack);
         } else if (p_82846_2_ != 1 && p_82846_2_ != 0) {
            if (this.canSmelt(itemstack1)) {
               if (!this.moveItemStackTo(itemstack1, 0, 1, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (this.isFuel(itemstack1)) {
               if (!this.moveItemStackTo(itemstack1, 1, 2, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= 3 && p_82846_2_ < 30) {
               if (!this.moveItemStackTo(itemstack1, 30, 39, false)) {
                  return ItemStack.EMPTY;
               }
            } else if (p_82846_2_ >= 30 && p_82846_2_ < 39 && !this.moveItemStackTo(itemstack1, 3, 30, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveItemStackTo(itemstack1, 3, 39, false)) {
            return ItemStack.EMPTY;
         }

         if (itemstack1.isEmpty()) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (itemstack1.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(p_82846_1_, itemstack1);
      }

      return itemstack;
   }

   protected boolean canSmelt(ItemStack p_217057_1_) {
      return this.level.getRecipeManager().getRecipeFor((IRecipeType)this.recipeType, new Inventory(p_217057_1_), this.level).isPresent();
   }

   protected boolean isFuel(ItemStack p_217058_1_) {
      return AbstractFurnaceTileEntity.isFuel(p_217058_1_);
   }

   @OnlyIn(Dist.CLIENT)
   public int getBurnProgress() {
      int i = this.data.get(2);
      int j = this.data.get(3);
      return j != 0 && i != 0 ? i * 24 / j : 0;
   }

   @OnlyIn(Dist.CLIENT)
   public int getLitProgress() {
      int i = this.data.get(1);
      if (i == 0) {
         i = 200;
      }

      return this.data.get(0) * 13 / i;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isLit() {
      return this.data.get(0) > 0;
   }
}