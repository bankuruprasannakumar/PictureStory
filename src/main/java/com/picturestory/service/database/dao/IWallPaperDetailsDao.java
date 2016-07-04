package com.picturestory.service.database.dao;

import com.picturestory.service.pojo.WallPaper;
import com.picturestory.service.response.ResponseData;

/**
 * Created by bankuru on 24/6/16.
 */
public interface IWallPaperDetailsDao {
    public boolean setWallPaper(WallPaper wallPaper);
    public WallPaper getWallPaper();
    public ResponseData getDetailedResponse();
    public WallPaper getWallPaperFromEndTime(long endTime);
    public WallPaper getWallPaperFromSetId(Long setId);

}
