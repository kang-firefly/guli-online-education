package com.guli.common;

/**
 * 登录用户上下文（ThreadLocal）
 * <p>由拦截器写入、请求结束时清理；Service 层可直接取当前用户，无需依赖 Web 对象。
 */
public final class UserContext {

    private static final ThreadLocal<LoginUser> HOLDER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(LoginUser user) {
        HOLDER.set(user);
    }

    public static LoginUser get() {
        return HOLDER.get();
    }

    public static Long getUserId() {
        LoginUser u = HOLDER.get();
        return u == null ? null : u.getId();
    }

    /** 取不到时抛未登录异常，供必须登录的方法使用 */
    public static LoginUser require() {
        LoginUser u = HOLDER.get();
        if (u == null) {
            throw new BusinessException(ResultCode.UNAUTHORIZED);
        }
        return u;
    }

    public static void clear() {
        HOLDER.remove();
    }
}
