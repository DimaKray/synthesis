package ua.krainov.synthesis.item;

import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import ua.krainov.synthesis.box.BoxRarity;

import java.util.ArrayList;
import java.util.List;

/**
 * Капітал-бокс. Затисни ПКМ — бокс «розкручується» 1.5 с (частинки кольору рідкості, звук),
 * потім лут з таблиці data/synthesis/loot_table/boxes/&lt;рідкість&gt;.json іде в інвентар.
 */
public class CapitalBoxItem extends Item {
    public static final int OPEN_TICKS = 30;

    private final BoxRarity rarity;

    public CapitalBoxItem(BoxRarity rarity, Properties properties) {
        super(properties);
        this.rarity = rarity;
    }

    public BoxRarity getBoxRarity() {
        return rarity;
    }

    @Override
    public Component getName(ItemStack stack) {
        return Component.translatable(getDescriptionId(stack)).withStyle(rarity.color);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return OPEN_TICKS;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.NONE; // своє обертання малює CapitalBoxClientExtension
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int remaining) {
        if (!(level instanceof ServerLevel server)) return;
        int done = OPEN_TICKS - remaining;
        if (done % 2 == 0) {
            server.sendParticles(new DustParticleOptions(rarity.particleColor(), 1.2f),
                    entity.getX(), entity.getY() + 1.1, entity.getZ(), 4, 0.45, 0.35, 0.45, 0.0);
        }
        if (done % 6 == 0) {
            float pitch = 0.6f + done / (float) OPEN_TICKS;
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(),
                    SoundEvents.EXPERIENCE_ORB_PICKUP, SoundSource.PLAYERS, 0.5f, pitch);
        }
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (!(level instanceof ServerLevel server) || !(entity instanceof ServerPlayer player)) return stack;

        LootTable table = server.getServer().reloadableRegistries().getLootTable(rarity.lootTable());
        LootParams params = new LootParams.Builder(server)
                .withParameter(LootContextParams.ORIGIN, player.position())
                .withParameter(LootContextParams.THIS_ENTITY, player)
                .withLuck(player.getLuck())
                .create(LootContextParamSets.GIFT);
        List<ItemStack> loot = new ArrayList<>(table.getRandomItems(params));

        List<Component> names = new ArrayList<>();
        for (ItemStack item : loot) {
            names.add(Component.literal(item.getCount() + "× ").append(item.getHoverName()));
            ItemHandlerHelper.giveItemToPlayer(player, item);
        }
        stack.consume(1, player);

        player.sendSystemMessage(Component.translatable("message.synthesis.box.opened", getName(stack),
                names.isEmpty() ? Component.literal("—") : ComponentUtils.formatList(names, Component.literal(", ")))
                .withStyle(ChatFormatting.GRAY));
        server.sendParticles(new DustParticleOptions(rarity.particleColor(), 2.0f),
                player.getX(), player.getY() + 1.1, player.getZ(), 30, 0.5, 0.5, 0.5, 0.0);
        if (rarity == BoxRarity.LEGENDARY || rarity == BoxRarity.EPIC) {
            server.sendParticles(ParticleTypes.TOTEM_OF_UNDYING, player.getX(), player.getY() + 1.1, player.getZ(),
                    rarity == BoxRarity.LEGENDARY ? 60 : 20, 0.4, 0.6, 0.4, 0.4);
        }
        level.playSound(null, player.getX(), player.getY(), player.getZ(),
                SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 0.8f, 1.3f);
        return stack;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable("tooltip.synthesis.box.open").withStyle(ChatFormatting.GRAY));
    }
}
