package com.blockafeller.twitch;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

public class MinecraftTwitchMessengerService {
    private static final String VERIFICATION_URL = "https://twitch.tv/activate";

    public static void sendAuthorizationLink(ServerPlayerEntity player, String userCode) {
        // Create the base message
        MutableText message = Text.literal("Click ");

        // Create the clickable link component for Twitch authorization
        MutableText link = Text.literal("[here]").setStyle(Style.EMPTY
                .withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, VERIFICATION_URL))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Open authorization link")))
                .withUnderline(true)
                .withColor(Formatting.BLUE));

        // Append additional text
        MutableText message2 = Text.literal(" to authorize your Twitch account.");

        // Combine the components
        MutableText finalMessage = message.append(link).append(message2);

        // Send the message with the clickable link
        player.sendMessage(finalMessage, false);

        // Create a message that copies the user code to the clipboard when clicked
        MutableText codeMessage = Text.literal("Click here to copy your code: ")
                .append(Text.literal(userCode).setStyle(Style.EMPTY
                        .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, userCode))
                        .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to copy the code to clipboard")))
                        .withColor(Formatting.GREEN)
                        .withBold(true)));

        // Send the message with the copy-to-clipboard functionality
        player.sendMessage(codeMessage, false);
    }

    public static void sendAuthorizationOffer(ServerPlayerEntity player) {
        // Clickable text to start viewer authentication (direct method call, no command)
        MutableText authLinkViewer = Text.literal("[start viewer authentication]").setStyle(Style.EMPTY
                .withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/authenticate viewer"))
                .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.literal("Click to start Twitch viewer authentication process")))
                .withUnderline(true)
                .withColor(Formatting.GREEN));

        MutableText viewerMessage = Text.literal("Click ").append(authLinkViewer).append(Text.literal(" to authenticate as a viewer."));
        player.sendMessage(viewerMessage, false);
    }
}