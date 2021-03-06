package com.yeahmobi.yscheduler.model.common;

/**
 * @author Leo.Liang
 */
public class UserContextHolder {

    private static ThreadLocal<UserContext> holder = new ThreadLocal<UserContext>();

    public static void setUserContext(UserContext userContext) {
        holder.set(userContext);
    }

    public static UserContext getUserContext() {
        return holder.get();
    }

    public static void clear() {
        holder.remove();
    }
}
