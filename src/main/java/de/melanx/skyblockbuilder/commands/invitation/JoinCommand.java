package de.melanx.skyblockbuilder.commands.invitation;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.ArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import de.melanx.skyblockbuilder.commands.Suggestions;
import de.melanx.skyblockbuilder.config.ConfigHandler;
import de.melanx.skyblockbuilder.data.SkyblockSavedData;
import de.melanx.skyblockbuilder.data.Team;
import de.melanx.skyblockbuilder.events.SkyblockHooks;
import de.melanx.skyblockbuilder.events.SkyblockJoinRequestEvent;
import de.melanx.skyblockbuilder.util.WorldUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class JoinCommand {

    public static ArgumentBuilder<CommandSourceStack, ?> register() {
        // Invites the given player
        return Commands.literal("join")
                .then(Commands.argument("team", StringArgumentType.string()).suggests(Suggestions.ALL_TEAMS)
                        .executes(context -> sendJoinRequest(context.getSource(), StringArgumentType.getString(context, "team"))));
    }

    private static int sendJoinRequest(CommandSourceStack source, String teamName) throws CommandSyntaxException {
        WorldUtil.checkSkyblock(source);
        ServerLevel level = source.getLevel();
        SkyblockSavedData data = SkyblockSavedData.get(level);
        ServerPlayer player = source.getPlayerOrException();
        Team team = data.getTeam(teamName);

        if (team == null) {
            source.sendSuccess(Component.translatable("skyblockbuilder.command.error.team_not_exist").withStyle(ChatFormatting.RED), false);
            return 0;
        }

        if (data.hasPlayerTeam(player)) {
            source.sendSuccess(Component.translatable("skyblockbuilder.command.error.user_has_team").withStyle(ChatFormatting.RED), false);
            return 0;
        }

        SkyblockJoinRequestEvent.SendRequest event = SkyblockHooks.onSendJoinRequest(player, team);
        switch (event.getResult()) {
            case DENY:
                source.sendSuccess(Component.translatable("skyblockbuilder.command.denied.join_request").withStyle(ChatFormatting.RED), false);
                return 0;
            case DEFAULT:
                if (!source.hasPermission(2)) {
                    if (!ConfigHandler.Utility.selfManage) {
                        source.sendSuccess(Component.translatable("skyblockbuilder.command.disabled.join_request").withStyle(ChatFormatting.RED), false);
                        return 0;
                    }
                    if (!team.allowsJoinRequests()) {
                        source.sendSuccess(Component.translatable("skyblockbuilder.command.disabled.team_join_request").withStyle(ChatFormatting.RED), false);
                        return 0;
                    }
                }
                break;
            case ALLOW:
                break;
        }

        team.sendJoinRequest(player);
        source.sendSuccess(Component.translatable("skyblockbuilder.command.success.join_request", teamName).withStyle(ChatFormatting.GOLD), true);
        return 1;
    }
}
