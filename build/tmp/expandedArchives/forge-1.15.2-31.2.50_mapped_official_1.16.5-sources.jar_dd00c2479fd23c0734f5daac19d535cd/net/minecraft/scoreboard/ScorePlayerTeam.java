package net.minecraft.scoreboard;

import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Set;
import javax.annotation.Nullable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ScorePlayerTeam extends Team {
   private final Scoreboard scoreboard;
   private final String name;
   private final Set<String> players = Sets.newHashSet();
   private ITextComponent displayName;
   private ITextComponent playerPrefix = new StringTextComponent("");
   private ITextComponent playerSuffix = new StringTextComponent("");
   private boolean allowFriendlyFire = true;
   private boolean seeFriendlyInvisibles = true;
   private Team.Visible nameTagVisibility = Team.Visible.ALWAYS;
   private Team.Visible deathMessageVisibility = Team.Visible.ALWAYS;
   private TextFormatting color = TextFormatting.RESET;
   private Team.CollisionRule collisionRule = Team.CollisionRule.ALWAYS;

   public ScorePlayerTeam(Scoreboard p_i2308_1_, String p_i2308_2_) {
      this.scoreboard = p_i2308_1_;
      this.name = p_i2308_2_;
      this.displayName = new StringTextComponent(p_i2308_2_);
   }

   public String getName() {
      return this.name;
   }

   public ITextComponent getDisplayName() {
      return this.displayName;
   }

   public ITextComponent func_197892_d() {
      ITextComponent itextcomponent = TextComponentUtils.func_197676_a(this.displayName.func_212638_h().func_211710_a((p_211543_1_) -> {
         p_211543_1_.func_179989_a(this.name).func_150209_a(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new StringTextComponent(this.name)));
      }));
      TextFormatting textformatting = this.getColor();
      if (textformatting != TextFormatting.RESET) {
         itextcomponent.func_211708_a(textformatting);
      }

      return itextcomponent;
   }

   public void setDisplayName(ITextComponent p_96664_1_) {
      if (p_96664_1_ == null) {
         throw new IllegalArgumentException("Name cannot be null");
      } else {
         this.displayName = p_96664_1_;
         this.scoreboard.onTeamChanged(this);
      }
   }

   public void setPlayerPrefix(@Nullable ITextComponent p_207408_1_) {
      this.playerPrefix = (ITextComponent)(p_207408_1_ == null ? new StringTextComponent("") : p_207408_1_.func_212638_h());
      this.scoreboard.onTeamChanged(this);
   }

   public ITextComponent getPlayerPrefix() {
      return this.playerPrefix;
   }

   public void setPlayerSuffix(@Nullable ITextComponent p_207409_1_) {
      this.playerSuffix = (ITextComponent)(p_207409_1_ == null ? new StringTextComponent("") : p_207409_1_.func_212638_h());
      this.scoreboard.onTeamChanged(this);
   }

   public ITextComponent getPlayerSuffix() {
      return this.playerSuffix;
   }

   public Collection<String> getPlayers() {
      return this.players;
   }

   public ITextComponent func_200540_a(ITextComponent p_200540_1_) {
      ITextComponent itextcomponent = (new StringTextComponent("")).func_150257_a(this.playerPrefix).func_150257_a(p_200540_1_).func_150257_a(this.playerSuffix);
      TextFormatting textformatting = this.getColor();
      if (textformatting != TextFormatting.RESET) {
         itextcomponent.func_211708_a(textformatting);
      }

      return itextcomponent;
   }

   public static ITextComponent func_200541_a(@Nullable Team p_200541_0_, ITextComponent p_200541_1_) {
      return p_200541_0_ == null ? p_200541_1_.func_212638_h() : p_200541_0_.func_200540_a(p_200541_1_);
   }

   public boolean isAllowFriendlyFire() {
      return this.allowFriendlyFire;
   }

   public void setAllowFriendlyFire(boolean p_96660_1_) {
      this.allowFriendlyFire = p_96660_1_;
      this.scoreboard.onTeamChanged(this);
   }

   public boolean canSeeFriendlyInvisibles() {
      return this.seeFriendlyInvisibles;
   }

   public void setSeeFriendlyInvisibles(boolean p_98300_1_) {
      this.seeFriendlyInvisibles = p_98300_1_;
      this.scoreboard.onTeamChanged(this);
   }

   public Team.Visible getNameTagVisibility() {
      return this.nameTagVisibility;
   }

   public Team.Visible getDeathMessageVisibility() {
      return this.deathMessageVisibility;
   }

   public void setNameTagVisibility(Team.Visible p_178772_1_) {
      this.nameTagVisibility = p_178772_1_;
      this.scoreboard.onTeamChanged(this);
   }

   public void setDeathMessageVisibility(Team.Visible p_178773_1_) {
      this.deathMessageVisibility = p_178773_1_;
      this.scoreboard.onTeamChanged(this);
   }

   public Team.CollisionRule getCollisionRule() {
      return this.collisionRule;
   }

   public void setCollisionRule(Team.CollisionRule p_186682_1_) {
      this.collisionRule = p_186682_1_;
      this.scoreboard.onTeamChanged(this);
   }

   public int packOptions() {
      int i = 0;
      if (this.isAllowFriendlyFire()) {
         i |= 1;
      }

      if (this.canSeeFriendlyInvisibles()) {
         i |= 2;
      }

      return i;
   }

   @OnlyIn(Dist.CLIENT)
   public void unpackOptions(int p_98298_1_) {
      this.setAllowFriendlyFire((p_98298_1_ & 1) > 0);
      this.setSeeFriendlyInvisibles((p_98298_1_ & 2) > 0);
   }

   public void setColor(TextFormatting p_178774_1_) {
      this.color = p_178774_1_;
      this.scoreboard.onTeamChanged(this);
   }

   public TextFormatting getColor() {
      return this.color;
   }
}