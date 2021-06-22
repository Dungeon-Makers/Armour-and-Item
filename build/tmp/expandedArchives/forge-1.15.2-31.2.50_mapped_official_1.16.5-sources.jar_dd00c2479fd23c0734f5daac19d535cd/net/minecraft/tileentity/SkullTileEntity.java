package net.minecraft.tileentity;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.minecraft.MinecraftSessionService;
import com.mojang.authlib.properties.Property;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.play.server.SUpdateTileEntityPacket;
import net.minecraft.server.management.PlayerProfileCache;
import net.minecraft.util.StringUtils;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class SkullTileEntity extends TileEntity implements ITickableTileEntity {
   private GameProfile owner;
   private int mouthTickCount;
   private boolean isMovingMouth;
   private static PlayerProfileCache profileCache;
   private static MinecraftSessionService sessionService;

   public SkullTileEntity() {
      super(TileEntityType.SKULL);
   }

   public static void setProfileCache(PlayerProfileCache p_184293_0_) {
      profileCache = p_184293_0_;
   }

   public static void setSessionService(MinecraftSessionService p_184294_0_) {
      sessionService = p_184294_0_;
   }

   public CompoundNBT save(CompoundNBT p_189515_1_) {
      super.save(p_189515_1_);
      if (this.owner != null) {
         CompoundNBT compoundnbt = new CompoundNBT();
         NBTUtil.writeGameProfile(compoundnbt, this.owner);
         p_189515_1_.put("Owner", compoundnbt);
      }

      return p_189515_1_;
   }

   public void func_145839_a(CompoundNBT p_145839_1_) {
      super.func_145839_a(p_145839_1_);
      if (p_145839_1_.contains("Owner", 10)) {
         this.setOwner(NBTUtil.readGameProfile(p_145839_1_.getCompound("Owner")));
      } else if (p_145839_1_.contains("ExtraType", 8)) {
         String s = p_145839_1_.getString("ExtraType");
         if (!StringUtils.isNullOrEmpty(s)) {
            this.setOwner(new GameProfile((UUID)null, s));
         }
      }

   }

   public void tick() {
      Block block = this.getBlockState().getBlock();
      if (block == Blocks.DRAGON_HEAD || block == Blocks.DRAGON_WALL_HEAD) {
         if (this.level.hasNeighborSignal(this.worldPosition)) {
            this.isMovingMouth = true;
            ++this.mouthTickCount;
         } else {
            this.isMovingMouth = false;
         }
      }

   }

   @OnlyIn(Dist.CLIENT)
   public float getMouthAnimation(float p_184295_1_) {
      return this.isMovingMouth ? (float)this.mouthTickCount + p_184295_1_ : (float)this.mouthTickCount;
   }

   @Nullable
   @OnlyIn(Dist.CLIENT)
   public GameProfile getOwnerProfile() {
      return this.owner;
   }

   @Nullable
   public SUpdateTileEntityPacket getUpdatePacket() {
      return new SUpdateTileEntityPacket(this.worldPosition, 4, this.getUpdateTag());
   }

   public CompoundNBT getUpdateTag() {
      return this.save(new CompoundNBT());
   }

   public void setOwner(@Nullable GameProfile p_195485_1_) {
      this.owner = p_195485_1_;
      this.updateOwnerProfile();
   }

   private void updateOwnerProfile() {
      this.owner = updateGameprofile(this.owner);
      this.setChanged();
   }

   public static GameProfile updateGameprofile(GameProfile p_174884_0_) {
      if (p_174884_0_ != null && !StringUtils.isNullOrEmpty(p_174884_0_.getName())) {
         if (p_174884_0_.isComplete() && p_174884_0_.getProperties().containsKey("textures")) {
            return p_174884_0_;
         } else if (profileCache != null && sessionService != null) {
            GameProfile gameprofile = profileCache.get(p_174884_0_.getName());
            if (gameprofile == null) {
               return p_174884_0_;
            } else {
               Property property = Iterables.getFirst(gameprofile.getProperties().get("textures"), (Property)null);
               if (property == null) {
                  gameprofile = sessionService.fillProfileProperties(gameprofile, true);
               }

               return gameprofile;
            }
         } else {
            return p_174884_0_;
         }
      } else {
         return p_174884_0_;
      }
   }
}