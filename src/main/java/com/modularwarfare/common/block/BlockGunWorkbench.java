package com.modularwarfare.common.block;

import com.google.common.collect.Lists;
import com.modularwarfare.ModularWarfare;
import com.modularwarfare.common.type.BaseType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockButtonWood;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.List;

public class BlockGunWorkbench extends BlockHorizontal {
    public static AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(
            -0.5D,
            0.0D,
            0.0D,
            1.5D,
            2.0D,
            1.0D
    );
    public static AxisAlignedBB BOUNDING_BOX_WE = new AxisAlignedBB(
            0.0D,
            0.0D,
            -0.5D,
            1.0D,
            2.0D,
            1.5D
    );

    public BlockGunWorkbench() {
        super(Material.WOOD);
        this.setResistance(10.0F);
        this.setHardness(2.0F);
        setRegistryName("gun_workbench");
        setUnlocalizedName("gun_workbench");
        setCreativeTab(CreativeTabs.DECORATIONS);

        ItemBlock itemBlock = new ItemBlock(this);
        itemBlock.setRegistryName(this.getRegistryName());
        ForgeRegistries.BLOCKS.register(this);
        ForgeRegistries.ITEMS.register(itemBlock);

        registerModels();
    }

    @Override
    public boolean isFullCube(IBlockState state) {
        return false;
    }

    @Override
    public boolean isOpaqueCube(IBlockState state) {
        return false;
    }

    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos) {
        EnumFacing face = state.getValue(BlockHorizontal.FACING);
        switch(face) {
            case EAST:
            case WEST:
                return BOUNDING_BOX_WE;

            default: return BOUNDING_BOX;
        }
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World worldIn, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, Entity entityIn, boolean customArgument) {
        EnumFacing facing = state.getValue(BlockHorizontal.FACING);
        switch (facing) {
            case NORTH:
            case EAST:
            case WEST:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOX_WE);
                break;
            default:
                addCollisionBoxToList(pos, entityBox, collidingBoxes, BOUNDING_BOX);
                break;
        }
    }

    private void registerModels() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
        if (!world.isRemote) {
            player.openGui(ModularWarfare.INSTANCE, 1, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public BlockFaceShape getBlockFaceShape(IBlockAccess world, IBlockState state, BlockPos pos, EnumFacing face) {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer) {
        EnumFacing playerFacing = placer.getHorizontalFacing().getOpposite(); // Get the opposite facing of the player
        return this.getDefaultState().withProperty(BlockHorizontal.FACING, playerFacing);
    }

    @Override
    protected BlockStateContainer createBlockState() {
        return new BlockStateContainer(this, BlockHorizontal.FACING);
    }

    @Override
    public IBlockState getStateFromMeta(int meta) {
        EnumFacing facing = EnumFacing.getHorizontal(meta);
        return getDefaultState().withProperty(BlockHorizontal.FACING, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        EnumFacing facing = state.getValue(BlockHorizontal.FACING);
        return facing.getHorizontalIndex();
    }

    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end) {
        AxisAlignedBB boundingBox = this.getBoundingBox(state, world, pos);
        RayTraceResult result = boundingBox.calculateIntercept(start, end);

        return result != null ? result : super.collisionRayTrace(state, world, pos, start, end);
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune) {
        super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        if (!worldIn.isRemote) {
            ItemStack itemstack = new ItemStack(Item.getItemFromBlock(this), 0);
            spawnAsEntity(worldIn, pos, itemstack);
        }
    }
}
