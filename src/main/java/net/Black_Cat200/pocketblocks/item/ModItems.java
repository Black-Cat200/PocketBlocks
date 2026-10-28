package net.Black_Cat200.pocketblocks.item;

import net.Black_Cat200.pocketblocks.PocketblocksMod;
import net.Black_Cat200.pocketblocks.item.custom.PocketBlocks;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS =
            DeferredRegister.createItems(PocketblocksMod.MODID);

    public static final DeferredItem<Item> POCKET_BLOCKS  = ITEMS.register("pocket_blocks",
            () -> new PocketBlocks(new Item.Properties().durability(32)));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);

    }
}

