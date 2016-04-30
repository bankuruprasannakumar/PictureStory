package com.picturestory.service.database.adapters.solr;

import com.picturestory.service.database.adapters.IDataAccessAdapter ;
import com.picturestory.service.network.NetworkConnection ;
import com.picturestory.service.response.ResponseData ;

import static com.picturestory.service.Constants .DB_SELECT_ENDPOINT;
import static com.picturestory.service.Constants .DB_UPDATE_ENDPOINT;

/**
 * Created by aasha.medhi on 9/24/15.
 */
public class SolrAdapter implements IDataAccessAdapter<ResponseData> {
    NetworkConnection connection;

    public SolrAdapter() {
        connection = new NetworkConnection();
    }

    @Override
    public ResponseData updateRequest(String query) {
        return connection.makeConnection(DB_UPDATE_ENDPOINT, NetworkConnection.METHOD.POST, query);
    }

    @Override
    public ResponseData selectRequest(String query) {
        return connection.makeConnection(DB_SELECT_ENDPOINT, NetworkConnection.METHOD.GET, query);
    }
}
