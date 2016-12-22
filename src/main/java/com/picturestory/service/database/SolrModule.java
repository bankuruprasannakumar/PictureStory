package com.picturestory.service.database;

/**
 * Created by bankuru on 30/4/16.
 */

import com.google.inject.AbstractModule;
import com.picturestory.service.database.adapters.IDataAccessAdapter;
import com.picturestory.service.database.adapters.solr.SolrAdapter;
import com.picturestory.service.database.dao.*;
import com.picturestory.service.pojo.UserTemplateBucketAssociation;

public class SolrModule  extends AbstractModule{
    @Override
    public void configure(){
        bind(IDataAccessAdapter.class).to(SolrAdapter.class);
        bind(IUserDetailsDao.class).to(UserDetailsDao.class);
        bind(IContentDetailsDao.class).to(ContentDetailsDao.class);
        bind(IContentUserLikeDao.class).to(ContentUserLikeDao.class);
        bind(IUserDetailsDao.class).to(UserDetailsDao.class);
        bind(IContentUserCommentDao.class).to(ContentUserCommentDao.class);
        bind(IUserFeedBackDao.class).to(UserFeedBackDao.class);
        bind(IUserFollowedUserDetailsDao.class).to(UserFollowedUserDetailsDao.class);
        bind(IUserFollowerDetailsDao.class).to(UserFollowerDetailsDao.class);
        bind(IUserUserDao.class).to(UserUserDao.class);
        bind(IUserGcmIdDao.class).to(UserGcmIdDao.class);
        bind(IWallPaperDetailsDao.class).to(WallPaperDetailsDao.class);
        bind(ICategoryDetailsDao.class).to(CategoryDetailsDao.class);
        bind(IContentCategoryDao.class).to(ContentCategoryDao.class);
        bind(ISharedContentAssociationDao.class).to(SharedContentAssociationDao.class);
        bind(ICommentUserLikeDao.class).to(CommentUserLikeDao.class);
        bind(IPostcardDetailsDao.class).to(PostcardDetailsDao.class);
        bind(IUserTemplateBucketDao.class).to(UserTemplateBucketDao.class);
        bind(IUserFolderAssociationDao.class).to(UserFolderAssociationDao.class);
        bind(IUserSetIdDao.class).to(UserSetIdDao.class);
    }
}
