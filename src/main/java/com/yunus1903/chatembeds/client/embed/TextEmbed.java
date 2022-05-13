package com.yunus1903.chatembeds.client.embed;

import com.yunus1903.chatembeds.ChatEmbeds;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.ChatHudLine;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import net.minecraft.util.Language;
import net.minecraft.util.math.MathHelper;
import org.jetbrains.annotations.Nullable;

import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.parser.ParserDelegator;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Text chat embed
 * @author Yunus1903
 * @since 29/08/2020
 */
public class TextEmbed extends Embed
{
    @Nullable
    private Text title, description;

    TextEmbed(URL url, int ticks, int ChatHudLineId)
    {
        super(url, ticks, ChatHudLineId);
    }

    @Override
    List<? extends ChatHudLine<OrderedText>> createChatHudLines()
    {
        List<ChatHudLine<OrderedText>> lines = new ArrayList<>();
        if (!loadText() || title == null) return lines;

        lines.add(new ChatHudLine<>(ticks, Language.getInstance().reorder(new LiteralText("")), chatHudLineId));
        lines.add(new ChatHudLine<>(ticks, Language.getInstance().reorder(title), chatHudLineId));
        lines.add(new ChatHudLine<>(ticks, Language.getInstance().reorder(new LiteralText("")), chatHudLineId));
        if (description != null)
        {
            int i = MathHelper.floor((double) ChatHud.getWidth(MinecraftClient.getInstance().options.chatWidth) / MinecraftClient.getInstance().options.chatScale);
            description = ((MutableText) description).formatted(Formatting.GRAY);
            List<OrderedText> list = MinecraftClient.getInstance().textRenderer.wrapLines(description, i);
            list.forEach(line -> lines.add(new ChatHudLine<>(ticks, line, chatHudLineId)));
            lines.add(new ChatHudLine<>(ticks, Language.getInstance().reorder(new LiteralText("")), chatHudLineId));
        }

        return lines;
    }

    private boolean loadText()
    {
        HTMLDocument doc = (HTMLDocument) new HTMLEditorKit().createDefaultDocument();
        HTMLEditorKit.Parser parser = new ParserDelegator();

        try
        {
            if (connection == null) return false;
            parser.parse(new InputStreamReader(connection.getInputStream()), doc.getReader(0), true);
        }
        catch (IOException e)
        {
            ChatEmbeds.LOGGER.error("Exception reading HTML", e);
        }

        try
        {
            title = new LiteralText(doc.getProperty("title").toString()).formatted(Formatting.GRAY, Formatting.UNDERLINE);
        }
        catch (NullPointerException ignored) { }

        if (title == null)
        {
            try
            {
                title = new LiteralText((String) doc.getElement(doc.getDefaultRootElement(), HTML.Attribute.NAME, "title").getAttributes().getAttribute(HTML.Attribute.CONTENT)).formatted(Formatting.GRAY, Formatting.UNDERLINE);
            }
            catch (NullPointerException ignored) { }
        }

        try
        {
            String desc = (String) doc.getElement(doc.getDefaultRootElement(), HTML.Attribute.NAME, "description").getAttributes().getAttribute(HTML.Attribute.CONTENT);
            description = new LiteralText(desc.replace("\r", "\n"));
        }
        catch (NullPointerException ignored) { }

        return true;
    }

    @Nullable
    public Text getTitle()
    {
        return title;
    }

    @Nullable
    public Text getDescription()
    {
        return description;
    }
}
