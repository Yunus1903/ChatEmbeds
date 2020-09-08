package com.yunus1903.chatembeds.client.embed;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.yunus1903.chatembeds.ChatEmbeds;
import com.yunus1903.chatembeds.ChatEmbedsConfig;
import com.yunus1903.chatembeds.client.EmbedChatLine;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.LanguageMap;
import net.minecraft.util.text.StringTextComponent;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Animated image Text chat embed
 * @author Yunus1903
 * @since 29/08/2020
 */
@SuppressWarnings("DuplicatedCode")
public class AnimatedImageEmbed extends Embed
{
    private List<NativeImage> frames;
    private List<NativeImage> scaledFrames;
    private List<ResourceLocation> frameResourceLocations;
    private List<Integer> frameDelays;

    protected EmbedChatLine<AnimatedImageEmbed> currentRenderer;
    protected int currentFrame;

    AnimatedImageEmbed(URL url, int ticks, int chatLineId)
    {
        super(url, ticks, chatLineId);
    }

    @Override
    List<? extends ChatLine<IReorderingProcessor>> createChatLines()
    {
        List<ChatLine<IReorderingProcessor>> lines = new ArrayList<>();
        if (!loadImage()) return lines;

        lines.add(new ChatLine<>(ticks, LanguageMap.getInstance().func_241870_a(new StringTextComponent("")), chatLineId));

        NativeImage scaledImage = scaledFrames.get(0);

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

            lines.add(new EmbedChatLine<AnimatedImageEmbed>(ticks, chatLineId, this)
            {
                private int time;
                private int lastFrameIndex;
                private int lastScrollPos;

                @Override
                public void render(Minecraft mc, MatrixStack matrixStack, int x, int y)
                {
                    if (lastScrollPos != mc.ingameGUI.getChatGUI().scrollPos)
                    {
                        currentRenderer = null;
                        lastScrollPos = mc.ingameGUI.getChatGUI().scrollPos;
                    }

                    if (currentRenderer == null) currentRenderer = this;

                    if (currentRenderer == this)
                    {
                        final int frameIndex = mc.frameTimer.getIndex();

                        if (lastFrameIndex != frameIndex)
                        {
                            lastFrameIndex = frameIndex;
                            time++;
                        }

                        if (time >= frameDelays.get(currentFrame))
                        {
                            time = 0;
                            if (currentFrame + 1 >= frames.size()) currentFrame = 0;
                            else currentFrame++;
                        }
                    }

                    mc.getTextureManager().bindTexture(frameResourceLocations.get(currentFrame));
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
        ImageReader reader = ImageIO.getImageReadersByFormatName("gif").next();
        ImageInputStream inputStream = null;
        try
        {
            if (connection == null) return false;
            inputStream = ImageIO.createImageInputStream(connection.getInputStream());
        }
        catch (IOException e)
        {
            ChatEmbeds.LOGGER.error("Exception creating image InputStream", e);
        }

        if (inputStream == null) return false;

        reader.setInput(inputStream, false);

        if (frames == null) frames = new ArrayList<>();
        if (scaledFrames == null) scaledFrames = new ArrayList<>();
        if (frameResourceLocations == null) frameResourceLocations = new ArrayList<>();
        if (frameDelays == null) frameDelays = new ArrayList<>();

        try
        {
            int numberOfFrames = reader.getNumImages(true);
            if (numberOfFrames == 0) return false;

            IIOMetadata metadata =  reader.getImageMetadata(0);
            String metaFormatName = metadata.getNativeMetadataFormatName();

            List<ImageFrame> imageFrames = new ArrayList<>();

            for (int i = 0; i < numberOfFrames; i++)
            {
                IIOMetadataNode root = (IIOMetadataNode) reader.getImageMetadata(i).getAsTree(metaFormatName);
                IIOMetadataNode imageDescriptor = getNode(root, "ImageDescriptor");
                IIOMetadataNode graphicControlExtension = getNode(root, "GraphicControlExtension");

                imageFrames.add(new ImageFrame(reader.read(i),
                        Integer.parseInt(imageDescriptor.getAttribute("imageLeftPosition")),
                        Integer.parseInt(imageDescriptor.getAttribute("imageTopPosition")),
                        Integer.parseInt(graphicControlExtension.getAttribute("delayTime"))));
            }

            for (int i = 0; i < imageFrames.size(); i++)
            {
                BufferedImage bufferedImage = new BufferedImage(imageFrames.get(0).width, imageFrames.get(0).height, BufferedImage.TYPE_INT_ARGB);
                Graphics2D graphics = bufferedImage.createGraphics();
                for (int j = 0; j <= i; j++)
                {
                    ImageFrame frame = imageFrames.get(j);
                    graphics.drawImage(frame.image, frame.left, frame.top, frame.width, frame.height, null);
                }
                graphics.dispose();
                ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
                ImageIO.write(bufferedImage, "gif", outputStream);
                NativeImage image = NativeImage.read(new ByteArrayInputStream(outputStream.toByteArray()));

                frames.add(image);
                scaledFrames.add(ImageEmbed.scaleImage(image, ChatEmbedsConfig.GeneralConfig.chatImageEmbedMaxWidth, ChatEmbedsConfig.GeneralConfig.chatImageEmbedMaxHeight));
                frameResourceLocations.add(Minecraft.getInstance().getTextureManager()
                        .getDynamicTextureLocation("chat_embed_animated_image_frame_" + i, new DynamicTexture(image)));
                frameDelays.add(imageFrames.get(i).delay);
            }
        }
        catch (IOException e)
        {
            ChatEmbeds.LOGGER.error("Exception loading animated image", e);
        }

        return !frames.isEmpty() && !scaledFrames.isEmpty() && !frameResourceLocations.isEmpty() && !frameDelays.isEmpty();
    }

    private static IIOMetadataNode getNode(IIOMetadataNode root, String nodeName)
    {
        for (int i = 0; i < root.getLength(); i++)
        {
            if (root.item(i).getNodeName().compareToIgnoreCase(nodeName) == 0)
                return (IIOMetadataNode) root.item(i);
        }
        IIOMetadataNode node = new IIOMetadataNode(nodeName);
        root.appendChild(node);
        return node;
    }

    public List<NativeImage> getFrames()
    {
        return frames;
    }

    public List<NativeImage> getScaledFrames()
    {
        return scaledFrames;
    }

    public List<ResourceLocation> getFrameResourceLocations()
    {
        return frameResourceLocations;
    }

    public List<Integer> getFrameDelays()
    {
        return frameDelays;
    }

    private static class ImageFrame
    {
        protected final BufferedImage image;
        protected final int width, height, left, top, delay;

        private ImageFrame(BufferedImage image, int left, int top, int delay)
        {
            this.image = image;
            this.width = image.getWidth();
            this.height = image.getHeight();
            this.left = left;
            this.top = top;
            this.delay = delay;
        }
    }
}
