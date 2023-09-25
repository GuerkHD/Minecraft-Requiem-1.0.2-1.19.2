package net.guerkhd.minecraftrequiem.block;

import net.guerkhd.minecraftrequiem.MinecraftRequiem;
import net.guerkhd.minecraftrequiem.item.ModCreativeModeTab;
import net.guerkhd.minecraftrequiem.item.ModItems;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DropExperienceBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

public class ModBlocks
{
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MinecraftRequiem.MOD_ID);

    public static final RegistryObject<Block> GUERK_BLOCK = registerBlock("guerk_block",
            () -> new Block(BlockBehaviour
                    .Properties.of(Material.STONE)
                    .strength(6f)
                    .requiresCorrectToolForDrops()), ModCreativeModeTab.MINECRAFTREQUIEM_TAB);

    public static final RegistryObject<Block> GUERK_ORE = registerBlock("guerk_ore",
            () -> new DropExperienceBlock(BlockBehaviour
                    .Properties.of(Material.STONE)
                    .strength(6f)
                    .requiresCorrectToolForDrops(),
                    UniformInt.of(1, 3)), ModCreativeModeTab.MINECRAFTREQUIEM_TAB);
    public static final RegistryObject<Block> DEEPSLATE_GUERK_ORE = registerBlock("deepslate_guerk_ore",
            () -> new DropExperienceBlock(BlockBehaviour
                    .Properties.of(Material.STONE)
                    .strength(6f)
                    .requiresCorrectToolForDrops(),
                    UniformInt.of(1, 3)), ModCreativeModeTab.MINECRAFTREQUIEM_TAB);


    public static final RegistryObject<Block> EDIBLE_GOLD_BLOCK = registerBlock("edible_gold_block",
            () -> new Block(BlockBehaviour
                    .Properties.of(Material.STONE)
                    .strength(6f)
                    .requiresCorrectToolForDrops()), ModCreativeModeTab.MINECRAFTREQUIEM_TAB);

    private static <T extends Block> RegistryObject<T> registerBlock(String name, Supplier<T> block, CreativeModeTab tab)
    {
        RegistryObject<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn, tab);
        return toReturn;
    }

    private static <T extends Block> RegistryObject<Item> registerBlockItem(String name, RegistryObject<T> block, CreativeModeTab tab)
    {
        return ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties().tab(tab)));
    }

    public static void register(IEventBus eventBus)
    {
        BLOCKS.register(eventBus);
    }
}
