package com.yunus1903.chatembeds.client;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.yunus1903.chatembeds.ChatEmbeds;
import com.yunus1903.chatembeds.ChatEmbedsConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.NativeImage;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextProperties;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Yunus1903
 * @since 10/08/2020
 */
public class Embed
{
    private static final String USER_AGENT = "Mozilla/4.0";

    private URL url;
    private Type type;

    private NativeImage image = null;
    private NativeImage originalImage = null;
    private ResourceLocation imageRL = null;

    private ITextProperties title = null;
    private ITextProperties description = null;

    /**
     * {@link Embed} type
     */
    public enum Type
    {
        IMAGE, TEXT
    }

    public Embed(String url)
    {
        try
        {
            if (new URI(url).getScheme() == null)
            {
                url = "http://" + url;
            }

            this.url = new URL(url);
        }
        catch (MalformedURLException | URISyntaxException e)
        {
            ChatEmbeds.LOGGER.error("Failed to parse URL", e);
        }

        String extension = url.substring(url.lastIndexOf(".") + 1);
        if (extension.contains("?")) extension = extension.substring(0, extension.indexOf("?"));

        if (extension.equals("png") || extension.equals("jpg") || extension.equals("jpeg") || extension.equals("gif"))
        {
            if (ChatEmbedsConfig.GeneralConfig.enableImageEmbeds && loadImage()) type = Type.IMAGE;
        }
        else
        {
            if (ChatEmbedsConfig.GeneralConfig.enableTextEmbeds && loadText()) type = Type.TEXT;
        }
    }

    /**
     * The {@link ChatLine}'s that represent the embed
     * @param ticks Minecraft ticks
     * @param chatLineId ID of the chat line
     * @return List with chatlines
     */
    public List<ChatLine> getLines(int ticks, int chatLineId)
    {
        List<ChatLine> lines = new ArrayList<>();
        if (type == Type.IMAGE && image != null && imageRL != null)
        {
            double imageHeight = image.getHeight();
            double lineHeight = 9.0D;
            double totalLines = imageHeight / lineHeight;
            lines.add(new ChatLine(ticks, new StringTextComponent(""), chatLineId));
            for (int i = 0; i < Math.ceil(totalLines); i++)
            {
                double heightScale = i == (int) totalLines ? (totalLines - i)  : 1.0D;
                lines.add(new ImageChatLine(ticks, chatLineId, url.toString(), image, imageRL, 0, (float) (i * lineHeight), image.getWidth(), (int) (lineHeight * heightScale), image.getWidth(), image.getHeight(), originalImage));
            }
        }
        else if (type == Type.TEXT && title != null)
        {
            lines.add(new ChatLine(ticks, new StringTextComponent(""), chatLineId));
            lines.add(new ChatLine(ticks, title, chatLineId));
            lines.add(new ChatLine(ticks, new StringTextComponent(""), chatLineId));
            if (description != null)
            {
                int i = MathHelper.floor((double) NewChatGui.calculateChatboxWidth(Minecraft.getInstance().gameSettings.chatWidth) / Minecraft.getInstance().gameSettings.chatScale);
                List<ITextProperties> list = RenderComponentsUtil.func_238505_a_(description, i, Minecraft.getInstance().fontRenderer);
                description = new StringTextComponent("");
                list.forEach(line ->
                        lines.add(new ChatLine(ticks, new StringTextComponent(line.getString()).mergeStyle(TextFormatting.GRAY), chatLineId)));
                lines.add(new ChatLine(ticks, new StringTextComponent(""), chatLineId));
            }
        }
        return lines;
    }

    /**
     * Load {@link Embed#originalImage}, {@link Embed#imageRL} and {@link Embed#image} from {@link Embed#url}
     * @return true if {@link Embed#originalImage}, {@link Embed#imageRL} and {@link Embed#image} are set
     */
    private boolean loadImage()
    {
        try
        {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.addRequestProperty("User-Agent", USER_AGENT);
            originalImage = NativeImage.read(urlConnection.getInputStream());
            urlConnection.disconnect();
        }
        catch (IOException e)
        {
            ChatEmbeds.LOGGER.error("NativeImage read error", e);
        }

        if (originalImage == null) return false;

        imageRL = Minecraft.getInstance().getTextureManager().getDynamicTextureLocation("chat_embed_image", new DynamicTexture(originalImage));

        int width = originalImage.getWidth();
        int height = originalImage.getHeight();

        if (width > ChatEmbedsConfig.GeneralConfig.chatImageEmbedMaxWidth)
        {
            width = ChatEmbedsConfig.GeneralConfig.chatImageEmbedMaxWidth;
            height = (int) (((float) originalImage.getHeight() / (float) originalImage.getWidth()) * (float) width);
        }

        if (height > ChatEmbedsConfig.GeneralConfig.chatImageEmbedMaxHeight)
        {
            height = ChatEmbedsConfig.GeneralConfig.chatImageEmbedMaxHeight;
            width = (int) (((float) originalImage.getWidth() / (float) originalImage.getHeight()) * (float) height);
        }

        image = new NativeImage(width, height, false);
        originalImage.resizeSubRectTo(0, 0, originalImage.getWidth(), originalImage.getHeight(), image);
        return true;
    }

    /**
     * Load {@link Embed#title} and {@link Embed#description} from {@link Embed#url}
     * @return true if succeeded in reading HTML
     */
    private boolean loadText()
    {
        try
        {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.addRequestProperty("User-Agent", USER_AGENT);

            HTMLDocument doc = (HTMLDocument) new HTMLEditorKit().createDefaultDocument();
            HTMLEditorKit.Parser parser = new ParserDelegator();
            parser.parse(new InputStreamReader(urlConnection.getInputStream()), doc.getReader(0), true);

            try
            {
                title = new StringTextComponent(doc.getProperty("title").toString()).mergeStyle(TextFormatting.GRAY, TextFormatting.UNDERLINE);
            }
            catch (NullPointerException ignored) { }

            if (title == null)
            {
                try
                {
                    title = new StringTextComponent((String) doc.getElement(doc.getDefaultRootElement(), HTML.Attribute.NAME, "title").getAttributes().getAttribute(HTML.Attribute.CONTENT)).mergeStyle(TextFormatting.GRAY, TextFormatting.UNDERLINE);
                }
                catch (NullPointerException ignored) { }
            }

            try
            {
                String desc = (String) doc.getElement(doc.getDefaultRootElement(), HTML.Attribute.NAME, "description").getAttributes().getAttribute(HTML.Attribute.CONTENT);
                description = new StringTextComponent(desc.replace("\r", "\n"));
            }
            catch (NullPointerException ignored) { }

            urlConnection.disconnect();
            return true;
        }
        catch (IOException e)
        {
            ChatEmbeds.LOGGER.error("HTML read error", e);
        }
        return false;
    }

    /**
     * {@link ChatLine} that renders a image instead of {@link ITextProperties}
     */
    public static class ImageChatLine extends ChatLine
    {
        private final String url;
        private final NativeImage image;
        private final NativeImage originalImage;
        private final ResourceLocation imageRL;
        private final float u0, v0;
        private final int destWidth, destHeight, textureWidth, textureHeight;

        public ImageChatLine(int p_i232239_1_, int p_i232239_3_, String url, NativeImage image, ResourceLocation imageRL, float u0, float v0, int destWidth, int destHeight, int textureWidth, int textureHeight)
        {
            this(p_i232239_1_, p_i232239_3_, url, image, imageRL, u0, v0, destWidth, destHeight, textureWidth, textureHeight, image);
        }

        public ImageChatLine(int p_i232239_1_, int p_i232239_3_, String url, NativeImage image, ResourceLocation imageRL, float u0, float v0, int destWidth, int destHeight, int textureWidth, int textureHeight, NativeImage originalImage)
        {
            super(p_i232239_1_, new StringTextComponent(""), p_i232239_3_);
            this.url = url;
            this.image = image;
            this.imageRL = imageRL;
            this.u0 = u0;
            this.v0 = v0;
            this.destWidth = destWidth;
            this.destHeight = destHeight;
            this.textureWidth = textureWidth;
            this.textureHeight = textureHeight;
            this.originalImage = originalImage;
        }

        /**
         * Renders {@link Embed.ImageChatLine#imageRL image}
         * @param mc Minecraft instance
         * @param matrixStack MatrixStack instance
         * @param x X position
         * @param y Y position
         */
        public void render(Minecraft mc, MatrixStack matrixStack, int x, int y)
        {
            mc.getTextureManager().bindTexture(imageRL);
            AbstractGui.blit(matrixStack, x, y, u0, v0, destWidth, destHeight, textureWidth, textureHeight);
        }

        /**
         * Image origin URL
         * @return {@link Embed.ImageChatLine#url}
         */
        public String getUrl()
        {
            return url;
        }

        /**
         * Scaled image
         * @return {@link Embed.ImageChatLine#image}
         */
        public NativeImage getImage()
        {
            return image;
        }

        /**
         * Original image
         * @return {@link Embed.ImageChatLine#originalImage}
         */
        public NativeImage getOriginalImage()
        {
            return originalImage;
        }
    }
}
