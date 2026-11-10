package com.guli.util;

import org.springframework.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * B 站视频链接解析
 * <p>只接受 bilibili.com 的完整视频链接，提取 BV 号与分P，拼成官方播放器地址。
 * <p>为什么要在这里做白名单拼装：前端 iframe 的 src 绝不能直接用管理员填的原始字符串，
 * 否则 `javascript:` 之类的伪协议会在页面里执行脚本。这里解析不出来就返回 null，
 * 前端只能渲染封面图。
 */
public final class BilibiliParser {

    /** 形如 /video/BV1xx411c7mD */
    private static final Pattern BV_PATTERN = Pattern.compile("/video/(BV[0-9A-Za-z]{10})");
    /** 分P参数 ?p=2 或 &p=2 */
    private static final Pattern PAGE_PATTERN = Pattern.compile("[?&]p=(\\d+)");

    private BilibiliParser() {
    }

    /** 解析结果：bvid + 分P（默认 1） */
    public record Parsed(String bvid, int page) {
    }

    /**
     * 解析链接；不是合法的 B 站视频链接则返回 null
     *
     * @param url 形如 https://www.bilibili.com/video/BV1xx411c7mD?p=2
     */
    public static Parsed parse(String url) {
        if (!StringUtils.hasText(url)) {
            return null;
        }
        String u = url.trim();
        // 域名白名单：必须含 bilibili.com，且不含 b23.tv 短链（短链要跟随跳转，不稳定）
        if (!u.startsWith("http") || !u.contains("bilibili.com")) {
            return null;
        }

        Matcher m = BV_PATTERN.matcher(u);
        if (!m.find()) {
            return null;
        }
        String bvid = m.group(1);

        int page = 1;
        Matcher pm = PAGE_PATTERN.matcher(u);
        if (pm.find()) {
            try {
                page = Math.max(1, Integer.parseInt(pm.group(1)));
            } catch (NumberFormatException ignored) {
                page = 1;
            }
        }
        return new Parsed(bvid, page);
    }

    /**
     * 拼官方播放器地址；传入 null 返回 null
     */
    public static String toEmbedUrl(Parsed parsed) {
        if (parsed == null) {
            return null;
        }
        // autoplay=0：不自动播放。用户点播放按钮属于"用户手势"，浏览器才允许带声音播放；
        // 若改成 autoplay=1，浏览器会强制静音（autoplay policy），演示时反而没声音。
        return "https://player.bilibili.com/player.html?bvid=" + parsed.bvid()
                + "&page=" + parsed.page()
                + "&high_quality=1&autoplay=0&danmaku=0&muted=0";
    }

    /** 便捷方法：原始链接 → 播放器地址 */
    public static String toEmbedUrl(String url) {
        return toEmbedUrl(parse(url));
    }

    /** 是否是系统支持的视频链接 */
    public static boolean isSupported(String url) {
        return parse(url) != null;
    }
}
