package com.yunus1903.chatembeds.client.embed;

import com.yunus1903.chatembeds.ChatEmbeds;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.gui.NewChatGui;
import net.minecraft.client.gui.RenderComponentsUtil;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.*;

import javax.annotation.Nullable;
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
    private ITextProperties title, description;

    TextEmbed(URL url, int ticks, int chatLineId)
    {
        super(url, ticks, chatLineId);
    }

    @Override
    List<? extends ChatLine<IReorderingProcessor>> createChatLines()
    {
        List<ChatLine<IReorderingProcessor>> lines = new ArrayList<>();
        if (!loadText() || title == null) return lines;

        lines.add(new ChatLine<>(ticks, LanguageMap.getInstance().func_241870_a(new StringTextComponent("")), chatLineId));
        lines.add(new ChatLine<>(ticks, LanguageMap.getInstance().func_241870_a(title), chatLineId));
        lines.add(new ChatLine<>(ticks, LanguageMap.getInstance().func_241870_a(new StringTextComponent("")), chatLineId));
        if (description != null)
        {
            int i = MathHelper.floor((double) NewChatGui.calculateChatboxWidth(Minecraft.getInstance().gameSettings.chatWidth) / Minecraft.getInstance().gameSettings.chatScale);
            description = ((IFormattableTextComponent) description).mergeStyle(TextFormatting.GRAY);
            List<IReorderingProcessor> list = RenderComponentsUtil.func_238505_a_(description, i, Minecraft.getInstance().fontRenderer);
            list.forEach(line -> lines.add(new ChatLine<>(ticks, line, chatLineId)));
            lines.add(new ChatLine<>(ticks, LanguageMap.getInstance().func_241870_a(new StringTextComponent("")), chatLineId));
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

        return true;
    }

    @Nullable
    public ITextProperties getTitle()
    {
        return title;
    }

    @Nullable
    public ITextProperties getDescription()
    {
        return description;
    }
}