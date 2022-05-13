package com.yunus1903.chatembeds;

import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.serializer.Toml4jConfigSerializer;
import me.shedaniel.cloth.clothconfig.shadowed.blue.endless.jankson.Comment;

@Config(name = "chatembeds")
public class ChatEmbedsConfig implements ConfigData {

    @Comment("Enable image embeds (png, jpg, jpeg)")
    public boolean enableImageEmbeds = true;

    @Comment("Enable animated image embeds (gif)")
    public boolean enableAnimatedImageEmbeds = true;

    @Comment("Enable text embeds (Work in progress)")
    public boolean enableTextEmbeds = false;

    @Comment("Max width of image embeds")
    public int chatImageEmbedMaxWidth = 300;

    @Comment("Max height of image embeds")
    public int chatImageEmbedMaxHeight = 100;

    @Comment("Remove the message containing the original url when embedding.")
    public boolean removeUrlMessage = true;

    public static ChatEmbedsConfig getConfig() {
        return AutoConfig.getConfigHolder(ChatEmbedsConfig.class).getConfig();
    }

    public static void registerConfig() {
        AutoConfig.register(ChatEmbedsConfig.class, Toml4jConfigSerializer::new);
    }
}
