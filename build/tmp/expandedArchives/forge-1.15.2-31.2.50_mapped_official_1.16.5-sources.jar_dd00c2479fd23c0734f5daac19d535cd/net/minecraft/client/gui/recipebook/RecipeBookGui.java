package net.minecraft.client.gui.recipebook;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.ToggleWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.resources.Language;
import net.minecraft.client.resources.LanguageManager;
import net.minecraft.client.util.ClientRecipeBook;
import net.minecraft.client.util.RecipeBookCategories;
import net.minecraft.client.util.SearchTreeManager;
import net.minecraft.inventory.container.RecipeBookContainer;
import net.minecraft.inventory.container.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipePlacer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeItemHelper;
import net.minecraft.network.play.client.CRecipeInfoPacket;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RecipeBookGui extends AbstractGui implements IRenderable, IGuiEventListener, IRecipeUpdateListener, IRecipePlacer<Ingredient> {
   protected static final ResourceLocation RECIPE_BOOK_LOCATION = new ResourceLocation("textures/gui/recipe_book.png");
   private int xOffset;
   private int width;
   private int height;
   protected final GhostRecipe ghostRecipe = new GhostRecipe();
   private final List<RecipeTabToggleWidget> tabButtons = Lists.newArrayList();
   private RecipeTabToggleWidget selectedTab;
   protected ToggleWidget filterButton;
   protected RecipeBookContainer<?> menu;
   protected Minecraft minecraft;
   private TextFieldWidget searchBox;
   private String lastSearch = "";
   protected ClientRecipeBook book;
   protected final RecipeBookPage recipeBookPage = new RecipeBookPage();
   protected final RecipeItemHelper stackedContents = new RecipeItemHelper();
   private int timesInventoryChanged;
   private boolean ignoreTextInput;

   public void init(int p_201520_1_, int p_201520_2_, Minecraft p_201520_3_, boolean p_201520_4_, RecipeBookContainer<?> p_201520_5_) {
      this.minecraft = p_201520_3_;
      this.width = p_201520_1_;
      this.height = p_201520_2_;
      this.menu = p_201520_5_;
      p_201520_3_.player.containerMenu = p_201520_5_;
      this.book = p_201520_3_.player.getRecipeBook();
      this.timesInventoryChanged = p_201520_3_.player.inventory.getTimesChanged();
      if (this.isVisible()) {
         this.initVisuals(p_201520_4_);
      }

      p_201520_3_.keyboardHandler.setSendRepeatsToGui(true);
   }

   public void initVisuals(boolean p_201518_1_) {
      this.xOffset = p_201518_1_ ? 0 : 86;
      int i = (this.width - 147) / 2 - this.xOffset;
      int j = (this.height - 166) / 2;
      this.stackedContents.clear();
      this.minecraft.player.inventory.fillStackedContents(this.stackedContents);
      this.menu.fillCraftSlotsStackedContents(this.stackedContents);
      String s = this.searchBox != null ? this.searchBox.getValue() : "";
      this.searchBox = new TextFieldWidget(this.minecraft.font, i + 25, j + 14, 80, 9 + 5, I18n.get("itemGroup.search"));
      this.searchBox.setMaxLength(50);
      this.searchBox.setBordered(false);
      this.searchBox.setVisible(true);
      this.searchBox.setTextColor(16777215);
      this.searchBox.setValue(s);
      this.recipeBookPage.init(this.minecraft, i, j);
      this.recipeBookPage.addListener(this);
      this.filterButton = new ToggleWidget(i + 110, j + 12, 26, 16, this.book.func_203432_a(this.menu));
      this.initFilterButtonTextures();
      this.tabButtons.clear();

      for(RecipeBookCategories recipebookcategories : this.menu.getRecipeBookCategories()) {
         this.tabButtons.add(new RecipeTabToggleWidget(recipebookcategories));
      }

      if (this.selectedTab != null) {
         this.selectedTab = this.tabButtons.stream().filter((p_209505_1_) -> {
            return p_209505_1_.getCategory().equals(this.selectedTab.getCategory());
         }).findFirst().orElse((RecipeTabToggleWidget)null);
      }

      if (this.selectedTab == null) {
         this.selectedTab = this.tabButtons.get(0);
      }

      this.selectedTab.setStateTriggered(true);
      this.updateCollections(false);
      this.updateTabs();
   }

   public boolean changeFocus(boolean p_changeFocus_1_) {
      return false;
   }

   protected void initFilterButtonTextures() {
      this.filterButton.initTextureValues(152, 41, 28, 18, RECIPE_BOOK_LOCATION);
   }

   public void removed() {
      this.searchBox = null;
      this.selectedTab = null;
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   public int updateScreenPosition(boolean p_193011_1_, int p_193011_2_, int p_193011_3_) {
      int i;
      if (this.isVisible() && !p_193011_1_) {
         i = 177 + (p_193011_2_ - p_193011_3_ - 200) / 2;
      } else {
         i = (p_193011_2_ - p_193011_3_) / 2;
      }

      return i;
   }

   public void toggleVisibility() {
      this.setVisible(!this.isVisible());
   }

   public boolean isVisible() {
      return this.book.func_192812_b();
   }

   protected void setVisible(boolean p_193006_1_) {
      this.book.func_192813_a(p_193006_1_);
      if (!p_193006_1_) {
         this.recipeBookPage.setInvisible();
      }

      this.sendUpdateSettings();
   }

   public void slotClicked(@Nullable Slot p_191874_1_) {
      if (p_191874_1_ != null && p_191874_1_.index < this.menu.getSize()) {
         this.ghostRecipe.clear();
         if (this.isVisible()) {
            this.updateStackedContents();
         }
      }

   }

   private void updateCollections(boolean p_193003_1_) {
      List<RecipeList> list = this.book.getCollection(this.selectedTab.getCategory());
      list.forEach((p_193944_1_) -> {
         p_193944_1_.canCraft(this.stackedContents, this.menu.getGridWidth(), this.menu.getGridHeight(), this.book);
      });
      List<RecipeList> list1 = Lists.newArrayList(list);
      list1.removeIf((p_193952_0_) -> {
         return !p_193952_0_.hasKnownRecipes();
      });
      list1.removeIf((p_193953_0_) -> {
         return !p_193953_0_.hasFitting();
      });
      String s = this.searchBox.getValue();
      if (!s.isEmpty()) {
         ObjectSet<RecipeList> objectset = new ObjectLinkedOpenHashSet<>(this.minecraft.getSearchTree(SearchTreeManager.RECIPE_COLLECTIONS).search(s.toLowerCase(Locale.ROOT)));
         list1.removeIf((p_193947_1_) -> {
            return !objectset.contains(p_193947_1_);
         });
      }

      if (this.book.func_203432_a(this.menu)) {
         list1.removeIf((p_193958_0_) -> {
            return !p_193958_0_.hasCraftable();
         });
      }

      this.recipeBookPage.updateCollections(list1, p_193003_1_);
   }

   private void updateTabs() {
      int i = (this.width - 147) / 2 - this.xOffset - 30;
      int j = (this.height - 166) / 2 + 3;
      int k = 27;
      int l = 0;

      for(RecipeTabToggleWidget recipetabtogglewidget : this.tabButtons) {
         RecipeBookCategories recipebookcategories = recipetabtogglewidget.getCategory();
         if (recipebookcategories != RecipeBookCategories.SEARCH && recipebookcategories != RecipeBookCategories.FURNACE_SEARCH) {
            if (recipetabtogglewidget.updateVisibility(this.book)) {
               recipetabtogglewidget.setPosition(i, j + 27 * l++);
               recipetabtogglewidget.startAnimation(this.minecraft);
            }
         } else {
            recipetabtogglewidget.visible = true;
            recipetabtogglewidget.setPosition(i, j + 27 * l++);
         }
      }

   }

   public void tick() {
      if (this.isVisible()) {
         if (this.timesInventoryChanged != this.minecraft.player.inventory.getTimesChanged()) {
            this.updateStackedContents();
            this.timesInventoryChanged = this.minecraft.player.inventory.getTimesChanged();
         }

      }
   }

   private void updateStackedContents() {
      this.stackedContents.clear();
      this.minecraft.player.inventory.fillStackedContents(this.stackedContents);
      this.menu.fillCraftSlotsStackedContents(this.stackedContents);
      this.updateCollections(false);
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      if (this.isVisible()) {
         RenderSystem.pushMatrix();
         RenderSystem.translatef(0.0F, 0.0F, 100.0F);
         this.minecraft.getTextureManager().bind(RECIPE_BOOK_LOCATION);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         int i = (this.width - 147) / 2 - this.xOffset;
         int j = (this.height - 166) / 2;
         this.blit(i, j, 1, 1, 147, 166);
         this.searchBox.render(p_render_1_, p_render_2_, p_render_3_);

         for(RecipeTabToggleWidget recipetabtogglewidget : this.tabButtons) {
            recipetabtogglewidget.render(p_render_1_, p_render_2_, p_render_3_);
         }

         this.filterButton.render(p_render_1_, p_render_2_, p_render_3_);
         this.recipeBookPage.func_194191_a(i, j, p_render_1_, p_render_2_, p_render_3_);
         RenderSystem.popMatrix();
      }
   }

   public void func_191876_c(int p_191876_1_, int p_191876_2_, int p_191876_3_, int p_191876_4_) {
      if (this.isVisible()) {
         this.recipeBookPage.func_193721_a(p_191876_3_, p_191876_4_);
         if (this.filterButton.isHovered()) {
            String s = this.func_205703_f();
            if (this.minecraft.screen != null) {
               this.minecraft.screen.renderTooltip(s, p_191876_3_, p_191876_4_);
            }
         }

         this.func_193015_d(p_191876_1_, p_191876_2_, p_191876_3_, p_191876_4_);
      }
   }

   protected String func_205703_f() {
      return I18n.get(this.filterButton.isStateTriggered() ? "gui.recipebook.toggleRecipes.craftable" : "gui.recipebook.toggleRecipes.all");
   }

   private void func_193015_d(int p_193015_1_, int p_193015_2_, int p_193015_3_, int p_193015_4_) {
      ItemStack itemstack = null;

      for(int i = 0; i < this.ghostRecipe.size(); ++i) {
         GhostRecipe.GhostIngredient ghostrecipe$ghostingredient = this.ghostRecipe.get(i);
         int j = ghostrecipe$ghostingredient.getX() + p_193015_1_;
         int k = ghostrecipe$ghostingredient.getY() + p_193015_2_;
         if (p_193015_3_ >= j && p_193015_4_ >= k && p_193015_3_ < j + 16 && p_193015_4_ < k + 16) {
            itemstack = ghostrecipe$ghostingredient.getItem();
         }
      }

      if (itemstack != null && this.minecraft.screen != null) {
         this.minecraft.screen.renderTooltip(this.minecraft.screen.getTooltipFromItem(itemstack), p_193015_3_, p_193015_4_);
      }

   }

   public void func_191864_a(int p_191864_1_, int p_191864_2_, boolean p_191864_3_, float p_191864_4_) {
      this.ghostRecipe.func_194188_a(this.minecraft, p_191864_1_, p_191864_2_, p_191864_3_, p_191864_4_);
   }

   public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
      if (this.isVisible() && !this.minecraft.player.isSpectator()) {
         if (this.recipeBookPage.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_, (this.width - 147) / 2 - this.xOffset, (this.height - 166) / 2, 147, 166)) {
            IRecipe<?> irecipe = this.recipeBookPage.getLastClickedRecipe();
            RecipeList recipelist = this.recipeBookPage.getLastClickedRecipeCollection();
            if (irecipe != null && recipelist != null) {
               if (!recipelist.isCraftable(irecipe) && this.ghostRecipe.getRecipe() == irecipe) {
                  return false;
               }

               this.ghostRecipe.clear();
               this.minecraft.gameMode.handlePlaceRecipe(this.minecraft.player.containerMenu.containerId, irecipe, Screen.hasShiftDown());
               if (!this.isOffsetNextToMainGUI()) {
                  this.setVisible(false);
               }
            }

            return true;
         } else if (this.searchBox.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            return true;
         } else if (this.filterButton.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
            boolean flag = this.toggleFiltering();
            this.filterButton.setStateTriggered(flag);
            this.sendUpdateSettings();
            this.updateCollections(false);
            return true;
         } else {
            for(RecipeTabToggleWidget recipetabtogglewidget : this.tabButtons) {
               if (recipetabtogglewidget.mouseClicked(p_mouseClicked_1_, p_mouseClicked_3_, p_mouseClicked_5_)) {
                  if (this.selectedTab != recipetabtogglewidget) {
                     this.selectedTab.setStateTriggered(false);
                     this.selectedTab = recipetabtogglewidget;
                     this.selectedTab.setStateTriggered(true);
                     this.updateCollections(true);
                  }

                  return true;
               }
            }

            return false;
         }
      } else {
         return false;
      }
   }

   protected boolean toggleFiltering() {
      boolean flag = !this.book.func_192815_c();
      this.book.func_192810_b(flag);
      return flag;
   }

   public boolean hasClickedOutside(double p_195604_1_, double p_195604_3_, int p_195604_5_, int p_195604_6_, int p_195604_7_, int p_195604_8_, int p_195604_9_) {
      if (!this.isVisible()) {
         return true;
      } else {
         boolean flag = p_195604_1_ < (double)p_195604_5_ || p_195604_3_ < (double)p_195604_6_ || p_195604_1_ >= (double)(p_195604_5_ + p_195604_7_) || p_195604_3_ >= (double)(p_195604_6_ + p_195604_8_);
         boolean flag1 = (double)(p_195604_5_ - 147) < p_195604_1_ && p_195604_1_ < (double)p_195604_5_ && (double)p_195604_6_ < p_195604_3_ && p_195604_3_ < (double)(p_195604_6_ + p_195604_8_);
         return flag && !flag1 && !this.selectedTab.isHovered();
      }
   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      this.ignoreTextInput = false;
      if (this.isVisible() && !this.minecraft.player.isSpectator()) {
         if (p_keyPressed_1_ == 256 && !this.isOffsetNextToMainGUI()) {
            this.setVisible(false);
            return true;
         } else if (this.searchBox.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
            this.checkSearchStringUpdate();
            return true;
         } else if (this.searchBox.isFocused() && this.searchBox.isVisible() && p_keyPressed_1_ != 256) {
            return true;
         } else if (this.minecraft.options.keyChat.matches(p_keyPressed_1_, p_keyPressed_2_) && !this.searchBox.isFocused()) {
            this.ignoreTextInput = true;
            this.searchBox.setFocus(true);
            return true;
         } else {
            return false;
         }
      } else {
         return false;
      }
   }

   public boolean keyReleased(int p_223281_1_, int p_223281_2_, int p_223281_3_) {
      this.ignoreTextInput = false;
      return IGuiEventListener.super.keyReleased(p_223281_1_, p_223281_2_, p_223281_3_);
   }

   public boolean charTyped(char p_charTyped_1_, int p_charTyped_2_) {
      if (this.ignoreTextInput) {
         return false;
      } else if (this.isVisible() && !this.minecraft.player.isSpectator()) {
         if (this.searchBox.charTyped(p_charTyped_1_, p_charTyped_2_)) {
            this.checkSearchStringUpdate();
            return true;
         } else {
            return IGuiEventListener.super.charTyped(p_charTyped_1_, p_charTyped_2_);
         }
      } else {
         return false;
      }
   }

   public boolean isMouseOver(double p_isMouseOver_1_, double p_isMouseOver_3_) {
      return false;
   }

   private void checkSearchStringUpdate() {
      String s = this.searchBox.getValue().toLowerCase(Locale.ROOT);
      this.pirateSpeechForThePeople(s);
      if (!s.equals(this.lastSearch)) {
         this.updateCollections(false);
         this.lastSearch = s;
      }

   }

   private void pirateSpeechForThePeople(String p_193716_1_) {
      if ("excitedze".equals(p_193716_1_)) {
         LanguageManager languagemanager = this.minecraft.getLanguageManager();
         Language language = languagemanager.getLanguage("en_pt");
         if (languagemanager.getSelected().compareTo(language) == 0) {
            return;
         }

         languagemanager.setSelected(language);
         this.minecraft.options.languageCode = language.getCode();
         net.minecraftforge.client.ForgeHooksClient.refreshResources(this.minecraft, net.minecraftforge.resource.VanillaResourceType.LANGUAGES);
         this.minecraft.font.func_78275_b(languagemanager.isBidirectional());
         this.minecraft.options.save();
      }

   }

   private boolean isOffsetNextToMainGUI() {
      return this.xOffset == 86;
   }

   public void recipesUpdated() {
      this.updateTabs();
      if (this.isVisible()) {
         this.updateCollections(false);
      }

   }

   public void recipesShown(List<IRecipe<?>> p_193001_1_) {
      for(IRecipe<?> irecipe : p_193001_1_) {
         this.minecraft.player.removeRecipeHighlight(irecipe);
      }

   }

   public void setupGhostRecipe(IRecipe<?> p_193951_1_, List<Slot> p_193951_2_) {
      ItemStack itemstack = p_193951_1_.getResultItem();
      this.ghostRecipe.setRecipe(p_193951_1_);
      this.ghostRecipe.addIngredient(Ingredient.of(itemstack), (p_193951_2_.get(0)).x, (p_193951_2_.get(0)).y);
      this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), p_193951_1_, p_193951_1_.getIngredients().iterator(), 0);
   }

   public void addItemToSlot(Iterator<Ingredient> p_201500_1_, int p_201500_2_, int p_201500_3_, int p_201500_4_, int p_201500_5_) {
      Ingredient ingredient = p_201500_1_.next();
      if (!ingredient.isEmpty()) {
         Slot slot = this.menu.slots.get(p_201500_2_);
         this.ghostRecipe.addIngredient(ingredient, slot.x, slot.y);
      }

   }

   protected void sendUpdateSettings() {
      if (this.minecraft.getConnection() != null) {
         this.minecraft.getConnection().send(new CRecipeInfoPacket(this.book.func_192812_b(), this.book.func_192815_c(), this.book.func_202883_c(), this.book.func_202884_d(), this.book.func_216758_e(), this.book.func_216761_f()));
      }

   }
}
