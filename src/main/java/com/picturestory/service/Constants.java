package com.picturestory.service;

/**
 * Created by bankuru on 29/4/16.
 */
public class Constants {

    //ec2-52-66-134-60.ap-south-1.compute.amazonaws.com
    public static final String DB_SELECT_ENDPOINT = "http://ec2-52-66-134-60.ap-south-1.compute.amazonaws.com:8983/solr/strandsContent/select";
    public static final String DB_UPDATE_ENDPOINT = "http://ec2-52-66-134-60.ap-south-1.compute.amazonaws.com:8983/solr/strandsContent/update?commit=true";

    //Solr params
    public static final String SUCCESS = "success";
    public static final String REGISTERED_TIME = "registeredTime";
    public static final String RESPONSE = "response";
    public static final String DOCS = "docs";
    public static final String WT_JSON = "wt=json";
    public static final String GCMID = "gcmId";
    public static final String FB_ID = "fbId";
    public static final String ALL = "*";
    public static final String AND = " AND ";
    public static final String SET = "set";
    public static final String DELETE_START = "{\"delete\":{\"query\":\"";
    public static final String DELETE_END = "\"}}";
    public static final String INSERT_START = "[";
    public static final String INSERT_END = "]";
    public static final String TRUE = "true";
    public static final String FALSE = "false";
    public static final String IOEXCEPTION_ERRORMESSAGE = "IO Exception";
    public static final int ERRORCODE_IOEXCEPTION = 102;
    public static final int ERRORCODE_INVALID_INPUT = 103;
    public static final int ERRORCODE_JSON_EXCEPTION = 101;
    public static final int ERRORCODE_INVALID_AUTH = 104;
    public static final String ACCEPT_CHARSET = "Accept-Charset";
    public static final String ERROR_MESSAGE = "errorMessage";
    public static final String NUMFOUND = "numFound";
    public static final String START = "start";
    public static final String ROWS = "rows";
    public static final String FOLLOWED_USER_ID = "followedUserId";
    public static final String WALL_PAPER = "wallPaper";
    public static final String SHARED_CONTEND_ID = "sharedContentId";
    public static final String PIXTORY_STATUS = "pixtoryStatus";
    public static final String POSTCARD_ID = "postcardId";
    public static final String TEMPLATE_COUNT = "templateCount";
    //Network params
    public static final String CHARSET = java.nio.charset.StandardCharsets.UTF_8.name();

    //Request params
    public static final String COOKIE_ID = "cookieId";
    public static final String USER_ID = "userId";
    public static final String LIKED_USER_ID = "likedUserId";
    public static final String USER_EMAIL = "userEmail";
    public static final String USER_IMAGE = "userImage";
    public static final String USER_NAME = "userName";
    public static final String USER_DESCRIPTION = "userDesc";
    public static final String PICTURE_DESCRIPTION = "pictureDescription";
    public static final String TITLE = "title";
    public static final String STORY = "story";
    public static final String STATUS = "status";
    public static final String COMMENTS = "comments";
    public static final String PROFILE_IMAGE = "profileImage";
    public static final String LIKED_USERS = "likedUsers";
    public static final String LIKED_USER_LIST = "likedUserList";
    public static final String FAVOUOURITE_LIST = "favouriteList";
    public static final String POSTCARD_LIST = "postcardList";
    public static final String CONTRIBUTED_LIST = "contributedList";
    public static final String USER_INTERESTS = "userInterests";


    public static final String PICTURE_SUMMARY = "pictureSummary";
    public static final String EDITORS_PICK = "editorsPick";
    public static final String PLACE = "place";
    public static final String DATE = "date";
    public static final String PICTURE_URL = "pictureUrl";
    public static final String IS_EXPERT = "isExpert";
    public static final String INGESTION_TIME = "ingestionTime";
    public static final String CATEGORY_ID="categoryId";
    public static final String CATEGORY_NAME="categoryName";
    public static final String SUB_CATEGORY_ID="subCategoryId";
    public static final String FOLLOWED_SUB_CATEGORY_ID="followedSubCategoryId";
    public static final String SUB_CATEGORY_NAME="subCategoryName";
    public static final String CONTENT_ID = "contentId";
    public static final String CONTENT_ID_LIST = "contentIdList";
    public static final String VIDEO_URL = "videoUrl";
    public static final String STREAM_URL = "streamUrl";
    public static final String CONTENT_LENGTH = "contentLength";
    public static final String COMMENT = "comment";
    public static final String COMMENT_ID = "commentId";
    public static final String CONTENT_DESC = "contentDesc";
    public static final String FOLLOWERS = "followers";
    public static final String FOLLOWING = "following";
    public static final String FOLLOWER_COUNT = "followerCount";
    public static final String ID = "id";
    public static final String NAME = "name";
    public static final String IMAGE_URL = "imageUrl";
    public static final String DESCRIPTION = "description";
    public static final String URL = "url";
    public static final String FOLLOWED_BY_USER = "followedByUser";
    public static final String LIKE_COUNT = "likeCount";
    public static final String LIKED_BY_USER = "likedByUser";
    public static final String SHARED_BY_USER = "sharedByUser";
    public static final String DOWNLOADED_BY_USER = "downloadedByUser";
    public static final String FULLCOUNT = "fullCount";
    public static final String PERSON_DETAILS="personDetails";
    public static final String USER_DETAILS="userDetails";
    public static final String PERSON_LIST="personList";
    public static final String CONTENT_LIST = "contentList";
    public static final String LIKED_CONTENT_LIST = "likedContentList";
    public static final String CONTRIBUTED_CONTENT_LIST = "contributedContentList";
    public static final String MY_POSTCARDS = "myPostCards";
    public static final String COMMENT_LIST = "commentList";
    public static final String COMMENT_COUNT = "commentCount";
    public static final String IS_WALLPAPER = "isWallPaper";
    public static final String SET_ID = "setId";
    public static final String CATEGORY_NAME_LIST = "categoryNameList";
    public static final String NUMBER_OF_USERS = "numberOfUsers";
    public static final String PNF_DATA = "pNfData";
    public static final String CONTENT = "content";
    public static final String DATA = "data";
    public static final String DIAGNOSTICS = "diagnostics";
    public static final String COMMENT_LIKED_USER_ID = "commentLikedUserId";

    //Error messages
    public static final String INVALID_USER_ID = "Invalid User Id";
    public static final String INVALID_USER_NAME = "Invalid User Name";
    public static final String INVALID_USER_EMAIL = "Invalid User E-Mail";
    public static final String INVALID_PERSON_ID = "Invalid Person Id";
    public static final String INVALID_CONTENT_ID = "Invalid Content Id";
    public static final String INVALID_DO_LIKE = "Invalid doLike";
    public static final String INVALID_DO_FOLLOW = "Invalid doFollow";
    public static final String INVALID_COMMENT = "Invalid comment";
    public static final String INVALID_USER_DESCRIPTION = "Invalid user Desription";
    public static final String INVALID_COMMENT_ID = "Invalid comment Id";
    public static final String INVALID_REQUEST = "Invalid request body";
    public static final String INVALID_GCMID = "Invalid gcmId";
    public static final String INVALID_FEEDBACK = "Invalid feedBack";
    public static final String INVALID_IMAGE = "Invalid Image";
    public static final String INVALID_STORY = "Invalid Story";
    public static final String INVALID_COOKIE = "Invalid Cookie";
    public static final String INVALID_MESSAGE = "Invalid message";
}
