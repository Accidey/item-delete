package com.xulai.itemdelete.command;

import java.util.List;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.xulai.itemdelete.BlockedItems;

import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

public final class ItemDeleteCommand {
    private ItemDeleteCommand() {
    }

    public static void register(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();
        dispatcher.register(Commands.literal("itemdelete")
                .requires(Commands.hasPermission(Commands.LEVEL_GAMEMASTERS))
                .then(Commands.literal("add").executes(ItemDeleteCommand::add))
                .then(Commands.literal("remove").executes(ItemDeleteCommand::remove))
                .then(Commands.literal("list").executes(ItemDeleteCommand::list)));
    }

    private static int add(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ItemStack stack = source.getPlayerOrException().getMainHandItem();
        if (stack.isEmpty()) {
            source.sendFailure(Component.translatable("command.itemdelete.empty_hand"));
            return 0;
        }
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!BlockedItems.addEntry(id)) {
            source.sendFailure(Component.translatable("command.itemdelete.already", id.toString()));
            return 0;
        }
        source.sendSuccess(() -> Component.translatable("command.itemdelete.added", id.toString()), true);
        source.sendSuccess(() -> Component.translatable("command.itemdelete.reload_hint"), false);
        return 1;
    }

    private static int remove(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        CommandSourceStack source = context.getSource();
        ItemStack stack = source.getPlayerOrException().getMainHandItem();
        if (stack.isEmpty()) {
            source.sendFailure(Component.translatable("command.itemdelete.empty_hand"));
            return 0;
        }
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (!BlockedItems.removeEntry(id)) {
            String wildcard = BlockedItems.findNamespaceWildcard(id);
            if (wildcard == null) {
                source.sendFailure(Component.translatable("command.itemdelete.missing", id.toString()));
            } else {
                source.sendFailure(Component.translatable("command.itemdelete.covered", id.toString(), wildcard));
            }
            return 0;
        }
        source.sendSuccess(() -> Component.translatable("command.itemdelete.removed", id.toString()), true);
        source.sendSuccess(() -> Component.translatable("command.itemdelete.reload_hint"), false);
        return 1;
    }

    private static int list(CommandContext<CommandSourceStack> context) {
        CommandSourceStack source = context.getSource();
        List<String> entries = BlockedItems.entries();
        if (entries.isEmpty()) {
            source.sendSuccess(() -> Component.translatable("command.itemdelete.list_empty"), false);
            return 0;
        }
        source.sendSuccess(() -> Component.translatable("command.itemdelete.list_header", entries.size()), false);
        for (String entry : entries) {
            source.sendSuccess(() -> Component.translatable("command.itemdelete.list_entry", entry), false);
        }
        return entries.size();
    }
}
