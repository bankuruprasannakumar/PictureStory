package com.picturestory.service.api.utilities;

import com.picturestory.service.database.adapters.solr.SolrAdapter;
import com.picturestory.service.database.dao.UserDetailsDao;
import com.picturestory.service.pojo.UserForMigration;
import com.picturestory.service.response.ResponseData;

import java.util.List;

/**
 * Created by bankuru on 23/11/16.
 */
public class UserMigrationToDifferentTime {


    public static boolean MigrationToChangeRegisteredTimeForUsers(long registeredTime) {
        SolrAdapter solrAdapter = new SolrAdapter();
        UserDetailsDao userDetailsDao = new UserDetailsDao(solrAdapter);
        List<UserForMigration> userList = userDetailsDao.getUserBeforeTimeForMigration(registeredTime);
        if (userList == null) {
            System.out.println("userList is null");
            return true;
        }
        boolean status = userDetailsDao.updateRegisteredTimeOfUser(userList, 1470748328000l);
        if (status) {
            return true;
        }
        return false;
    }

//    public static void main(String[] args) {
//        System.out.println(MigrationToChangeRegisteredTimeForUsers(1470505706001l));
//        return;
//    }
}
