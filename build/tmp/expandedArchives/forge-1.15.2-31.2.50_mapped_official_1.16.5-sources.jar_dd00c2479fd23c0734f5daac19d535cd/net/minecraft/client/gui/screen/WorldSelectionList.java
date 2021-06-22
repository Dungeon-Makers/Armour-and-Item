package net.minecraft.client.gui.screen;

import com.google.common.hash.Hashing;
import com.mojang.blaze3d.systems.RenderSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.client.AnvilConverterException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.list.ExtendedList;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.client.resources.I18n;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SharedConstants;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.Util;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.storage.SaveFormat;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.world.storage.WorldSummary;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@OnlyIn(Dist.CLIENT)
public class WorldSelectionList extends ExtendedList<WorldSelectionList.Entry> {
   private static final Logger LOGGER = LogManager.getLogger();
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat();
   private static final ResourceLocation ICON_MISSING = new ResourceLocation("textures/misc/unknown_server.png");
   private static final ResourceLocation ICON_OVERLAY_LOCATION = new ResourceLocation("textures/gui/world_selection.png");
   private final WorldSelectionScreen screen;
   @Nullable
   private List<WorldSummary> cachedList;

   public WorldSelectionList(WorldSelectionScreen p_i49846_1_, Minecraft p_i49846_2_, int p_i49846_3_, int p_i49846_4_, int p_i49846_5_, int p_i49846_6_, int p_i49846_7_, Supplier<String> p_i49846_8_, @Nullable WorldSelectionList p_i49846_9_) {
      super(p_i49846_2_, p_i49846_3_, p_i49846_4_, p_i49846_5_, p_i49846_6_, p_i49846_7_);
      this.screen = p_i49846_1_;
      if (p_i49846_9_ != null) {
         this.cachedList = p_i49846_9_.cachedList;
      }

      this.refreshList(p_i49846_8_, false);
   }

   public void refreshList(Supplier<String> p_212330_1_, boolean p_212330_2_) {
      this.clearEntries();
      SaveFormat saveformat = this.minecraft.getLevelSource();
      if (this.cachedList == null || p_212330_2_) {
         try {
            this.cachedList = saveformat.getLevelList();
         } catch (AnvilConverterException anvilconverterexception) {
            LOGGER.error("Couldn't load level list", (Throwable)anvilconverterexception);
            this.minecraft.setScreen(new ErrorScreen(new TranslationTextComponent("selectWorld.unable_to_load"), anvilconverterexception.getMessage()));
            return;
         }

         Collections.sort(this.cachedList);
      }

      String s = p_212330_1_.get().toLowerCase(Locale.ROOT);

      for(WorldSummary worldsummary : this.cachedList) {
         if (worldsummary.getLevelName().toLowerCase(Locale.ROOT).contains(s) || worldsummary.getLevelId().toLowerCase(Locale.ROOT).contains(s)) {
            this.addEntry(new WorldSelectionList.Entry(this, worldsummary, this.minecraft.getLevelSource()));
         }
      }

   }

   protected int getScrollbarPosition() {
      return super.getScrollbarPosition() + 20;
   }

   public int getRowWidth() {
      return super.getRowWidth() + 50;
   }

   protected boolean isFocused() {
      return this.screen.getFocused() == this;
   }

   public void setSelected(@Nullable WorldSelectionList.Entry p_setSelected_1_) {
      super.setSelected(p_setSelected_1_);
      if (p_setSelected_1_ != null) {
         WorldSummary worldsummary = p_setSelected_1_.summary;
         NarratorChatListener.INSTANCE.sayNow((new TranslationTextComponent("narrator.select", new TranslationTextComponent("narrator.select.world", worldsummary.getLevelName(), new Date(worldsummary.getLastPlayed()), worldsummary.isHardcore() ? I18n.get("gameMode.hardcore") : I18n.get("gameMode." + worldsummary.getGameMode().getName()), worldsummary.hasCheats() ? I18n.get("selectWorld.cheats") : "", worldsummary.func_200538_i()))).getString());
      }

   }

   protected void moveSelection(int p_moveSelection_1_) {
      super.moveSelection(p_moveSelection_1_);
      this.screen.updateButtonStatus(true);
   }

   public Optional<WorldSelectionList.Entry> getSelectedOpt() {
      return Optional.ofNullable(this.getSelected());
   }

   public WorldSelectionScreen getScreen() {
      return this.screen;
   }

   @OnlyIn(Dist.CLIENT)
   public final class Entry extends ExtendedList.AbstractListEntry<WorldSelectionList.Entry> implements AutoCloseable {
      private final Minecraft minecraft;
      private final WorldSelectionScreen screen;
      private final WorldSummary summary;
      private final ResourceLocation iconLocation;
      private File iconFile;
      @Nullable
      private final DynamicTexture icon;
      private long lastClickTime;

      public Entry(WorldSelectionList p_i50631_2_, WorldSummary p_i50631_3_, SaveFormat p_i50631_4_) {
         this.screen = p_i50631_2_.getScreen();
         this.summary = p_i50631_3_;
         this.minecraft = Minecraft.getInstance();
         this.iconLocation = new ResourceLocation("worlds/" + Hashing.sha1().hashUnencodedChars(p_i50631_3_.getLevelId()) + "/icon");
         this.iconFile = p_i50631_4_.func_186352_b(p_i50631_3_.getLevelId(), "icon.png");
         if (!this.iconFile.isFile()) {
            this.iconFile = null;
         }

         this.icon = this.loadServerIcon();
      }

      public void render(int p_render_1_, int p_render_2_, int p_render_3_, int p_render_4_, int p_render_5_, int p_render_6_, int p_render_7_, boolean p_render_8_, float p_render_9_) {
         String s = this.summary.getLevelName();
         String s1 = this.summary.getLevelId() + " (" + WorldSelectionList.DATE_FORMAT.format(new Date(this.summary.getLastPlayed())) + ")";
         if (StringUtils.isEmpty(s)) {
            s = I18n.get("selectWorld.world") + " " + (p_render_1_ + 1);
         }

         String s2 = "";
         if (this.summary.isRequiresConversion()) {
            s2 = I18n.get("selectWorld.conversion") + " " + s2;
         } else {
            s2 = I18n.get("gameMode." + this.summary.getGameMode().getName());
            if (this.summary.isHardcore()) {
               s2 = TextFormatting.DARK_RED + I18n.get("gameMode.hardcore") + TextFormatting.RESET;
            }

            if (this.summary.hasCheats()) {
               s2 = s2 + ", " + I18n.get("selectWorld.cheats");
            }

            String s3 = this.summary.func_200538_i().func_150254_d();
            if (this.summary.markVersionInList()) {
               if (this.summary.askToOpenWorld()) {
                  s2 = s2 + ", " + I18n.get("selectWorld.version") + " " + TextFormatting.RED + s3 + TextFormatting.RESET;
               } else {
                  s2 = s2 + ", " + I18n.get("selectWorld.version") + " " + TextFormatting.ITALIC + s3 + TextFormatting.RESET;
               }
            } else {
               s2 = s2 + ", " + I18n.get("selectWorld.version") + " " + s3;
            }
         }

         this.minecraft.font.func_211126_b(s, (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 1), 16777215);
         this.minecraft.font.func_211126_b(s1, (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 9 + 3), 8421504);
         this.minecraft.font.func_211126_b(s2, (float)(p_render_3_ + 32 + 3), (float)(p_render_2_ + 9 + 9 + 3), 8421504);
         RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
         this.minecraft.getTextureManager().bind(this.icon != null ? this.iconLocation : WorldSelectionList.ICON_MISSING);
         RenderSystem.enableBlend();
         AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, 0.0F, 32, 32, 32, 32);
         RenderSystem.disableBlend();
         if (this.minecraft.options.touchscreen || p_render_8_) {
            this.minecraft.getTextureManager().bind(WorldSelectionList.ICON_OVERLAY_LOCATION);
            AbstractGui.fill(p_render_3_, p_render_2_, p_render_3_ + 32, p_render_2_ + 32, -1601138544);
            RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
            int j = p_render_6_ - p_render_3_;
            int i = j < 32 ? 32 : 0;
            if (this.summary.markVersionInList()) {
               AbstractGui.blit(p_render_3_, p_render_2_, 32.0F, (float)i, 32, 32, 256, 256);
               if (this.summary.func_202842_n()) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, (float)i, 32, 32, 256, 256);
                  if (j < 32) {
                     ITextComponent itextcomponent = (new TranslationTextComponent("selectWorld.tooltip.unsupported", this.summary.func_200538_i())).func_211708_a(TextFormatting.RED);
                     this.screen.func_184861_a(this.minecraft.font.func_78280_d(itextcomponent.func_150254_d(), 175));
                  }
               } else if (this.summary.askToOpenWorld()) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 96.0F, (float)i, 32, 32, 256, 256);
                  if (j < 32) {
                     this.screen.func_184861_a(TextFormatting.RED + I18n.get("selectWorld.tooltip.fromNewerVersion1") + "\n" + TextFormatting.RED + I18n.get("selectWorld.tooltip.fromNewerVersion2"));
                  }
               } else if (!SharedConstants.getCurrentVersion().isStable()) {
                  AbstractGui.blit(p_render_3_, p_render_2_, 64.0F, (float)i, 32, 32, 256, 256);
                  if (j < 32) {
                     this.screen.func_184861_a(TextFormatting.GOLD + I18n.get("selectWorld.tooltip.snapshot1") + "\n" + TextFormatting.GOLD + I18n.get("selectWorld.tooltip.snapshot2"));
                  }
               }
            } else {
               AbstractGui.blit(p_render_3_, p_render_2_, 0.0F, (float)i, 32, 32, 256, 256);
            }
         }

      }

      public boolean mouseClicked(double p_mouseClicked_1_, double p_mouseClicked_3_, int p_mouseClicked_5_) {
         WorldSelectionList.this.setSelected(this);
         this.screen.updateButtonStatus(WorldSelectionList.this.getSelectedOpt().isPresent());
         if (p_mouseClicked_1_ - (double)WorldSelectionList.this.getRowLeft() <= 32.0D) {
            this.joinWorld();
            return true;
         } else if (Util.getMillis() - this.lastClickTime < 250L) {
            this.joinWorld();
            return true;
         } else {
            this.lastClickTime = Util.getMillis();
            return false;
         }
      }

      public void joinWorld() {
         if (!this.summary.shouldBackup() && !this.summary.func_202842_n()) {
            if (this.summary.askToOpenWorld()) {
               this.minecraft.setScreen(new ConfirmScreen((p_214434_1_) -> {
                  if (p_214434_1_) {
                     try {
                        this.loadWorld();
                     } catch (Exception exception) {
                        WorldSelectionList.LOGGER.error("Failure to open 'future world'", (Throwable)exception);
                        this.minecraft.setScreen(new AlertScreen(() -> {
                           this.minecraft.setScreen(this.screen);
                        }, new TranslationTextComponent("selectWorld.futureworld.error.title"), new TranslationTextComponent("selectWorld.futureworld.error.text")));
                     }
                  } else {
                     this.minecraft.setScreen(this.screen);
                  }

               }, new TranslationTextComponent("selectWorld.versionQuestion"), new TranslationTextComponent("selectWorld.versionWarning", this.summary.func_200538_i().func_150254_d()), I18n.get("selectWorld.versionJoinButton"), I18n.get("gui.cancel")));
            } else {
               this.loadWorld();
            }
         } else {
            ITextComponent itextcomponent = new TranslationTextComponent("selectWorld.backupQuestion");
            ITextComponent itextcomponent1 = new TranslationTextComponent("selectWorld.backupWarning", this.summary.func_200538_i().func_150254_d(), SharedConstants.getCurrentVersion().getName());
            if (this.summary.func_202842_n()) {
               itextcomponent = new TranslationTextComponent("selectWorld.backupQuestion.customized");
               itextcomponent1 = new TranslationTextComponent("selectWorld.backupWarning.customized");
            }

            this.minecraft.setScreen(new ConfirmBackupScreen(this.screen, (p_214436_1_, p_214436_2_) -> {
               if (p_214436_1_) {
                  String s = this.summary.getLevelId();
                  EditWorldScreen.func_200212_a(this.minecraft.getLevelSource(), s);
               }

               this.loadWorld();
            }, itextcomponent, itextcomponent1, false));
         }

      }

      public void deleteWorld() {
         this.minecraft.setScreen(new ConfirmScreen((p_214440_1_) -> {
            if (p_214440_1_) {
               this.minecraft.setScreen(new WorkingScreen());
               SaveFormat saveformat = this.minecraft.getLevelSource();
               saveformat.func_75802_e(this.summary.getLevelId());
               WorldSelectionList.this.refreshList(() -> {
                  return this.screen.searchBox.getValue();
               }, true);
            }

            this.minecraft.setScreen(this.screen);
         }, new TranslationTextComponent("selectWorld.deleteQuestion"), new TranslationTextComponent("selectWorld.deleteWarning", this.summary.getLevelName()), I18n.get("selectWorld.deleteButton"), I18n.get("gui.cancel")));
      }

      public void editWorld() {
         this.minecraft.setScreen(new EditWorldScreen((p_214435_1_) -> {
            if (p_214435_1_) {
               WorldSelectionList.this.refreshList(() -> {
                  return this.screen.searchBox.getValue();
               }, true);
            }

            this.minecraft.setScreen(this.screen);
         }, this.summary.getLevelId()));
      }

      public void recreateWorld() {
         try {
            this.minecraft.setScreen(new WorkingScreen());
            CreateWorldScreen createworldscreen = new CreateWorldScreen(this.screen);
            SaveHandler savehandler = this.minecraft.getLevelSource().func_197715_a(this.summary.getLevelId(), (MinecraftServer)null);
            WorldInfo worldinfo = savehandler.func_75757_d();
            if (worldinfo != null) {
               createworldscreen.func_146318_a(worldinfo);
               if (this.summary.func_202842_n()) {
                  this.minecraft.setScreen(new ConfirmScreen((p_214439_2_) -> {
                     this.minecraft.setScreen((Screen)(p_214439_2_ ? createworldscreen : this.screen));
                  }, new TranslationTextComponent("selectWorld.recreate.customized.title"), new TranslationTextComponent("selectWorld.recreate.customized.text"), I18n.get("gui.proceed"), I18n.get("gui.cancel")));
               } else {
                  this.minecraft.setScreen(createworldscreen);
               }
            }
         } catch (Exception exception) {
            WorldSelectionList.LOGGER.error("Unable to recreate world", (Throwable)exception);
            this.minecraft.setScreen(new AlertScreen(() -> {
               this.minecraft.setScreen(this.screen);
            }, new TranslationTextComponent("selectWorld.recreate.error.title"), new TranslationTextComponent("selectWorld.recreate.error.text")));
         }

      }

      private void loadWorld() {
         this.minecraft.getSoundManager().play(SimpleSound.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
         if (this.minecraft.getLevelSource().levelExists(this.summary.getLevelId())) {
            this.minecraft.func_71371_a(this.summary.getLevelId(), this.summary.getLevelName(), (WorldSettings)null);
         }

      }

      @Nullable
      private DynamicTexture loadServerIcon() {
         boolean flag = this.iconFile != null && this.iconFile.isFile();
         if (flag) {
            try (InputStream inputstream = new FileInputStream(this.iconFile)) {
               NativeImage nativeimage = NativeImage.read(inputstream);
               Validate.validState(nativeimage.getWidth() == 64, "Must be 64 pixels wide");
               Validate.validState(nativeimage.getHeight() == 64, "Must be 64 pixels high");
               DynamicTexture dynamictexture = new DynamicTexture(nativeimage);
               this.minecraft.getTextureManager().register(this.iconLocation, dynamictexture);
               return dynamictexture;
            } catch (Throwable throwable) {
               WorldSelectionList.LOGGER.error("Invalid icon for world {}", this.summary.getLevelId(), throwable);
               this.iconFile = null;
               return null;
            }
         } else {
            this.minecraft.getTextureManager().release(this.iconLocation);
            return null;
         }
      }

      public void close() {
         if (this.icon != null) {
            this.icon.close();
         }

      }
   }
}