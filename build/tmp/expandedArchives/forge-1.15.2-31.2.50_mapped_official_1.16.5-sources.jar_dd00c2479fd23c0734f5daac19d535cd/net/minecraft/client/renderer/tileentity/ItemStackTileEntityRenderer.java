package net.minecraft.client.renderer.tileentity;

import com.mojang.authlib.GameProfile;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;
import net.minecraft.block.AbstractBannerBlock;
import net.minecraft.block.AbstractSkullBlock;
import net.minecraft.block.BedBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShulkerBoxBlock;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.model.ShieldModel;
import net.minecraft.client.renderer.entity.model.TridentModel;
import net.minecraft.client.renderer.model.Material;
import net.minecraft.client.renderer.model.ModelBakery;
import net.minecraft.item.BlockItem;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.tileentity.BannerPattern;
import net.minecraft.tileentity.BannerTileEntity;
import net.minecraft.tileentity.BedTileEntity;
import net.minecraft.tileentity.ChestTileEntity;
import net.minecraft.tileentity.ConduitTileEntity;
import net.minecraft.tileentity.EnderChestTileEntity;
import net.minecraft.tileentity.ShulkerBoxTileEntity;
import net.minecraft.tileentity.SkullTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TrappedChestTileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.StringUtils;

@OnlyIn(Dist.CLIENT)
public class ItemStackTileEntityRenderer {
   private static final ShulkerBoxTileEntity[] SHULKER_BOXES = Arrays.stream(DyeColor.values()).sorted(Comparator.comparingInt(DyeColor::getId)).map(ShulkerBoxTileEntity::new).toArray((p_199929_0_) -> {
      return new ShulkerBoxTileEntity[p_199929_0_];
   });
   private static final ShulkerBoxTileEntity DEFAULT_SHULKER_BOX = new ShulkerBoxTileEntity((DyeColor)null);
   public static final ItemStackTileEntityRenderer instance = new ItemStackTileEntityRenderer();
   private final ChestTileEntity chest = new ChestTileEntity();
   private final ChestTileEntity trappedChest = new TrappedChestTileEntity();
   private final EnderChestTileEntity enderChest = new EnderChestTileEntity();
   private final BannerTileEntity banner = new BannerTileEntity();
   private final BedTileEntity bed = new BedTileEntity();
   private final ConduitTileEntity conduit = new ConduitTileEntity();
   private final ShieldModel shieldModel = new ShieldModel();
   private final TridentModel tridentModel = new TridentModel();

   public void func_228364_a_(ItemStack p_228364_1_, MatrixStack p_228364_2_, IRenderTypeBuffer p_228364_3_, int p_228364_4_, int p_228364_5_) {
      Item item = p_228364_1_.getItem();
      if (item instanceof BlockItem) {
         Block block = ((BlockItem)item).getBlock();
         if (block instanceof AbstractSkullBlock) {
            GameProfile gameprofile = null;
            if (p_228364_1_.hasTag()) {
               CompoundNBT compoundnbt = p_228364_1_.getTag();
               if (compoundnbt.contains("SkullOwner", 10)) {
                  gameprofile = NBTUtil.readGameProfile(compoundnbt.getCompound("SkullOwner"));
               } else if (compoundnbt.contains("SkullOwner", 8) && !StringUtils.isBlank(compoundnbt.getString("SkullOwner"))) {
                  GameProfile gameprofile1 = new GameProfile((UUID)null, compoundnbt.getString("SkullOwner"));
                  gameprofile = SkullTileEntity.updateGameprofile(gameprofile1);
                  compoundnbt.remove("SkullOwner");
                  compoundnbt.put("SkullOwner", NBTUtil.writeGameProfile(new CompoundNBT(), gameprofile));
               }
            }

            SkullTileEntityRenderer.renderSkull((Direction)null, 180.0F, ((AbstractSkullBlock)block).getType(), gameprofile, 0.0F, p_228364_2_, p_228364_3_, p_228364_4_);
         } else {
            TileEntity tileentity;
            if (block instanceof AbstractBannerBlock) {
               this.banner.fromItem(p_228364_1_, ((AbstractBannerBlock)block).getColor());
               tileentity = this.banner;
            } else if (block instanceof BedBlock) {
               this.bed.setColor(((BedBlock)block).getColor());
               tileentity = this.bed;
            } else if (block == Blocks.CONDUIT) {
               tileentity = this.conduit;
            } else if (block == Blocks.CHEST) {
               tileentity = this.chest;
            } else if (block == Blocks.ENDER_CHEST) {
               tileentity = this.enderChest;
            } else if (block == Blocks.TRAPPED_CHEST) {
               tileentity = this.trappedChest;
            } else {
               if (!(block instanceof ShulkerBoxBlock)) {
                  return;
               }

               DyeColor dyecolor = ShulkerBoxBlock.getColorFromItem(item);
               if (dyecolor == null) {
                  tileentity = DEFAULT_SHULKER_BOX;
               } else {
                  tileentity = SHULKER_BOXES[dyecolor.getId()];
               }
            }

            TileEntityRendererDispatcher.instance.renderItem(tileentity, p_228364_2_, p_228364_3_, p_228364_4_, p_228364_5_);
         }
      } else {
         if (item == Items.SHIELD) {
            boolean flag = p_228364_1_.getTagElement("BlockEntityTag") != null;
            p_228364_2_.pushPose();
            p_228364_2_.scale(1.0F, -1.0F, -1.0F);
            Material material = flag ? ModelBakery.SHIELD_BASE : ModelBakery.NO_PATTERN_SHIELD;
            IVertexBuilder ivertexbuilder = material.sprite().wrap(ItemRenderer.getFoilBuffer(p_228364_3_, this.shieldModel.renderType(material.atlasLocation()), false, p_228364_1_.hasFoil()));
            this.shieldModel.handle().render(p_228364_2_, ivertexbuilder, p_228364_4_, p_228364_5_, 1.0F, 1.0F, 1.0F, 1.0F);
            if (flag) {
               List<Pair<BannerPattern, DyeColor>> list = BannerTileEntity.createPatterns(ShieldItem.getColor(p_228364_1_), BannerTileEntity.getItemPatterns(p_228364_1_));
               BannerTileEntityRenderer.renderPatterns(p_228364_2_, p_228364_3_, p_228364_4_, p_228364_5_, this.shieldModel.plate(), material, false, list);
            } else {
               this.shieldModel.plate().render(p_228364_2_, ivertexbuilder, p_228364_4_, p_228364_5_, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            p_228364_2_.popPose();
         } else if (item == Items.TRIDENT) {
            p_228364_2_.pushPose();
            p_228364_2_.scale(1.0F, -1.0F, -1.0F);
            IVertexBuilder ivertexbuilder1 = ItemRenderer.getFoilBuffer(p_228364_3_, this.tridentModel.renderType(TridentModel.TEXTURE), false, p_228364_1_.hasFoil());
            this.tridentModel.renderToBuffer(p_228364_2_, ivertexbuilder1, p_228364_4_, p_228364_5_, 1.0F, 1.0F, 1.0F, 1.0F);
            p_228364_2_.popPose();
         }

      }
   }
}