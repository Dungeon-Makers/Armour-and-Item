package net.minecraft.client.gui.screen;

import net.minecraft.client.GameSettings;
import net.minecraft.client.gui.AccessibilityScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.button.LockIconButton;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.settings.AbstractOption;
import net.minecraft.network.play.client.CLockDifficultyPacket;
import net.minecraft.network.play.client.CSetDifficultyPacket;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.Difficulty;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class OptionsScreen extends Screen {
   private static final AbstractOption[] OPTION_SCREEN_OPTIONS = new AbstractOption[]{AbstractOption.FOV};
   private final Screen lastScreen;
   private final GameSettings options;
   private Button difficultyButton;
   private LockIconButton lockButton;
   private Difficulty currentDifficulty;

   public OptionsScreen(Screen p_i1046_1_, GameSettings p_i1046_2_) {
      super(new TranslationTextComponent("options.title"));
      this.lastScreen = p_i1046_1_;
      this.options = p_i1046_2_;
   }

   protected void init() {
      int i = 0;

      for(AbstractOption abstractoption : OPTION_SCREEN_OPTIONS) {
         int j = this.width / 2 - 155 + i % 2 * 160;
         int k = this.height / 6 - 12 + 24 * (i >> 1);
         this.addButton(abstractoption.createButton(this.minecraft.options, j, k, 150));
         ++i;
      }

      if (this.minecraft.level != null) {
         this.currentDifficulty = this.minecraft.level.getDifficulty();
         this.difficultyButton = this.addButton(new Button(this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, this.func_175355_a(this.currentDifficulty), (p_213051_1_) -> {
            this.currentDifficulty = Difficulty.byId(this.currentDifficulty.getId() + 1);
            this.minecraft.getConnection().send(new CSetDifficultyPacket(this.currentDifficulty));
            this.difficultyButton.setMessage(this.func_175355_a(this.currentDifficulty));
         }));
         if (this.minecraft.hasSingleplayerServer() && !this.minecraft.level.getLevelData().isHardcore()) {
            this.difficultyButton.setWidth(this.difficultyButton.getWidth() - 20);
            this.lockButton = this.addButton(new LockIconButton(this.difficultyButton.x + this.difficultyButton.getWidth(), this.difficultyButton.y, (p_213054_1_) -> {
               this.minecraft.setScreen(new ConfirmScreen(this::lockCallback, new TranslationTextComponent("difficulty.lock.title"), new TranslationTextComponent("difficulty.lock.question", new TranslationTextComponent("options.difficulty." + this.minecraft.level.getLevelData().getDifficulty().getKey()))));
            }));
            this.lockButton.setLocked(this.minecraft.level.getLevelData().isDifficultyLocked());
            this.lockButton.active = !this.lockButton.isLocked();
            this.difficultyButton.active = !this.lockButton.isLocked();
         } else {
            this.difficultyButton.active = false;
         }
      } else {
         this.addButton(new OptionButton(this.width / 2 - 155 + i % 2 * 160, this.height / 6 - 12 + 24 * (i >> 1), 150, 20, AbstractOption.REALMS_NOTIFICATIONS, AbstractOption.REALMS_NOTIFICATIONS.func_216743_c(this.options), (p_213057_1_) -> {
            AbstractOption.REALMS_NOTIFICATIONS.toggle(this.options);
            this.options.save();
            p_213057_1_.setMessage(AbstractOption.REALMS_NOTIFICATIONS.func_216743_c(this.options));
         }));
      }

      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 48 - 6, 150, 20, I18n.get("options.skinCustomisation"), (p_213055_1_) -> {
         this.minecraft.setScreen(new CustomizeSkinScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 48 - 6, 150, 20, I18n.get("options.sounds"), (p_213061_1_) -> {
         this.minecraft.setScreen(new OptionsSoundsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 72 - 6, 150, 20, I18n.get("options.video"), (p_213059_1_) -> {
         this.minecraft.setScreen(new VideoSettingsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 72 - 6, 150, 20, I18n.get("options.controls"), (p_213052_1_) -> {
         this.minecraft.setScreen(new ControlsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 96 - 6, 150, 20, I18n.get("options.language"), (p_213053_1_) -> {
         this.minecraft.setScreen(new LanguageScreen(this, this.options, this.minecraft.getLanguageManager()));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 96 - 6, 150, 20, I18n.get("options.chat.title"), (p_213049_1_) -> {
         this.minecraft.setScreen(new ChatOptionsScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 120 - 6, 150, 20, I18n.get("options.resourcepack"), (p_213060_1_) -> {
         this.minecraft.setScreen(new ResourcePacksScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 + 5, this.height / 6 + 120 - 6, 150, 20, I18n.get("options.accessibility.title"), (p_213058_1_) -> {
         this.minecraft.setScreen(new AccessibilityScreen(this, this.options));
      }));
      this.addButton(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, I18n.get("gui.done"), (p_213056_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
   }

   public String func_175355_a(Difficulty p_175355_1_) {
      return (new TranslationTextComponent("options.difficulty")).func_150258_a(": ").func_150257_a(p_175355_1_.getDisplayName()).func_150254_d();
   }

   private void lockCallback(boolean p_213050_1_) {
      this.minecraft.setScreen(this);
      if (p_213050_1_ && this.minecraft.level != null) {
         this.minecraft.getConnection().send(new CLockDifficultyPacket(true));
         this.lockButton.setLocked(true);
         this.lockButton.active = false;
         this.difficultyButton.active = false;
      }

   }

   public void removed() {
      this.options.save();
   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 15, 16777215);
      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   @Override
   public void onClose() {
      // We need to consider 2 potential parent screens here:
      // 1. From the main menu, in which case display the main menu
      // 2. From the pause menu, in which case exit back to game
      this.minecraft.setScreen(this.lastScreen instanceof IngameMenuScreen ? null : this.lastScreen);
   }
}
