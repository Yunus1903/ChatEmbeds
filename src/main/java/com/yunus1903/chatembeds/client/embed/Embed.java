package com.yunus1903.chatembeds.client.embed;

import com.yunus1903.chatembeds.ChatEmbeds;
import com.yunus1903.chatembeds.ChatEmbedsConfig;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.text.OrderedText;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.*;
import java.util.List;

/**
 * Abstract class for chat embeds
 * @author Yunus1903
 * @since 29/08/2020
 */
public abstract class Embed
{
    private static final String USER_AGENT = "Mozilla/4.0";

    final URL url;
    final int ticks, chatHudLineId;
    @Nullable
    final HttpURLConnection connection;
    private final List<? extends ChatHudLine<OrderedText>> chatHudLines;

    /**
     * Constructor
     * @param url {@link URL} source of the embed
     * @param ticks Minecraft ticks
     * @param chatHudLineId ID of the chat line
     */
    Embed(URL url, int ticks, int chatHudLineId)
    {
        this.url = url;
        this.ticks = ticks;
        this.chatHudLineId = chatHudLineId;
        this.connection = openConnection();
        this.chatHudLines = createChatHudLines();
        if (this.connection != null) this.connection.disconnect();
    }

    /**
     * Opens {@link HttpURLConnection URL connection}
     * @return Instance of {@link HttpURLConnection}
     */
    @Nullable
    private HttpURLConnection openConnection()
    {
        try
        {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.addRequestProperty("User-Agent", USER_AGENT);
            return connection;
        }
        catch (IOException e)
        {
            ChatEmbeds.LOGGER.error("Failed to open connection", e);
        }
        return null;
    }

    /**
     * Creates the ChatHudLines
     * @return A list of {@link ChatHudLine ChatHudLines}
     */
    abstract List<? extends ChatHudLine<OrderedText>> createChatHudLines();

    /**
     * @return List of {@link ChatHudLine ChatHudLines}
     */
    public List<? extends ChatHudLine<OrderedText>> getChatHudLines()
    {
        return chatHudLines;
    }

    /**
     * @return Source {@link Embed#url} of this embed
     */
    public URL getUrl()
    {
        return url;
    }

    /**
     * Builder class to create the {@link Embed} instance
     */
    public static class Builder
    {
        private final String url;
        private final int ticks, chatHudLineId;

        /**
         * Builder constructor
         * @param url Source of the embed as a String
         * @param ticks Minecraft ticks
         * @param chatHudLineId ID of the chat line
         */
        public Builder(String url, int ticks, int chatHudLineId)
        {
            this.url = url;
            this.ticks = ticks;
            this.chatHudLineId = chatHudLineId;
        }

        /**
         * Builds the embed
         * @return A instance of {@link Embed}
         */
        @Nullable
        public Embed build()
        {
            URL parsedURL = parseURL(url);
            if (parsedURL == null) return null;

            String extension = parsedURL.toString().substring(parsedURL.toString().lastIndexOf(".") + 1);
            if (extension.contains("?")) extension = extension.substring(0, extension.indexOf("?"));

            ChatEmbedsConfig config = ChatEmbedsConfig.getConfig();
            try
            {
                String imageURL = ImageExtractor.extractImageURL(parsedURL);
                if (imageURL != null)
                {
                    parsedURL = new URL(imageURL);
                    if (extension.equals("gif") || extension.equals("gifv"))
                    {
                        if (config.enableAnimatedImageEmbeds)
                            return new AnimatedImageEmbed(parsedURL, ticks, chatHudLineId);
                    }
                    else if (config.enableImageEmbeds)
                        return new ImageEmbed(parsedURL, ticks, chatHudLineId);
                }
            }
            catch (MalformedURLException e)
            {
                ChatEmbeds.LOGGER.debug("Failed to recreate URL", e);
            }
            catch (IOException ignored) { }

            if (config.enableTextEmbeds) return new TextEmbed(parsedURL, ticks, chatHudLineId);
            return null;
        }

        /**
         * Parse string into {@link URL}
         * @param url URL as a string
         * @return Parsed {@link URL}
         */
        @Nullable
        private static URL parseURL(String url)
        {
            URL parsedURL = null;
            try
            {
                if (new URI(url).getScheme() == null)
                {
                    url = "http://" + url;
                }

                parsedURL = new URL(url);
            }
            catch (MalformedURLException | URISyntaxException e)
            {
                ChatEmbeds.LOGGER.error("Failed to parse URL", e);
            }
            return parsedURL;
        }
    }
}
