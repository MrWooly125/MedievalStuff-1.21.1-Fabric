package net.mrwooly357.medievalstuff.block.custom.heaters;

import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.*;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.mrwooly357.medievalstuff.block.entity.custom.heaters.AbstractHeaterBlockEntity;
import net.mrwooly357.medievalstuff.block.entity.custom.heaters.AbstractHeaterLevel1BlockEntity;
import net.mrwooly357.medievalstuff.util.ModTags;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractHeaterBlock extends BlockWithEntity implements BlockEntityProvider {
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final BooleanProperty LIT = Properties.LIT;

    protected AbstractHeaterBlock(Settings settings) {
        super(settings);
    }


    @Override
    protected ItemActionResult onUseWithItem(ItemStack stack, BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!state.get(LIT)) {

                if (stack.isIn(ModTags.Items.HEATER_ARSONISTS)) {

                    if (world.getBlockEntity(pos) instanceof AbstractHeaterBlockEntity blockEntity) {

                        for (int slot = 0; slot < blockEntity.size(); slot++) {

                            if (!blockEntity.getStack(slot).isEmpty()) {

                                world.setBlockState(pos, state.with(LIT, true));
                                if (stack.isOf(Items.FLINT_AND_STEEL)) {

                                    stack.damage(1, player, EquipmentSlot.MAINHAND);
                                } else {
                                    stack.decrementUnlessCreative(1, player);
                                }

                                world.playSound(player, pos, SoundEvents.ITEM_FLINTANDSTEEL_USE, SoundCategory.PLAYERS, 1.0F, 1.0F);

                                return ItemActionResult.SUCCESS;
                            }
                        }
                    }
                }
            } else if (stack.isIn(ItemTags.SHOVELS)) {

            world.setBlockState(pos, state.with(LIT, false));
            stack.damage(1, player, EquipmentSlot.MAINHAND);
            world.playSound(player, pos, SoundEvents.BLOCK_FIRE_EXTINGUISH, SoundCategory.PLAYERS, 1.0F, 1.0F);

            if (world.getBlockEntity(pos) instanceof AbstractHeaterBlockEntity blockEntity) {
                blockEntity.setBurnTime(0);
            }
            return ItemActionResult.SUCCESS;
        }

        return super.onUseWithItem(stack, state, world, pos, player, hand, hit);
    }

    /**
     * Defines the codec of heater.
     * @return Heater's codec.
     *
     * <p>Note: every heater should have a unique codec.
     */
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return null;
    }

    /**
     * Defines the block entity attached to the heater.
     * @param pos Heater's position.
     * @param state Heater's state.
     * @return Block entity of this heater.
     */
    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return null;
    }

    /**
     * This method defines how the game should render the block.
     * @param state Defines the heater's state.
     * @return The way game should render the heater.
     */
    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    /**
     * In this case it's used to drop heater's inventory when it is broken.
     * @param state Heater's last state.
     * @param world The world where this happens.
     * @param pos Heater's block position.
     * @param newState Heater's new state.
     */
    @Override
    protected void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity instanceof AbstractHeaterLevel1BlockEntity) {
                ItemScatterer.spawn(world, pos, ((AbstractHeaterLevel1BlockEntity) blockEntity));
                world.updateComparators(pos, this);
            }

            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    /**
     * Defines which state heater should have on placement.
     * @param ctx Context of action.
     * @return The state heater should have when placed as a block.
     */
    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getHorizontalPlayerFacing().getOpposite()).with(LIT, false);
    }

    @Override
    protected BlockState rotate(BlockState state, BlockRotation rotation) {
        return state.with(FACING, rotation.rotate(state.get(FACING)));
    }

    @Override
    protected BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation(state.get(FACING)));
    }

    /**
     * Adds properties to heater like FACING or LIT and etc
     * @param builder Used to add properties.
     */
    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, LIT);
    }

    @Override
    public void onSteppedOn(World world, BlockPos pos, BlockState state, Entity entity) {
        if (!world.isClient && entity instanceof LivingEntity && state.get(LIT)) {
            entity.damage(entity.getDamageSources().inFire(), 1.0F);
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if (state.get(LIT)) {
            double d = (double)pos.getX() + 0.5;
            double e = pos.getY();
            double f = (double)pos.getZ() + 0.5;
            if (random.nextDouble() < 0.1) {
                world.playSound(d, e, f, SoundEvents.BLOCK_BLASTFURNACE_FIRE_CRACKLE, SoundCategory.BLOCKS, 1.0F, 1.0F, false);
            }

            Direction direction = state.get(FACING);
            Direction.Axis axis = direction.getAxis();
            double h = random.nextDouble() * 0.6 - 0.3;
            double i = axis == Direction.Axis.X ? (double)direction.getOffsetX() * 0.52 : h;
            double j = random.nextDouble() * 9.0 / 16.0;
            double k = axis == Direction.Axis.Z ? (double)direction.getOffsetZ() * 0.52 : h;
            world.addParticle(ParticleTypes.SMOKE, d + i, e + j + 0.4, f + k, 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected boolean hasComparatorOutput(BlockState state) {
        return true;
    }

    @Override
    protected int getComparatorOutput(BlockState state, World world, BlockPos pos) {
        return ScreenHandler.calculateComparatorOutput(world.getBlockEntity(pos));
    }
}