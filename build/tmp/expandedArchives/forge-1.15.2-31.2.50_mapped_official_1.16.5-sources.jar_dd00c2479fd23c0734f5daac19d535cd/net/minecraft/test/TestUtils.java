package net.minecraft.test;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Streams;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LecternBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.DebugPacketSender;
import net.minecraft.tileentity.StructureBlockTileEntity;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.server.ServerWorld;

public class TestUtils {
   public static ITestLogger TEST_REPORTER = new TestLogger();

   public static void func_229542_a_(TestTracker p_229542_0_, TestCollection p_229542_1_) {
      p_229542_0_.startExecution();
      p_229542_1_.add(p_229542_0_);
      p_229542_0_.addListener(new ITestCallback() {
         public void testStructureLoaded(TestTracker p_225644_1_) {
            TestUtils.spawnBeacon(p_225644_1_, Blocks.LIGHT_GRAY_STAINED_GLASS);
         }

         public void testFailed(TestTracker p_225645_1_) {
            TestUtils.spawnBeacon(p_225645_1_, p_225645_1_.isRequired() ? Blocks.RED_STAINED_GLASS : Blocks.ORANGE_STAINED_GLASS);
            TestUtils.spawnLectern(p_225645_1_, Util.describeError(p_225645_1_.getError()));
            TestUtils.visualizeFailedTest(p_225645_1_);
         }
      });
      p_229542_0_.func_229502_a_(2);
   }

   public static Collection<TestTracker> func_229549_a_(Collection<TestBatch> p_229549_0_, BlockPos p_229549_1_, ServerWorld p_229549_2_, TestCollection p_229549_3_) {
      TestExecutor testexecutor = new TestExecutor(p_229549_0_, p_229549_1_, p_229549_2_, p_229549_3_);
      testexecutor.start();
      return testexecutor.getTestInfos();
   }

   public static Collection<TestTracker> func_229561_b_(Collection<TestFunctionInfo> p_229561_0_, BlockPos p_229561_1_, ServerWorld p_229561_2_, TestCollection p_229561_3_) {
      return func_229549_a_(groupTestsIntoBatches(p_229561_0_), p_229561_1_, p_229561_2_, p_229561_3_);
   }

   public static Collection<TestBatch> groupTestsIntoBatches(Collection<TestFunctionInfo> p_229548_0_) {
      Map<String, Collection<TestFunctionInfo>> map = Maps.newHashMap();
      p_229548_0_.forEach((p_229551_1_) -> {
         String s = p_229551_1_.getBatchName();
         Collection<TestFunctionInfo> collection = map.computeIfAbsent(s, (p_229543_0_) -> {
            return Lists.newArrayList();
         });
         collection.add(p_229551_1_);
      });
      return map.keySet().stream().flatMap((p_229550_1_) -> {
         Collection<TestFunctionInfo> collection = map.get(p_229550_1_);
         Consumer<ServerWorld> consumer = TestRegistry.getBeforeBatchFunction(p_229550_1_);
         AtomicInteger atomicinteger = new AtomicInteger();
         return Streams.stream(Iterables.partition(collection, 100)).map((p_229545_4_) -> {
            return new TestBatch(p_229550_1_ + ":" + atomicinteger.incrementAndGet(), collection, consumer);
         });
      }).collect(Collectors.toList());
   }

   private static void visualizeFailedTest(TestTracker p_229563_0_) {
      Throwable throwable = p_229563_0_.getError();
      String s = p_229563_0_.getTestName() + " failed! " + Util.describeError(throwable);
      say(p_229563_0_.getLevel(), TextFormatting.RED, s);
      if (throwable instanceof TestBlockPosException) {
         TestBlockPosException testblockposexception = (TestBlockPosException)throwable;
         showRedBox(p_229563_0_.getLevel(), testblockposexception.getAbsolutePos(), testblockposexception.getMessageToShowAtBlock());
      }

      TEST_REPORTER.onTestFailed(p_229563_0_);
   }

   private static void spawnBeacon(TestTracker p_229559_0_, Block p_229559_1_) {
      ServerWorld serverworld = p_229559_0_.getLevel();
      BlockPos blockpos = p_229559_0_.getStructureBlockPos();
      BlockPos blockpos1 = blockpos.offset(-1, -1, -1);
      serverworld.setBlockAndUpdate(blockpos1, Blocks.BEACON.defaultBlockState());
      BlockPos blockpos2 = blockpos1.offset(0, 1, 0);
      serverworld.setBlockAndUpdate(blockpos2, p_229559_1_.defaultBlockState());

      for(int i = -1; i <= 1; ++i) {
         for(int j = -1; j <= 1; ++j) {
            BlockPos blockpos3 = blockpos1.offset(i, -1, j);
            serverworld.setBlockAndUpdate(blockpos3, Blocks.IRON_BLOCK.defaultBlockState());
         }
      }

   }

   private static void spawnLectern(TestTracker p_229560_0_, String p_229560_1_) {
      ServerWorld serverworld = p_229560_0_.getLevel();
      BlockPos blockpos = p_229560_0_.getStructureBlockPos();
      BlockPos blockpos1 = blockpos.offset(-1, 1, -1);
      serverworld.setBlockAndUpdate(blockpos1, Blocks.LECTERN.defaultBlockState());
      BlockState blockstate = serverworld.getBlockState(blockpos1);
      ItemStack itemstack = createBook(p_229560_0_.getTestName(), p_229560_0_.isRequired(), p_229560_1_);
      LecternBlock.tryPlaceBook(serverworld, blockpos1, blockstate, itemstack);
   }

   private static ItemStack createBook(String p_229546_0_, boolean p_229546_1_, String p_229546_2_) {
      ItemStack itemstack = new ItemStack(Items.WRITABLE_BOOK);
      ListNBT listnbt = new ListNBT();
      StringBuffer stringbuffer = new StringBuffer();
      Arrays.stream(p_229546_0_.split("\\.")).forEach((p_229547_1_) -> {
         stringbuffer.append(p_229547_1_).append('\n');
      });
      if (!p_229546_1_) {
         stringbuffer.append("(optional)\n");
      }

      stringbuffer.append("-------------------\n");
      listnbt.add(StringNBT.valueOf(stringbuffer.toString() + p_229546_2_));
      itemstack.addTagElement("pages", listnbt);
      return itemstack;
   }

   private static void say(ServerWorld p_229556_0_, TextFormatting p_229556_1_, String p_229556_2_) {
      p_229556_0_.getPlayers((p_229557_0_) -> {
         return true;
      }).forEach((p_229544_2_) -> {
         p_229544_2_.sendMessage((new StringTextComponent(p_229556_2_)).func_211708_a(p_229556_1_));
      });
   }

   public static void clearMarkers(ServerWorld p_229552_0_) {
      DebugPacketSender.sendGameTestClearPacket(p_229552_0_);
   }

   private static void showRedBox(ServerWorld p_229554_0_, BlockPos p_229554_1_, String p_229554_2_) {
      DebugPacketSender.sendGameTestAddMarker(p_229554_0_, p_229554_1_, p_229554_2_, -2130771968, Integer.MAX_VALUE);
   }

   public static void clearAllTests(ServerWorld p_229555_0_, BlockPos p_229555_1_, TestCollection p_229555_2_, int p_229555_3_) {
      p_229555_2_.clear();
      BlockPos blockpos = p_229555_1_.offset(-p_229555_3_, 0, -p_229555_3_);
      BlockPos blockpos1 = p_229555_1_.offset(p_229555_3_, 0, p_229555_3_);
      BlockPos.betweenClosedStream(blockpos, blockpos1).filter((p_229562_1_) -> {
         return p_229555_0_.getBlockState(p_229562_1_).getBlock() == Blocks.STRUCTURE_BLOCK;
      }).forEach((p_229553_1_) -> {
         StructureBlockTileEntity structureblocktileentity = (StructureBlockTileEntity)p_229555_0_.getBlockEntity(p_229553_1_);
         BlockPos blockpos2 = structureblocktileentity.getBlockPos();
         MutableBoundingBox mutableboundingbox = StructureHelper.getStructureBoundingBox(blockpos2, structureblocktileentity.getStructureSize(), 2);
         StructureHelper.clearSpaceForStructure(mutableboundingbox, blockpos2.getY(), p_229555_0_);
      });
   }
}