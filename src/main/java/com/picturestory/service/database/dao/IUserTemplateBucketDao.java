package com.picturestory.service.database.dao;

import com.picturestory.service.pojo.Template;
import com.picturestory.service.pojo.TemplateBucket;
import com.picturestory.service.pojo.UserTemplateBucketAssociation;

import java.util.List;

/**
 * Created by bankuru on 4/11/16.
 */
public interface IUserTemplateBucketDao<T> {
    public boolean incrementUserTemplateCount(int userId);
    public int getUserTemplateCount(int userId);
    public boolean unlockUserTemplateBucket(int userId, int bucketId);
    public T getUserTemplateBucketAssociation(int userId);
    public List<Template> getTemplatesForBucketIds(List<Integer> bucketIds);
    public List<TemplateBucket> getAllBuckets();
}
