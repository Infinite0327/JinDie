package kdec.apple.cloud.app.common.context;

import kdec.apple.cloud.app.common.LoginUser;

/**
 * 用户上下文
 *
 * 基于 ThreadLocal 保存当前请求用户信息
 */
public class UserContext {

    /**
     * 当前线程用户
     */
    private static final ThreadLocal<LoginUser> THREAD_LOCAL =
            new ThreadLocal<>();

    /**
     * 设置当前用户
     */
    public static void set(LoginUser user) {
        THREAD_LOCAL.set(user);
    }

    /**
     * 获取当前用户
     */
    public static LoginUser get() {
        return THREAD_LOCAL.get();
    }

    /**
     * 获取当前用户ID
     */
    public static Long getCurrentUserId() {

        LoginUser user = THREAD_LOCAL.get();

        return user == null ? null : user.getUserId();
    }

    /**
     * 获取当前用户名
     */
    public static String getCurrentUsername() {

        LoginUser user = THREAD_LOCAL.get();

        return user == null ? null : user.getUserName();
    }

    /**
     * 获取当前角色
     */
    public static String getCurrentRole() {

        LoginUser user = THREAD_LOCAL.get();

        return user == null ? null : user.getRole();
    }

    /**
     * 清除上下文
     */
    public static void clear() {
        THREAD_LOCAL.remove();
    }
}