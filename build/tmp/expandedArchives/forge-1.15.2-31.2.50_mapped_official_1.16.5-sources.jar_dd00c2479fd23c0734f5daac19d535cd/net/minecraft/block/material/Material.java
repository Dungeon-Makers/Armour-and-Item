package net.minecraft.block.material;

public final class Material {
   public static final Material AIR = (new Material.Builder(MaterialColor.NONE)).noCollider().notSolidBlocking().nonSolid().replaceable().build();
   public static final Material STRUCTURAL_AIR = (new Material.Builder(MaterialColor.NONE)).noCollider().notSolidBlocking().nonSolid().replaceable().build();
   public static final Material PORTAL = (new Material.Builder(MaterialColor.NONE)).noCollider().notSolidBlocking().nonSolid().notPushable().build();
   public static final Material CLOTH_DECORATION = (new Material.Builder(MaterialColor.WOOL)).noCollider().notSolidBlocking().nonSolid().flammable().build();
   public static final Material PLANT = (new Material.Builder(MaterialColor.PLANT)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().build();
   public static final Material WATER_PLANT = (new Material.Builder(MaterialColor.WATER)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().build();
   public static final Material REPLACEABLE_PLANT = (new Material.Builder(MaterialColor.PLANT)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().flammable().build();
   public static final Material REPLACEABLE_WATER_PLANT = (new Material.Builder(MaterialColor.WATER)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().build();
   public static final Material WATER = (new Material.Builder(MaterialColor.WATER)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().liquid().build();
   public static final Material BUBBLE_COLUMN = (new Material.Builder(MaterialColor.WATER)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().liquid().build();
   public static final Material LAVA = (new Material.Builder(MaterialColor.FIRE)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().liquid().build();
   public static final Material TOP_SNOW = (new Material.Builder(MaterialColor.SNOW)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().func_200510_d().build();
   public static final Material FIRE = (new Material.Builder(MaterialColor.NONE)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().replaceable().build();
   public static final Material DECORATION = (new Material.Builder(MaterialColor.NONE)).noCollider().notSolidBlocking().nonSolid().destroyOnPush().build();
   public static final Material WEB = (new Material.Builder(MaterialColor.WOOL)).noCollider().notSolidBlocking().destroyOnPush().func_200510_d().build();
   public static final Material BUILDABLE_GLASS = (new Material.Builder(MaterialColor.NONE)).build();
   public static final Material CLAY = (new Material.Builder(MaterialColor.CLAY)).build();
   public static final Material DIRT = (new Material.Builder(MaterialColor.DIRT)).build();
   public static final Material GRASS = (new Material.Builder(MaterialColor.GRASS)).build();
   public static final Material ICE_SOLID = (new Material.Builder(MaterialColor.ICE)).build();
   public static final Material SAND = (new Material.Builder(MaterialColor.SAND)).build();
   public static final Material SPONGE = (new Material.Builder(MaterialColor.COLOR_YELLOW)).build();
   public static final Material SHULKER_SHELL = (new Material.Builder(MaterialColor.COLOR_PURPLE)).build();
   public static final Material WOOD = (new Material.Builder(MaterialColor.WOOD)).flammable().build();
   public static final Material BAMBOO_SAPLING = (new Material.Builder(MaterialColor.WOOD)).flammable().destroyOnPush().noCollider().build();
   public static final Material BAMBOO = (new Material.Builder(MaterialColor.WOOD)).flammable().destroyOnPush().build();
   public static final Material WOOL = (new Material.Builder(MaterialColor.WOOL)).flammable().build();
   public static final Material EXPLOSIVE = (new Material.Builder(MaterialColor.FIRE)).flammable().notSolidBlocking().build();
   public static final Material LEAVES = (new Material.Builder(MaterialColor.PLANT)).flammable().notSolidBlocking().destroyOnPush().build();
   public static final Material GLASS = (new Material.Builder(MaterialColor.NONE)).notSolidBlocking().build();
   public static final Material ICE = (new Material.Builder(MaterialColor.ICE)).notSolidBlocking().build();
   public static final Material CACTUS = (new Material.Builder(MaterialColor.PLANT)).notSolidBlocking().destroyOnPush().build();
   public static final Material STONE = (new Material.Builder(MaterialColor.STONE)).func_200510_d().build();
   public static final Material METAL = (new Material.Builder(MaterialColor.METAL)).func_200510_d().build();
   public static final Material SNOW = (new Material.Builder(MaterialColor.SNOW)).func_200510_d().build();
   public static final Material HEAVY_METAL = (new Material.Builder(MaterialColor.METAL)).func_200510_d().notPushable().build();
   public static final Material BARRIER = (new Material.Builder(MaterialColor.NONE)).func_200510_d().notPushable().build();
   public static final Material PISTON = (new Material.Builder(MaterialColor.STONE)).notPushable().build();
   public static final Material CORAL = (new Material.Builder(MaterialColor.PLANT)).destroyOnPush().build();
   public static final Material VEGETABLE = (new Material.Builder(MaterialColor.PLANT)).destroyOnPush().build();
   public static final Material EGG = (new Material.Builder(MaterialColor.PLANT)).destroyOnPush().build();
   public static final Material CAKE = (new Material.Builder(MaterialColor.NONE)).destroyOnPush().build();
   private final MaterialColor color;
   private final PushReaction pushReaction;
   private final boolean blocksMotion;
   private final boolean flammable;
   private final boolean field_76241_J;
   private final boolean liquid;
   private final boolean solidBlocking;
   private final boolean replaceable;
   private final boolean solid;

   public Material(MaterialColor p_i48243_1_, boolean p_i48243_2_, boolean p_i48243_3_, boolean p_i48243_4_, boolean p_i48243_5_, boolean p_i48243_6_, boolean p_i48243_7_, boolean p_i48243_8_, PushReaction p_i48243_9_) {
      this.color = p_i48243_1_;
      this.liquid = p_i48243_2_;
      this.solid = p_i48243_3_;
      this.blocksMotion = p_i48243_4_;
      this.solidBlocking = p_i48243_5_;
      this.field_76241_J = p_i48243_6_;
      this.flammable = p_i48243_7_;
      this.replaceable = p_i48243_8_;
      this.pushReaction = p_i48243_9_;
   }

   public boolean isLiquid() {
      return this.liquid;
   }

   public boolean isSolid() {
      return this.solid;
   }

   public boolean blocksMotion() {
      return this.blocksMotion;
   }

   public boolean isFlammable() {
      return this.flammable;
   }

   public boolean isReplaceable() {
      return this.replaceable;
   }

   public boolean isSolidBlocking() {
      return this.solidBlocking;
   }

   public boolean func_76229_l() {
      return this.field_76241_J;
   }

   public PushReaction getPushReaction() {
      return this.pushReaction;
   }

   public MaterialColor getColor() {
      return this.color;
   }

   public static class Builder {
      private PushReaction pushReaction = PushReaction.NORMAL;
      private boolean blocksMotion = true;
      private boolean flammable;
      private boolean field_200515_d = true;
      private boolean liquid;
      private boolean replaceable;
      private boolean solid = true;
      private final MaterialColor color;
      private boolean solidBlocking = true;

      public Builder(MaterialColor p_i48270_1_) {
         this.color = p_i48270_1_;
      }

      public Material.Builder liquid() {
         this.liquid = true;
         return this;
      }

      public Material.Builder nonSolid() {
         this.solid = false;
         return this;
      }

      public Material.Builder noCollider() {
         this.blocksMotion = false;
         return this;
      }

      private Material.Builder notSolidBlocking() {
         this.solidBlocking = false;
         return this;
      }

      protected Material.Builder func_200510_d() {
         this.field_200515_d = false;
         return this;
      }

      protected Material.Builder flammable() {
         this.flammable = true;
         return this;
      }

      public Material.Builder replaceable() {
         this.replaceable = true;
         return this;
      }

      protected Material.Builder destroyOnPush() {
         this.pushReaction = PushReaction.DESTROY;
         return this;
      }

      protected Material.Builder notPushable() {
         this.pushReaction = PushReaction.BLOCK;
         return this;
      }

      public Material build() {
         return new Material(this.color, this.liquid, this.solid, this.blocksMotion, this.solidBlocking, this.field_200515_d, this.flammable, this.replaceable, this.pushReaction);
      }
   }
}