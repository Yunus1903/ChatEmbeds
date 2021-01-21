package com.yunus1903.chatembeds.client.screen;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.yunus1903.chatembeds.client.embed.ImageEmbed;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.util.ResourceLocation;

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
        super.tick();
        imageResourceLocation = minecraft.getTextureManager()
                .getDynamicTextureLocation("embed_fullscreen_image", new DynamicTexture(embed.getImage()));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (minecraft == null) return;
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        minecraft.getTextureManager().bindTexture(imageResourceLocation);
        AbstractGui.blit(matrixStack, (width - scaledImageWidth) / 2,
                (height - scaledImageHeight) / 2, 0, 0,
                scaledImageWidth, scaledImageHeight, scaledImageWidth, scaledImageHeight);
    }
}