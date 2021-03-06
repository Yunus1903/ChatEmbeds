package com.yunus1903.chatembeds.client.embed;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.yunus1903.chatembeds.ChatEmbeds;
import com.yunus1903.chatembeds.ChatEmbedsConfig;
import com.yunus1903.chatembeds.client.EmbedChatLine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Image chat embed
 * @author Yunus1903
 * @since 29/08/2020
 */
@SuppressWarnings("DuplicatedCode")
public class ImageEmbed extends Embed
{
    private NativeImage image, scaledImage;
    private ResourceLocation imageResourceLocation;

    ImageEmbed(URL url, int ticks, int chatLineId)
    {
        super(url, ticks, chatLineId);
    }

    @Override
    List<? extends ChatLine<IReorderingProcessor>> createChatLines()
    {
        List<ChatLine<IReorderingProcessor>> lines = new ArrayList<>();
        if (!loadImage()) return lines;

        if (!ChatEmbedsConfig.GeneralConfig.removeUrlMessage)
            lines.add(new ChatLine<>(ticks, LanguageMap.getInstance().func_241870_a(new StringTextComponent("")), chatLineId));

        double imageHeight = scaledImage.getHeight();
        double lineHeight = 9.0D;
        double totalLines = imageHeight / lineHeight;

        for (int i = 0; i < Math.ceil(totalLines); i++)
        {
            double heightScale = i == (int) totalLines ? (totalLines - i) : 1.0D;
            float u0 = 0;
            float v0 = (float) (i * lineHeight);
            int destWidth = scaledImage.getWidth();
            int destHeight = (int) (lineHeight * heightScale);
            int textureWidth = scaledImage.getWidth();
            int textureHeight = scaledImage.getHeight();

            lines.add(new EmbedChatLine<ImageEmbed>(ticks, chatLineId, this)
            {
                @Override
                public void render(Minecraft mc, MatrixStack matrixStack, int x, int y)
                {
                    mc.getTextureManager().bindTexture(imageResourceLocation);
                    AbstractGui.blit(matrixStack, x, y, u0, v0, destWidth, destHeight, textureWidth, textureHeight);
                }

                @Override
                public int getWidth()
                {
                    return scaledImage.getWidth();
                }
            });
        }

        return lines;
    }

    private boolean loadImage()
    {
        try
        {
            if (connection == null) return false;
            image = NativeImage.read(connection.getInputStream());
        }
        catch (IOException e)
        {
            ChatEmbeds.LOGGER.error("Exception reading image", e);
        }

        if (image == null) return false;

        NewChatGui gui = Minecraft.getInstance().ingameGUI.persistantChatGUI;
        scaledImage = scaleImage(image,
                Math.min(ChatEmbedsConfig.GeneralConfig.chatImageEmbedMaxWidth, gui.getChatWidth()),
                ChatEmbedsConfig.GeneralConfig.chatImageEmbedMaxHeight);
        imageResourceLocation = Minecraft.getInstance().getTextureManager()
                .getDynamicTextureLocation("chat_embed_image", new DynamicTexture(image));

        return true;
    }

    protected static NativeImage scaleImage(NativeImage image, int maxWidth, int maxHeight)
    {
        int width = image.getWidth();
        int height = image.getHeight();

        if (width > maxWidth)
        {
            width = maxWidth;
            height = (int) (((float) image.getHeight() / (float) image.getWidth()) * (float) width);
        }

        if (height > maxHeight)
        {
            height = maxHeight;
            width = (int) (((float) image.getWidth() / (float) image.getHeight()) * (float) height);
        }

        NativeImage scaledImage = new NativeImage(width, height, false);
        image.resizeSubRectTo(0, 0, image.getWidth(), image.getHeight(), scaledImage);
        return scaledImage;
    }

    public NativeImage getImage()
    {
        return image;
    }

    public NativeImage getScaledImage()
    {
        return scaledImage;
    }

    public ResourceLocation getImageResourceLocation()
    {
        return imageResourceLocation;
    }
}