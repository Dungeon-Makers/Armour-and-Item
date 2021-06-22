package net.minecraft.client.gui.screen;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import java.util.List;
import java.util.ListIterator;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.ChangePageButton;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.play.client.CEditBookPacket;
import net.minecraft.util.Hand;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EditBookScreen extends Screen {
   private final PlayerEntity owner;
   private final ItemStack book;
   private boolean isModified;
   private boolean isSigning;
   private int frameTick;
   private int currentPage;
   private final List<String> pages = Lists.newArrayList();
   private String title = "";
   private int field_214240_i;
   private int field_214241_j;
   private long lastClickTime;
   private int lastIndex = -1;
   private ChangePageButton forwardButton;
   private ChangePageButton backButton;
   private Button doneButton;
   private Button signButton;
   private Button finalizeButton;
   private Button cancelButton;
   private final Hand hand;

   public EditBookScreen(PlayerEntity p_i51100_1_, ItemStack p_i51100_2_, Hand p_i51100_3_) {
      super(NarratorChatListener.NO_TITLE);
      this.owner = p_i51100_1_;
      this.book = p_i51100_2_;
      this.hand = p_i51100_3_;
      CompoundNBT compoundnbt = p_i51100_2_.getTag();
      if (compoundnbt != null) {
         ListNBT listnbt = compoundnbt.getList("pages", 8).copy();

         for(int i = 0; i < listnbt.size(); ++i) {
            this.pages.add(listnbt.getString(i));
         }
      }

      if (this.pages.isEmpty()) {
         this.pages.add("");
      }

   }

   private int getNumPages() {
      return this.pages.size();
   }

   public void tick() {
      super.tick();
      ++this.frameTick;
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.signButton = this.addButton(new Button(this.width / 2 - 100, 196, 98, 20, I18n.get("book.signButton"), (p_214201_1_) -> {
         this.isSigning = true;
         this.updateButtonVisibility();
      }));
      this.doneButton = this.addButton(new Button(this.width / 2 + 2, 196, 98, 20, I18n.get("gui.done"), (p_214204_1_) -> {
         this.minecraft.setScreen((Screen)null);
         this.saveChanges(false);
      }));
      this.finalizeButton = this.addButton(new Button(this.width / 2 - 100, 196, 98, 20, I18n.get("book.finalizeButton"), (p_214195_1_) -> {
         if (this.isSigning) {
            this.saveChanges(true);
            this.minecraft.setScreen((Screen)null);
         }

      }));
      this.cancelButton = this.addButton(new Button(this.width / 2 + 2, 196, 98, 20, I18n.get("gui.cancel"), (p_214212_1_) -> {
         if (this.isSigning) {
            this.isSigning = false;
         }

         this.updateButtonVisibility();
      }));
      int i = (this.width - 192) / 2;
      int j = 2;
      this.forwardButton = this.addButton(new ChangePageButton(i + 116, 159, true, (p_214208_1_) -> {
         this.pageForward();
      }, true));
      this.backButton = this.addButton(new ChangePageButton(i + 43, 159, false, (p_214205_1_) -> {
         this.pageBack();
      }, true));
      this.updateButtonVisibility();
   }

   private String func_214219_a(String p_214219_1_) {
      StringBuilder stringbuilder = new StringBuilder();

      for(char c0 : p_214219_1_.toCharArray()) {
         if (c0 != 167 && c0 != 127) {
            stringbuilder.append(c0);
         }
      }

      return stringbuilder.toString();
   }

   private void pageBack() {
      if (this.currentPage > 0) {
         --this.currentPage;
         this.field_214240_i = 0;
         this.field_214241_j = this.field_214240_i;
      }

      this.updateButtonVisibility();
   }

   private void pageForward() {
      if (this.currentPage < this.getNumPages() - 1) {
         ++this.currentPage;
         this.field_214240_i = 0;
         this.field_214241_j = this.field_214240_i;
      } else {
         this.appendPageToBook();
         if (this.currentPage < this.getNumPages() - 1) {
            ++this.currentPage;
         }

         this.field_214240_i = 0;
         this.field_214241_j = this.field_214240_i;
      }

      this.updateButtonVisibility();
   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void updateButtonVisibility() {
      this.backButton.visible = !this.isSigning && this.currentPage > 0;
      this.forwardButton.visible = !this.isSigning;
      this.doneButton.visible = !this.isSigning;
      this.signButton.visible = !this.isSigning;
      this.cancelButton.visible = this.isSigning;
      this.finalizeButton.visible = this.isSigning;
      this.finalizeButton.active = !this.title.trim().isEmpty();
   }

   private void eraseEmptyTrailingPages() {
      ListIterator<String> listiterator = this.pages.listIterator(this.pages.size());

      while(listiterator.hasPrevious() && listiterator.previous().isEmpty()) {
         listiterator.remove();
      }

   }

   private void saveChanges(boolean p_214198_1_) {
      if (this.isModified) {
         this.eraseEmptyTrailingPages();
         ListNBT listnbt = new ListNBT();
         this.pages.stream().map(StringNBT::valueOf).forEach(listnbt::add);
         if (!this.pages.isEmpty()) {
            this.book.addTagElement("pages", listnbt);
         }

         if (p_214198_1_) {
            this.book.addTagElement("author", StringNBT.valueOf(this.owner.getGameProfile().getName()));
            this.book.addTagElement("title", StringNBT.valueOf(this.title.trim()));
         }

         this.minecraft.getConnection().send(new CEditBookPacket(this.book, p_214198_1_, this.hand));
      }
   }

   private void appendPageToBook() {
      if (this.getNumPages() < 100) {
         this.pages.add("");
         this.isModified = true;
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else {
         return this.isSigning ? this.titleKeyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) : this.bookKeyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);
      }
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      if (super.charTyped(p_charTyped_1_, p_charTyped_2_)) {
         return true;
      } else if (this.isSigning) {
         if (this.title.length() < 16 && SharedConstants.isAllowedChatCharacter(p_charTyped_1_)) {
            this.title = this.title + Character.toString(p_charTyped_1_);
            this.updateButtonVisibility();
            this.isModified = true;
            return true;
         } else {
            return false;
         }
      } else if (SharedConstants.isAllowedChatCharacter(p_charTyped_1_)) {
         this.func_214202_k(Character.toString(p_charTyped_1_));
         return true;
      } else {
         return false;
      }
   }

   private boolean bookKeyPressed(int p_214230_1_, int p_214230_2_, int p_214230_3_) {
      String s = this.getCurrentPageText();
      if (Screen.isSelectAll(p_214230_1_)) {
         this.field_214241_j = 0;
         this.field_214240_i = s.length();
         return true;
      } else if (Screen.isCopy(p_214230_1_)) {
         this.minecraft.keyboardHandler.setClipboard(this.func_214231_i());
         return true;
      } else if (Screen.isPaste(p_214230_1_)) {
         this.func_214202_k(this.func_214219_a(TextFormatting.stripFormatting(this.minecraft.keyboardHandler.getClipboard().replaceAll("\\r", ""))));
         this.field_214241_j = this.field_214240_i;
         return true;
      } else if (Screen.isCut(p_214230_1_)) {
         this.minecraft.keyboardHandler.setClipboard(this.func_214231_i());
         this.func_214192_g();
         return true;
      } else {
         switch(p_214230_1_) {
         case 257:
         case 335:
            this.func_214202_k("\n");
            return true;
         case 259:
            this.func_214207_b(s);
            return true;
         case 261:
            this.func_214221_c(s);
            return true;
         case 262:
            this.func_214218_e(s);
            return true;
         case 263:
            this.func_214200_d(s);
            return true;
         case 264:
            this.func_214209_g(s);
            return true;
         case 265:
            this.func_214197_f(s);
            return true;
         case 266:
            this.backButton.onPress();
            return true;
         case 267:
            this.forwardButton.onPress();
            return true;
         case 268:
            this.func_214220_h(s);
            return true;
         case 269:
            this.func_214211_i(s);
            return true;
         default:
            return false;
         }
      }
   }

   private void func_214207_b(String p_214207_1_) {
      if (!p_214207_1_.isEmpty()) {
         if (this.field_214241_j != this.field_214240_i) {
            this.func_214192_g();
         } else if (this.field_214240_i > 0) {
            String s = (new StringBuilder(p_214207_1_)).deleteCharAt(Math.max(0, this.field_214240_i - 1)).toString();
            this.setCurrentPageText(s);
            this.field_214240_i = Math.max(0, this.field_214240_i - 1);
            this.field_214241_j = this.field_214240_i;
         }
      }

   }

   private void func_214221_c(String p_214221_1_) {
      if (!p_214221_1_.isEmpty()) {
         if (this.field_214241_j != this.field_214240_i) {
            this.func_214192_g();
         } else if (this.field_214240_i < p_214221_1_.length()) {
            String s = (new StringBuilder(p_214221_1_)).deleteCharAt(Math.max(0, this.field_214240_i)).toString();
            this.setCurrentPageText(s);
         }
      }

   }

   private void func_214200_d(String p_214200_1_) {
      int i = this.font.isBidirectional() ? 1 : -1;
      if (Screen.hasControlDown()) {
         this.field_214240_i = this.font.func_216863_a(p_214200_1_, i, this.field_214240_i, true);
      } else {
         this.field_214240_i = Math.max(0, this.field_214240_i + i);
      }

      if (!Screen.hasShiftDown()) {
         this.field_214241_j = this.field_214240_i;
      }

   }

   private void func_214218_e(String p_214218_1_) {
      int i = this.font.isBidirectional() ? -1 : 1;
      if (Screen.hasControlDown()) {
         this.field_214240_i = this.font.func_216863_a(p_214218_1_, i, this.field_214240_i, true);
      } else {
         this.field_214240_i = Math.min(p_214218_1_.length(), this.field_214240_i + i);
      }

      if (!Screen.hasShiftDown()) {
         this.field_214241_j = this.field_214240_i;
      }

   }

   private void func_214197_f(String p_214197_1_) {
      if (!p_214197_1_.isEmpty()) {
         EditBookScreen.Point editbookscreen$point = this.func_214194_c(p_214197_1_, this.field_214240_i);
         if (editbookscreen$point.y == 0) {
            this.field_214240_i = 0;
            if (!Screen.hasShiftDown()) {
               this.field_214241_j = this.field_214240_i;
            }
         } else {
            int i = this.func_214203_a(p_214197_1_, new EditBookScreen.Point(editbookscreen$point.x + this.func_214206_a(p_214197_1_, this.field_214240_i) / 3, editbookscreen$point.y - 9));
            if (i >= 0) {
               this.field_214240_i = i;
               if (!Screen.hasShiftDown()) {
                  this.field_214241_j = this.field_214240_i;
               }
            }
         }
      }

   }

   private void func_214209_g(String p_214209_1_) {
      if (!p_214209_1_.isEmpty()) {
         EditBookScreen.Point editbookscreen$point = this.func_214194_c(p_214209_1_, this.field_214240_i);
         int i = this.font.wordWrapHeight(p_214209_1_ + "" + TextFormatting.BLACK + "_", 114);
         if (editbookscreen$point.y + 9 == i) {
            this.field_214240_i = p_214209_1_.length();
            if (!Screen.hasShiftDown()) {
               this.field_214241_j = this.field_214240_i;
            }
         } else {
            int j = this.func_214203_a(p_214209_1_, new EditBookScreen.Point(editbookscreen$point.x + this.func_214206_a(p_214209_1_, this.field_214240_i) / 3, editbookscreen$point.y + 9));
            if (j >= 0) {
               this.field_214240_i = j;
               if (!Screen.hasShiftDown()) {
                  this.field_214241_j = this.field_214240_i;
               }
            }
         }
      }

   }

   private void func_214220_h(String p_214220_1_) {
      this.field_214240_i = this.func_214203_a(p_214220_1_, new EditBookScreen.Point(0, this.func_214194_c(p_214220_1_, this.field_214240_i).y));
      if (!Screen.hasShiftDown()) {
         this.field_214241_j = this.field_214240_i;
      }

   }

   private void func_214211_i(String p_214211_1_) {
      this.field_214240_i = this.func_214203_a(p_214211_1_, new EditBookScreen.Point(113, this.func_214194_c(p_214211_1_, this.field_214240_i).y));
      if (!Screen.hasShiftDown()) {
         this.field_214241_j = this.field_214240_i;
      }

   }

   private void func_214192_g() {
      if (this.field_214241_j != this.field_214240_i) {
         String s = this.getCurrentPageText();
         int i = Math.min(this.field_214240_i, this.field_214241_j);
         int j = Math.max(this.field_214240_i, this.field_214241_j);
         String s1 = s.substring(0, i) + s.substring(j);
         this.field_214240_i = i;
         this.field_214241_j = this.field_214240_i;
         this.setCurrentPageText(s1);
      }
   }

   private int func_214206_a(String p_214206_1_, int p_214206_2_) {
      return (int)this.font.func_211125_a(p_214206_1_.charAt(MathHelper.clamp(p_214206_2_, 0, p_214206_1_.length() - 1)));
   }

   private boolean titleKeyPressed(int p_214196_1_, int p_214196_2_, int p_214196_3_) {
      switch(p_214196_1_) {
      case 257:
      case 335:
         if (!this.title.isEmpty()) {
            this.saveChanges(true);
            this.minecraft.setScreen((Screen)null);
         }

         return true;
      case 259:
         if (!this.title.isEmpty()) {
            this.title = this.title.substring(0, this.title.length() - 1);
            this.updateButtonVisibility();
         }

         return true;
      default:
         return false;
      }
   }

   private String getCurrentPageText() {
      return this.currentPage >= 0 && this.currentPage < this.pages.size() ? this.pages.get(this.currentPage) : "";
   }

   private void setCurrentPageText(String p_214217_1_) {
      if (this.currentPage >= 0 && this.currentPage < this.pages.size()) {
         this.pages.set(this.currentPage, p_214217_1_);
         this.isModified = true;
      }

   }

   private void func_214202_k(String p_214202_1_) {
      if (this.field_214241_j != this.field_214240_i) {
         this.func_214192_g();
      }

      String s = this.getCurrentPageText();
      this.field_214240_i = MathHelper.clamp(this.field_214240_i, 0, s.length());
      String s1 = (new StringBuilder(s)).insert(this.field_214240_i, p_214202_1_).toString();
      int i = this.font.wordWrapHeight(s1 + "" + TextFormatting.BLACK + "_", 114);
      if (i <= 128 && s1.length() < 1024) {
         this.setCurrentPageText(s1);
         this.field_214241_j = this.field_214240_i = Math.min(this.getCurrentPageText().length(), this.field_214240_i + p_214202_1_.length());
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.setFocused((IGuiEventListener)null);
      RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
      this.minecraft.getTextureManager().bind(ReadBookScreen.BOOK_LOCATION);
      int i = (this.width - 192) / 2;
      int j = 2;
      this.blit(i, 2, 0, 0, 192, 192);
      if (this.isSigning) {
         String s = this.title;
         if (this.frameTick / 6 % 2 == 0) {
            s = s + "" + TextFormatting.BLACK + "_";
         } else {
            s = s + "" + TextFormatting.GRAY + "_";
         }

         String s1 = I18n.get("book.editTitle");
         int k = this.func_214225_l(s1);
         this.font.func_211126_b(s1, (float)(i + 36 + (114 - k) / 2), 34.0F, 0);
         int l = this.func_214225_l(s);
         this.font.func_211126_b(s, (float)(i + 36 + (114 - l) / 2), 50.0F, 0);
         String s2 = I18n.get("book.byAuthor", this.owner.getName().getString());
         int i1 = this.func_214225_l(s2);
         this.font.func_211126_b(TextFormatting.DARK_GRAY + s2, (float)(i + 36 + (114 - i1) / 2), 60.0F, 0);
         String s3 = I18n.get("book.finalizeWarning");
         this.font.func_78279_b(s3, i + 36, 82, 114, 0);
      } else {
         String s4 = I18n.get("book.pageIndicator", this.currentPage + 1, this.getNumPages());
         String s5 = this.getCurrentPageText();
         int j1 = this.func_214225_l(s4);
         this.font.func_211126_b(s4, (float)(i - j1 + 192 - 44), 18.0F, 0);
         this.font.func_78279_b(s5, i + 36, 32, 114, 0);
         this.func_214222_m(s5);
         if (this.frameTick / 6 % 2 == 0) {
            EditBookScreen.Point editbookscreen$point = this.func_214194_c(s5, this.field_214240_i);
            if (this.font.isBidirectional()) {
               this.func_214227_a(editbookscreen$point);
               editbookscreen$point.x = editbookscreen$point.x - 4;
            }

            this.func_214224_c(editbookscreen$point);
            if (this.field_214240_i < s5.length()) {
               AbstractGui.fill(editbookscreen$point.x, editbookscreen$point.y - 1, editbookscreen$point.x + 1, editbookscreen$point.y + 9, -16777216);
            } else {
               this.font.func_211126_b("_", (float)editbookscreen$point.x, (float)editbookscreen$point.y, 0);
            }
         }
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   private int func_214225_l(String p_214225_1_) {
      return this.font.width(this.font.isBidirectional() ? this.font.bidirectionalShaping(p_214225_1_) : p_214225_1_);
   }

   private int func_214216_b(String p_214216_1_, int p_214216_2_) {
      return this.font.func_78259_e(p_214216_1_, p_214216_2_);
   }

   private String func_214231_i() {
      String s = this.getCurrentPageText();
      int i = Math.min(this.field_214240_i, this.field_214241_j);
      int j = Math.max(this.field_214240_i, this.field_214241_j);
      return s.substring(i, j);
   }

   private void func_214222_m(String p_214222_1_) {
      if (this.field_214241_j != this.field_214240_i) {
         int i = Math.min(this.field_214240_i, this.field_214241_j);
         int j = Math.max(this.field_214240_i, this.field_214241_j);
         String s = p_214222_1_.substring(i, j);
         int k = this.font.func_216863_a(p_214222_1_, 1, j, true);
         String s1 = p_214222_1_.substring(i, k);
         EditBookScreen.Point editbookscreen$point = this.func_214194_c(p_214222_1_, i);
         EditBookScreen.Point editbookscreen$point1 = new EditBookScreen.Point(editbookscreen$point.x, editbookscreen$point.y + 9);

         while(!s.isEmpty()) {
            int l = this.func_214216_b(s1, 114 - editbookscreen$point.x);
            if (s.length() <= l) {
               editbookscreen$point1.x = editbookscreen$point.x + this.func_214225_l(s);
               this.func_214223_a(editbookscreen$point, editbookscreen$point1);
               break;
            }

            l = Math.min(l, s.length() - 1);
            String s2 = s.substring(0, l);
            char c0 = s.charAt(l);
            boolean flag = c0 == ' ' || c0 == '\n';
            s = TextFormatting.func_211164_a(s2) + s.substring(l + (flag ? 1 : 0));
            s1 = TextFormatting.func_211164_a(s2) + s1.substring(l + (flag ? 1 : 0));
            editbookscreen$point1.x = editbookscreen$point.x + this.func_214225_l(s2 + " ");
            this.func_214223_a(editbookscreen$point, editbookscreen$point1);
            editbookscreen$point.x = 0;
            editbookscreen$point.y = editbookscreen$point.y + 9;
            editbookscreen$point1.y = editbookscreen$point1.y + 9;
         }

      }
   }

   private void func_214223_a(EditBookScreen.Point p_214223_1_, EditBookScreen.Point p_214223_2_) {
      EditBookScreen.Point editbookscreen$point = new EditBookScreen.Point(p_214223_1_.x, p_214223_1_.y);
      EditBookScreen.Point editbookscreen$point1 = new EditBookScreen.Point(p_214223_2_.x, p_214223_2_.y);
      if (this.font.isBidirectional()) {
         this.func_214227_a(editbookscreen$point);
         this.func_214227_a(editbookscreen$point1);
         int i = editbookscreen$point1.x;
         editbookscreen$point1.x = editbookscreen$point.x;
         editbookscreen$point.x = i;
      }

      this.func_214224_c(editbookscreen$point);
      this.func_214224_c(editbookscreen$point1);
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder bufferbuilder = tessellator.getBuilder();
      RenderSystem.color4f(0.0F, 0.0F, 255.0F, 255.0F);
      RenderSystem.disableTexture();
      RenderSystem.enableColorLogicOp();
      RenderSystem.logicOp(GlStateManager.LogicOp.OR_REVERSE);
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
      bufferbuilder.vertex((double)editbookscreen$point.x, (double)editbookscreen$point1.y, 0.0D).endVertex();
      bufferbuilder.vertex((double)editbookscreen$point1.x, (double)editbookscreen$point1.y, 0.0D).endVertex();
      bufferbuilder.vertex((double)editbookscreen$point1.x, (double)editbookscreen$point.y, 0.0D).endVertex();
      bufferbuilder.vertex((double)editbookscreen$point.x, (double)editbookscreen$point.y, 0.0D).endVertex();
      tessellator.end();
      RenderSystem.disableColorLogicOp();
      RenderSystem.enableTexture();
   }

   private EditBookScreen.Point func_214194_c(String p_214194_1_, int p_214194_2_) {
      EditBookScreen.Point editbookscreen$point = new EditBookScreen.Point();
      int i = 0;
      int j = 0;

      for(String s = p_214194_1_; !s.isEmpty(); j = i) {
         int k = this.func_214216_b(s, 114);
         if (s.length() <= k) {
            String s3 = s.substring(0, Math.min(Math.max(p_214194_2_ - j, 0), s.length()));
            editbookscreen$point.x = editbookscreen$point.x + this.func_214225_l(s3);
            break;
         }

         String s1 = s.substring(0, k);
         char c0 = s.charAt(k);
         boolean flag = c0 == ' ' || c0 == '\n';
         s = TextFormatting.func_211164_a(s1) + s.substring(k + (flag ? 1 : 0));
         i += s1.length() + (flag ? 1 : 0);
         if (i - 1 >= p_214194_2_) {
            String s2 = s1.substring(0, Math.min(Math.max(p_214194_2_ - j, 0), s1.length()));
            editbookscreen$point.x = editbookscreen$point.x + this.func_214225_l(s2);
            break;
         }

         editbookscreen$point.y = editbookscreen$point.y + 9;
      }

      return editbookscreen$point;
   }

   private void func_214227_a(EditBookScreen.Point p_214227_1_) {
      if (this.font.isBidirectional()) {
         p_214227_1_.x = 114 - p_214227_1_.x;
      }

   }

   private void func_214210_b(EditBookScreen.Point p_214210_1_) {
      p_214210_1_.x = p_214210_1_.x - (this.width - 192) / 2 - 36;
      p_214210_1_.y = p_214210_1_.y - 32;
   }

   private void func_214224_c(EditBookScreen.Point p_214224_1_) {
      p_214224_1_.x = p_214224_1_.x + (this.width - 192) / 2 + 36;
      p_214224_1_.y = p_214224_1_.y + 32;
   }

   private int func_214226_d(String p_214226_1_, int p_214226_2_) {
      if (p_214226_2_ < 0) {
         return 0;
      } else {
         float f1 = 0.0F;
         boolean flag = false;
         String s = p_214226_1_ + " ";

         for(int i = 0; i < s.length(); ++i) {
            char c0 = s.charAt(i);
            float f2 = this.font.func_211125_a(c0);
            if (c0 == 167 && i < s.length() - 1) {
               ++i;
               c0 = s.charAt(i);
               if (c0 != 'l' && c0 != 'L') {
                  if (c0 == 'r' || c0 == 'R') {
                     flag = false;
                  }
               } else {
                  flag = true;
               }

               f2 = 0.0F;
            }

            float f = f1;
            f1 += f2;
            if (flag && f2 > 0.0F) {
               ++f1;
            }

            if ((float)p_214226_2_ >= f && (float)p_214226_2_ < f1) {
               return i;
            }
         }

         return (float)p_214226_2_ >= f1 ? s.length() - 1 : -1;
      }
   }

   private int func_214203_a(String p_214203_1_, EditBookScreen.Point p_214203_2_) {
      int i = 16 * 9;
      if (p_214203_2_.y > i) {
         return -1;
      } else {
         int j = Integer.MIN_VALUE;
         int k = 9;
         int l = 0;

         for(String s = p_214203_1_; !s.isEmpty() && j < i; k += 9) {
            int i1 = this.func_214216_b(s, 114);
            if (i1 < s.length()) {
               String s1 = s.substring(0, i1);
               if (p_214203_2_.y >= j && p_214203_2_.y < k) {
                  int k1 = this.func_214226_d(s1, p_214203_2_.x);
                  return k1 < 0 ? -1 : l + k1;
               }

               char c0 = s.charAt(i1);
               boolean flag = c0 == ' ' || c0 == '\n';
               s = TextFormatting.func_211164_a(s1) + s.substring(i1 + (flag ? 1 : 0));
               l += s1.length() + (flag ? 1 : 0);
            } else if (p_214203_2_.y >= j && p_214203_2_.y < k) {
               int j1 = this.func_214226_d(s, p_214203_2_.x);
               return j1 < 0 ? -1 : l + j1;
            }

            j = k;
         }

         return p_214203_1_.length();
      }
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (p_mouseClicked_5_ == 0) {
         long i = Util.getMillis();
         String s = this.getCurrentPageText();
         if (!s.isEmpty()) {
            EditBookScreen.Point editbookscreen$point = new EditBookScreen.Point((int)p_mouseClicked_1_, (int)p_mouseClicked_3_);
            this.func_214210_b(editbookscreen$point);
            this.func_214227_a(editbookscreen$point);
            int j = this.func_214203_a(s, editbookscreen$point);
            if (j >= 0) {
               if (j == this.lastIndex && i - this.lastClickTime < 250L) {
                  if (this.field_214241_j == this.field_214240_i) {
                     this.field_214241_j = this.font.func_216863_a(s, -1, j, false);
                     this.field_214240_i = this.font.func_216863_a(s, 1, j, false);
                  } else {
                     this.field_214241_j = 0;
                     this.field_214240_i = this.getCurrentPageText().length();
                  }
               } else {
                  this.field_214240_i = j;
                  if (!Screen.hasShiftDown()) {
                     this.field_214241_j = this.field_214240_i;
                  }
               }
            }

            this.lastIndex = j;
         }

         this.lastClickTime = i;
      }

      return super.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_);
   }

   public boolean mouseDragged(double p_mouseDragged_1_, double p_mouseDragged_3_, int p_mouseDragged_5_, double p_mouseDragged_6_, double p_mouseDragged_8_) {
      if (p_mouseDragged_5_ == 0 && this.currentPage >= 0 && this.currentPage < this.pages.size()) {
         String s = this.pages.get(this.currentPage);
         EditBookScreen.Point editbookscreen$point = new EditBookScreen.Point((int)p_mouseDragged_1_, (int)p_mouseDragged_3_);
         this.func_214210_b(editbookscreen$point);
         this.func_214227_a(editbookscreen$point);
         int i = this.func_214203_a(s, editbookscreen$point);
         if (i >= 0) {
            this.field_214240_i = i;
         }
      }

      return super.mouseDragged(p_mouseDragged_1_, p_mouseDragged_3_, p_mouseDragged_5_, p_mouseDragged_6_, p_mouseDragged_8_);
   }

   @OnlyIn(Dist.CLIENT)
   class Point {
      private int x;
      private int y;

      Point() {
      }

      Point(int p_i50636_2_, int p_i50636_3_) {
         this.x = p_i50636_2_;
         this.y = p_i50636_3_;
      }
   }
}