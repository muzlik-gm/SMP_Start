package com.muzlik.smpstart.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Utility for formatting and sending messages with HEX color support.
 */
public class MessageUtils {

    private static final Pattern HEX_PATTERN = Pattern.compile("&#([A-Fa-f0-9]{6})");
    private static final String PREFIX = "&#00FBFF&lSMP &#2E7D32» &f";

    /**
     * Translates color codes, including HEX (&#rrggbb).
     */
    public static String color(String message) {
        if (message == null) return "";

        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String hexCode = matcher.group(1);
            matcher.appendReplacement(sb, ChatColor.of("#" + hexCode).toString());
        }
        matcher.appendTail(sb);

        return ChatColor.translateAlternateColorCodes('&', sb.toString());
    }

    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(color(PREFIX + message));
    }

    public static void sendRawMessage(CommandSender sender, String message) {
        sender.sendMessage(color(message));
    }

    public static void sendError(CommandSender sender, String message) {
        sender.sendMessage(color("&#FF5555&lERROR » &7" + message));
    }

    public static void sendSuccess(CommandSender sender, String message) {
        sender.sendMessage(color("&#55FF55&lSUCCESS » &7" + message));
    }

    public static void sendInfo(CommandSender sender, String message) {
        sender.sendMessage(color("&#FFFFAA&lINFO » &7" + message));
    }
}
