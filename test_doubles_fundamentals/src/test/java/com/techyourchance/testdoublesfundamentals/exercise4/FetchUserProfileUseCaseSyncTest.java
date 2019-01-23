package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;

public class FetchUserProfileUseCaseSyncTest {

    public static final String USER_ID = "User01";
    FetchUserProfileUseCaseSync SUT;
    private UserProfileHttpEndpointSyncTd mUserProfileHttpEndpointSync;
    private UsersCacheTd mUsersCache;
    private String FULLNAME = "Belema";
    private String IMAGE_URL = "http://image.url";

    @Before
    public void setUp() throws Exception {
        mUserProfileHttpEndpointSync = new UserProfileHttpEndpointSyncTd();
        mUsersCache = new UsersCacheTd();
        SUT = new FetchUserProfileUseCaseSync(mUserProfileHttpEndpointSync, mUsersCache);
    }

    /**
     * Test cases
     */
    // check that the profileURL is passed to the EndpointResult
    // check that the user is cached after successful request
    // check that the user not cache when failure occurs
    // check that UserProfileHttpEndpointSync returns the correct result
    // check that useCaseResult from fetchUserProfileSync is correct



    @Test
    public void fetchUserProfile_success_userIdPassedToEndpoint() throws Exception {
        SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(mUserProfileHttpEndpointSync.mUserId, is(USER_ID));
    }

    @Test
    public void fetchUserProfile_success_userShouldBeCached() throws Exception {
        SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(mUsersCache.mUser.getFullName(), is(FULLNAME));
        Assert.assertThat(mUsersCache.mUser.getUserId(), is(USER_ID));
        Assert.assertThat(mUsersCache.mUser.getImageUrl(), is(IMAGE_URL));
    }

    @Test
    public void fetchUserProfile_generalFailure_userNotCached() throws Exception {
        mUserProfileHttpEndpointSync.mGeneralError = true;
        SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(mUsersCache.mUser.getFullName(), is(""));
        Assert.assertThat(mUsersCache.mUser.getUserId(), is(""));
        Assert.assertThat(mUsersCache.mUser.getImageUrl(), is(""));
    }

    @Test
    public void fetchUserProfile_authFailure_userNotCached() throws Exception {
        mUserProfileHttpEndpointSync.mAuthError = true;
        SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(mUsersCache.mUser.getFullName(), is(""));
        Assert.assertThat(mUsersCache.mUser.getUserId(), is(""));
        Assert.assertThat(mUsersCache.mUser.getImageUrl(), is(""));
    }

    @Test
    public void fetchUserProfile_serverError_userNotCached() throws Exception {
        mUserProfileHttpEndpointSync.mServerError = true;
        SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(mUsersCache.mUser.getFullName(), is(""));
        Assert.assertThat(mUsersCache.mUser.getUserId(), is(""));
        Assert.assertThat(mUsersCache.mUser.getImageUrl(), is(""));
    }

    @Test
    public void fetchUserProfile_success_successReturned() throws Exception {
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.SUCCESS));

    }

    @Test
    public void fetchUserProfile_generalFailure_failureReturned() throws Exception {
        mUserProfileHttpEndpointSync.mGeneralError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }


    @Test
    public void fetchUserProfile_serverFailure_failureReturned() throws Exception {
        mUserProfileHttpEndpointSync.mServerError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserProfile_authFailure_failureReturned() throws Exception {
        mUserProfileHttpEndpointSync.mAuthError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void fetchUserProfile_networkFailure_networkErrorReturned() throws Exception {
        mUserProfileHttpEndpointSync.mNetworkError = true;
        FetchUserProfileUseCaseSync.UseCaseResult result = SUT.fetchUserProfileSync(USER_ID);
        Assert.assertThat(result, is(FetchUserProfileUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }




    class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync {

        public String mUserId;
        public boolean mGeneralError;
        public boolean mAuthError;
        public boolean mServerError;
        public boolean mNetworkError;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            mUserId = userId;

            if (mGeneralError) {
                return new EndpointResult(EndpointResultStatus.GENERAL_ERROR, "", "", "");
            } else if (mAuthError) {
                return new EndpointResult(EndpointResultStatus.AUTH_ERROR, "", "", "");
            } else if (mServerError){
                return new EndpointResult(EndpointResultStatus.SERVER_ERROR, "","", "");
            } else if (mNetworkError){
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(EndpointResultStatus.SUCCESS, userId, FULLNAME, IMAGE_URL);
            }
        }
    }

    class UsersCacheTd implements UsersCache {

        public User mUser = new User("", "", "");

        @Override
        public void cacheUser(User user) {
            mUser = user;
        }

        @Nullable
        @Override
        public User getUser(String userId) {
            return mUser;
        }
    }
}