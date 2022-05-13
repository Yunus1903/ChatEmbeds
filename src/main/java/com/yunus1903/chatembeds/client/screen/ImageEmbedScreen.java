package com.yunus1903.chatembeds.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.yunus1903.chatembeds.client.embed.ImageEmbed;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.resources.ResourceLocation;

/**
 * @author Yunus1903
 * @since 11/08/2020
 */
public class ImageEmbedScreen extends AbstractImageEmbedScreen<ImageEmbed>
{
    private ResourceLocation imageResourceLocation;

    public ImageEmbedScreen(ChatScreen parent, int scrollPos, ImageEmbed embed)
    {
        super(parent, scrollPos, embed, embed.getImage());
    }

    @Override
    protected void init()
    {
        if (minecraft == null) return;
        super.init();
        super.tick();
        imageResourceLocation = minecraft.getTextureManager()
                .register("embed_fullscreen_image", new DynamicTexture(embed.getImage()));
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (minecraft == null) return;
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        RenderSystem.setShaderTexture(0, imageResourceLocation);
        RenderSystem.enableBlend();
        GuiComponent.blit(matrixStack, (width - scaledImageWidth) / 2,
                (height - scaledImageHeight) / 2, 0, 0,
                scaledImageWidth, scaledImageHeight, scaledImageWidth, scaledImageHeight);
    }
}
