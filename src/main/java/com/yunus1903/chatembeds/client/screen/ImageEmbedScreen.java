package com.yunus1903.chatembeds.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.yunus1903.chatembeds.client.embed.ImageEmbed;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

/**
 * @author Yunus1903
 * @since 11/08/2020
 */
public class ImageEmbedScreen extends AbstractImageEmbedScreen<ImageEmbed>
{
    private Identifier imageResourceLocation;

    public ImageEmbedScreen(ChatScreen parent, int scrollPos, ImageEmbed embed)
    {
        super(parent, scrollPos, embed, embed.getImage());
    }

    @Override
    protected void init()
    {
        if (client == null) return;
        super.init();
        super.tick();
        imageResourceLocation = client.getTextureManager()
                .registerDynamicTexture("embed_fullscreen_image", new NativeImageBackedTexture(embed.getImage()));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks)
    {
        if (client == null) return;
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        RenderSystem.setShaderTexture(0, imageResourceLocation);
        RenderSystem.enableBlend();
        DrawableHelper.drawTexture(matrixStack, (width - scaledImageWidth) / 2,
                (height - scaledImageHeight) / 2, 0, 0,
                scaledImageWidth, scaledImageHeight, scaledImageWidth, scaledImageHeight);
    }
}
