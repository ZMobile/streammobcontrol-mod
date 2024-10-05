package com.blockafeller.mixin;

import com.blockafeller.util.StreamerUtil;
import net.minecraft.network.ClientConnection;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.HashSet;
import java.util.Set;

import static com.blockafeller.util.StreamerUtil.isStreamer;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {

    // Inject into the method that sends the join message
    @Inject(method = "Lnet/minecraft/server/PlayerManager;broadcast(Lnet/minecraft/text/Text;Z)V", at = @At("HEAD"), cancellable = true)
    public void broadcast(Text message, boolean overlay, CallbackInfo ci) {
        if (shouldSuppressMessage(message)) {
            ci.cancel();
        }
    }


    // Inject into the method that sends the leave message
    /*@Inject(method = "remove", at = @At("HEAD"), cancellable = true)
    private void onPlayerDisconnect(ServerPlayerEntity player, CallbackInfo info) {
        // Suppress the leave message by cancelling the method execution
        if (!isStreamer(player)) {
            //info.cancel();
        }
    }*/


    // Logic to determine whether to suppress the message
    private boolean shouldSuppressMessage(Text message) {
        // Get the list of current streamers
        Set<String> streamerNames = getAllStreamerNames();

        // Convert the message to a string
        String msg = message.getString();

        // Check if the message contains any streamer name followed by " joined" or " left"
        for (String streamerName : streamerNames) {
            if (msg.contains(" " + streamerName + " joined the game") || msg.contains(" " + streamerName + " left the game")) {
                return false;  // Don't suppress if the message is for a streamer
            }
        }

        // Suppress the message if it doesn't match any streamer
        return true;
    }

    private Set<String> getAllStreamerNames() {
        Set<String> streamerNames = new HashSet<>();

        // Get the current server instance (via the PlayerManager mixin target)
        PlayerManager playerManager = (PlayerManager) (Object) this;

        // Check if there are players connected
        if (playerManager != null) {
            for (ServerPlayerEntity player : playerManager.getPlayerList()) {
                if (isStreamer(player)) {
                    streamerNames.add(player.getEntityName());
                }
            }
        }

        return streamerNames;
    }
}