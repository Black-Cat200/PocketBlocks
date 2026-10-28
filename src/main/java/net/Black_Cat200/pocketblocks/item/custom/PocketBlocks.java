package net.Black_Cat200.pocketblocks.item.custom;

import net.Black_Cat200.pocketblocks.client.screen.PocketBlocksScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class PocketBlocks extends Item {
    public PocketBlocks(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);

        if (level.isClientSide()) {
            openTetrisScreen();
        }

        return InteractionResultHolder.sidedSuccess(itemStack, level.isClientSide());
    }

    private void openTetrisScreen() {
        // Используем отложенное открытие экрана, чтобы не вызывать клиентский код на сервере напрямую
        Minecraft.getInstance().setScreen(new PocketBlocksScreen(Component.literal("Tetris")));
    }
}