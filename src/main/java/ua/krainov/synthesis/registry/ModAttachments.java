package ua.krainov.synthesis.registry;

import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import ua.krainov.synthesis.SynthesisMod;
import ua.krainov.synthesis.box.BoxTimer;

/** Дані, що «чіпляються» до гравця (зберігаються разом з ним). */
public final class ModAttachments {
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS =
            DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, SynthesisMod.MODID);

    /** Таймер активної гри для Капітал-боксів. copyOnDeath — не скидається після смерті. */
    public static final DeferredHolder<AttachmentType<?>, AttachmentType<BoxTimer>> BOX_TIMER =
            ATTACHMENTS.register("box_timer", () -> AttachmentType.builder(() -> new BoxTimer())
                    .serialize(BoxTimer.CODEC)
                    .copyOnDeath()
                    .build());

    private ModAttachments() {}
}
