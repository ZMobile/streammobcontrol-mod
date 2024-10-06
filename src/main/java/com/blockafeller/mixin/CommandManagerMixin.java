package com.blockafeller.mixin;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.command.CommandRegistryAccess;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.command.argument.MessageArgumentType;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.minecraft.server.command.CommandManager.literal;
import static net.minecraft.server.command.CommandManager.argument;

@Mixin(CommandManager.class)
public abstract class CommandManagerMixin {

    @Shadow @Final private CommandDispatcher<ServerCommandSource> dispatcher;

    @Inject(method = "<init>", at = @At("RETURN"))
    private void onInit(CommandManager.RegistrationEnvironment environment, CommandRegistryAccess commandRegistryAccess, CallbackInfo ci) {
        // Remove the commands you want to suppress from the dispatcher
        dispatcher.getRoot().getChildren().removeIf(node -> {
            String name = node.getName();
            return name.equals("tell") || name.equals("msg") || name.equals("w") || name.equals("whisper") || name.equals("me");
        });

        // Register new commands with custom behavior
        registerSuppressedCommand(dispatcher, "tell");
        registerSuppressedCommand(dispatcher, "msg");
        registerSuppressedCommand(dispatcher, "w");
        registerSuppressedCommand(dispatcher, "whisper");
        registerSuppressedCommand(dispatcher, "me");
    }

    private void registerSuppressedCommand(CommandDispatcher<ServerCommandSource> dispatcher, String commandName) {
        dispatcher.register(literal(commandName)
                .then(argument("target", EntityArgumentType.players())
                        .then(argument("message", MessageArgumentType.message())
                                .executes(context -> suppressWhisperCommand(context)))));
    }

    private int suppressWhisperCommand(CommandContext<ServerCommandSource> context) {
        context.getSource().sendError(Text.literal("That is disabled on this server."));
        return 1; // Return 1 to indicate failure
    }
}