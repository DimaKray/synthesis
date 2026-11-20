package ua.krainov.synthesis.box;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.ServerChatEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.config.SynthesisConfig;
import ua.krainov.synthesis.registry.ModAttachments;

import java.util.Arrays;

/** Події для Капітал-боксів: щосекундний тік гравця, «активність» і команди /synthesis box. */
@EventBusSubscriber(modid = SynthesisMod.MODID)
public final class BoxEvents {
    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent.Post event) {
        if (event.getEntity() instanceof ServerPlayer player && player.tickCount % 20 == 0) {
            BoxService.tickSecond(player);
        }
    }

    // --- що вважаємо «гравець щось робить» (окрім руху й поворотів голови) ---

    private static void act(Player player) {
        if (player instanceof ServerPlayer sp) sp.getData(ModAttachments.BOX_TIMER).markActive();
    }

    @SubscribeEvent
    public static void onBreak(BlockEvent.BreakEvent event) {
        act(event.getPlayer());
    }

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent event) {
        act(event.getEntity());
    }

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        act(event.getEntity());
    }

    @SubscribeEvent
    public static void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        act(event.getEntity());
    }

    @SubscribeEvent
    public static void onChat(ServerChatEvent event) {
        act(event.getPlayer());
    }

    // --- команди ---

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        String[] rarities = Arrays.stream(BoxRarity.values()).map(BoxRarity::id).toArray(String[]::new);
        event.getDispatcher().register(Commands.literal("synthesis")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("box")
                        .then(Commands.literal("give")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .then(Commands.argument("rarity", StringArgumentType.word())
                                                .suggests((ctx, builder) -> SharedSuggestionProvider.suggest(rarities, builder))
                                                .executes(BoxEvents::giveCommand))))
                        .then(Commands.literal("timer")
                                .then(Commands.argument("player", EntityArgument.player())
                                        .executes(BoxEvents::timerCommand)))));
    }

    private static int giveCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        BoxRarity rarity = BoxRarity.byId(StringArgumentType.getString(ctx, "rarity"));
        if (rarity == null) {
            ctx.getSource().sendFailure(Component.translatable("command.synthesis.box.bad_rarity"));
            return 0;
        }
        BoxService.give(player, rarity);
        ctx.getSource().sendSuccess(() -> Component.translatable("command.synthesis.box.given",
                player.getDisplayName(), rarity.id()), true);
        return 1;
    }

    private static int timerCommand(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        ServerPlayer player = EntityArgument.getPlayer(ctx, "player");
        BoxTimer timer = player.getData(ModAttachments.BOX_TIMER);
        int interval = SynthesisConfig.BOX_INTERVAL_MINUTES.get() * 60;
        int left = Math.max(0, interval - timer.activeSeconds);
        boolean afk = timer.idleSeconds >= SynthesisConfig.BOX_AFK_MINUTES.get() * 60;
        int chance = (int) Math.round(BoxService.currentChance() * 100);
        ctx.getSource().sendSuccess(() -> Component.translatable("command.synthesis.box.timer",
                player.getDisplayName(), left / 60, left % 60, chance,
                Component.translatable(afk ? "command.synthesis.box.afk_yes" : "command.synthesis.box.afk_no")), false);
        return 1;
    }

    private BoxEvents() {}
}
