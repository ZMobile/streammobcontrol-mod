package com.blockafeller.command.multiworld;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.io.File;

import me.isaiah.multiworld.ICreator;
import me.isaiah.multiworld.InfoSuggest;
import me.isaiah.multiworld.command.CreateCommand;
import me.isaiah.multiworld.command.DifficultyCommand;
import me.isaiah.multiworld.command.GameruleCommand;
import me.isaiah.multiworld.command.SetspawnCommand;
import me.isaiah.multiworld.command.SpawnCommand;
import me.isaiah.multiworld.command.TpCommand;
import me.isaiah.multiworld.perm.Perm;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.gen.chunk.ChunkGenerator;

public class HeadlessMultiworldMod {
    public static final String MOD_ID = "multiworld";
    public static MinecraftServer mc;
    public static String CMD = "mw";
    public static ICreator world_creator;
    public static final String VERSION = "1.8";
    private static final char COLOR_CHAR = '§';

    public HeadlessMultiworldMod() {
    }

    public static void setICreator(ICreator ic) {
        world_creator = ic;
    }

    public static ICreator get_world_creator() {
        return world_creator;
    }

    public static ServerWorld create_world(String id, Identifier dim, ChunkGenerator gen, Difficulty dif, long seed) {
        return world_creator.create_world(id, dim, gen, dif, seed);
    }

    public static void init() {
        System.out.println(" Multiworld init");
    }

    public static Identifier new_id(String id) {
        return Identifier.tryParse(id);
    }

    public static void on_server_started(MinecraftServer mc) {
        me.isaiah.multiworld.MultiworldMod.mc = mc;
        File cfg_folder = new File("config");
        if (cfg_folder.exists()) {
            File folder = new File(cfg_folder, "multiworld");
            File worlds = new File(folder, "worlds");
            if (worlds.exists()) {
                File[] var4 = worlds.listFiles();
                int var5 = var4.length;

                for(int var6 = 0; var6 < var5; ++var6) {
                    File f = var4[var6];
                    if (!f.getName().equals("minecraft")) {
                        File[] var8 = f.listFiles();
                        int var9 = var8.length;

                        for(int var10 = 0; var10 < var9; ++var10) {
                            File fi = var8[var10];
                            String id = f.getName() + ":" + fi.getName().replace(".yml", "");
                            System.out.println("Found saved world " + id);
                            CreateCommand.reinit_world_from_config(mc, id);
                        }
                    }
                }
            }
        }

    }

    public static ServerPlayerEntity get_player(ServerCommandSource s) throws CommandSyntaxException {
        ServerPlayerEntity plr = s.getPlayer();
        if (null == plr) {
            throw ServerCommandSource.REQUIRES_PLAYER_EXCEPTION.create();
        } else {
            return plr;
        }
    }

    public static void register_commands(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register((LiteralArgumentBuilder)((LiteralArgumentBuilder)((LiteralArgumentBuilder)CommandManager.literal(CMD).requires((source) -> {
            try {
                return source.hasPermissionLevel(1) || Perm.has(get_player(source), "multiworld.cmd") || Perm.has(get_player(source), "multiworld.admin");
            } catch (Exception var2) {
                return source.hasPermissionLevel(1);
            }
        })).executes((ctx) -> {
            return broadcast((ServerCommandSource)ctx.getSource(), Formatting.AQUA, (String)null);
        })).then(CommandManager.argument("message", StringArgumentType.greedyString()).suggests(new InfoSuggest()).executes((ctx) -> {
            try {
                return broadcast((ServerCommandSource)ctx.getSource(), Formatting.AQUA, StringArgumentType.getString(ctx, "message"));
            } catch (Exception var2) {
                Exception e = var2;
                e.printStackTrace();
                return 1;
            }
        })));
    }

    public static int broadcast(ServerCommandSource source, Formatting formatting, String message) throws CommandSyntaxException {
        ServerPlayerEntity plr = get_player(source);
        if (null == message) {
            plr.sendMessage(text("Multiworld Mod for Minecraft " + mc.getVersion(), Formatting.AQUA), false);
            World world = plr.getWorld();
            Identifier id = world.getRegistryKey().getValue();
            message(plr, "Currently in: " + id.toString());
            return 1;
        } else {
            boolean ALL = Perm.has(plr, "multiworld.admin");
            String[] args = message.split(" ");
            if (args[0].equalsIgnoreCase("help")) {
                String[] lines = new String[]{"&4Multiworld Mod Commands:&r", "&a/mw spawn&r - Teleport to current world spawn", "&a/mw setspawn&r - Sets the current world spawn", "&a/mw tp <id>&r - Teleport to a world", "&a/mw list&r - List all worlds", "&a/mw gamerule <rule> <value>&r - Change a worlds Gamerules", "&a/mw create <id> <env>&r - create a new world", "&a/mw difficulty <value> [world id] - Sets the difficulty of a world"};
                String[] var7 = lines;
                int var8 = lines.length;

                for(int var9 = 0; var9 < var8; ++var9) {
                    String s = var7[var9];
                    message(plr, s);
                }
            }

            if (args[0].equalsIgnoreCase("debugtick")) {
                ServerWorld w = (ServerWorld)plr.getWorld();
                Identifier id = w.getRegistryKey().getValue();
                message(plr, "World ID: " + id.toString());
                message(plr, "Players : " + w.getPlayers().size());
                w.tick(() -> {
                    return true;
                });
            }

            if (args[0].equalsIgnoreCase("setspawn") && (ALL || Perm.has(plr, "multiworld.setspawn"))) {
                return SetspawnCommand.run(mc, plr, args);
            } else if (!args[0].equalsIgnoreCase("spawn") || !ALL && !Perm.has(plr, "multiworld.spawn")) {
                if (!args[0].equalsIgnoreCase("gamerule") || !ALL && !Perm.has(plr, "multiworld.gamerule")) {
                    if (!args[0].equalsIgnoreCase("difficulty") || !ALL && !Perm.has(plr, "multiworld.difficulty")) {
                        if (args[0].equalsIgnoreCase("tp")) {
                            if (!ALL && !Perm.has(plr, "multiworld.tp")) {
                                plr.sendMessage(Text.of("No permission! Missing permission: multiworld.tp"), false);
                                return 1;
                            } else if (args.length == 1) {
                                plr.sendMessage(text_plain("Usage: /" + CMD + " tp <world>"), false);
                                return 0;
                            } else {
                                return TpCommand.run(mc, plr, args);
                            }
                        } else {
                            if (args[0].equalsIgnoreCase("list")) {
                                if (!ALL && !Perm.has(plr, "multiworld.cmd")) {
                                    plr.sendMessage(Text.of("No permission! Missing permission: multiworld.cmd"), false);
                                    return 1;
                                }

                                plr.sendMessage(text("All Worlds:", Formatting.AQUA), false);
                                mc.getWorlds().forEach((worldx) -> {
                                    String name = worldx.getRegistryKey().getValue().toString();
                                    if (name.startsWith("multiworld:")) {
                                        name = name.replace("multiworld:", "");
                                    }

                                    plr.sendMessage(text_plain("- " + name), false);
                                });
                            }

                            if (!args[0].equalsIgnoreCase("version") || !ALL && !Perm.has(plr, "multiworld.cmd")) {
                                if (args[0].equalsIgnoreCase("create")) {
                                    if (!ALL && !Perm.has(plr, "multiworld.create")) {
                                        message(plr, "No permission! Missing permission: multiworld.create");
                                        return 1;
                                    } else {
                                        return CreateCommand.run(mc, plr, args);
                                    }
                                } else {
                                    return 1;
                                }
                            } else {
                                message(plr, "Multiworld Mod version 1.8");
                                return 1;
                            }
                        }
                    } else {
                        return DifficultyCommand.run(mc, plr, args);
                    }
                } else {
                    return GameruleCommand.run(mc, plr, args);
                }
            } else {
                return SpawnCommand.run(mc, plr, args);
            }
        }
    }

    /** @deprecated */
    @Deprecated
    public static Text text(String txt, Formatting color) {
        return world_creator.colored_literal(txt, color);
    }

    public static void message(PlayerEntity player, String message) {
        try {
            player.sendMessage(Text.of(translate_alternate_color_codes('&', message)), false);
        } catch (Exception var3) {
            Exception e = var3;
            e.printStackTrace();
        }

    }

    private static String translate_alternate_color_codes(char altColorChar, String textToTranslate) {
        char[] b = textToTranslate.toCharArray();

        for(int i = 0; i < b.length - 1; ++i) {
            if (b[i] == altColorChar && "0123456789AaBbCcDdEeFfKkLlMmNnOoRr".indexOf(b[i + 1]) > -1) {
                b[i] = 167;
                b[i + 1] = Character.toLowerCase(b[i + 1]);
            }
        }

        return new String(b);
    }

    public static Text text_plain(String txt) {
        return Text.of(txt);
    }
}
