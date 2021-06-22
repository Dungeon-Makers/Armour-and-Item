package com.mojang.realmsclient.gui;

import java.util.List;
import net.minecraft.realms.RealmListEntry;
import net.minecraft.realms.RealmsObjectSelectionList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class ListButton {
   public final int width;
   public final int height;
   public final int xOffset;
   public final int yOffset;

   public ListButton(int p_i51779_1_, int p_i51779_2_, int p_i51779_3_, int p_i51779_4_) {
      this.width = p_i51779_1_;
      this.height = p_i51779_2_;
      this.xOffset = p_i51779_3_;
      this.yOffset = p_i51779_4_;
   }

   public void func_225118_a(int p_225118_1_, int p_225118_2_, int p_225118_3_, int p_225118_4_) {
      int i = p_225118_1_ + this.xOffset;
      int j = p_225118_2_ + this.yOffset;
      boolean flag = false;
      if (p_225118_3_ >= i && p_225118_3_ <= i + this.width && p_225118_4_ >= j && p_225118_4_ <= j + this.height) {
         flag = true;
      }

      this.func_225120_a(i, j, flag);
   }

   protected abstract void func_225120_a(int p_225120_1_, int p_225120_2_, boolean p_225120_3_);

   public int getRight() {
      return this.xOffset + this.width;
   }

   public int getBottom() {
      return this.yOffset + this.height;
   }

   public abstract void onClick(int p_225121_1_);

   public static void func_225124_a(List<ListButton> p_225124_0_, RealmsObjectSelectionList p_225124_1_, int p_225124_2_, int p_225124_3_, int p_225124_4_, int p_225124_5_) {
      for(ListButton listbutton : p_225124_0_) {
         if (p_225124_1_.getRowWidth() > listbutton.getRight()) {
            listbutton.func_225118_a(p_225124_2_, p_225124_3_, p_225124_4_, p_225124_5_);
         }
      }

   }

   public static void func_225119_a(RealmsObjectSelectionList p_225119_0_, RealmListEntry p_225119_1_, List<ListButton> p_225119_2_, int p_225119_3_, double p_225119_4_, double p_225119_6_) {
      if (p_225119_3_ == 0) {
         int i = p_225119_0_.children().indexOf(p_225119_1_);
         if (i > -1) {
            p_225119_0_.selectItem(i);
            int j = p_225119_0_.getRowLeft();
            int k = p_225119_0_.getRowTop(i);
            int l = (int)(p_225119_4_ - (double)j);
            int i1 = (int)(p_225119_6_ - (double)k);

            for(ListButton listbutton : p_225119_2_) {
               if (l >= listbutton.xOffset && l <= listbutton.getRight() && i1 >= listbutton.yOffset && i1 <= listbutton.getBottom()) {
                  listbutton.onClick(i);
               }
            }
         }
      }

   }
}