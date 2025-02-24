package top.aetheria.travelguideplatform.common.constant;

public class AppConstants {

    // 用户状态
    public static final int USER_STATUS_NORMAL = 1;
    public static final int USER_STATUS_BANNED = 0;

    // JWT相关
    public static final String JWT_CLAIM_USER_ID = "userId";
    public static final String JWT_HEADER_KEY = "Authorization";
    public static final String JWT_TOKEN_PREFIX = "Bearer "; // 注意后面的空格

    // 攻略(guide)模块
    public static final int GUIDE_STATUS_DRAFT = 0;
    public static final int GUIDE_STATUS_PUBLISHED = 1;
    public static final int GUIDE_STATUS_DELETED = -1;
}
