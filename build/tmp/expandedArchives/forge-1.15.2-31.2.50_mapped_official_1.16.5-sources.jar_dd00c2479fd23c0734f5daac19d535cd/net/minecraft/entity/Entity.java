package net.minecraft.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.PushReaction;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.command.CommandSource;
import net.minecraft.command.ICommandSource;
import net.minecraft.command.arguments.EntityAnchorArgument;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ReportedException;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.entity.item.BoatEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.IFluidState;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.DoubleNBT;
import net.minecraft.nbt.FloatNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.network.play.server.SEntityPacket;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.Tag;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.INameable;
import net.minecraft.util.Mirror;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ReuseableStream;
import net.minecraft.util.Rotation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.shapes.IBooleanFunction;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.world.Explosion;
import net.minecraft.world.GameRules;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.gen.Heightmap;
import net.minecraft.world.server.ServerWorld;
import net.minecraft.world.server.TicketType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity extends net.minecraftforge.common.capabilities.CapabilityProvider<Entity> implements INameable, ICommandSource, net.minecraftforge.common.extensions.IForgeEntity {
   protected static final Logger LOGGER = LogManager.getLogger();
   private static final AtomicInteger ENTITY_COUNTER = new AtomicInteger();
   private static final List<ItemStack> EMPTY_LIST = Collections.emptyList();
   private static final AxisAlignedBB INITIAL_AABB = new AxisAlignedBB(0.0D, 0.0D, 0.0D, 0.0D, 0.0D, 0.0D);
   private static double viewScale = 1.0D;
   @Deprecated // Forge: Use the getter to allow overriding in mods
   private final EntityType<?> type;
   private int id = ENTITY_COUNTER.incrementAndGet();
   public boolean blocksBuilding;
   private final List<Entity> passengers = Lists.newArrayList();
   protected int boardingCooldown;
   @Nullable
   private Entity vehicle;
   public boolean forcedLoading;
   public World level;
   public double xo;
   public double yo;
   public double zo;
   private double field_70165_t;
   private double field_70163_u;
   private double field_70161_v;
   private Vec3d deltaMovement = Vec3d.ZERO;
   public float yRot;
   public float xRot;
   public float yRotO;
   public float xRotO;
   private AxisAlignedBB bb = INITIAL_AABB;
   public boolean onGround;
   public boolean horizontalCollision;
   public boolean verticalCollision;
   public boolean field_70132_H;
   public boolean hurtMarked;
   protected Vec3d stuckSpeedMultiplier = Vec3d.ZERO;
   @Deprecated //Forge: Use isAlive, remove(boolean) and revive() instead of directly accessing this field. To allow the entity to react to and better control this information.
   public boolean removed;
   public float walkDistO;
   public float walkDist;
   public float moveDist;
   public float fallDistance;
   private float nextStep = 1.0F;
   private float nextFlap = 1.0F;
   public double xOld;
   public double yOld;
   public double zOld;
   public float maxUpStep;
   public boolean noPhysics;
   public float pushthrough;
   protected final Random random = new Random();
   public int tickCount;
   private int remainingFireTicks = -this.getFireImmuneTicks();
   protected boolean wasTouchingWater;
   protected double field_211517_W;
   protected boolean wasEyeInWater;
   protected boolean field_213329_S;
   public int invulnerableTime;
   protected boolean firstTick = true;
   protected final EntityDataManager entityData;
   protected static final DataParameter<Byte> DATA_SHARED_FLAGS_ID = EntityDataManager.defineId(Entity.class, DataSerializers.BYTE);
   private static final DataParameter<Integer> DATA_AIR_SUPPLY_ID = EntityDataManager.defineId(Entity.class, DataSerializers.INT);
   private static final DataParameter<Optional<ITextComponent>> DATA_CUSTOM_NAME = EntityDataManager.defineId(Entity.class, DataSerializers.OPTIONAL_COMPONENT);
   private static final DataParameter<Boolean> DATA_CUSTOM_NAME_VISIBLE = EntityDataManager.defineId(Entity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> DATA_SILENT = EntityDataManager.defineId(Entity.class, DataSerializers.BOOLEAN);
   private static final DataParameter<Boolean> DATA_NO_GRAVITY = EntityDataManager.defineId(Entity.class, DataSerializers.BOOLEAN);
   protected static final DataParameter<Pose> DATA_POSE = EntityDataManager.defineId(Entity.class, DataSerializers.POSE);
   public boolean inChunk;
   public int xChunk;
   public int yChunk;
   public int zChunk;
   public long field_70118_ct;
   public long field_70117_cu;
   public long field_70116_cv;
   public boolean noCulling;
   public boolean hasImpulse;
   public int field_71088_bW;
   protected boolean isInsidePortal;
   protected int portalTime;
   public DimensionType field_71093_bK;
   protected BlockPos field_181016_an;
   protected Vec3d field_181017_ao;
   protected Direction field_181018_ap;
   private boolean invulnerable;
   protected UUID uuid = MathHelper.createInsecureUUID(this.random);
   protected String stringUUID = this.uuid.toString();
   protected boolean glowing;
   private final Set<String> tags = Sets.newHashSet();
   private boolean forceChunkAddition;
   private final double[] pistonDeltas = new double[]{0.0D, 0.0D, 0.0D};
   private long pistonDeltasGameTime;
   private EntitySize dimensions;
   private float eyeHeight;

   public Entity(EntityType<?> p_i48580_1_, World p_i48580_2_) {
      super(Entity.class);
      this.type = p_i48580_1_;
      this.level = p_i48580_2_;
      this.dimensions = p_i48580_1_.getDimensions();
      this.setPos(0.0D, 0.0D, 0.0D);
      if (p_i48580_2_ != null) {
         this.field_71093_bK = p_i48580_2_.dimension.func_186058_p();
      }

      this.entityData = new EntityDataManager(this);
      this.entityData.define(DATA_SHARED_FLAGS_ID, (byte)0);
      this.entityData.define(DATA_AIR_SUPPLY_ID, this.getMaxAirSupply());
      this.entityData.define(DATA_CUSTOM_NAME_VISIBLE, false);
      this.entityData.define(DATA_CUSTOM_NAME, Optional.empty());
      this.entityData.define(DATA_SILENT, false);
      this.entityData.define(DATA_NO_GRAVITY, false);
      this.entityData.define(DATA_POSE, Pose.STANDING);
      this.defineSynchedData();
      this.eyeHeight = getEyeHeightForge(Pose.STANDING, this.dimensions);
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.EntityEvent.EntityConstructing(this));
      this.gatherCapabilities();
   }

   @OnlyIn(Dist.CLIENT)
   public int getTeamColor() {
      Team team = this.getTeam();
      return team != null && team.getColor().getColor() != null ? team.getColor().getColor() : 16777215;
   }

   public boolean isSpectator() {
      return false;
   }

   public final void unRide() {
      if (this.isVehicle()) {
         this.ejectPassengers();
      }

      if (this.isPassenger()) {
         this.stopRiding();
      }

   }

   public void setPacketCoordinates(double p_213312_1_, double p_213312_3_, double p_213312_5_) {
      this.field_70118_ct = SEntityPacket.entityToPacket(p_213312_1_);
      this.field_70117_cu = SEntityPacket.entityToPacket(p_213312_3_);
      this.field_70116_cv = SEntityPacket.entityToPacket(p_213312_5_);
   }

   public EntityType<?> getType() {
      return this.type;
   }

   public int getId() {
      return this.id;
   }

   public void setId(int p_145769_1_) {
      this.id = p_145769_1_;
   }

   public Set<String> getTags() {
      return this.tags;
   }

   public boolean addTag(String p_184211_1_) {
      return this.tags.size() >= 1024 ? false : this.tags.add(p_184211_1_);
   }

   public boolean removeTag(String p_184197_1_) {
      return this.tags.remove(p_184197_1_);
   }

   public void kill() {
      this.remove();
   }

   protected abstract void defineSynchedData();

   public EntityDataManager getEntityData() {
      return this.entityData;
   }

   public boolean equals(Object p_equals_1_) {
      if (p_equals_1_ instanceof Entity) {
         return ((Entity)p_equals_1_).id == this.id;
      } else {
         return false;
      }
   }

   public int hashCode() {
      return this.id;
   }

   @OnlyIn(Dist.CLIENT)
   protected void resetPos() {
      if (this.level != null) {
         for(double d0 = this.getY(); d0 > 0.0D && d0 < this.level.func_201675_m().getHeight(); ++d0) {
            this.setPos(this.getX(), d0, this.getZ());
            if (this.level.noCollision(this)) {
               break;
            }
         }

         this.setDeltaMovement(Vec3d.ZERO);
         this.xRot = 0.0F;
      }
   }

   public void remove() {
      this.remove(false);
   }

   public void remove(boolean keepData) {
      this.removed = true;
      if (!keepData)
         this.invalidateCaps();
   }

   protected void setPose(Pose p_213301_1_) {
      this.entityData.set(DATA_POSE, p_213301_1_);
   }

   public Pose getPose() {
      return this.entityData.get(DATA_POSE);
   }

   protected void setRot(float p_70101_1_, float p_70101_2_) {
      this.yRot = p_70101_1_ % 360.0F;
      this.xRot = p_70101_2_ % 360.0F;
   }

   public void setPos(double p_70107_1_, double p_70107_3_, double p_70107_5_) {
      this.setPosRaw(p_70107_1_, p_70107_3_, p_70107_5_);
      if (this.isAddedToWorld() && !this.level.isClientSide && level instanceof ServerWorld) ((ServerWorld)this.level).updateChunkPos(this); // Forge - Process chunk registration after moving.
      float f = this.dimensions.width / 2.0F;
      float f1 = this.dimensions.height;
      this.setBoundingBox(new AxisAlignedBB(p_70107_1_ - (double)f, p_70107_3_, p_70107_5_ - (double)f, p_70107_1_ + (double)f, p_70107_3_ + (double)f1, p_70107_5_ + (double)f));
   }

   protected void reapplyPosition() {
      this.setPos(this.field_70165_t, this.field_70163_u, this.field_70161_v);
   }

   @OnlyIn(Dist.CLIENT)
   public void turn(double p_195049_1_, double p_195049_3_) {
      double d0 = p_195049_3_ * 0.15D;
      double d1 = p_195049_1_ * 0.15D;
      this.xRot = (float)((double)this.xRot + d0);
      this.yRot = (float)((double)this.yRot + d1);
      this.xRot = MathHelper.clamp(this.xRot, -90.0F, 90.0F);
      this.xRotO = (float)((double)this.xRotO + d0);
      this.yRotO = (float)((double)this.yRotO + d1);
      this.xRotO = MathHelper.clamp(this.xRotO, -90.0F, 90.0F);
      if (this.vehicle != null) {
         this.vehicle.onPassengerTurned(this);
      }

   }

   public void tick() {
      if (!this.level.isClientSide) {
         this.setSharedFlag(6, this.isGlowing());
      }

      this.baseTick();
   }

   public void baseTick() {
      this.level.getProfiler().push("entityBaseTick");
      if (this.isPassenger() && this.getVehicle().removed) {
         this.stopRiding();
      }

      if (this.boardingCooldown > 0) {
         --this.boardingCooldown;
      }

      this.walkDistO = this.walkDist;
      this.xRotO = this.xRot;
      this.yRotO = this.yRot;
      this.handleNetherPortal();
      this.func_174830_Y();
      this.func_205011_p();
      if (this.level.isClientSide) {
         this.clearFire();
      } else if (this.remainingFireTicks > 0) {
         if (this.func_70045_F()) {
            this.remainingFireTicks -= 4;
            if (this.remainingFireTicks < 0) {
               this.clearFire();
            }
         } else {
            if (this.remainingFireTicks % 20 == 0) {
               this.hurt(DamageSource.ON_FIRE, 1.0F);
            }

            --this.remainingFireTicks;
         }
      }

      if (this.isInLava()) {
         this.lavaHurt();
         this.fallDistance *= 0.5F;
      }

      if (this.getY() < -64.0D) {
         this.outOfWorld();
      }

      if (!this.level.isClientSide) {
         this.setSharedFlag(0, this.remainingFireTicks > 0);
      }

      this.firstTick = false;
      this.level.getProfiler().pop();
   }

   protected void processPortalCooldown() {
      if (this.field_71088_bW > 0) {
         --this.field_71088_bW;
      }

   }

   public int getPortalWaitTime() {
      return 1;
   }

   protected void lavaHurt() {
      if (!this.func_70045_F()) {
         this.setSecondsOnFire(15);
         this.hurt(DamageSource.LAVA, 4.0F);
      }
   }

   public void setSecondsOnFire(int p_70015_1_) {
      int i = p_70015_1_ * 20;
      if (this instanceof LivingEntity) {
         i = ProtectionEnchantment.getFireAfterDampener((LivingEntity)this, i);
      }

      if (this.remainingFireTicks < i) {
         this.remainingFireTicks = i;
      }

   }

   public void func_223308_g(int p_223308_1_) {
      this.remainingFireTicks = p_223308_1_;
   }

   public int getRemainingFireTicks() {
      return this.remainingFireTicks;
   }

   public void clearFire() {
      this.remainingFireTicks = 0;
   }

   protected void outOfWorld() {
      this.remove();
   }

   public boolean isFree(double p_70038_1_, double p_70038_3_, double p_70038_5_) {
      return this.isFree(this.getBoundingBox().move(p_70038_1_, p_70038_3_, p_70038_5_));
   }

   private boolean isFree(AxisAlignedBB p_174809_1_) {
      return this.level.noCollision(this, p_174809_1_) && !this.level.containsAnyLiquid(p_174809_1_);
   }

   public void move(MoverType p_213315_1_, Vec3d p_213315_2_) {
      if (this.noPhysics) {
         this.setBoundingBox(this.getBoundingBox().move(p_213315_2_));
         this.setLocationFromBoundingbox();
      } else {
         if (p_213315_1_ == MoverType.PISTON) {
            p_213315_2_ = this.limitPistonMovement(p_213315_2_);
            if (p_213315_2_.equals(Vec3d.ZERO)) {
               return;
            }
         }

         this.level.getProfiler().push("move");
         if (this.stuckSpeedMultiplier.lengthSqr() > 1.0E-7D) {
            p_213315_2_ = p_213315_2_.multiply(this.stuckSpeedMultiplier);
            this.stuckSpeedMultiplier = Vec3d.ZERO;
            this.setDeltaMovement(Vec3d.ZERO);
         }

         p_213315_2_ = this.maybeBackOffFromEdge(p_213315_2_, p_213315_1_);
         Vec3d vec3d = this.collide(p_213315_2_);
         if (vec3d.lengthSqr() > 1.0E-7D) {
            this.setBoundingBox(this.getBoundingBox().move(vec3d));
            this.setLocationFromBoundingbox();
         }

         this.level.getProfiler().pop();
         this.level.getProfiler().push("rest");
         this.horizontalCollision = !MathHelper.equal(p_213315_2_.x, vec3d.x) || !MathHelper.equal(p_213315_2_.z, vec3d.z);
         this.verticalCollision = p_213315_2_.y != vec3d.y;
         this.onGround = this.verticalCollision && p_213315_2_.y < 0.0D;
         this.field_70132_H = this.horizontalCollision || this.verticalCollision;
         BlockPos blockpos = this.getOnPos();
         BlockState blockstate = this.level.getBlockState(blockpos);
         this.checkFallDamage(vec3d.y, this.onGround, blockstate, blockpos);
         Vec3d vec3d1 = this.getDeltaMovement();
         if (p_213315_2_.x != vec3d.x) {
            this.setDeltaMovement(0.0D, vec3d1.y, vec3d1.z);
         }

         if (p_213315_2_.z != vec3d.z) {
            this.setDeltaMovement(vec3d1.x, vec3d1.y, 0.0D);
         }

         Block block = blockstate.getBlock();
         if (p_213315_2_.y != vec3d.y) {
            block.updateEntityAfterFallOn(this.level, this);
         }

         if (this.onGround && !this.isSteppingCarefully()) {
            block.stepOn(this.level, blockpos, this);
         }

         if (this.isMovementNoisy() && !this.isPassenger()) {
            double d0 = vec3d.x;
            double d1 = vec3d.y;
            double d2 = vec3d.z;
            if (block != Blocks.LADDER && block != Blocks.SCAFFOLDING) {
               d1 = 0.0D;
            }

            this.walkDist = (float)((double)this.walkDist + (double)MathHelper.sqrt(getHorizontalDistanceSqr(vec3d)) * 0.6D);
            this.moveDist = (float)((double)this.moveDist + (double)MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2) * 0.6D);
            if (this.moveDist > this.nextStep && !blockstate.isAir(this.level, blockpos)) {
               this.nextStep = this.nextStep();
               if (this.isInWater()) {
                  Entity entity = this.isVehicle() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
                  float f = entity == this ? 0.35F : 0.4F;
                  Vec3d vec3d2 = entity.getDeltaMovement();
                  float f1 = MathHelper.sqrt(vec3d2.x * vec3d2.x * (double)0.2F + vec3d2.y * vec3d2.y + vec3d2.z * vec3d2.z * (double)0.2F) * f;
                  if (f1 > 1.0F) {
                     f1 = 1.0F;
                  }

                  this.playSwimSound(f1);
               } else {
                  this.playStepSound(blockpos, blockstate);
               }
            } else if (this.moveDist > this.nextFlap && this.makeFlySound() && blockstate.isAir(this.level, blockpos)) {
               this.nextFlap = this.playFlySound(this.moveDist);
            }
         }

         try {
            this.field_213329_S = false;
            this.checkInsideBlocks();
         } catch (Throwable throwable) {
            CrashReport crashreport = CrashReport.forThrowable(throwable, "Checking entity block collision");
            CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being checked for collision");
            this.fillCrashReportCategory(crashreportcategory);
            throw new ReportedException(crashreport);
         }

         this.setDeltaMovement(this.getDeltaMovement().multiply((double)this.getBlockSpeedFactor(), 1.0D, (double)this.getBlockSpeedFactor()));
         boolean flag = this.isInWaterRainOrBubble();
         if (this.level.func_147470_e(this.getBoundingBox().deflate(0.001D))) {
            if (!flag) {
               ++this.remainingFireTicks;
               if (this.remainingFireTicks == 0) {
                  this.setSecondsOnFire(8);
               }
            }

            this.burn(1);
         } else if (this.remainingFireTicks <= 0) {
            this.remainingFireTicks = -this.getFireImmuneTicks();
         }

         if (flag && this.isOnFire()) {
            this.playSound(SoundEvents.GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
            this.remainingFireTicks = -this.getFireImmuneTicks();
         }

         this.level.getProfiler().pop();
      }
   }

   protected BlockPos getOnPos() {
      int i = MathHelper.floor(this.field_70165_t);
      int j = MathHelper.floor(this.field_70163_u - (double)0.2F);
      int k = MathHelper.floor(this.field_70161_v);
      BlockPos blockpos = new BlockPos(i, j, k);
      if (this.level.isEmptyBlock(blockpos)) {
         BlockPos blockpos1 = blockpos.below();
         BlockState blockstate = this.level.getBlockState(blockpos1);
         if (blockstate.collisionExtendsVertically(this.level, blockpos1, this)) {
            return blockpos1;
         }
      }

      return blockpos;
   }

   protected float getBlockJumpFactor() {
      float f = this.level.getBlockState(new BlockPos(this)).getBlock().getJumpFactor();
      float f1 = this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getJumpFactor();
      return (double)f == 1.0D ? f1 : f;
   }

   protected float getBlockSpeedFactor() {
      Block block = this.level.getBlockState(new BlockPos(this)).getBlock();
      float f = block.getSpeedFactor();
      if (block != Blocks.WATER && block != Blocks.BUBBLE_COLUMN) {
         return (double)f == 1.0D ? this.level.getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getSpeedFactor() : f;
      } else {
         return f;
      }
   }

   protected BlockPos getBlockPosBelowThatAffectsMyMovement() {
      return new BlockPos(this.field_70165_t, this.getBoundingBox().minY - 0.5000001D, this.field_70161_v);
   }

   protected Vec3d maybeBackOffFromEdge(Vec3d p_225514_1_, MoverType p_225514_2_) {
      return p_225514_1_;
   }

   protected Vec3d limitPistonMovement(Vec3d p_213308_1_) {
      if (p_213308_1_.lengthSqr() <= 1.0E-7D) {
         return p_213308_1_;
      } else {
         long i = this.level.getGameTime();
         if (i != this.pistonDeltasGameTime) {
            Arrays.fill(this.pistonDeltas, 0.0D);
            this.pistonDeltasGameTime = i;
         }

         if (p_213308_1_.x != 0.0D) {
            double d2 = this.applyPistonMovementRestriction(Direction.Axis.X, p_213308_1_.x);
            return Math.abs(d2) <= (double)1.0E-5F ? Vec3d.ZERO : new Vec3d(d2, 0.0D, 0.0D);
         } else if (p_213308_1_.y != 0.0D) {
            double d1 = this.applyPistonMovementRestriction(Direction.Axis.Y, p_213308_1_.y);
            return Math.abs(d1) <= (double)1.0E-5F ? Vec3d.ZERO : new Vec3d(0.0D, d1, 0.0D);
         } else if (p_213308_1_.z != 0.0D) {
            double d0 = this.applyPistonMovementRestriction(Direction.Axis.Z, p_213308_1_.z);
            return Math.abs(d0) <= (double)1.0E-5F ? Vec3d.ZERO : new Vec3d(0.0D, 0.0D, d0);
         } else {
            return Vec3d.ZERO;
         }
      }
   }

   private double applyPistonMovementRestriction(Direction.Axis p_213304_1_, double p_213304_2_) {
      int i = p_213304_1_.ordinal();
      double d0 = MathHelper.clamp(p_213304_2_ + this.pistonDeltas[i], -0.51D, 0.51D);
      p_213304_2_ = d0 - this.pistonDeltas[i];
      this.pistonDeltas[i] = d0;
      return p_213304_2_;
   }

   private Vec3d collide(Vec3d p_213306_1_) {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      ISelectionContext iselectioncontext = ISelectionContext.of(this);
      VoxelShape voxelshape = this.level.getWorldBorder().getCollisionShape();
      Stream<VoxelShape> stream = VoxelShapes.joinIsNotEmpty(voxelshape, VoxelShapes.create(axisalignedbb.deflate(1.0E-7D)), IBooleanFunction.AND) ? Stream.empty() : Stream.of(voxelshape);
      Stream<VoxelShape> stream1 = this.level.func_223439_a(this, axisalignedbb.expandTowards(p_213306_1_), ImmutableSet.of());
      ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream<>(Stream.concat(stream1, stream));
      Vec3d vec3d = p_213306_1_.lengthSqr() == 0.0D ? p_213306_1_ : collideBoundingBoxHeuristically(this, p_213306_1_, axisalignedbb, this.level, iselectioncontext, reuseablestream);
      boolean flag = p_213306_1_.x != vec3d.x;
      boolean flag1 = p_213306_1_.y != vec3d.y;
      boolean flag2 = p_213306_1_.z != vec3d.z;
      boolean flag3 = this.onGround || flag1 && p_213306_1_.y < 0.0D;
      if (this.maxUpStep > 0.0F && flag3 && (flag || flag2)) {
         Vec3d vec3d1 = collideBoundingBoxHeuristically(this, new Vec3d(p_213306_1_.x, (double)this.maxUpStep, p_213306_1_.z), axisalignedbb, this.level, iselectioncontext, reuseablestream);
         Vec3d vec3d2 = collideBoundingBoxHeuristically(this, new Vec3d(0.0D, (double)this.maxUpStep, 0.0D), axisalignedbb.expandTowards(p_213306_1_.x, 0.0D, p_213306_1_.z), this.level, iselectioncontext, reuseablestream);
         if (vec3d2.y < (double)this.maxUpStep) {
            Vec3d vec3d3 = collideBoundingBoxHeuristically(this, new Vec3d(p_213306_1_.x, 0.0D, p_213306_1_.z), axisalignedbb.move(vec3d2), this.level, iselectioncontext, reuseablestream).add(vec3d2);
            if (getHorizontalDistanceSqr(vec3d3) > getHorizontalDistanceSqr(vec3d1)) {
               vec3d1 = vec3d3;
            }
         }

         if (getHorizontalDistanceSqr(vec3d1) > getHorizontalDistanceSqr(vec3d)) {
            return vec3d1.add(collideBoundingBoxHeuristically(this, new Vec3d(0.0D, -vec3d1.y + p_213306_1_.y, 0.0D), axisalignedbb.move(vec3d1), this.level, iselectioncontext, reuseablestream));
         }
      }

      return vec3d;
   }

   public static double getHorizontalDistanceSqr(Vec3d p_213296_0_) {
      return p_213296_0_.x * p_213296_0_.x + p_213296_0_.z * p_213296_0_.z;
   }

   public static Vec3d collideBoundingBoxHeuristically(@Nullable Entity p_223307_0_, Vec3d p_223307_1_, AxisAlignedBB p_223307_2_, World p_223307_3_, ISelectionContext p_223307_4_, ReuseableStream<VoxelShape> p_223307_5_) {
      boolean flag = p_223307_1_.x == 0.0D;
      boolean flag1 = p_223307_1_.y == 0.0D;
      boolean flag2 = p_223307_1_.z == 0.0D;
      if ((!flag || !flag1) && (!flag || !flag2) && (!flag1 || !flag2)) {
         ReuseableStream<VoxelShape> reuseablestream = new ReuseableStream<>(Stream.concat(p_223307_5_.getStream(), p_223307_3_.getBlockCollisions(p_223307_0_, p_223307_2_.expandTowards(p_223307_1_))));
         return collideBoundingBoxLegacy(p_223307_1_, p_223307_2_, reuseablestream);
      } else {
         return collideBoundingBox(p_223307_1_, p_223307_2_, p_223307_3_, p_223307_4_, p_223307_5_);
      }
   }

   public static Vec3d collideBoundingBoxLegacy(Vec3d p_223310_0_, AxisAlignedBB p_223310_1_, ReuseableStream<VoxelShape> p_223310_2_) {
      double d0 = p_223310_0_.x;
      double d1 = p_223310_0_.y;
      double d2 = p_223310_0_.z;
      if (d1 != 0.0D) {
         d1 = VoxelShapes.collide(Direction.Axis.Y, p_223310_1_, p_223310_2_.getStream(), d1);
         if (d1 != 0.0D) {
            p_223310_1_ = p_223310_1_.move(0.0D, d1, 0.0D);
         }
      }

      boolean flag = Math.abs(d0) < Math.abs(d2);
      if (flag && d2 != 0.0D) {
         d2 = VoxelShapes.collide(Direction.Axis.Z, p_223310_1_, p_223310_2_.getStream(), d2);
         if (d2 != 0.0D) {
            p_223310_1_ = p_223310_1_.move(0.0D, 0.0D, d2);
         }
      }

      if (d0 != 0.0D) {
         d0 = VoxelShapes.collide(Direction.Axis.X, p_223310_1_, p_223310_2_.getStream(), d0);
         if (!flag && d0 != 0.0D) {
            p_223310_1_ = p_223310_1_.move(d0, 0.0D, 0.0D);
         }
      }

      if (!flag && d2 != 0.0D) {
         d2 = VoxelShapes.collide(Direction.Axis.Z, p_223310_1_, p_223310_2_.getStream(), d2);
      }

      return new Vec3d(d0, d1, d2);
   }

   public static Vec3d collideBoundingBox(Vec3d p_213313_0_, AxisAlignedBB p_213313_1_, IWorldReader p_213313_2_, ISelectionContext p_213313_3_, ReuseableStream<VoxelShape> p_213313_4_) {
      double d0 = p_213313_0_.x;
      double d1 = p_213313_0_.y;
      double d2 = p_213313_0_.z;
      if (d1 != 0.0D) {
         d1 = VoxelShapes.collide(Direction.Axis.Y, p_213313_1_, p_213313_2_, d1, p_213313_3_, p_213313_4_.getStream());
         if (d1 != 0.0D) {
            p_213313_1_ = p_213313_1_.move(0.0D, d1, 0.0D);
         }
      }

      boolean flag = Math.abs(d0) < Math.abs(d2);
      if (flag && d2 != 0.0D) {
         d2 = VoxelShapes.collide(Direction.Axis.Z, p_213313_1_, p_213313_2_, d2, p_213313_3_, p_213313_4_.getStream());
         if (d2 != 0.0D) {
            p_213313_1_ = p_213313_1_.move(0.0D, 0.0D, d2);
         }
      }

      if (d0 != 0.0D) {
         d0 = VoxelShapes.collide(Direction.Axis.X, p_213313_1_, p_213313_2_, d0, p_213313_3_, p_213313_4_.getStream());
         if (!flag && d0 != 0.0D) {
            p_213313_1_ = p_213313_1_.move(d0, 0.0D, 0.0D);
         }
      }

      if (!flag && d2 != 0.0D) {
         d2 = VoxelShapes.collide(Direction.Axis.Z, p_213313_1_, p_213313_2_, d2, p_213313_3_, p_213313_4_.getStream());
      }

      return new Vec3d(d0, d1, d2);
   }

   protected float nextStep() {
      return (float)((int)this.moveDist + 1);
   }

   public void setLocationFromBoundingbox() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();
      this.setPosRaw((axisalignedbb.minX + axisalignedbb.maxX) / 2.0D, axisalignedbb.minY, (axisalignedbb.minZ + axisalignedbb.maxZ) / 2.0D);
      if (this.isAddedToWorld() && !this.level.isClientSide && level instanceof ServerWorld) ((ServerWorld)this.level).updateChunkPos(this); // Forge - Process chunk registration after moving.
   }

   protected SoundEvent getSwimSound() {
      return SoundEvents.GENERIC_SWIM;
   }

   protected SoundEvent getSwimSplashSound() {
      return SoundEvents.GENERIC_SPLASH;
   }

   protected SoundEvent getSwimHighSpeedSplashSound() {
      return SoundEvents.GENERIC_SPLASH;
   }

   protected void checkInsideBlocks() {
      AxisAlignedBB axisalignedbb = this.getBoundingBox();

      try (
         BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.func_185345_c(axisalignedbb.minX + 0.001D, axisalignedbb.minY + 0.001D, axisalignedbb.minZ + 0.001D);
         BlockPos.PooledMutable blockpos$pooledmutable1 = BlockPos.PooledMutable.func_185345_c(axisalignedbb.maxX - 0.001D, axisalignedbb.maxY - 0.001D, axisalignedbb.maxZ - 0.001D);
         BlockPos.PooledMutable blockpos$pooledmutable2 = BlockPos.PooledMutable.func_185346_s();
      ) {
         if (this.level.hasChunksAt(blockpos$pooledmutable, blockpos$pooledmutable1)) {
            for(int i = blockpos$pooledmutable.getX(); i <= blockpos$pooledmutable1.getX(); ++i) {
               for(int j = blockpos$pooledmutable.getY(); j <= blockpos$pooledmutable1.getY(); ++j) {
                  for(int k = blockpos$pooledmutable.getZ(); k <= blockpos$pooledmutable1.getZ(); ++k) {
                     blockpos$pooledmutable2.set(i, j, k);
                     BlockState blockstate = this.level.getBlockState(blockpos$pooledmutable2);

                     try {
                        blockstate.entityInside(this.level, blockpos$pooledmutable2, this);
                        this.onInsideBlock(blockstate);
                     } catch (Throwable throwable) {
                        CrashReport crashreport = CrashReport.forThrowable(throwable, "Colliding entity with block");
                        CrashReportCategory crashreportcategory = crashreport.addCategory("Block being collided with");
                        CrashReportCategory.populateBlockDetails(crashreportcategory, blockpos$pooledmutable2, blockstate);
                        throw new ReportedException(crashreport);
                     }
                  }
               }
            }
         }
      }

   }

   protected void onInsideBlock(BlockState p_191955_1_) {
   }

   protected void playStepSound(BlockPos p_180429_1_, BlockState p_180429_2_) {
      if (!p_180429_2_.getMaterial().isLiquid()) {
         BlockState blockstate = this.level.getBlockState(p_180429_1_.above());
         SoundType soundtype = blockstate.getBlock() == Blocks.SNOW ? blockstate.getSoundType(level, p_180429_1_, this) : p_180429_2_.getSoundType(level, p_180429_1_, this);
         this.playSound(soundtype.getStepSound(), soundtype.getVolume() * 0.15F, soundtype.getPitch());
      }
   }

   protected void playSwimSound(float p_203006_1_) {
      this.playSound(this.getSwimSound(), p_203006_1_, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
   }

   protected float playFlySound(float p_191954_1_) {
      return 0.0F;
   }

   protected boolean makeFlySound() {
      return false;
   }

   public void playSound(SoundEvent p_184185_1_, float p_184185_2_, float p_184185_3_) {
      if (!this.isSilent()) {
         this.level.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), p_184185_1_, this.getSoundSource(), p_184185_2_, p_184185_3_);
      }

   }

   public boolean isSilent() {
      return this.entityData.get(DATA_SILENT);
   }

   public void setSilent(boolean p_174810_1_) {
      this.entityData.set(DATA_SILENT, p_174810_1_);
   }

   public boolean isNoGravity() {
      return this.entityData.get(DATA_NO_GRAVITY);
   }

   public void setNoGravity(boolean p_189654_1_) {
      this.entityData.set(DATA_NO_GRAVITY, p_189654_1_);
   }

   protected boolean isMovementNoisy() {
      return true;
   }

   protected void checkFallDamage(double p_184231_1_, boolean p_184231_3_, BlockState p_184231_4_, BlockPos p_184231_5_) {
      if (p_184231_3_) {
         if (this.fallDistance > 0.0F) {
            p_184231_4_.getBlock().fallOn(this.level, p_184231_5_, this, this.fallDistance);
         }

         this.fallDistance = 0.0F;
      } else if (p_184231_1_ < 0.0D) {
         this.fallDistance = (float)((double)this.fallDistance - p_184231_1_);
      }

   }

   @Nullable
   public AxisAlignedBB func_70046_E() {
      return null;
   }

   protected void burn(int p_70081_1_) {
      if (!this.func_70045_F()) {
         this.hurt(DamageSource.IN_FIRE, (float)p_70081_1_);
      }

   }

   public final boolean func_70045_F() {
      return this.getType().fireImmune();
   }

   public boolean causeFallDamage(float p_225503_1_, float p_225503_2_) {
      if (this.isVehicle()) {
         for(Entity entity : this.getPassengers()) {
            entity.causeFallDamage(p_225503_1_, p_225503_2_);
         }
      }

      return false;
   }

   public boolean isInWater() {
      return this.wasTouchingWater;
   }

   private boolean isInRain() {
      boolean flag;
      try (BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.func_209907_b(this)) {
         flag = this.level.isRainingAt(blockpos$pooledmutable) || this.level.isRainingAt(blockpos$pooledmutable.set(this.getX(), this.getY() + (double)this.dimensions.height, this.getZ()));
      }

      return flag;
   }

   private boolean isInBubbleColumn() {
      return this.level.getBlockState(new BlockPos(this)).getBlock() == Blocks.BUBBLE_COLUMN;
   }

   public boolean isInWaterOrRain() {
      return this.isInWater() || this.isInRain();
   }

   public boolean isInWaterRainOrBubble() {
      return this.isInWater() || this.isInRain() || this.isInBubbleColumn();
   }

   public boolean isInWaterOrBubble() {
      return this.isInWater() || this.isInBubbleColumn();
   }

   public boolean isUnderWater() {
      return this.wasEyeInWater && this.isInWater();
   }

   private void func_205011_p() {
      this.func_70072_I();
      this.updateFluidOnEyes();
      this.updateSwimming();
   }

   public void updateSwimming() {
      if (this.isSwimming()) {
         this.setSwimming(this.isSprinting() && this.isInWater() && !this.isPassenger());
      } else {
         this.setSwimming(this.isSprinting() && this.isUnderWater() && !this.isPassenger());
      }

   }

   public boolean func_70072_I() {
      if (this.getVehicle() instanceof BoatEntity) {
         this.wasTouchingWater = false;
      } else if (this.updateFluidHeightAndDoFluidPushing(FluidTags.WATER)) {
         if (!this.wasTouchingWater && !this.firstTick) {
            this.doWaterSplashEffect();
         }

         this.fallDistance = 0.0F;
         this.wasTouchingWater = true;
         this.clearFire();
      } else {
         this.wasTouchingWater = false;
      }

      return this.wasTouchingWater;
   }

   private void updateFluidOnEyes() {
      this.wasEyeInWater = this.func_213290_a(FluidTags.WATER, true);
   }

   protected void doWaterSplashEffect() {
      Entity entity = this.isVehicle() && this.getControllingPassenger() != null ? this.getControllingPassenger() : this;
      float f = entity == this ? 0.2F : 0.9F;
      Vec3d vec3d = entity.getDeltaMovement();
      float f1 = MathHelper.sqrt(vec3d.x * vec3d.x * (double)0.2F + vec3d.y * vec3d.y + vec3d.z * vec3d.z * (double)0.2F) * f;
      if (f1 > 1.0F) {
         f1 = 1.0F;
      }

      if ((double)f1 < 0.25D) {
         this.playSound(this.getSwimSplashSound(), f1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
      } else {
         this.playSound(this.getSwimHighSpeedSplashSound(), f1, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
      }

      float f2 = (float)MathHelper.floor(this.getY());

      for(int i = 0; (float)i < 1.0F + this.dimensions.width * 20.0F; ++i) {
         float f3 = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
         float f4 = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
         this.level.addParticle(ParticleTypes.BUBBLE, this.getX() + (double)f3, (double)(f2 + 1.0F), this.getZ() + (double)f4, vec3d.x, vec3d.y - (double)(this.random.nextFloat() * 0.2F), vec3d.z);
      }

      for(int j = 0; (float)j < 1.0F + this.dimensions.width * 20.0F; ++j) {
         float f5 = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
         float f6 = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
         this.level.addParticle(ParticleTypes.SPLASH, this.getX() + (double)f5, (double)(f2 + 1.0F), this.getZ() + (double)f6, vec3d.x, vec3d.y, vec3d.z);
      }

   }

   public void func_174830_Y() {
      if (this.isSprinting() && !this.isInWater()) {
         this.func_174808_Z();
      }

   }

   protected void func_174808_Z() {
      int i = MathHelper.floor(this.getX());
      int j = MathHelper.floor(this.getY() - (double)0.2F);
      int k = MathHelper.floor(this.getZ());
      BlockPos blockpos = new BlockPos(i, j, k);
      BlockState blockstate = this.level.getBlockState(blockpos);
      if (!blockstate.addRunningEffects(level, blockpos, this))
      if (blockstate.getRenderShape() != BlockRenderType.INVISIBLE) {
         Vec3d vec3d = this.getDeltaMovement();
         this.level.addParticle(new BlockParticleData(ParticleTypes.BLOCK, blockstate).setPos(blockpos), this.getX() + ((double)this.random.nextFloat() - 0.5D) * (double)this.dimensions.width, this.getY() + 0.1D, this.getZ() + ((double)this.random.nextFloat() - 0.5D) * (double)this.dimensions.width, vec3d.x * -4.0D, 1.5D, vec3d.z * -4.0D);
      }

   }

   public boolean isEyeInFluid(Tag<Fluid> p_208600_1_) {
      return this.func_213290_a(p_208600_1_, false);
   }

   public boolean func_213290_a(Tag<Fluid> p_213290_1_, boolean p_213290_2_) {
      if (this.getVehicle() instanceof BoatEntity) {
         return false;
      } else {
         double d0 = this.getEyeY();
         BlockPos blockpos = new BlockPos(this.getX(), d0, this.getZ());
         if (p_213290_2_ && !this.level.hasChunk(blockpos.getX() >> 4, blockpos.getZ() >> 4)) {
            return false;
         } else {
            IFluidState ifluidstate = this.level.getFluidState(blockpos);
            return ifluidstate.isEntityInside(level, blockpos, this, d0, p_213290_1_, true);
         }
      }
   }

   public void func_213292_aB() {
      this.field_213329_S = true;
   }

   public boolean isInLava() {
      return this.field_213329_S;
   }

   public void moveRelative(float p_213309_1_, Vec3d p_213309_2_) {
      Vec3d vec3d = getInputVector(p_213309_2_, p_213309_1_, this.yRot);
      this.setDeltaMovement(this.getDeltaMovement().add(vec3d));
   }

   private static Vec3d getInputVector(Vec3d p_213299_0_, float p_213299_1_, float p_213299_2_) {
      double d0 = p_213299_0_.lengthSqr();
      if (d0 < 1.0E-7D) {
         return Vec3d.ZERO;
      } else {
         Vec3d vec3d = (d0 > 1.0D ? p_213299_0_.normalize() : p_213299_0_).scale((double)p_213299_1_);
         float f = MathHelper.sin(p_213299_2_ * ((float)Math.PI / 180F));
         float f1 = MathHelper.cos(p_213299_2_ * ((float)Math.PI / 180F));
         return new Vec3d(vec3d.x * (double)f1 - vec3d.z * (double)f, vec3d.y, vec3d.z * (double)f1 + vec3d.x * (double)f);
      }
   }

   public float getBrightness() {
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable(this.getX(), 0.0D, this.getZ());
      if (this.level.hasChunkAt(blockpos$mutable)) {
         blockpos$mutable.setY(MathHelper.floor(this.getEyeY()));
         return this.level.getBrightness(blockpos$mutable);
      } else {
         return 0.0F;
      }
   }

   public void setLevel(World p_70029_1_) {
      this.level = p_70029_1_;
   }

   public void absMoveTo(double p_70080_1_, double p_70080_3_, double p_70080_5_, float p_70080_7_, float p_70080_8_) {
      double d0 = MathHelper.clamp(p_70080_1_, -3.0E7D, 3.0E7D);
      double d1 = MathHelper.clamp(p_70080_5_, -3.0E7D, 3.0E7D);
      this.xo = d0;
      this.yo = p_70080_3_;
      this.zo = d1;
      this.setPos(d0, p_70080_3_, d1);
      this.yRot = p_70080_7_ % 360.0F;
      this.xRot = MathHelper.clamp(p_70080_8_, -90.0F, 90.0F) % 360.0F;
      this.yRotO = this.yRot;
      this.xRotO = this.xRot;
   }

   public void moveTo(BlockPos p_174828_1_, float p_174828_2_, float p_174828_3_) {
      this.moveTo((double)p_174828_1_.getX() + 0.5D, (double)p_174828_1_.getY(), (double)p_174828_1_.getZ() + 0.5D, p_174828_2_, p_174828_3_);
   }

   public void moveTo(double p_70012_1_, double p_70012_3_, double p_70012_5_, float p_70012_7_, float p_70012_8_) {
      this.setPosAndOldPos(p_70012_1_, p_70012_3_, p_70012_5_);
      this.yRot = p_70012_7_;
      this.xRot = p_70012_8_;
      this.reapplyPosition();
   }

   public void setPosAndOldPos(double p_226286_1_, double p_226286_3_, double p_226286_5_) {
      this.setPosRaw(p_226286_1_, p_226286_3_, p_226286_5_);
      this.xo = p_226286_1_;
      this.yo = p_226286_3_;
      this.zo = p_226286_5_;
      this.xOld = p_226286_1_;
      this.yOld = p_226286_3_;
      this.zOld = p_226286_5_;
   }

   public float distanceTo(Entity p_70032_1_) {
      float f = (float)(this.getX() - p_70032_1_.getX());
      float f1 = (float)(this.getY() - p_70032_1_.getY());
      float f2 = (float)(this.getZ() - p_70032_1_.getZ());
      return MathHelper.sqrt(f * f + f1 * f1 + f2 * f2);
   }

   public double distanceToSqr(double p_70092_1_, double p_70092_3_, double p_70092_5_) {
      double d0 = this.getX() - p_70092_1_;
      double d1 = this.getY() - p_70092_3_;
      double d2 = this.getZ() - p_70092_5_;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public double distanceToSqr(Entity p_70068_1_) {
      return this.distanceToSqr(p_70068_1_.position());
   }

   public double distanceToSqr(Vec3d p_195048_1_) {
      double d0 = this.getX() - p_195048_1_.x;
      double d1 = this.getY() - p_195048_1_.y;
      double d2 = this.getZ() - p_195048_1_.z;
      return d0 * d0 + d1 * d1 + d2 * d2;
   }

   public void playerTouch(PlayerEntity p_70100_1_) {
   }

   public void push(Entity p_70108_1_) {
      if (!this.isPassengerOfSameVehicle(p_70108_1_)) {
         if (!p_70108_1_.noPhysics && !this.noPhysics) {
            double d0 = p_70108_1_.getX() - this.getX();
            double d1 = p_70108_1_.getZ() - this.getZ();
            double d2 = MathHelper.absMax(d0, d1);
            if (d2 >= (double)0.01F) {
               d2 = (double)MathHelper.sqrt(d2);
               d0 = d0 / d2;
               d1 = d1 / d2;
               double d3 = 1.0D / d2;
               if (d3 > 1.0D) {
                  d3 = 1.0D;
               }

               d0 = d0 * d3;
               d1 = d1 * d3;
               d0 = d0 * (double)0.05F;
               d1 = d1 * (double)0.05F;
               d0 = d0 * (double)(1.0F - this.pushthrough);
               d1 = d1 * (double)(1.0F - this.pushthrough);
               if (!this.isVehicle()) {
                  this.push(-d0, 0.0D, -d1);
               }

               if (!p_70108_1_.isVehicle()) {
                  p_70108_1_.push(d0, 0.0D, d1);
               }
            }

         }
      }
   }

   public void push(double p_70024_1_, double p_70024_3_, double p_70024_5_) {
      this.setDeltaMovement(this.getDeltaMovement().add(p_70024_1_, p_70024_3_, p_70024_5_));
      this.hasImpulse = true;
   }

   protected void markHurt() {
      this.hurtMarked = true;
   }

   public boolean hurt(DamageSource p_70097_1_, float p_70097_2_) {
      if (this.isInvulnerableTo(p_70097_1_)) {
         return false;
      } else {
         this.markHurt();
         return false;
      }
   }

   public final Vec3d getViewVector(float p_70676_1_) {
      return this.calculateViewVector(this.getViewXRot(p_70676_1_), this.getViewYRot(p_70676_1_));
   }

   public float getViewXRot(float p_195050_1_) {
      return p_195050_1_ == 1.0F ? this.xRot : MathHelper.lerp(p_195050_1_, this.xRotO, this.xRot);
   }

   public float getViewYRot(float p_195046_1_) {
      return p_195046_1_ == 1.0F ? this.yRot : MathHelper.lerp(p_195046_1_, this.yRotO, this.yRot);
   }

   protected final Vec3d calculateViewVector(float p_174806_1_, float p_174806_2_) {
      float f = p_174806_1_ * ((float)Math.PI / 180F);
      float f1 = -p_174806_2_ * ((float)Math.PI / 180F);
      float f2 = MathHelper.cos(f1);
      float f3 = MathHelper.sin(f1);
      float f4 = MathHelper.cos(f);
      float f5 = MathHelper.sin(f);
      return new Vec3d((double)(f3 * f4), (double)(-f5), (double)(f2 * f4));
   }

   public final Vec3d getUpVector(float p_213286_1_) {
      return this.calculateUpVector(this.getViewXRot(p_213286_1_), this.getViewYRot(p_213286_1_));
   }

   protected final Vec3d calculateUpVector(float p_213320_1_, float p_213320_2_) {
      return this.calculateViewVector(p_213320_1_ - 90.0F, p_213320_2_);
   }

   public final Vec3d getEyePosition(float p_174824_1_) {
      if (p_174824_1_ == 1.0F) {
         return new Vec3d(this.getX(), this.getEyeY(), this.getZ());
      } else {
         double d0 = MathHelper.lerp((double)p_174824_1_, this.xo, this.getX());
         double d1 = MathHelper.lerp((double)p_174824_1_, this.yo, this.getY()) + (double)this.getEyeHeight();
         double d2 = MathHelper.lerp((double)p_174824_1_, this.zo, this.getZ());
         return new Vec3d(d0, d1, d2);
      }
   }

   public RayTraceResult pick(double p_213324_1_, float p_213324_3_, boolean p_213324_4_) {
      Vec3d vec3d = this.getEyePosition(p_213324_3_);
      Vec3d vec3d1 = this.getViewVector(p_213324_3_);
      Vec3d vec3d2 = vec3d.add(vec3d1.x * p_213324_1_, vec3d1.y * p_213324_1_, vec3d1.z * p_213324_1_);
      return this.level.clip(new RayTraceContext(vec3d, vec3d2, RayTraceContext.BlockMode.OUTLINE, p_213324_4_ ? RayTraceContext.FluidMode.ANY : RayTraceContext.FluidMode.NONE, this));
   }

   public boolean isPickable() {
      return false;
   }

   public boolean isPushable() {
      return false;
   }

   public void awardKillScore(Entity p_191956_1_, int p_191956_2_, DamageSource p_191956_3_) {
      if (p_191956_1_ instanceof ServerPlayerEntity) {
         CriteriaTriggers.ENTITY_KILLED_PLAYER.trigger((ServerPlayerEntity)p_191956_1_, this, p_191956_3_);
      }

   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRender(double p_145770_1_, double p_145770_3_, double p_145770_5_) {
      double d0 = this.getX() - p_145770_1_;
      double d1 = this.getY() - p_145770_3_;
      double d2 = this.getZ() - p_145770_5_;
      double d3 = d0 * d0 + d1 * d1 + d2 * d2;
      return this.shouldRenderAtSqrDistance(d3);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldRenderAtSqrDistance(double p_70112_1_) {
      double d0 = this.getBoundingBox().getSize();
      if (Double.isNaN(d0)) {
         d0 = 1.0D;
      }

      d0 = d0 * 64.0D * viewScale;
      return p_70112_1_ < d0 * d0;
   }

   public boolean saveAsPassenger(CompoundNBT p_184198_1_) {
      String s = this.getEncodeId();
      if (!this.removed && s != null) {
         p_184198_1_.putString("id", s);
         this.saveWithoutId(p_184198_1_);
         return true;
      } else {
         return false;
      }
   }

   public boolean save(CompoundNBT p_70039_1_) {
      return this.isPassenger() ? false : this.saveAsPassenger(p_70039_1_);
   }

   public CompoundNBT saveWithoutId(CompoundNBT p_189511_1_) {
      try {
         p_189511_1_.put("Pos", this.newDoubleList(this.getX(), this.getY(), this.getZ()));
         Vec3d vec3d = this.getDeltaMovement();
         p_189511_1_.put("Motion", this.newDoubleList(vec3d.x, vec3d.y, vec3d.z));
         p_189511_1_.put("Rotation", this.newFloatList(this.yRot, this.xRot));
         p_189511_1_.putFloat("FallDistance", this.fallDistance);
         p_189511_1_.putShort("Fire", (short)this.remainingFireTicks);
         p_189511_1_.putShort("Air", (short)this.getAirSupply());
         p_189511_1_.putBoolean("OnGround", this.onGround);
         p_189511_1_.putInt("Dimension", this.field_71093_bK.func_186068_a());
         p_189511_1_.putBoolean("Invulnerable", this.invulnerable);
         p_189511_1_.putInt("PortalCooldown", this.field_71088_bW);
         p_189511_1_.putUUID("UUID", this.getUUID());
         ITextComponent itextcomponent = this.getCustomName();
         if (itextcomponent != null) {
            p_189511_1_.putString("CustomName", ITextComponent.Serializer.toJson(itextcomponent));
         }

         if (this.isCustomNameVisible()) {
            p_189511_1_.putBoolean("CustomNameVisible", this.isCustomNameVisible());
         }

         if (this.isSilent()) {
            p_189511_1_.putBoolean("Silent", this.isSilent());
         }

         if (this.isNoGravity()) {
            p_189511_1_.putBoolean("NoGravity", this.isNoGravity());
         }

         if (this.glowing) {
            p_189511_1_.putBoolean("Glowing", this.glowing);
         }
         p_189511_1_.putBoolean("CanUpdate", canUpdate);

         if (!this.tags.isEmpty()) {
            ListNBT listnbt = new ListNBT();

            for(String s : this.tags) {
               listnbt.add(StringNBT.valueOf(s));
            }

            p_189511_1_.put("Tags", listnbt);
         }

         CompoundNBT caps = serializeCaps();
         if (caps != null) p_189511_1_.put("ForgeCaps", caps);
         if (persistentData != null) p_189511_1_.put("ForgeData", persistentData);

         this.addAdditionalSaveData(p_189511_1_);
         if (this.isVehicle()) {
            ListNBT listnbt1 = new ListNBT();

            for(Entity entity : this.getPassengers()) {
               CompoundNBT compoundnbt = new CompoundNBT();
               if (entity.saveAsPassenger(compoundnbt)) {
                  listnbt1.add(compoundnbt);
               }
            }

            if (!listnbt1.isEmpty()) {
               p_189511_1_.put("Passengers", listnbt1);
            }
         }

         return p_189511_1_;
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Saving entity NBT");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being saved");
         this.fillCrashReportCategory(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   public void load(CompoundNBT p_70020_1_) {
      try {
         ListNBT listnbt = p_70020_1_.getList("Pos", 6);
         ListNBT listnbt2 = p_70020_1_.getList("Motion", 6);
         ListNBT listnbt3 = p_70020_1_.getList("Rotation", 5);
         double d0 = listnbt2.getDouble(0);
         double d1 = listnbt2.getDouble(1);
         double d2 = listnbt2.getDouble(2);
         this.setDeltaMovement(Math.abs(d0) > 10.0D ? 0.0D : d0, Math.abs(d1) > 10.0D ? 0.0D : d1, Math.abs(d2) > 10.0D ? 0.0D : d2);
         this.setPosAndOldPos(listnbt.getDouble(0), listnbt.getDouble(1), listnbt.getDouble(2));
         this.yRot = listnbt3.getFloat(0);
         this.xRot = listnbt3.getFloat(1);
         this.yRotO = this.yRot;
         this.xRotO = this.xRot;
         this.setYHeadRot(this.yRot);
         this.setYBodyRot(this.yRot);
         this.fallDistance = p_70020_1_.getFloat("FallDistance");
         this.remainingFireTicks = p_70020_1_.getShort("Fire");
         this.setAirSupply(p_70020_1_.getShort("Air"));
         this.onGround = p_70020_1_.getBoolean("OnGround");
         if (p_70020_1_.contains("Dimension")) {
            this.field_71093_bK = DimensionType.func_186069_a(p_70020_1_.getInt("Dimension"));
         }

         this.invulnerable = p_70020_1_.getBoolean("Invulnerable");
         this.field_71088_bW = p_70020_1_.getInt("PortalCooldown");
         if (p_70020_1_.hasUUID("UUID")) {
            this.uuid = p_70020_1_.getUUID("UUID");
            this.stringUUID = this.uuid.toString();
         }

         if (Double.isFinite(this.getX()) && Double.isFinite(this.getY()) && Double.isFinite(this.getZ())) {
            if (Double.isFinite((double)this.yRot) && Double.isFinite((double)this.xRot)) {
               this.reapplyPosition();
               this.setRot(this.yRot, this.xRot);
               if (p_70020_1_.contains("CustomName", 8)) {
                  this.setCustomName(ITextComponent.Serializer.func_150699_a(p_70020_1_.getString("CustomName")));
               }

               this.setCustomNameVisible(p_70020_1_.getBoolean("CustomNameVisible"));
               this.setSilent(p_70020_1_.getBoolean("Silent"));
               this.setNoGravity(p_70020_1_.getBoolean("NoGravity"));
               this.setGlowing(p_70020_1_.getBoolean("Glowing"));
               if (p_70020_1_.contains("ForgeData", 10)) persistentData = p_70020_1_.getCompound("ForgeData");
               if (p_70020_1_.contains("CanUpdate", 99)) this.canUpdate(p_70020_1_.getBoolean("CanUpdate"));
               if (p_70020_1_.contains("ForgeCaps", 10)) deserializeCaps(p_70020_1_.getCompound("ForgeCaps"));
               if (p_70020_1_.contains("Tags", 9)) {
                  this.tags.clear();
                  ListNBT listnbt1 = p_70020_1_.getList("Tags", 8);
                  int i = Math.min(listnbt1.size(), 1024);

                  for(int j = 0; j < i; ++j) {
                     this.tags.add(listnbt1.getString(j));
                  }
               }

               this.readAdditionalSaveData(p_70020_1_);
               if (this.repositionEntityAfterLoad()) {
                  this.reapplyPosition();
               }

            } else {
               throw new IllegalStateException("Entity has invalid rotation");
            }
         } else {
            throw new IllegalStateException("Entity has invalid position");
         }
      } catch (Throwable throwable) {
         CrashReport crashreport = CrashReport.forThrowable(throwable, "Loading entity NBT");
         CrashReportCategory crashreportcategory = crashreport.addCategory("Entity being loaded");
         this.fillCrashReportCategory(crashreportcategory);
         throw new ReportedException(crashreport);
      }
   }

   protected boolean repositionEntityAfterLoad() {
      return true;
   }

   @Nullable
   public final String getEncodeId() {
      EntityType<?> entitytype = this.getType();
      ResourceLocation resourcelocation = EntityType.getKey(entitytype);
      return entitytype.canSerialize() && resourcelocation != null ? resourcelocation.toString() : null;
   }

   protected abstract void readAdditionalSaveData(CompoundNBT p_70037_1_);

   protected abstract void addAdditionalSaveData(CompoundNBT p_213281_1_);

   protected ListNBT newDoubleList(double... p_70087_1_) {
      ListNBT listnbt = new ListNBT();

      for(double d0 : p_70087_1_) {
         listnbt.add(DoubleNBT.valueOf(d0));
      }

      return listnbt;
   }

   protected ListNBT newFloatList(float... p_70049_1_) {
      ListNBT listnbt = new ListNBT();

      for(float f : p_70049_1_) {
         listnbt.add(FloatNBT.valueOf(f));
      }

      return listnbt;
   }

   @Nullable
   public ItemEntity spawnAtLocation(IItemProvider p_199703_1_) {
      return this.spawnAtLocation(p_199703_1_, 0);
   }

   @Nullable
   public ItemEntity spawnAtLocation(IItemProvider p_199702_1_, int p_199702_2_) {
      return this.spawnAtLocation(new ItemStack(p_199702_1_), (float)p_199702_2_);
   }

   @Nullable
   public ItemEntity spawnAtLocation(ItemStack p_199701_1_) {
      return this.spawnAtLocation(p_199701_1_, 0.0F);
   }

   @Nullable
   public ItemEntity spawnAtLocation(ItemStack p_70099_1_, float p_70099_2_) {
      if (p_70099_1_.isEmpty()) {
         return null;
      } else if (this.level.isClientSide) {
         return null;
      } else {
         ItemEntity itementity = new ItemEntity(this.level, this.getX(), this.getY() + (double)p_70099_2_, this.getZ(), p_70099_1_);
         itementity.setDefaultPickUpDelay();
         if (captureDrops() != null) captureDrops().add(itementity);
         else
         this.level.addFreshEntity(itementity);
         return itementity;
      }
   }

   public boolean isAlive() {
      return !this.removed;
   }

   public boolean isInWall() {
      if (this.noPhysics) {
         return false;
      } else {
         try (BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.func_185346_s()) {
            for(int i = 0; i < 8; ++i) {
               int j = MathHelper.floor(this.getY() + (double)(((float)((i >> 0) % 2) - 0.5F) * 0.1F) + (double)this.eyeHeight);
               int k = MathHelper.floor(this.getX() + (double)(((float)((i >> 1) % 2) - 0.5F) * this.dimensions.width * 0.8F));
               int l = MathHelper.floor(this.getZ() + (double)(((float)((i >> 2) % 2) - 0.5F) * this.dimensions.width * 0.8F));
               if (blockpos$pooledmutable.getX() != k || blockpos$pooledmutable.getY() != j || blockpos$pooledmutable.getZ() != l) {
                  blockpos$pooledmutable.set(k, j, l);
                  if (this.level.getBlockState(blockpos$pooledmutable).isSuffocating(this.level, blockpos$pooledmutable)) {
                     boolean flag = true;
                     return flag;
                  }
               }
            }

            return false;
         }
      }
   }

   public boolean interact(PlayerEntity p_184230_1_, Hand p_184230_2_) {
      return false;
   }

   @Nullable
   public AxisAlignedBB func_70114_g(Entity p_70114_1_) {
      return null;
   }

   public void rideTick() {
      this.setDeltaMovement(Vec3d.ZERO);
      if (canUpdate())
      this.tick();
      if (this.isPassenger()) {
         this.getVehicle().positionRider(this);
      }
   }

   public void positionRider(Entity p_184232_1_) {
      this.positionRider(p_184232_1_, Entity::setPos);
   }

   public void positionRider(Entity p_226266_1_, Entity.IMoveCallback p_226266_2_) {
      if (this.hasPassenger(p_226266_1_)) {
         p_226266_2_.accept(p_226266_1_, this.getX(), this.getY() + this.getPassengersRidingOffset() + p_226266_1_.getMyRidingOffset(), this.getZ());
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void onPassengerTurned(Entity p_184190_1_) {
   }

   public double getMyRidingOffset() {
      return 0.0D;
   }

   public double getPassengersRidingOffset() {
      return (double)this.dimensions.height * 0.75D;
   }

   public boolean startRiding(Entity p_184220_1_) {
      return this.startRiding(p_184220_1_, false);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean showVehicleHealth() {
      return this instanceof LivingEntity;
   }

   public boolean startRiding(Entity p_184205_1_, boolean p_184205_2_) {
      for(Entity entity = p_184205_1_; entity.vehicle != null; entity = entity.vehicle) {
         if (entity.vehicle == this) {
            return false;
         }
      }

      if (!net.minecraftforge.event.ForgeEventFactory.canMountEntity(this, p_184205_1_, true)) return false;
      if (p_184205_2_ || this.canRide(p_184205_1_) && p_184205_1_.canAddPassenger(this)) {
         if (this.isPassenger()) {
            this.stopRiding();
         }

         this.vehicle = p_184205_1_;
         this.vehicle.addPassenger(this);
         return true;
      } else {
         return false;
      }
   }

   protected boolean canRide(Entity p_184228_1_) {
      return this.boardingCooldown <= 0;
   }

   protected boolean canEnterPose(Pose p_213298_1_) {
      return this.level.noCollision(this, this.getBoundingBoxForPose(p_213298_1_));
   }

   public void ejectPassengers() {
      for(int i = this.passengers.size() - 1; i >= 0; --i) {
         this.passengers.get(i).stopRiding();
      }

   }

   public void stopRiding() {
      if (this.vehicle != null) {
         Entity entity = this.vehicle;
         if (!net.minecraftforge.event.ForgeEventFactory.canMountEntity(this, entity, false)) return;
         this.vehicle = null;
         entity.removePassenger(this);
      }

   }

   protected void addPassenger(Entity p_184200_1_) {
      if (p_184200_1_.getVehicle() != this) {
         throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
      } else {
         if (!this.level.isClientSide && p_184200_1_ instanceof PlayerEntity && !(this.getControllingPassenger() instanceof PlayerEntity)) {
            this.passengers.add(0, p_184200_1_);
         } else {
            this.passengers.add(p_184200_1_);
         }

      }
   }

   protected void removePassenger(Entity p_184225_1_) {
      if (p_184225_1_.getVehicle() == this) {
         throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
      } else {
         this.passengers.remove(p_184225_1_);
         p_184225_1_.boardingCooldown = 60;
      }
   }

   protected boolean canAddPassenger(Entity p_184219_1_) {
      return this.getPassengers().size() < 1;
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpTo(double p_180426_1_, double p_180426_3_, double p_180426_5_, float p_180426_7_, float p_180426_8_, int p_180426_9_, boolean p_180426_10_) {
      this.setPos(p_180426_1_, p_180426_3_, p_180426_5_);
      this.setRot(p_180426_7_, p_180426_8_);
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpHeadTo(float p_208000_1_, int p_208000_2_) {
      this.setYHeadRot(p_208000_1_);
   }

   public float getPickRadius() {
      return 0.0F;
   }

   public Vec3d getLookAngle() {
      return this.calculateViewVector(this.xRot, this.yRot);
   }

   public Vec2f getRotationVector() {
      return new Vec2f(this.xRot, this.yRot);
   }

   @OnlyIn(Dist.CLIENT)
   public Vec3d getForward() {
      return Vec3d.directionFromRotation(this.getRotationVector());
   }

   public void handleInsidePortal(BlockPos p_181015_1_) {
      if (this.field_71088_bW > 0) {
         this.field_71088_bW = this.getDimensionChangingDelay();
      } else {
         if (!this.level.isClientSide && !p_181015_1_.equals(this.field_181016_an)) {
            this.field_181016_an = new BlockPos(p_181015_1_);
            NetherPortalBlock netherportalblock = (NetherPortalBlock)Blocks.NETHER_PORTAL;
            BlockPattern.PatternHelper blockpattern$patternhelper = NetherPortalBlock.func_181089_f(this.level, this.field_181016_an);
            double d0 = blockpattern$patternhelper.getForwards().getAxis() == Direction.Axis.X ? (double)blockpattern$patternhelper.getFrontTopLeft().getZ() : (double)blockpattern$patternhelper.getFrontTopLeft().getX();
            double d1 = Math.abs(MathHelper.func_181160_c((blockpattern$patternhelper.getForwards().getAxis() == Direction.Axis.X ? this.getZ() : this.getX()) - (double)(blockpattern$patternhelper.getForwards().getClockWise().getAxisDirection() == Direction.AxisDirection.NEGATIVE ? 1 : 0), d0, d0 - (double)blockpattern$patternhelper.getWidth()));
            double d2 = MathHelper.func_181160_c(this.getY() - 1.0D, (double)blockpattern$patternhelper.getFrontTopLeft().getY(), (double)(blockpattern$patternhelper.getFrontTopLeft().getY() - blockpattern$patternhelper.getHeight()));
            this.field_181017_ao = new Vec3d(d1, d2, 0.0D);
            this.field_181018_ap = blockpattern$patternhelper.getForwards();
         }

         this.isInsidePortal = true;
      }
   }

   protected void handleNetherPortal() {
      if (this.level instanceof ServerWorld) {
         int i = this.getPortalWaitTime();
         if (this.isInsidePortal) {
            if (this.level.getServer().isNetherEnabled() && !this.isPassenger() && this.portalTime++ >= i) {
               this.level.getProfiler().push("portal");
               this.portalTime = i;
               this.field_71088_bW = this.getDimensionChangingDelay();
               this.func_212321_a(this.level.dimension.func_186058_p() == DimensionType.field_223228_b_ ? DimensionType.field_223227_a_ : DimensionType.field_223228_b_);
               this.level.getProfiler().pop();
            }

            this.isInsidePortal = false;
         } else {
            if (this.portalTime > 0) {
               this.portalTime -= 4;
            }

            if (this.portalTime < 0) {
               this.portalTime = 0;
            }
         }

         this.processPortalCooldown();
      }
   }

   public int getDimensionChangingDelay() {
      return 300;
   }

   @OnlyIn(Dist.CLIENT)
   public void lerpMotion(double p_70016_1_, double p_70016_3_, double p_70016_5_) {
      this.setDeltaMovement(p_70016_1_, p_70016_3_, p_70016_5_);
   }

   @OnlyIn(Dist.CLIENT)
   public void handleEntityEvent(byte p_70103_1_) {
      switch(p_70103_1_) {
      case 53:
         HoneyBlock.showSlideParticles(this);
      default:
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void animateHurt() {
   }

   public Iterable<ItemStack> getHandSlots() {
      return EMPTY_LIST;
   }

   public Iterable<ItemStack> getArmorSlots() {
      return EMPTY_LIST;
   }

   public Iterable<ItemStack> getAllSlots() {
      return Iterables.concat(this.getHandSlots(), this.getArmorSlots());
   }

   public void setItemSlot(EquipmentSlotType p_184201_1_, ItemStack p_184201_2_) {
   }

   public boolean isOnFire() {
      boolean flag = this.level != null && this.level.isClientSide;
      return !this.func_70045_F() && (this.remainingFireTicks > 0 || flag && this.getSharedFlag(0));
   }

   public boolean isPassenger() {
      return this.getVehicle() != null;
   }

   public boolean isVehicle() {
      return !this.getPassengers().isEmpty();
   }

   @Deprecated //Forge: Use rider sensitive version
   public boolean rideableUnderWater() {
      return true;
   }

   public void setShiftKeyDown(boolean p_226284_1_) {
      this.setSharedFlag(1, p_226284_1_);
   }

   public boolean isShiftKeyDown() {
      return this.getSharedFlag(1);
   }

   public boolean isSteppingCarefully() {
      return this.isShiftKeyDown();
   }

   public boolean isSuppressingBounce() {
      return this.isShiftKeyDown();
   }

   public boolean isDiscrete() {
      return this.isShiftKeyDown();
   }

   public boolean isDescending() {
      return this.isShiftKeyDown();
   }

   public boolean isCrouching() {
      return this.getPose() == Pose.CROUCHING;
   }

   public boolean isSprinting() {
      return this.getSharedFlag(3);
   }

   public void setSprinting(boolean p_70031_1_) {
      this.setSharedFlag(3, p_70031_1_);
   }

   public boolean isSwimming() {
      return this.getSharedFlag(4);
   }

   public boolean isVisuallySwimming() {
      return this.getPose() == Pose.SWIMMING;
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isVisuallyCrawling() {
      return this.isVisuallySwimming() && !this.isInWater();
   }

   public void setSwimming(boolean p_204711_1_) {
      this.setSharedFlag(4, p_204711_1_);
   }

   public boolean isGlowing() {
      return this.glowing || this.level.isClientSide && this.getSharedFlag(6);
   }

   public void setGlowing(boolean p_184195_1_) {
      this.glowing = p_184195_1_;
      if (!this.level.isClientSide) {
         this.setSharedFlag(6, this.glowing);
      }

   }

   public boolean isInvisible() {
      return this.getSharedFlag(5);
   }

   @OnlyIn(Dist.CLIENT)
   public boolean isInvisibleTo(PlayerEntity p_98034_1_) {
      if (p_98034_1_.isSpectator()) {
         return false;
      } else {
         Team team = this.getTeam();
         return team != null && p_98034_1_ != null && p_98034_1_.getTeam() == team && team.canSeeFriendlyInvisibles() ? false : this.isInvisible();
      }
   }

   @Nullable
   public Team getTeam() {
      return this.level.getScoreboard().getPlayersTeam(this.getScoreboardName());
   }

   public boolean isAlliedTo(Entity p_184191_1_) {
      return this.isAlliedTo(p_184191_1_.getTeam());
   }

   public boolean isAlliedTo(Team p_184194_1_) {
      return this.getTeam() != null ? this.getTeam().isAlliedTo(p_184194_1_) : false;
   }

   public void setInvisible(boolean p_82142_1_) {
      this.setSharedFlag(5, p_82142_1_);
   }

   protected boolean getSharedFlag(int p_70083_1_) {
      return (this.entityData.get(DATA_SHARED_FLAGS_ID) & 1 << p_70083_1_) != 0;
   }

   protected void setSharedFlag(int p_70052_1_, boolean p_70052_2_) {
      byte b0 = this.entityData.get(DATA_SHARED_FLAGS_ID);
      if (p_70052_2_) {
         this.entityData.set(DATA_SHARED_FLAGS_ID, (byte)(b0 | 1 << p_70052_1_));
      } else {
         this.entityData.set(DATA_SHARED_FLAGS_ID, (byte)(b0 & ~(1 << p_70052_1_)));
      }

   }

   public int getMaxAirSupply() {
      return 300;
   }

   public int getAirSupply() {
      return this.entityData.get(DATA_AIR_SUPPLY_ID);
   }

   public void setAirSupply(int p_70050_1_) {
      this.entityData.set(DATA_AIR_SUPPLY_ID, p_70050_1_);
   }

   public void func_70077_a(LightningBoltEntity p_70077_1_) {
      ++this.remainingFireTicks;
      if (this.remainingFireTicks == 0) {
         this.setSecondsOnFire(8);
      }

      this.hurt(DamageSource.LIGHTNING_BOLT, 5.0F);
   }

   public void onAboveBubbleCol(boolean p_203002_1_) {
      Vec3d vec3d = this.getDeltaMovement();
      double d0;
      if (p_203002_1_) {
         d0 = Math.max(-0.9D, vec3d.y - 0.03D);
      } else {
         d0 = Math.min(1.8D, vec3d.y + 0.1D);
      }

      this.setDeltaMovement(vec3d.x, d0, vec3d.z);
   }

   public void onInsideBubbleColumn(boolean p_203004_1_) {
      Vec3d vec3d = this.getDeltaMovement();
      double d0;
      if (p_203004_1_) {
         d0 = Math.max(-0.3D, vec3d.y - 0.03D);
      } else {
         d0 = Math.min(0.7D, vec3d.y + 0.06D);
      }

      this.setDeltaMovement(vec3d.x, d0, vec3d.z);
      this.fallDistance = 0.0F;
   }

   public void func_70074_a(LivingEntity p_70074_1_) {
   }

   protected void moveTowardsClosestSpace(double p_213282_1_, double p_213282_3_, double p_213282_5_) {
      BlockPos blockpos = new BlockPos(p_213282_1_, p_213282_3_, p_213282_5_);
      Vec3d vec3d = new Vec3d(p_213282_1_ - (double)blockpos.getX(), p_213282_3_ - (double)blockpos.getY(), p_213282_5_ - (double)blockpos.getZ());
      BlockPos.Mutable blockpos$mutable = new BlockPos.Mutable();
      Direction direction = Direction.UP;
      double d0 = Double.MAX_VALUE;

      for(Direction direction1 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
         blockpos$mutable.set(blockpos).move(direction1);
         if (!this.level.getBlockState(blockpos$mutable).func_224756_o(this.level, blockpos$mutable)) {
            double d1 = vec3d.get(direction1.getAxis());
            double d2 = direction1.getAxisDirection() == Direction.AxisDirection.POSITIVE ? 1.0D - d1 : d1;
            if (d2 < d0) {
               d0 = d2;
               direction = direction1;
            }
         }
      }

      float f = this.random.nextFloat() * 0.2F + 0.1F;
      float f1 = (float)direction.getAxisDirection().getStep();
      Vec3d vec3d1 = this.getDeltaMovement().scale(0.75D);
      if (direction.getAxis() == Direction.Axis.X) {
         this.setDeltaMovement((double)(f1 * f), vec3d1.y, vec3d1.z);
      } else if (direction.getAxis() == Direction.Axis.Y) {
         this.setDeltaMovement(vec3d1.x, (double)(f1 * f), vec3d1.z);
      } else if (direction.getAxis() == Direction.Axis.Z) {
         this.setDeltaMovement(vec3d1.x, vec3d1.y, (double)(f1 * f));
      }

   }

   public void makeStuckInBlock(BlockState p_213295_1_, Vec3d p_213295_2_) {
      this.fallDistance = 0.0F;
      this.stuckSpeedMultiplier = p_213295_2_;
   }

   private static void func_207712_c(ITextComponent p_207712_0_) {
      p_207712_0_.func_211710_a((p_213318_0_) -> {
         p_213318_0_.func_150241_a((ClickEvent)null);
      }).getSiblings().forEach(Entity::func_207712_c);
   }

   public ITextComponent getName() {
      ITextComponent itextcomponent = this.getCustomName();
      if (itextcomponent != null) {
         ITextComponent itextcomponent1 = itextcomponent.func_212638_h();
         func_207712_c(itextcomponent1);
         return itextcomponent1;
      } else {
         return this.getTypeName();
      }
   }

   protected ITextComponent getTypeName() {
      return this.getType().getDescription(); // Forge: Use getter to allow overriding by mods
   }

   public boolean is(Entity p_70028_1_) {
      return this == p_70028_1_;
   }

   public float getYHeadRot() {
      return 0.0F;
   }

   public void setYHeadRot(float p_70034_1_) {
   }

   public void setYBodyRot(float p_181013_1_) {
   }

   public boolean isAttackable() {
      return true;
   }

   public boolean skipAttackInteraction(Entity p_85031_1_) {
      return false;
   }

   public String toString() {
      return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getName().getContents(), this.id, this.level == null ? "~NULL~" : this.level.getLevelData().getLevelName(), this.getX(), this.getY(), this.getZ());
   }

   public boolean isInvulnerableTo(DamageSource p_180431_1_) {
      return this.invulnerable && p_180431_1_ != DamageSource.OUT_OF_WORLD && !p_180431_1_.isCreativePlayer();
   }

   public boolean isInvulnerable() {
      return this.invulnerable;
   }

   public void setInvulnerable(boolean p_184224_1_) {
      this.invulnerable = p_184224_1_;
   }

   public void copyPosition(Entity p_82149_1_) {
      this.moveTo(p_82149_1_.getX(), p_82149_1_.getY(), p_82149_1_.getZ(), p_82149_1_.yRot, p_82149_1_.xRot);
   }

   public void restoreFrom(Entity p_180432_1_) {
      CompoundNBT compoundnbt = p_180432_1_.saveWithoutId(new CompoundNBT());
      compoundnbt.remove("Dimension");
      this.load(compoundnbt);
      this.field_71088_bW = p_180432_1_.field_71088_bW;
      this.field_181016_an = p_180432_1_.field_181016_an;
      this.field_181017_ao = p_180432_1_.field_181017_ao;
      this.field_181018_ap = p_180432_1_.field_181018_ap;
   }

   @Nullable
   public Entity func_212321_a(DimensionType p_212321_1_) {
      return this.changeDimension(p_212321_1_, getServer().getLevel(p_212321_1_).getPortalForcer());
   }
   @Nullable
   public Entity changeDimension(DimensionType p_212321_1_, net.minecraftforge.common.util.ITeleporter teleporter) {
      if (!net.minecraftforge.common.ForgeHooks.onTravelToDimension(this, p_212321_1_)) return null;
      if (!this.level.isClientSide && !this.removed) {
         this.level.getProfiler().push("changeDimension");
         MinecraftServer minecraftserver = this.getServer();
         DimensionType dimensiontype = this.field_71093_bK;
         ServerWorld serverworld = minecraftserver.getLevel(dimensiontype);
         ServerWorld serverworld1 = minecraftserver.getLevel(p_212321_1_);
         this.field_71093_bK = p_212321_1_;
         this.unRide();
         this.level.getProfiler().push("reposition");
         Entity transportedEntity = teleporter.placeEntity(this, serverworld, serverworld1, this.yRot, spawnPortal -> { //Forge: Start vanilla logic
         Vec3d vec3d = this.getDeltaMovement();
         float f = 0.0F;
         BlockPos blockpos;
         if (dimensiontype == DimensionType.field_223229_c_ && p_212321_1_ == DimensionType.field_223227_a_) {
            blockpos = serverworld1.getHeightmapPos(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, serverworld1.func_175694_M());
         } else if (p_212321_1_ == DimensionType.field_223229_c_) {
            blockpos = serverworld1.func_180504_m();
         } else {
            double movementFactor = serverworld.func_201675_m().getMovementFactor() / serverworld1.func_201675_m().getMovementFactor();
            double d0 = this.getX() * movementFactor;
            double d1 = this.getZ() * movementFactor;

            double d3 = Math.min(-2.9999872E7D, serverworld1.getWorldBorder().getMinX() + 16.0D);
            double d4 = Math.min(-2.9999872E7D, serverworld1.getWorldBorder().getMinZ() + 16.0D);
            double d5 = Math.min(2.9999872E7D, serverworld1.getWorldBorder().getMaxX() - 16.0D);
            double d6 = Math.min(2.9999872E7D, serverworld1.getWorldBorder().getMaxZ() - 16.0D);
            d0 = MathHelper.clamp(d0, d3, d5);
            d1 = MathHelper.clamp(d1, d4, d6);
            Vec3d vec3d1 = this.func_181014_aG();
            blockpos = new BlockPos(d0, this.getY(), d1);
            if (spawnPortal) {
            BlockPattern.PortalInfo blockpattern$portalinfo = serverworld1.getPortalForcer().func_222272_a(blockpos, vec3d, this.func_181012_aH(), vec3d1.x, vec3d1.y, this instanceof PlayerEntity);
            if (blockpattern$portalinfo == null) {
               return null;
            }

            blockpos = new BlockPos(blockpattern$portalinfo.pos);
            vec3d = blockpattern$portalinfo.speed;
            f = (float)blockpattern$portalinfo.field_222507_c;
            }
         }

         this.level.getProfiler().popPush("reloading");
         Entity entity = this.getType().create(serverworld1);
         if (entity != null) {
            entity.restoreFrom(this);
            entity.moveTo(blockpos, entity.yRot + f, entity.xRot);
            entity.setDeltaMovement(vec3d);
            serverworld1.addFromAnotherDimension(entity);
         }
            return entity;
         });//Forge: End vanilla logic

         this.remove(false);
         this.level.getProfiler().pop();
         serverworld.resetEmptyTime();
         serverworld1.resetEmptyTime();
         this.level.getProfiler().pop();
         return transportedEntity;
      } else {
         return null;
      }
   }

   public boolean canChangeDimensions() {
      return true;
   }

   public float getBlockExplosionResistance(Explosion p_180428_1_, IBlockReader p_180428_2_, BlockPos p_180428_3_, BlockState p_180428_4_, IFluidState p_180428_5_, float p_180428_6_) {
      return p_180428_6_;
   }

   public boolean shouldBlockExplode(Explosion p_174816_1_, IBlockReader p_174816_2_, BlockPos p_174816_3_, BlockState p_174816_4_, float p_174816_5_) {
      return true;
   }

   public int getMaxFallDistance() {
      return 3;
   }

   public Vec3d func_181014_aG() {
      if (this.field_181017_ao == null) return Vec3d.ZERO;
      return this.field_181017_ao;
   }

   public Direction func_181012_aH() {
      if (this.field_181018_ap == null) return Direction.NORTH;
      return this.field_181018_ap;
   }

   public boolean isIgnoringBlockTriggers() {
      return false;
   }

   public void fillCrashReportCategory(CrashReportCategory p_85029_1_) {
      p_85029_1_.setDetail("Entity Type", () -> {
         return EntityType.getKey(this.getType()) + " (" + this.getClass().getCanonicalName() + ")";
      });
      p_85029_1_.setDetail("Entity ID", this.id);
      p_85029_1_.setDetail("Entity Name", () -> {
         return this.getName().getString();
      });
      p_85029_1_.setDetail("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.getX(), this.getY(), this.getZ()));
      p_85029_1_.setDetail("Entity's Block location", CrashReportCategory.formatLocation(MathHelper.floor(this.getX()), MathHelper.floor(this.getY()), MathHelper.floor(this.getZ())));
      Vec3d vec3d = this.getDeltaMovement();
      p_85029_1_.setDetail("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3d.x, vec3d.y, vec3d.z));
      p_85029_1_.setDetail("Entity's Passengers", () -> {
         return this.getPassengers().toString();
      });
      p_85029_1_.setDetail("Entity's Vehicle", () -> {
         return this.getVehicle().toString();
      });
   }

   @OnlyIn(Dist.CLIENT)
   public boolean displayFireAnimation() {
      return this.isOnFire() && !this.isSpectator();
   }

   public void setUUID(UUID p_184221_1_) {
      this.uuid = p_184221_1_;
      this.stringUUID = this.uuid.toString();
   }

   public UUID getUUID() {
      return this.uuid;
   }

   public String getStringUUID() {
      return this.stringUUID;
   }

   public String getScoreboardName() {
      return this.stringUUID;
   }

   public boolean isPushedByFluid() {
      return true;
   }

   @OnlyIn(Dist.CLIENT)
   public static double getViewScale() {
      return viewScale;
   }

   @OnlyIn(Dist.CLIENT)
   public static void setViewScale(double p_184227_0_) {
      viewScale = p_184227_0_;
   }

   public ITextComponent getDisplayName() {
      return ScorePlayerTeam.func_200541_a(this.getTeam(), this.getName()).func_211710_a((p_211516_1_) -> {
         p_211516_1_.func_150209_a(this.createHoverEvent()).func_179989_a(this.getStringUUID());
      });
   }

   public void setCustomName(@Nullable ITextComponent p_200203_1_) {
      this.entityData.set(DATA_CUSTOM_NAME, Optional.ofNullable(p_200203_1_));
   }

   @Nullable
   public ITextComponent getCustomName() {
      return this.entityData.get(DATA_CUSTOM_NAME).orElse((ITextComponent)null);
   }

   public boolean hasCustomName() {
      return this.entityData.get(DATA_CUSTOM_NAME).isPresent();
   }

   public void setCustomNameVisible(boolean p_174805_1_) {
      this.entityData.set(DATA_CUSTOM_NAME_VISIBLE, p_174805_1_);
   }

   public boolean isCustomNameVisible() {
      return this.entityData.get(DATA_CUSTOM_NAME_VISIBLE);
   }

   public final void teleportToWithTicket(double p_223102_1_, double p_223102_3_, double p_223102_5_) {
      if (this.level instanceof ServerWorld) {
         ChunkPos chunkpos = new ChunkPos(new BlockPos(p_223102_1_, p_223102_3_, p_223102_5_));
         ((ServerWorld)this.level).getChunkSource().addRegionTicket(TicketType.POST_TELEPORT, chunkpos, 0, this.getId());
         this.level.getChunk(chunkpos.x, chunkpos.z);
         this.teleportTo(p_223102_1_, p_223102_3_, p_223102_5_);
      }
   }

   public void teleportTo(double p_70634_1_, double p_70634_3_, double p_70634_5_) {
      if (this.level instanceof ServerWorld) {
         ServerWorld serverworld = (ServerWorld)this.level;
         this.moveTo(p_70634_1_, p_70634_3_, p_70634_5_, this.yRot, this.xRot);
         this.getSelfAndPassengers().forEach((p_226267_1_) -> {
            serverworld.updateChunkPos(p_226267_1_);
            p_226267_1_.forceChunkAddition = true;
            p_226267_1_.func_226265_a_(Entity::moveTo);
         });
      }
   }

   @OnlyIn(Dist.CLIENT)
   public boolean shouldShowName() {
      return this.isCustomNameVisible();
   }

   public void onSyncedDataUpdated(DataParameter<?> p_184206_1_) {
      if (DATA_POSE.equals(p_184206_1_)) {
         this.refreshDimensions();
      }

   }

   public void refreshDimensions() {
      EntitySize entitysize = this.dimensions;
      Pose pose = this.getPose();
      EntitySize entitysize1 = this.getDimensions(pose);
      this.dimensions = entitysize1;
      this.eyeHeight = getEyeHeightForge(pose, entitysize1);
      if (entitysize1.width < entitysize.width) {
         double d0 = (double)entitysize1.width / 2.0D;
         this.setBoundingBox(new AxisAlignedBB(this.getX() - d0, this.getY(), this.getZ() - d0, this.getX() + d0, this.getY() + (double)entitysize1.height, this.getZ() + d0));
      } else {
         AxisAlignedBB axisalignedbb = this.getBoundingBox();
         this.setBoundingBox(new AxisAlignedBB(axisalignedbb.minX, axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX + (double)entitysize1.width, axisalignedbb.minY + (double)entitysize1.height, axisalignedbb.minZ + (double)entitysize1.width));
         if (entitysize1.width > entitysize.width && !this.firstTick && !this.level.isClientSide) {
            float f = entitysize.width - entitysize1.width;
            this.move(MoverType.SELF, new Vec3d((double)f, 0.0D, (double)f));
         }

      }
   }

   public Direction getDirection() {
      return Direction.fromYRot((double)this.yRot);
   }

   public Direction getMotionDirection() {
      return this.getDirection();
   }

   protected HoverEvent createHoverEvent() {
      CompoundNBT compoundnbt = new CompoundNBT();
      ResourceLocation resourcelocation = EntityType.getKey(this.getType());
      compoundnbt.putString("id", this.getStringUUID());
      if (resourcelocation != null) {
         compoundnbt.putString("type", resourcelocation.toString());
      }

      compoundnbt.putString("name", ITextComponent.Serializer.toJson(this.getName()));
      return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new StringTextComponent(compoundnbt.toString()));
   }

   public boolean broadcastToPlayer(ServerPlayerEntity p_174827_1_) {
      return true;
   }

   public AxisAlignedBB getBoundingBox() {
      return this.bb;
   }

   @OnlyIn(Dist.CLIENT)
   public AxisAlignedBB getBoundingBoxForCulling() {
      return this.getBoundingBox();
   }

   protected AxisAlignedBB getBoundingBoxForPose(Pose p_213321_1_) {
      EntitySize entitysize = this.getDimensions(p_213321_1_);
      float f = entitysize.width / 2.0F;
      Vec3d vec3d = new Vec3d(this.getX() - (double)f, this.getY(), this.getZ() - (double)f);
      Vec3d vec3d1 = new Vec3d(this.getX() + (double)f, this.getY() + (double)entitysize.height, this.getZ() + (double)f);
      return new AxisAlignedBB(vec3d, vec3d1);
   }

   public void setBoundingBox(AxisAlignedBB p_174826_1_) {
      this.bb = p_174826_1_;
   }

   protected float getEyeHeight(Pose p_213316_1_, EntitySize p_213316_2_) {
      return p_213316_2_.height * 0.85F;
   }

   @OnlyIn(Dist.CLIENT)
   public float getEyeHeight(Pose p_213307_1_) {
      return this.getEyeHeight(p_213307_1_, this.getDimensions(p_213307_1_));
   }

   public final float getEyeHeight() {
      return this.eyeHeight;
   }

   public boolean setSlot(int p_174820_1_, ItemStack p_174820_2_) {
      return false;
   }

   public void sendMessage(ITextComponent p_145747_1_) {
   }

   public BlockPos func_180425_c() {
      return new BlockPos(this);
   }

   public Vec3d func_174791_d() {
      return this.position();
   }

   public World getCommandSenderWorld() {
      return this.level;
   }

   @Nullable
   public MinecraftServer getServer() {
      return this.level.getServer();
   }

   public ActionResultType interactAt(PlayerEntity p_184199_1_, Vec3d p_184199_2_, Hand p_184199_3_) {
      return ActionResultType.PASS;
   }

   public boolean ignoreExplosion() {
      return false;
   }

   protected void doEnchantDamageEffects(LivingEntity p_174815_1_, Entity p_174815_2_) {
      if (p_174815_2_ instanceof LivingEntity) {
         EnchantmentHelper.doPostHurtEffects((LivingEntity)p_174815_2_, p_174815_1_);
      }

      EnchantmentHelper.doPostDamageEffects(p_174815_1_, p_174815_2_);
   }

   public void startSeenByPlayer(ServerPlayerEntity p_184178_1_) {
   }

   public void stopSeenByPlayer(ServerPlayerEntity p_184203_1_) {
   }

   public float rotate(Rotation p_184229_1_) {
      float f = MathHelper.wrapDegrees(this.yRot);
      switch(p_184229_1_) {
      case CLOCKWISE_180:
         return f + 180.0F;
      case COUNTERCLOCKWISE_90:
         return f + 270.0F;
      case CLOCKWISE_90:
         return f + 90.0F;
      default:
         return f;
      }
   }

   public float mirror(Mirror p_184217_1_) {
      float f = MathHelper.wrapDegrees(this.yRot);
      switch(p_184217_1_) {
      case LEFT_RIGHT:
         return -f;
      case FRONT_BACK:
         return 180.0F - f;
      default:
         return f;
      }
   }

   public boolean onlyOpCanSetNbt() {
      return false;
   }

   public boolean func_184189_br() {
      boolean flag = this.forceChunkAddition;
      this.forceChunkAddition = false;
      return flag;
   }

   @Nullable
   public Entity getControllingPassenger() {
      return null;
   }

   public List<Entity> getPassengers() {
      return (List<Entity>)(this.passengers.isEmpty() ? Collections.emptyList() : Lists.newArrayList(this.passengers));
   }

   public boolean hasPassenger(Entity p_184196_1_) {
      for(Entity entity : this.getPassengers()) {
         if (entity.equals(p_184196_1_)) {
            return true;
         }
      }

      return false;
   }

   public boolean hasPassenger(Class<? extends Entity> p_205708_1_) {
      for(Entity entity : this.getPassengers()) {
         if (p_205708_1_.isAssignableFrom(entity.getClass())) {
            return true;
         }
      }

      return false;
   }

   public Collection<Entity> getIndirectPassengers() {
      Set<Entity> set = Sets.newHashSet();

      for(Entity entity : this.getPassengers()) {
         set.add(entity);
         entity.fillIndirectPassengers(false, set);
      }

      return set;
   }

   public Stream<Entity> getSelfAndPassengers() {
      return Stream.concat(Stream.of(this), this.passengers.stream().flatMap(Entity::getSelfAndPassengers));
   }

   public boolean hasOnePlayerPassenger() {
      Set<Entity> set = Sets.newHashSet();
      this.fillIndirectPassengers(true, set);
      return set.size() == 1;
   }

   private void fillIndirectPassengers(boolean p_200604_1_, Set<Entity> p_200604_2_) {
      for(Entity entity : this.getPassengers()) {
         if (!p_200604_1_ || ServerPlayerEntity.class.isAssignableFrom(entity.getClass())) {
            p_200604_2_.add(entity);
         }

         entity.fillIndirectPassengers(p_200604_1_, p_200604_2_);
      }

   }

   public Entity getRootVehicle() {
      Entity entity;
      for(entity = this; entity.isPassenger(); entity = entity.getVehicle()) {
         ;
      }

      return entity;
   }

   public boolean isPassengerOfSameVehicle(Entity p_184223_1_) {
      return this.getRootVehicle() == p_184223_1_.getRootVehicle();
   }

   public boolean hasIndirectPassenger(Entity p_184215_1_) {
      for(Entity entity : this.getPassengers()) {
         if (entity.equals(p_184215_1_)) {
            return true;
         }

         if (entity.hasIndirectPassenger(p_184215_1_)) {
            return true;
         }
      }

      return false;
   }

   public void func_226265_a_(Entity.IMoveCallback p_226265_1_) {
      for(Entity entity : this.passengers) {
         this.positionRider(entity, p_226265_1_);
      }

   }

   public boolean isControlledByLocalInstance() {
      Entity entity = this.getControllingPassenger();
      if (entity instanceof PlayerEntity) {
         return ((PlayerEntity)entity).isLocalPlayer();
      } else {
         return !this.level.isClientSide;
      }
   }

   @Nullable
   public Entity getVehicle() {
      return this.vehicle;
   }

   public PushReaction getPistonPushReaction() {
      return PushReaction.NORMAL;
   }

   public SoundCategory getSoundSource() {
      return SoundCategory.NEUTRAL;
   }

   protected int getFireImmuneTicks() {
      return 1;
   }

   public CommandSource createCommandSourceStack() {
      return new CommandSource(this, this.position(), this.getRotationVector(), this.level instanceof ServerWorld ? (ServerWorld)this.level : null, this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.level.getServer(), this);
   }

   protected int getPermissionLevel() {
      return 0;
   }

   public boolean hasPermissions(int p_211513_1_) {
      return this.getPermissionLevel() >= p_211513_1_;
   }

   public boolean acceptsSuccess() {
      return this.level.getGameRules().getBoolean(GameRules.RULE_SENDCOMMANDFEEDBACK);
   }

   public boolean acceptsFailure() {
      return true;
   }

   public boolean shouldInformAdmins() {
      return true;
   }

   public void lookAt(EntityAnchorArgument.Type p_200602_1_, Vec3d p_200602_2_) {
      Vec3d vec3d = p_200602_1_.apply(this);
      double d0 = p_200602_2_.x - vec3d.x;
      double d1 = p_200602_2_.y - vec3d.y;
      double d2 = p_200602_2_.z - vec3d.z;
      double d3 = (double)MathHelper.sqrt(d0 * d0 + d2 * d2);
      this.xRot = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(d1, d3) * (double)(180F / (float)Math.PI))));
      this.yRot = MathHelper.wrapDegrees((float)(MathHelper.atan2(d2, d0) * (double)(180F / (float)Math.PI)) - 90.0F);
      this.setYHeadRot(this.yRot);
      this.xRotO = this.xRot;
      this.yRotO = this.yRot;
   }

   public boolean updateFluidHeightAndDoFluidPushing(Tag<Fluid> p_210500_1_) {
      AxisAlignedBB axisalignedbb = this.getBoundingBox().deflate(0.001D);
      int i = MathHelper.floor(axisalignedbb.minX);
      int j = MathHelper.ceil(axisalignedbb.maxX);
      int k = MathHelper.floor(axisalignedbb.minY);
      int l = MathHelper.ceil(axisalignedbb.maxY);
      int i1 = MathHelper.floor(axisalignedbb.minZ);
      int j1 = MathHelper.ceil(axisalignedbb.maxZ);
      if (!this.level.hasChunksAt(i, k, i1, j, l, j1)) {
         return false;
      } else {
         double d0 = 0.0D;
         boolean flag = this.isPushedByFluid();
         boolean flag1 = false;
         Vec3d vec3d = Vec3d.ZERO;
         int k1 = 0;

         try (BlockPos.PooledMutable blockpos$pooledmutable = BlockPos.PooledMutable.func_185346_s()) {
            for(int l1 = i; l1 < j; ++l1) {
               for(int i2 = k; i2 < l; ++i2) {
                  for(int j2 = i1; j2 < j1; ++j2) {
                     blockpos$pooledmutable.set(l1, i2, j2);
                     IFluidState ifluidstate = this.level.getFluidState(blockpos$pooledmutable);
                     if (ifluidstate.is(p_210500_1_)) {
                        double d1 = (double)((float)i2 + ifluidstate.getHeight(this.level, blockpos$pooledmutable));
                        if (d1 >= axisalignedbb.minY) {
                           flag1 = true;
                           d0 = Math.max(d1 - axisalignedbb.minY, d0);
                           if (flag) {
                              Vec3d vec3d1 = ifluidstate.getFlow(this.level, blockpos$pooledmutable);
                              if (d0 < 0.4D) {
                                 vec3d1 = vec3d1.scale(d0);
                              }

                              vec3d = vec3d.add(vec3d1);
                              ++k1;
                           }
                        }
                     }
                  }
               }
            }
         }

         if (vec3d.length() > 0.0D) {
            if (k1 > 0) {
               vec3d = vec3d.scale(1.0D / (double)k1);
            }

            if (!(this instanceof PlayerEntity)) {
               vec3d = vec3d.normalize();
            }

            this.setDeltaMovement(this.getDeltaMovement().add(vec3d.scale(0.014D)));
         }

         this.field_211517_W = d0;
         return flag1;
      }
   }

   public double func_212107_bY() {
      return this.field_211517_W;
   }

   public final float getBbWidth() {
      return this.dimensions.width;
   }

   public final float getBbHeight() {
      return this.dimensions.height;
   }

   public abstract IPacket<?> getAddEntityPacket();

   public EntitySize getDimensions(Pose p_213305_1_) {
      return this.type.getDimensions();
   }

   public Vec3d position() {
      return new Vec3d(this.field_70165_t, this.field_70163_u, this.field_70161_v);
   }

   public Vec3d getDeltaMovement() {
      return this.deltaMovement;
   }

   public void setDeltaMovement(Vec3d p_213317_1_) {
      this.deltaMovement = p_213317_1_;
   }

   public void setDeltaMovement(double p_213293_1_, double p_213293_3_, double p_213293_5_) {
      this.setDeltaMovement(new Vec3d(p_213293_1_, p_213293_3_, p_213293_5_));
   }

   public final double getX() {
      return this.field_70165_t;
   }

   public double getX(double p_226275_1_) {
      return this.field_70165_t + (double)this.getBbWidth() * p_226275_1_;
   }

   public double getRandomX(double p_226282_1_) {
      return this.getX((2.0D * this.random.nextDouble() - 1.0D) * p_226282_1_);
   }

   public final double getY() {
      return this.field_70163_u;
   }

   public double getY(double p_226283_1_) {
      return this.field_70163_u + (double)this.getBbHeight() * p_226283_1_;
   }

   public double getRandomY() {
      return this.getY(this.random.nextDouble());
   }

   public double getEyeY() {
      return this.field_70163_u + (double)this.eyeHeight;
   }

   public final double getZ() {
      return this.field_70161_v;
   }

   public double getZ(double p_226285_1_) {
      return this.field_70161_v + (double)this.getBbWidth() * p_226285_1_;
   }

   public double getRandomZ(double p_226287_1_) {
      return this.getZ((2.0D * this.random.nextDouble() - 1.0D) * p_226287_1_);
   }

   public void setPosRaw(double p_226288_1_, double p_226288_3_, double p_226288_5_) {
      this.field_70165_t = p_226288_1_;
      this.field_70163_u = p_226288_3_;
      this.field_70161_v = p_226288_5_;
      if (this.isAddedToWorld() && !this.level.isClientSide && !this.removed) this.level.getChunk((int) Math.floor(this.field_70165_t) >> 4, (int) Math.floor(this.field_70161_v) >> 4); // Forge - ensure target chunk is loaded.
   }

   public void checkDespawn() {
   }

   public void moveTo(double p_225653_1_, double p_225653_3_, double p_225653_5_) {
      this.moveTo(p_225653_1_, p_225653_3_, p_225653_5_, this.yRot, this.xRot);
   }

   @FunctionalInterface
   public interface IMoveCallback {
      void accept(Entity p_accept_1_, double p_accept_2_, double p_accept_4_, double p_accept_6_);
   }

   /* ================================== Forge Start =====================================*/

   private boolean canUpdate = true;
   @Override
   public void canUpdate(boolean value) {
      this.canUpdate = value;
   }
   @Override
   public boolean canUpdate() {
      return this.canUpdate;
   }
   private Collection<ItemEntity> captureDrops = null;
   @Override
   public Collection<ItemEntity> captureDrops() {
      return captureDrops;
   }
   @Override
   public Collection<ItemEntity> captureDrops(Collection<ItemEntity> value) {
      Collection<ItemEntity> ret = captureDrops;
      this.captureDrops = value;
      return ret;
   }
   private CompoundNBT persistentData;
   @Override
   public CompoundNBT getPersistentData() {
      if (persistentData == null)
         persistentData = new CompoundNBT();
      return persistentData;
   }
   @Override
   public boolean canTrample(BlockState state, BlockPos pos, float fallDistance) {
      return level.random.nextFloat() < fallDistance - 0.5F
              && this instanceof LivingEntity
              && (this instanceof PlayerEntity || net.minecraftforge.event.ForgeEventFactory.getMobGriefingEvent(level, this))
              && this.getBbWidth() * this.getBbWidth() * this.getBbHeight() > 0.512F;
   }

   /**
    * Internal use for keeping track of entities that are tracked by a world, to
    * allow guarantees that entity position changes will force a chunk load, avoiding
    * potential issues with entity desyncing and bad chunk data.
    */
   private boolean isAddedToWorld;

   @Override
   public final boolean isAddedToWorld() { return this.isAddedToWorld; }

   @Override
   public void onAddedToWorld() { this.isAddedToWorld = true; }

   @Override
   public void onRemovedFromWorld() { this.isAddedToWorld = false; }

   @Override
   public void revive() {
      this.removed = false;
      this.reviveCaps();
   }

   private float getEyeHeightForge(Pose pose, EntitySize size) {
      net.minecraftforge.event.entity.EntityEvent.EyeHeight evt = new net.minecraftforge.event.entity.EntityEvent.EyeHeight(this, pose, size, this.getEyeHeight(pose, size));
      net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(evt);
      return evt.getNewHeight();
   }
}
