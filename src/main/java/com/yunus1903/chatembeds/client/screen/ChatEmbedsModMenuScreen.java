package com.yunus1903.chatembeds.client.screen;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import com.yunus1903.chatembeds.ChatEmbedsConfig;
import me.shedaniel.autoconfig.AutoConfig;

public class ChatEmbedsModMenuScreen implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> AutoConfig.getConfigScreen(ChatEmbedsConfig.class, parent).get();
    }
}
