package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointStatus;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;


/**
 * Created by Belema Ogan on 1/24/2019.
 */

public class FetchUserUseCaseSyncImpl implements FetchUserUseCaseSync{

    private final UsersCache mUsersCache;
    private final FetchUserHttpEndpointSync mFetchUserHttpEndpointSync;



    public FetchUserUseCaseSyncImpl(UsersCache usersCache, FetchUserHttpEndpointSync fetchUserHttpEndpointSync) {
        mUsersCache = usersCache;
        mFetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {
        User user = mUsersCache.getUser(userId);
        EndpointResult status;
        if (user == null){
            try {
                status = mFetchUserHttpEndpointSync.fetchUserSync(userId);
            } catch (NetworkErrorException e) {
                return new UseCaseResult(Status.NETWORK_ERROR, user);
            }

            switch (status.getStatus()) {
                case SUCCESS:
                    user = new User(status.getUserId(), status.getUsername());
                    mUsersCache.cacheUser(user);
                    break;

                case AUTH_ERROR:
                case GENERAL_ERROR:
                    return new UseCaseResult(Status.FAILURE, user);
                default:
                    throw  new RuntimeException("Invalid status");
            }
        }
        return new UseCaseResult(Status.SUCCESS, user);
    }


}
