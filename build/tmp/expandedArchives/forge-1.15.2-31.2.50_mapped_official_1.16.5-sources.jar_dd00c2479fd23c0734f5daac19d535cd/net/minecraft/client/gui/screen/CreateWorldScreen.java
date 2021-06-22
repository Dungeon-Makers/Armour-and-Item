package net.minecraft.client.gui.screen;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.JsonOps;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTDynamicOps;
import net.minecraft.util.FileUtil;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.GameType;
import net.minecraft.world.WorldSettings;
import net.minecraft.world.WorldType;
import net.minecraft.world.storage.WorldInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class CreateWorldScreen extends Screen {
   private final Screen lastScreen;
   private TextFieldWidget nameEdit;
   private TextFieldWidget field_146335_h;
   private String resultFolder;
   private CreateWorldScreen.GameMode gameMode = CreateWorldScreen.GameMode.SURVIVAL;
   @Nullable
   private CreateWorldScreen.GameMode oldGameMode;
   private boolean field_146341_s = true;
   private boolean commands;
   private boolean commandsChanged;
   private boolean field_146338_v;
   private boolean hardCore;
   private boolean field_146345_x;
   private boolean displayOptions;
   private Button createButton;
   private Button modeButton;
   private Button moreOptionsButton;
   private Button field_146325_B;
   private Button field_146326_C;
   private Button field_146320_D;
   private Button commandsButton;
   private Button field_146322_F;
   private String gameModeHelp1;
   private String gameModeHelp2;
   private String field_146329_I;
   private String initName;
   private int field_146331_K;
   public CompoundNBT field_146334_a = new CompoundNBT();

   public CreateWorldScreen(Screen p_i46320_1_) {
      super(new TranslationTextComponent("selectWorld.create"));
      this.lastScreen = p_i46320_1_;
      this.field_146329_I = "";
      this.initName = I18n.get("selectWorld.newWorld");
   }

   public void tick() {
      this.nameEdit.tick();
      this.field_146335_h.tick();
   }

   protected void init() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(true);
      this.nameEdit = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, I18n.get("selectWorld.enterName")) {
         protected String getNarrationMessage() {
            return super.getNarrationMessage() + ". " + I18n.get("selectWorld.resultFolder") + " " + CreateWorldScreen.this.resultFolder;
         }
      };
      this.nameEdit.setValue(this.initName);
      this.nameEdit.setResponder((p_214319_1_) -> {
         this.initName = p_214319_1_;
         this.createButton.active = !this.nameEdit.getValue().isEmpty();
         this.updateResultFolder();
      });
      this.children.add(this.nameEdit);
      this.modeButton = this.addButton(new Button(this.width / 2 - 75, 115, 150, 20, I18n.get("selectWorld.gameMode"), (p_214316_1_) -> {
         switch(this.gameMode) {
         case SURVIVAL:
            this.setGameMode(CreateWorldScreen.GameMode.HARDCORE);
            break;
         case HARDCORE:
            this.setGameMode(CreateWorldScreen.GameMode.CREATIVE);
            break;
         case CREATIVE:
            this.setGameMode(CreateWorldScreen.GameMode.SURVIVAL);
         }

         p_214316_1_.queueNarration(250);
      }) {
         public String getMessage() {
            return I18n.get("selectWorld.gameMode") + ": " + I18n.get("selectWorld.gameMode." + CreateWorldScreen.this.gameMode.name);
         }

         protected String getNarrationMessage() {
            return super.getNarrationMessage() + ". " + CreateWorldScreen.this.gameModeHelp1 + " " + CreateWorldScreen.this.gameModeHelp2;
         }
      });
      this.field_146335_h = new TextFieldWidget(this.font, this.width / 2 - 100, 60, 200, 20, I18n.get("selectWorld.enterSeed"));
      this.field_146335_h.setValue(this.field_146329_I);
      this.field_146335_h.setResponder((p_214313_1_) -> {
         this.field_146329_I = this.field_146335_h.getValue();
      });
      this.children.add(this.field_146335_h);
      this.field_146325_B = this.addButton(new Button(this.width / 2 - 155, 100, 150, 20, I18n.get("selectWorld.mapFeatures"), (p_214322_1_) -> {
         this.field_146341_s = !this.field_146341_s;
         p_214322_1_.queueNarration(250);
      }) {
         public String getMessage() {
            return I18n.get("selectWorld.mapFeatures") + ' ' + I18n.get(CreateWorldScreen.this.field_146341_s ? "options.on" : "options.off");
         }

         protected String getNarrationMessage() {
            return super.getNarrationMessage() + ". " + I18n.get("selectWorld.mapFeatures.info");
         }
      });
      this.field_146325_B.visible = false;
      this.field_146320_D = this.addButton(new Button(this.width / 2 + 5, 100, 150, 20, I18n.get("selectWorld.mapType"), (p_214320_1_) -> {
         ++this.field_146331_K;
         if (this.field_146331_K >= WorldType.field_77139_a.length) {
            this.field_146331_K = 0;
         }

         while(!this.func_175299_g()) {
            ++this.field_146331_K;
            if (this.field_146331_K >= WorldType.field_77139_a.length) {
               this.field_146331_K = 0;
            }
         }

         this.field_146334_a = new CompoundNBT();
         this.setDisplayOptions(this.displayOptions);
         p_214320_1_.queueNarration(250);
      }) {
         public String getMessage() {
            return I18n.get("selectWorld.mapType") + ' ' + I18n.get(WorldType.field_77139_a[CreateWorldScreen.this.field_146331_K].func_77128_b());
         }

         protected String getNarrationMessage() {
            WorldType worldtype = WorldType.field_77139_a[CreateWorldScreen.this.field_146331_K];
            return worldtype.func_151357_h() ? super.getNarrationMessage() + ". " + I18n.get(worldtype.func_151359_c()) : super.getNarrationMessage();
         }
      });
      this.field_146320_D.visible = false;
      this.field_146322_F = this.addButton(new Button(this.width / 2 + 5, 120, 150, 20, I18n.get("selectWorld.customizeType"), (p_214314_1_) -> {
         WorldType.field_77139_a[this.field_146331_K].onCustomizeButton(this.minecraft, CreateWorldScreen.this);
      }));
      this.field_146322_F.visible = false;
      this.commandsButton = this.addButton(new Button(this.width / 2 - 155, 151, 150, 20, I18n.get("selectWorld.allowCommands"), (p_214315_1_) -> {
         this.commandsChanged = true;
         this.commands = !this.commands;
         p_214315_1_.queueNarration(250);
      }) {
         public String getMessage() {
            return I18n.get("selectWorld.allowCommands") + ' ' + I18n.get(CreateWorldScreen.this.commands && !CreateWorldScreen.this.hardCore ? "options.on" : "options.off");
         }

         protected String getNarrationMessage() {
            return super.getNarrationMessage() + ". " + I18n.get("selectWorld.allowCommands.info");
         }
      });
      this.commandsButton.visible = false;
      this.field_146326_C = this.addButton(new Button(this.width / 2 + 5, 151, 150, 20, I18n.get("selectWorld.bonusItems"), (p_214312_1_) -> {
         this.field_146338_v = !this.field_146338_v;
         p_214312_1_.queueNarration(250);
      }) {
         public String getMessage() {
            return I18n.get("selectWorld.bonusItems") + ' ' + I18n.get(CreateWorldScreen.this.field_146338_v && !CreateWorldScreen.this.hardCore ? "options.on" : "options.off");
         }
      });
      this.field_146326_C.visible = false;
      this.moreOptionsButton = this.addButton(new Button(this.width / 2 - 75, 187, 150, 20, I18n.get("selectWorld.moreWorldOptions"), (p_214321_1_) -> {
         this.toggleDisplayOptions();
      }));
      this.createButton = this.addButton(new Button(this.width / 2 - 155, this.height - 28, 150, 20, I18n.get("selectWorld.create"), (p_214318_1_) -> {
         this.onCreate();
      }));
      this.createButton.active = !this.initName.isEmpty();
      this.addButton(new Button(this.width / 2 + 5, this.height - 28, 150, 20, I18n.get("gui.cancel"), (p_214317_1_) -> {
         this.minecraft.setScreen(this.lastScreen);
      }));
      this.setDisplayOptions(this.displayOptions);
      this.setInitialFocus(this.nameEdit);
      this.setGameMode(this.gameMode);
      this.updateResultFolder();
   }

   private void updateGameModeHelp() {
      this.gameModeHelp1 = I18n.get("selectWorld.gameMode." + this.gameMode.name + ".line1");
      this.gameModeHelp2 = I18n.get("selectWorld.gameMode." + this.gameMode.name + ".line2");
   }

   private void updateResultFolder() {
      this.resultFolder = this.nameEdit.getValue().trim();
      if (this.resultFolder.isEmpty()) {
         this.resultFolder = "World";
      }

      try {
         this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
      } catch (Exception var4) {
         this.resultFolder = "World";

         try {
            this.resultFolder = FileUtil.findAvailableName(this.minecraft.getLevelSource().getBaseDir(), this.resultFolder, "");
         } catch (Exception exception) {
            throw new RuntimeException("Could not create save folder", exception);
         }
      }

   }

   public void removed() {
      this.minecraft.keyboardHandler.setSendRepeatsToGui(false);
   }

   private void onCreate() {
      this.minecraft.setScreen((Screen)null);
      if (!this.field_146345_x) {
         this.field_146345_x = true;
         long i = (new Random()).nextLong();
         String s = this.field_146335_h.getValue();
         if (!StringUtils.isEmpty(s)) {
            try {
               long j = Long.parseLong(s);
               if (j != 0L) {
                  i = j;
               }
            } catch (NumberFormatException var6) {
               i = (long)s.hashCode();
            }
         }

         WorldType.field_77139_a[this.field_146331_K].onGUICreateWorldPress();

         WorldSettings worldsettings = new WorldSettings(i, this.gameMode.gameType, this.field_146341_s, this.hardCore, WorldType.field_77139_a[this.field_146331_K]);
         worldsettings.func_205390_a(Dynamic.convert(NBTDynamicOps.INSTANCE, JsonOps.INSTANCE, this.field_146334_a));
         if (this.field_146338_v && !this.hardCore) {
            worldsettings.func_77159_a();
         }

         if (this.commands && !this.hardCore) {
            worldsettings.func_77166_b();
         }

         this.minecraft.func_71371_a(this.resultFolder, this.nameEdit.getValue().trim(), worldsettings);
      }
   }

   private boolean func_175299_g() {
      WorldType worldtype = WorldType.field_77139_a[this.field_146331_K];
      if (worldtype != null && worldtype.func_77126_d()) {
         return worldtype == WorldType.field_180272_g ? hasShiftDown() : true;
      } else {
         return false;
      }
   }

   private void toggleDisplayOptions() {
      this.setDisplayOptions(!this.displayOptions);
   }

   private void setGameMode(CreateWorldScreen.GameMode p_228200_1_) {
      if (!this.commandsChanged) {
         this.commands = p_228200_1_ == CreateWorldScreen.GameMode.CREATIVE;
      }

      if (p_228200_1_ == CreateWorldScreen.GameMode.HARDCORE) {
         this.hardCore = true;
         this.commandsButton.active = false;
         this.field_146326_C.active = false;
      } else {
         this.hardCore = false;
         this.commandsButton.active = true;
         this.field_146326_C.active = true;
      }

      this.gameMode = p_228200_1_;
      this.updateGameModeHelp();
   }

   private void setDisplayOptions(boolean p_146316_1_) {
      this.displayOptions = p_146316_1_;
      this.modeButton.visible = !this.displayOptions;
      this.field_146320_D.visible = this.displayOptions;
      if (WorldType.field_77139_a[this.field_146331_K] == WorldType.field_180272_g) {
         this.modeButton.active = false;
         if (this.oldGameMode == null) {
            this.oldGameMode = this.gameMode;
         }

         this.setGameMode(CreateWorldScreen.GameMode.DEBUG);
         this.field_146325_B.visible = false;
         this.field_146326_C.visible = false;
         this.commandsButton.visible = false;
         this.field_146322_F.visible = false;
      } else {
         this.modeButton.active = true;
         if (this.oldGameMode != null) {
            this.setGameMode(this.oldGameMode);
         }

         this.field_146325_B.visible = this.displayOptions && WorldType.field_77139_a[this.field_146331_K] != WorldType.field_180271_f;
         this.field_146326_C.visible = this.displayOptions;
         this.commandsButton.visible = this.displayOptions;
         this.field_146322_F.visible = this.displayOptions && WorldType.field_77139_a[this.field_146331_K].func_205393_e();
      }

      this.field_146335_h.setVisible(this.displayOptions);
      this.nameEdit.setVisible(!this.displayOptions);
      if (this.displayOptions) {
         this.moreOptionsButton.setMessage(I18n.get("gui.done"));
      } else {
         this.moreOptionsButton.setMessage(I18n.get("selectWorld.moreWorldOptions"));
      }

   }

   public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
      if (super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_)) {
         return true;
      } else if (p_keyPressed_1_ != 257 && p_keyPressed_1_ != 335) {
         return false;
      } else {
         this.onCreate();
         return true;
      }
   }

   public void onClose() {
      if (this.displayOptions) {
         this.setDisplayOptions(false);
      } else {
         this.minecraft.setScreen(this.lastScreen);
      }

   }

   public void render(int p_render_1_, int p_render_2_, float p_render_3_) {
      this.renderBackground();
      this.drawCenteredString(this.font, this.title.func_150254_d(), this.width / 2, 20, -1);
      if (this.displayOptions) {
         this.drawString(this.font, I18n.get("selectWorld.enterSeed"), this.width / 2 - 100, 47, -6250336);
         this.drawString(this.font, I18n.get("selectWorld.seedInfo"), this.width / 2 - 100, 85, -6250336);
         if (this.field_146325_B.visible) {
            this.drawString(this.font, I18n.get("selectWorld.mapFeatures.info"), this.width / 2 - 150, 122, -6250336);
         }

         if (this.commandsButton.visible) {
            this.drawString(this.font, I18n.get("selectWorld.allowCommands.info"), this.width / 2 - 150, 172, -6250336);
         }

         this.field_146335_h.render(p_render_1_, p_render_2_, p_render_3_);
         if (WorldType.field_77139_a[this.field_146331_K].func_151357_h()) {
            this.font.func_78279_b(I18n.get(WorldType.field_77139_a[this.field_146331_K].func_151359_c()), this.field_146320_D.x + 2, this.field_146320_D.y + 22, this.field_146320_D.getWidth(), 10526880);
         }
      } else {
         this.drawString(this.font, I18n.get("selectWorld.enterName"), this.width / 2 - 100, 47, -6250336);
         this.drawString(this.font, I18n.get("selectWorld.resultFolder") + " " + this.resultFolder, this.width / 2 - 100, 85, -6250336);
         this.nameEdit.render(p_render_1_, p_render_2_, p_render_3_);
         this.drawCenteredString(this.font, this.gameModeHelp1, this.width / 2, 137, -6250336);
         this.drawCenteredString(this.font, this.gameModeHelp2, this.width / 2, 149, -6250336);
      }

      super.render(p_render_1_, p_render_2_, p_render_3_);
   }

   public void func_146318_a(WorldInfo p_146318_1_) {
      this.initName = p_146318_1_.getLevelName();
      this.field_146329_I = Long.toString(p_146318_1_.func_76063_b());
      WorldType worldtype = p_146318_1_.func_76067_t() == WorldType.field_180271_f ? WorldType.field_77137_b : p_146318_1_.func_76067_t();
      this.field_146331_K = worldtype.func_82747_f();
      this.field_146334_a = p_146318_1_.func_211027_A();
      this.field_146341_s = p_146318_1_.func_76089_r();
      this.commands = p_146318_1_.getAllowCommands();
      if (p_146318_1_.isHardcore()) {
         this.gameMode = CreateWorldScreen.GameMode.HARDCORE;
      } else if (p_146318_1_.getGameType().isSurvival()) {
         this.gameMode = CreateWorldScreen.GameMode.SURVIVAL;
      } else if (p_146318_1_.getGameType().isCreative()) {
         this.gameMode = CreateWorldScreen.GameMode.CREATIVE;
      }

   }

   @OnlyIn(Dist.CLIENT)
   static enum GameMode {
      SURVIVAL("survival", GameType.SURVIVAL),
      HARDCORE("hardcore", GameType.SURVIVAL),
      CREATIVE("creative", GameType.CREATIVE),
      DEBUG("spectator", GameType.SPECTATOR);

      private final String name;
      private final GameType gameType;

      private GameMode(String p_i225940_3_, GameType p_i225940_4_) {
         this.name = p_i225940_3_;
         this.gameType = p_i225940_4_;
      }
   }
}
