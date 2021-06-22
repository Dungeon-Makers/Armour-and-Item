package net.minecraft.tileentity;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentUtils;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SignTileEntity extends TileEntity {
   public final ITextComponent[] messages = new ITextComponent[]{new StringTextComponent(""), new StringTextComponent(""), new StringTextComponent(""), new StringTextComponent("")};
   private boolean isEditable = true;
   private PlayerEntity playerWhoMayEdit;
   private final String[] renderMessages = new String[4];
   private DyeColor color = DyeColor.BLACK;

   public SignTileEntity() {
      super(TileEntityType.SIGN);
   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);

      for(int i = 0; i < 4; ++i) {
         String s = ITextComponent.Serializer.toJson(this.messages[i]);
         p_189515_1_.putString("Text" + (i + 1), s);
      }

      p_189515_1_.putString("Color", this.color.getName());
      return p_189515_1_;
   }

   public void func_145839_a(CompoundNBT p_145839_1_) {
      this.isEditable = false;
      super.func_145839_a(p_145839_1_);
      this.color = DyeColor.byName(p_145839_1_.getString("Color"), DyeColor.BLACK);

      for(int i = 0; i < 4; ++i) {
         String s = p_145839_1_.getString("Text" + (i + 1));
         ITextComponent itextcomponent = ITextComponent.Serializer.func_150699_a(s.isEmpty() ? "\"\"" : s);
         if (this.level instanceof ServerWorld) {
            try {
               this.messages[i] = TextComponentUtils.func_197680_a(this.createCommandSourceStack((ServerPlayerEntity)null), itextcomponent, (Entity)null, 0);
            } catch (CommandSyntaxException var6) {
               this.messages[i] = itextcomponent;
            }
         } else {
            this.messages[i] = itextcomponent;
         }

         this.renderMessages[i] = null;
      }

   }

   @OnlyIn(Dist.CLIENT)
   public ITextComponent getMessage(int p_212366_1_) {
      return this.messages[p_212366_1_];
   }

   public void setMessage(int p_212365_1_, ITextComponent p_212365_2_) {
      this.messages[p_212365_1_] = p_212365_2_;
      this.renderMessages[p_212365_1_] = null;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public String func_212364_a(int p_212364_1_, Function<ITextComponent, String> p_212364_2_) {
      if (this.renderMessages[p_212364_1_] == null && this.messages[p_212364_1_] != null) {
         this.renderMessages[p_212364_1_] = p_212364_2_.apply(this.messages[p_212364_1_]);
      }

      return this.renderMessages[p_212364_1_];
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.worldPosition, 9, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      return this.save(new CompoundNBT());
   }

   public boolean onlyOpCanSetNbt() {
      return true;
   }

   public boolean isEditable() {
      return this.isEditable;
   }

   @OnlyIn(Dist.CLIENT)
   public void setEditable(boolean p_145913_1_) {
      this.isEditable = p_145913_1_;
      if (!p_145913_1_) {
         this.playerWhoMayEdit = null;
      }

   }

   public void setAllowedPlayerEditor(PlayerEntity p_145912_1_) {
      this.playerWhoMayEdit = p_145912_1_;
   }

   public PlayerEntity getPlayerWhoMayEdit() {
      return this.playerWhoMayEdit;
   }

   public boolean executeClickCommands(PlayerEntity p_174882_1_) {
      for(ITextComponent itextcomponent : this.messages) {
         Style style = itextcomponent == null ? null : itextcomponent.getStyle();
         if (style != null && style.getClickEvent() != null) {
            ClickEvent clickevent = style.getClickEvent();
            if (clickevent.getAction() == ClickEvent.Action.RUN_COMMAND) {
               p_174882_1_.getServer().getCommands().performCommand(this.createCommandSourceStack((ServerPlayerEntity)p_174882_1_), clickevent.getValue());
            }
         }
      }

      return true;
   }

   public CommandSource createCommandSourceStack(@Nullable ServerPlayerEntity p_195539_1_) {
      String s = p_195539_1_ == null ? "Sign" : p_195539_1_.getName().getString();
      ITextComponent itextcomponent = (ITextComponent)(p_195539_1_ == null ? new StringTextComponent("Sign") : p_195539_1_.getDisplayName());
      return new CommandSource(ICommandSource.NULL, new Vec3d((double)this.worldPosition.getX() + 0.5D, (double)this.worldPosition.getY() + 0.5D, (double)this.worldPosition.getZ() + 0.5D), Vec2f.ZERO, (ServerWorld)this.level, 2, s, itextcomponent, this.level.getServer(), p_195539_1_);
   }

   public DyeColor getColor() {
      return this.color;
   }

   public boolean setColor(DyeColor p_214068_1_) {
      if (p_214068_1_ != this.getColor()) {
         this.color = p_214068_1_;
         this.setChanged();
         this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
         return true;
      } else {
         return false;
      }
   }
}