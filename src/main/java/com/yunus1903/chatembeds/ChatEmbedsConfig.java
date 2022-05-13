package com.yunus1903.chatembeds;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;

/**
 * @author Yunus1903
 * @since 12/08/2020
 */
@Mod.EventBusSubscriber(modid = ChatEmbeds.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ChatEmbedsConfig
{
    public static final GeneralConfig GENERAL;

    public static final ForgeConfigSpec CLIENT_SPEC;

    static
    {
        final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

        GENERAL = new GeneralConfig(BUILDER);

        CLIENT_SPEC = BUILDER.build();
    }

    @SubscribeEvent
    public static void onModConfigEvent(final ModConfigEvent event)
    {
        if (event.getConfig().getSpec() == CLIENT_SPEC)
        {
            GENERAL.bake();
        }
    }

    public static class GeneralConfig
    {
        public static boolean enableImageEmbeds;
        public static boolean enableAnimatedImageEmbeds;
        public static boolean enableTextEmbeds;
        public static int chatImageEmbedMaxWidth;
        public static int chatImageEmbedMaxHeight;
        public static boolean removeUrlMessage;

        private final ForgeConfigSpec.BooleanValue ENABLE_IMAGE_EMBEDS;
        private final ForgeConfigSpec.BooleanValue ENABLE_ANIMATED_IMAGE_EMBEDS;
        private final ForgeConfigSpec.BooleanValue ENABLE_TEXT_EMBEDS;
        private final ForgeConfigSpec.IntValue CHAT_IMAGE_EMBED_MAX_WIDTH;
        private final ForgeConfigSpec.IntValue CHAT_IMAGE_EMBED_MAX_HEIGHT;
        private final ForgeConfigSpec.BooleanValue REMOVE_URL_MESSAGE;

        public GeneralConfig(ForgeConfigSpec.Builder builder)
        {
            builder.comment("General config").push("general");

            ENABLE_IMAGE_EMBEDS = builder
                    .comment("Enable image embeds (png, jpg, jpeg)")
                    .define("enableImageEmbeds", true);

            ENABLE_ANIMATED_IMAGE_EMBEDS = builder
                    .comment("Enable animated image embeds (gif)")
                    .define("enableAnimatedImageEmbeds", true);

            ENABLE_TEXT_EMBEDS = builder
                    .comment("Enable text embeds (Work in progress)")
                    .define("enableTextEmbeds", false);

            CHAT_IMAGE_EMBED_MAX_WIDTH = builder
                    .comment("Max width of image embeds")
                    .defineInRange("chatImageEmbedMaxWidth", 300, 0, 320);

            CHAT_IMAGE_EMBED_MAX_HEIGHT = builder
                    .comment("Max height of image embeds")
                    .defineInRange("chatImageEmbedMaxHeight", 100, 0, 320);

            REMOVE_URL_MESSAGE = builder
                    .comment("Remove the message containing the original url when embedding.")
                    .define("removeUrlMessage", true);

            builder.pop();
        }

        public void bake()
        {
            enableImageEmbeds = ENABLE_IMAGE_EMBEDS.get();
            enableAnimatedImageEmbeds = ENABLE_ANIMATED_IMAGE_EMBEDS.get();
            enableTextEmbeds = ENABLE_TEXT_EMBEDS.get();
            chatImageEmbedMaxWidth = CHAT_IMAGE_EMBED_MAX_WIDTH.get();
            chatImageEmbedMaxHeight = CHAT_IMAGE_EMBED_MAX_HEIGHT.get();
            removeUrlMessage = REMOVE_URL_MESSAGE.get();
        }
    }
}
