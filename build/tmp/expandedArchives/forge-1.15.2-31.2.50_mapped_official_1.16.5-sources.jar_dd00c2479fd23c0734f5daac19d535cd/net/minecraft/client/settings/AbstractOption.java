package net.minecraft.client.settings;

import net.minecraft.client.GameSettings;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.chat.NarratorChatListener;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.ChatVisibility;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class AbstractOption {
   public static final SliderPercentageOption BIOME_BLEND_RADIUS = new SliderPercentageOption("options.biomeBlendRadius", 0.0D, 7.0D, 1.0F, (p_216607_0_) -> {
      return (double)p_216607_0_.biomeBlendRadius;
   }, (p_216660_0_, p_216660_1_) -> {
      p_216660_0_.biomeBlendRadius = MathHelper.clamp(p_216660_1_.intValue(), 0, 7);
      Minecraft.getInstance().levelRenderer.allChanged();
   }, (p_216595_0_, p_216595_1_) -> {
      double d0 = p_216595_1_.get(p_216595_0_);
      String s = p_216595_1_.func_216617_a();
      int i = (int)d0 * 2 + 1;
      return s + I18n.get("options.biomeBlendRadius." + i);
   });
   public static final SliderPercentageOption CHAT_HEIGHT_FOCUSED = new SliderPercentageOption("options.chat.height.focused", 0.0D, 1.0D, 0.0F, (p_216587_0_) -> {
      return p_216587_0_.chatHeightFocused;
   }, (p_216600_0_, p_216600_1_) -> {
      p_216600_0_.chatHeightFocused = p_216600_1_;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (p_216642_0_, p_216642_1_) -> {
      double d0 = p_216642_1_.toPct(p_216642_1_.get(p_216642_0_));
      return p_216642_1_.func_216617_a() + NewChatGui.getHeight(d0) + "px";
   });
   public static final SliderPercentageOption CHAT_HEIGHT_UNFOCUSED = new SliderPercentageOption("options.chat.height.unfocused", 0.0D, 1.0D, 0.0F, (p_216611_0_) -> {
      return p_216611_0_.chatHeightUnfocused;
   }, (p_216650_0_, p_216650_1_) -> {
      p_216650_0_.chatHeightUnfocused = p_216650_1_;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (p_216604_0_, p_216604_1_) -> {
      double d0 = p_216604_1_.toPct(p_216604_1_.get(p_216604_0_));
      return p_216604_1_.func_216617_a() + NewChatGui.getHeight(d0) + "px";
   });
   public static final SliderPercentageOption CHAT_OPACITY = new SliderPercentageOption("options.chat.opacity", 0.0D, 1.0D, 0.0F, (p_216649_0_) -> {
      return p_216649_0_.chatOpacity;
   }, (p_216578_0_, p_216578_1_) -> {
      p_216578_0_.chatOpacity = p_216578_1_;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (p_216592_0_, p_216592_1_) -> {
      double d0 = p_216592_1_.toPct(p_216592_1_.get(p_216592_0_));
      return p_216592_1_.func_216617_a() + (int)(d0 * 90.0D + 10.0D) + "%";
   });
   public static final SliderPercentageOption CHAT_SCALE = new SliderPercentageOption("options.chat.scale", 0.0D, 1.0D, 0.0F, (p_216591_0_) -> {
      return p_216591_0_.chatScale;
   }, (p_216624_0_, p_216624_1_) -> {
      p_216624_0_.chatScale = p_216624_1_;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (p_216637_0_, p_216637_1_) -> {
      double d0 = p_216637_1_.toPct(p_216637_1_.get(p_216637_0_));
      String s = p_216637_1_.func_216617_a();
      return d0 == 0.0D ? s + I18n.get("options.off") : s + (int)(d0 * 100.0D) + "%";
   });
   public static final SliderPercentageOption CHAT_WIDTH = new SliderPercentageOption("options.chat.width", 0.0D, 1.0D, 0.0F, (p_216601_0_) -> {
      return p_216601_0_.chatWidth;
   }, (p_216620_0_, p_216620_1_) -> {
      p_216620_0_.chatWidth = p_216620_1_;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (p_216673_0_, p_216673_1_) -> {
      double d0 = p_216673_1_.toPct(p_216673_1_.get(p_216673_0_));
      return p_216673_1_.func_216617_a() + NewChatGui.getWidth(d0) + "px";
   });
   public static final SliderPercentageOption FOV = new SliderPercentageOption("options.fov", 30.0D, 110.0D, 1.0F, (p_216655_0_) -> {
      return p_216655_0_.fov;
   }, (p_216612_0_, p_216612_1_) -> {
      p_216612_0_.fov = p_216612_1_;
   }, (p_216590_0_, p_216590_1_) -> {
      double d0 = p_216590_1_.get(p_216590_0_);
      String s = p_216590_1_.func_216617_a();
      if (d0 == 70.0D) {
         return s + I18n.get("options.fov.min");
      } else {
         return d0 == p_216590_1_.getMaxValue() ? s + I18n.get("options.fov.max") : s + (int)d0;
      }
   });
   public static final SliderPercentageOption FRAMERATE_LIMIT = new SliderPercentageOption("options.framerateLimit", 10.0D, 260.0D, 10.0F, (p_216672_0_) -> {
      return (double)p_216672_0_.framerateLimit;
   }, (p_216608_0_, p_216608_1_) -> {
      p_216608_0_.framerateLimit = p_216608_1_.intValue();
      Minecraft.getInstance().getWindow().setFramerateLimit(p_216608_0_.framerateLimit);
   }, (p_216645_0_, p_216645_1_) -> {
      double d0 = p_216645_1_.get(p_216645_0_);
      String s = p_216645_1_.func_216617_a();
      return d0 == p_216645_1_.getMaxValue() ? s + I18n.get("options.framerateLimit.max") : s + I18n.get("options.framerate", (int)d0);
   });
   public static final SliderPercentageOption GAMMA = new SliderPercentageOption("options.gamma", 0.0D, 1.0D, 0.0F, (p_216636_0_) -> {
      return p_216636_0_.gamma;
   }, (p_216651_0_, p_216651_1_) -> {
      p_216651_0_.gamma = p_216651_1_;
   }, (p_216594_0_, p_216594_1_) -> {
      double d0 = p_216594_1_.toPct(p_216594_1_.get(p_216594_0_));
      String s = p_216594_1_.func_216617_a();
      if (d0 == 0.0D) {
         return s + I18n.get("options.gamma.min");
      } else {
         return d0 == 1.0D ? s + I18n.get("options.gamma.max") : s + "+" + (int)(d0 * 100.0D) + "%";
      }
   });
   public static final SliderPercentageOption MIPMAP_LEVELS = new SliderPercentageOption("options.mipmapLevels", 0.0D, 4.0D, 1.0F, (p_216667_0_) -> {
      return (double)p_216667_0_.mipmapLevels;
   }, (p_216585_0_, p_216585_1_) -> {
      p_216585_0_.mipmapLevels = p_216585_1_.intValue();
   }, (p_216629_0_, p_216629_1_) -> {
      double d0 = p_216629_1_.get(p_216629_0_);
      String s = p_216629_1_.func_216617_a();
      return d0 == 0.0D ? s + I18n.get("options.off") : s + (int)d0;
   });
   public static final SliderPercentageOption MOUSE_WHEEL_SENSITIVITY = new SliderMultiplierOption("options.mouseWheelSensitivity", 0.01D, 10.0D, 0.01F, (p_216581_0_) -> {
      return p_216581_0_.mouseWheelSensitivity;
   }, (p_216628_0_, p_216628_1_) -> {
      p_216628_0_.mouseWheelSensitivity = p_216628_1_;
   }, (p_216675_0_, p_216675_1_) -> {
      double d0 = p_216675_1_.toPct(p_216675_1_.get(p_216675_0_));
      return p_216675_1_.func_216617_a() + String.format("%.2f", p_216675_1_.toValue(d0));
   });
   public static final BooleanOption RAW_MOUSE_INPUT = new BooleanOption("options.rawMouseInput", (p_225287_0_) -> {
      return p_225287_0_.rawMouseInput;
   }, (p_225259_0_, p_225259_1_) -> {
      p_225259_0_.rawMouseInput = p_225259_1_;
      MainWindow mainwindow = Minecraft.getInstance().getWindow();
      if (mainwindow != null) {
         mainwindow.updateRawMouseInput(p_225259_1_);
      }

   });
   public static final SliderPercentageOption RENDER_DISTANCE = new SliderPercentageOption("options.renderDistance", 2.0D, 16.0D, 1.0F, (p_216658_0_) -> {
      return (double)p_216658_0_.renderDistance;
   }, (p_216579_0_, p_216579_1_) -> {
      p_216579_0_.renderDistance = p_216579_1_.intValue();
      Minecraft.getInstance().levelRenderer.needsUpdate();
   }, (p_216664_0_, p_216664_1_) -> {
      double d0 = p_216664_1_.get(p_216664_0_);
      return p_216664_1_.func_216617_a() + I18n.get("options.chunks", (int)d0);
   });
   public static final SliderPercentageOption SENSITIVITY = new SliderPercentageOption("options.sensitivity", 0.0D, 1.0D, 0.0F, (p_216654_0_) -> {
      return p_216654_0_.sensitivity;
   }, (p_216644_0_, p_216644_1_) -> {
      p_216644_0_.sensitivity = p_216644_1_;
   }, (p_216641_0_, p_216641_1_) -> {
      double d0 = p_216641_1_.toPct(p_216641_1_.get(p_216641_0_));
      String s = p_216641_1_.func_216617_a();
      if (d0 == 0.0D) {
         return s + I18n.get("options.sensitivity.min");
      } else {
         return d0 == 1.0D ? s + I18n.get("options.sensitivity.max") : s + (int)(d0 * 200.0D) + "%";
      }
   });
   public static final SliderPercentageOption TEXT_BACKGROUND_OPACITY = new SliderPercentageOption("options.accessibility.text_background_opacity", 0.0D, 1.0D, 0.0F, (p_216597_0_) -> {
      return p_216597_0_.textBackgroundOpacity;
   }, (p_216593_0_, p_216593_1_) -> {
      p_216593_0_.textBackgroundOpacity = p_216593_1_;
      Minecraft.getInstance().gui.getChat().rescaleChat();
   }, (p_216626_0_, p_216626_1_) -> {
      return p_216626_1_.func_216617_a() + (int)(p_216626_1_.toPct(p_216626_1_.get(p_216626_0_)) * 100.0D) + "%";
   });
   public static final IteratableOption AMBIENT_OCCLUSION = new IteratableOption("options.ao", (p_216653_0_, p_216653_1_) -> {
      p_216653_0_.ambientOcclusion = AmbientOcclusionStatus.byId(p_216653_0_.ambientOcclusion.getId() + p_216653_1_);
      Minecraft.getInstance().levelRenderer.allChanged();
   }, (p_216630_0_, p_216630_1_) -> {
      return p_216630_1_.func_216617_a() + I18n.get(p_216630_0_.ambientOcclusion.getKey());
   });
   public static final IteratableOption ATTACK_INDICATOR = new IteratableOption("options.attackIndicator", (p_216615_0_, p_216615_1_) -> {
      p_216615_0_.attackIndicator = AttackIndicatorStatus.byId(p_216615_0_.attackIndicator.getId() + p_216615_1_);
   }, (p_216609_0_, p_216609_1_) -> {
      return p_216609_1_.func_216617_a() + I18n.get(p_216609_0_.attackIndicator.getKey());
   });
   public static final IteratableOption CHAT_VISIBILITY = new IteratableOption("options.chat.visibility", (p_216640_0_, p_216640_1_) -> {
      p_216640_0_.chatVisibility = ChatVisibility.byId((p_216640_0_.chatVisibility.getId() + p_216640_1_) % 3);
   }, (p_216598_0_, p_216598_1_) -> {
      return p_216598_1_.func_216617_a() + I18n.get(p_216598_0_.chatVisibility.getKey());
   });
   public static final IteratableOption GRAPHICS = new IteratableOption("options.graphics", (p_216577_0_, p_216577_1_) -> {
      p_216577_0_.field_74347_j = !p_216577_0_.field_74347_j;
      Minecraft.getInstance().levelRenderer.allChanged();
   }, (p_216633_0_, p_216633_1_) -> {
      return p_216633_0_.field_74347_j ? p_216633_1_.func_216617_a() + I18n.get("options.graphics.fancy") : p_216633_1_.func_216617_a() + I18n.get("options.graphics.fast");
   });
   public static final IteratableOption GUI_SCALE = new IteratableOption("options.guiScale", (p_216674_0_, p_216674_1_) -> {
      p_216674_0_.guiScale = Integer.remainderUnsigned(p_216674_0_.guiScale + p_216674_1_, Minecraft.getInstance().getWindow().calculateScale(0, Minecraft.getInstance().isEnforceUnicode()) + 1);
   }, (p_216668_0_, p_216668_1_) -> {
      return p_216668_1_.func_216617_a() + (p_216668_0_.guiScale == 0 ? I18n.get("options.guiScale.auto") : p_216668_0_.guiScale);
   });
   public static final IteratableOption MAIN_HAND = new IteratableOption("options.mainHand", (p_216584_0_, p_216584_1_) -> {
      p_216584_0_.mainHand = p_216584_0_.mainHand.getOpposite();
   }, (p_216596_0_, p_216596_1_) -> {
      return p_216596_1_.func_216617_a() + p_216596_0_.mainHand;
   });
   public static final IteratableOption NARRATOR = new IteratableOption("options.narrator", (p_216648_0_, p_216648_1_) -> {
      if (NarratorChatListener.INSTANCE.isActive()) {
         p_216648_0_.narratorStatus = NarratorStatus.byId(p_216648_0_.narratorStatus.getId() + p_216648_1_);
      } else {
         p_216648_0_.narratorStatus = NarratorStatus.OFF;
      }

      NarratorChatListener.INSTANCE.updateNarratorStatus(p_216648_0_.narratorStatus);
   }, (p_216632_0_, p_216632_1_) -> {
      return NarratorChatListener.INSTANCE.isActive() ? p_216632_1_.func_216617_a() + I18n.get(p_216632_0_.narratorStatus.func_216824_b()) : p_216632_1_.func_216617_a() + I18n.get("options.narrator.notavailable");
   });
   public static final IteratableOption PARTICLES = new IteratableOption("options.particles", (p_216622_0_, p_216622_1_) -> {
      p_216622_0_.particles = ParticleStatus.byId(p_216622_0_.particles.getId() + p_216622_1_);
   }, (p_216616_0_, p_216616_1_) -> {
      return p_216616_1_.func_216617_a() + I18n.get(p_216616_0_.particles.getKey());
   });
   public static final IteratableOption RENDER_CLOUDS = new IteratableOption("options.renderClouds", (p_216605_0_, p_216605_1_) -> {
      p_216605_0_.renderClouds = CloudOption.byId(p_216605_0_.renderClouds.getId() + p_216605_1_);
   }, (p_216602_0_, p_216602_1_) -> {
      return p_216602_1_.func_216617_a() + I18n.get(p_216602_0_.renderClouds.getKey());
   });
   public static final IteratableOption TEXT_BACKGROUND = new IteratableOption("options.accessibility.text_background", (p_216665_0_, p_216665_1_) -> {
      p_216665_0_.backgroundForChatOnly = !p_216665_0_.backgroundForChatOnly;
   }, (p_216639_0_, p_216639_1_) -> {
      return p_216639_1_.func_216617_a() + I18n.get(p_216639_0_.backgroundForChatOnly ? "options.accessibility.text_background.chat" : "options.accessibility.text_background.everywhere");
   });
   public static final BooleanOption AUTO_JUMP = new BooleanOption("options.autoJump", (p_216619_0_) -> {
      return p_216619_0_.autoJump;
   }, (p_216621_0_, p_216621_1_) -> {
      p_216621_0_.autoJump = p_216621_1_;
   });
   public static final BooleanOption AUTO_SUGGESTIONS = new BooleanOption("options.autoSuggestCommands", (p_216643_0_) -> {
      return p_216643_0_.autoSuggestions;
   }, (p_216656_0_, p_216656_1_) -> {
      p_216656_0_.autoSuggestions = p_216656_1_;
   });
   public static final BooleanOption CHAT_COLOR = new BooleanOption("options.chat.color", (p_216669_0_) -> {
      return p_216669_0_.chatColors;
   }, (p_216659_0_, p_216659_1_) -> {
      p_216659_0_.chatColors = p_216659_1_;
   });
   public static final BooleanOption CHAT_LINKS = new BooleanOption("options.chat.links", (p_216583_0_) -> {
      return p_216583_0_.chatLinks;
   }, (p_216670_0_, p_216670_1_) -> {
      p_216670_0_.chatLinks = p_216670_1_;
   });
   public static final BooleanOption CHAT_LINKS_PROMPT = new BooleanOption("options.chat.links.prompt", (p_216610_0_) -> {
      return p_216610_0_.chatLinksPrompt;
   }, (p_216652_0_, p_216652_1_) -> {
      p_216652_0_.chatLinksPrompt = p_216652_1_;
   });
   public static final BooleanOption DISCRETE_MOUSE_SCROLL = new BooleanOption("options.discrete_mouse_scroll", (p_216634_0_) -> {
      return p_216634_0_.discreteMouseScroll;
   }, (p_216625_0_, p_216625_1_) -> {
      p_216625_0_.discreteMouseScroll = p_216625_1_;
   });
   public static final BooleanOption ENABLE_VSYNC = new BooleanOption("options.vsync", (p_216661_0_) -> {
      return p_216661_0_.enableVsync;
   }, (p_216635_0_, p_216635_1_) -> {
      p_216635_0_.enableVsync = p_216635_1_;
      if (Minecraft.getInstance().getWindow() != null) {
         Minecraft.getInstance().getWindow().updateVsync(p_216635_0_.enableVsync);
      }

   });
   public static final BooleanOption ENTITY_SHADOWS = new BooleanOption("options.entityShadows", (p_216576_0_) -> {
      return p_216576_0_.entityShadows;
   }, (p_216588_0_, p_216588_1_) -> {
      p_216588_0_.entityShadows = p_216588_1_;
   });
   public static final BooleanOption FORCE_UNICODE_FONT = new BooleanOption("options.forceUnicodeFont", (p_216657_0_) -> {
      return p_216657_0_.forceUnicodeFont;
   }, (p_216631_0_, p_216631_1_) -> {
      p_216631_0_.forceUnicodeFont = p_216631_1_;
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.func_211500_ak() != null) {
         minecraft.func_211500_ak().func_216883_a(p_216631_0_.forceUnicodeFont, Util.backgroundExecutor(), minecraft);
      }

   });
   public static final BooleanOption INVERT_MOUSE = new BooleanOption("options.invertMouse", (p_216627_0_) -> {
      return p_216627_0_.invertYMouse;
   }, (p_216603_0_, p_216603_1_) -> {
      p_216603_0_.invertYMouse = p_216603_1_;
   });
   public static final BooleanOption REALMS_NOTIFICATIONS = new BooleanOption("options.realmsNotifications", (p_216606_0_) -> {
      return p_216606_0_.realmsNotifications;
   }, (p_216618_0_, p_216618_1_) -> {
      p_216618_0_.realmsNotifications = p_216618_1_;
   });
   public static final BooleanOption REDUCED_DEBUG_INFO = new BooleanOption("options.reducedDebugInfo", (p_216582_0_) -> {
      return p_216582_0_.reducedDebugInfo;
   }, (p_216613_0_, p_216613_1_) -> {
      p_216613_0_.reducedDebugInfo = p_216613_1_;
   });
   public static final BooleanOption SHOW_SUBTITLES = new BooleanOption("options.showSubtitles", (p_216663_0_) -> {
      return p_216663_0_.showSubtitles;
   }, (p_216662_0_, p_216662_1_) -> {
      p_216662_0_.showSubtitles = p_216662_1_;
   });
   public static final BooleanOption SNOOPER_ENABLED = new BooleanOption("options.snooper", (p_216638_0_) -> {
      if (p_216638_0_.snooperEnabled) {
         ;
      }

      return false;
   }, (p_216676_0_, p_216676_1_) -> {
      p_216676_0_.snooperEnabled = p_216676_1_;
   });
   public static final IteratableOption TOGGLE_CROUCH = new IteratableOption("key.sneak", (p_228043_0_, p_228043_1_) -> {
      p_228043_0_.toggleCrouch = !p_228043_0_.toggleCrouch;
   }, (p_228041_0_, p_228041_1_) -> {
      return p_228041_1_.func_216617_a() + I18n.get(p_228041_0_.toggleCrouch ? "options.key.toggle" : "options.key.hold");
   });
   public static final IteratableOption TOGGLE_SPRINT = new IteratableOption("key.sprint", (p_228039_0_, p_228039_1_) -> {
      p_228039_0_.toggleSprint = !p_228039_0_.toggleSprint;
   }, (p_228037_0_, p_228037_1_) -> {
      return p_228037_1_.func_216617_a() + I18n.get(p_228037_0_.toggleSprint ? "options.key.toggle" : "options.key.hold");
   });
   public static final BooleanOption TOUCHSCREEN = new BooleanOption("options.touchscreen", (p_216647_0_) -> {
      return p_216647_0_.touchscreen;
   }, (p_216580_0_, p_216580_1_) -> {
      p_216580_0_.touchscreen = p_216580_1_;
   });
   public static final BooleanOption USE_FULLSCREEN = new BooleanOption("options.fullscreen", (p_228040_0_) -> {
      return p_228040_0_.fullscreen;
   }, (p_228042_0_, p_228042_1_) -> {
      p_228042_0_.fullscreen = p_228042_1_;
      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.getWindow() != null && minecraft.getWindow().isFullscreen() != p_228042_0_.fullscreen) {
         minecraft.getWindow().toggleFullScreen();
         p_228042_0_.fullscreen = minecraft.getWindow().isFullscreen();
      }

   });
   public static final BooleanOption VIEW_BOBBING = new BooleanOption("options.viewBobbing", (p_228036_0_) -> {
      return p_228036_0_.bobView;
   }, (p_228038_0_, p_228038_1_) -> {
      p_228038_0_.bobView = p_228038_1_;
   });
   private final String field_216693_Q;

   public AbstractOption(String p_i51158_1_) {
      this.field_216693_Q = p_i51158_1_;
   }

   public abstract Widget createButton(GameSettings p_216586_1_, int p_216586_2_, int p_216586_3_, int p_216586_4_);

   public String func_216617_a() {
      return I18n.get(this.field_216693_Q) + ": ";
   }
}