package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.example10.PingServerSyncUseCase;
import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.Status;
import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.UseCaseResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointStatus;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.verification.VerificationMode;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;

/**
 * Created by Belema Ogan on 1/24/2019.
 */

@RunWith(MockitoJUnitRunner.class)
public class FetchUserUseCaseSyncImplTest {

    public static final String USER_ID = "user1";
    public static final String USERNAME = "username";
    @Mock UsersCache mUsersCache;
    @Mock FetchUserHttpEndpointSync mFetchUserHttpEndpointSync;
    FetchUserUseCaseSyncImpl SUT;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchUserUseCaseSyncImpl(mUsersCache, mFetchUserHttpEndpointSync);
    }

    //check that fetchUser calls UsersCache's getUser

    @Test
    public void fetchUser_getUserInUsersCacheClassCalled() throws Exception {
        userCached();
        SUT.fetchUserSync(USER_ID);
        Mockito.verify(mUsersCache).getUser(USER_ID);
    }

    //check that userId is passed to fetchUserSync in FetchUserHttpEndpoint when user is not cached

    @Test
    public void fetchUser_noUserInCache_correctUserIdPassedToFetchUserHttpEndpoint() throws Exception {
        noUserInCache();
        success();
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        SUT.fetchUserSync(USER_ID);
        Mockito.verify(mFetchUserHttpEndpointSync).fetchUserSync(argumentCaptor.capture());
        assertThat(argumentCaptor.getValue(), is(USER_ID));
    }

    private void noUserInCache() {
        Mockito.when(mUsersCache.getUser(any(String.class)))
                .thenReturn(null);

    }

    //check that fetchUserSync in FetchUserHttpEndpoint is not called when user is cached

    @Test
    public void fetchUser_userCached_noInteractionWithFetchUserHttpEndpoint() throws Exception {
        userCached();
        SUT.fetchUserSync(USER_ID);
        Mockito.verifyZeroInteractions(mFetchUserHttpEndpointSync);
    }

    private void userCached() {
        Mockito.when(mUsersCache.getUser(any(String.class)))
                .thenReturn(new User("", ""));
    }


    //check that success is returned when user was fetched successfully

    @Test
    public void fetchUser_success_successReturned() throws Exception {
        success();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        Assert.assertThat(result.getStatus(), is(Status.SUCCESS));
    }

    private void success() throws NetworkErrorException {
        Mockito.when(mFetchUserHttpEndpointSync.fetchUserSync(any(String.class)))
                .thenReturn(new EndpointResult(EndpointStatus.SUCCESS, USER_ID, USERNAME));
    }


    //check that failure is returned on general error

    @Test
    public void fetchUser_generalError_failureReturned() throws Exception {
        generalError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        Assert.assertThat(result.getStatus(), is(Status.FAILURE));
    }

    private void generalError() throws NetworkErrorException {
        Mockito.when(mFetchUserHttpEndpointSync.fetchUserSync(any(String.class)))
                .thenReturn(new EndpointResult(EndpointStatus.GENERAL_ERROR, "", ""));
    }

    //check that failure is returned on auth error

    @Test
    public void fetchUser_authError_failureReturned() throws Exception {
        authError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        Assert.assertThat(result.getStatus(), is(Status.FAILURE));
    }

    private void authError() throws NetworkErrorException {
        Mockito.when(mFetchUserHttpEndpointSync.fetchUserSync(any(String.class)))
                .thenReturn(new EndpointResult(EndpointStatus.AUTH_ERROR, "", ""));
    }

    //check that network failure is returned on network error

    @Test
    public void fetchUser_networkError_networkFailureReturned() throws Exception {
        networkError();
        UseCaseResult result = SUT.fetchUserSync(USER_ID);
        Assert.assertThat(result.getStatus(), is(Status.NETWORK_ERROR));
    }

    private void networkError() throws NetworkErrorException {
        Mockito.when(mFetchUserHttpEndpointSync.fetchUserSync(any(String.class)))
                .thenThrow(new NetworkErrorException());
    }

    //check that user is cached after success

    @Test
    public void fetchUser_success_userCached() throws Exception {
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        success();
        SUT.fetchUserSync(USER_ID);
        Mockito.verify(mUsersCache).cacheUser(argumentCaptor.capture());
        Assert.assertThat(argumentCaptor.getValue().getUserId(), is(USER_ID));
        Assert.assertThat(argumentCaptor.getValue().getUsername(), is(USERNAME));
    }

    //check that user is not cached on failure

    @Test
    public void fetchUser_generalError_userNotCached() throws Exception {
        generalError();
        SUT.fetchUserSync(USER_ID);
        Mockito.verify(mUsersCache, Mockito.times(1)).getUser(any(String.class));
        Mockito.verifyNoMoreInteractions(mUsersCache);
    }

    @Test
    public void fetchUser_authError_userNotCached() throws Exception {
        authError();
        SUT.fetchUserSync(USER_ID);
        Mockito.verify(mUsersCache, Mockito.times(1)).getUser(any(String.class));
        Mockito.verifyNoMoreInteractions(mUsersCache);
    }

    @Test
    public void fetchUser_networkError_userNotCached() throws Exception {
        networkError();
        SUT.fetchUserSync(USER_ID);
        Mockito.verify(mUsersCache, Mockito.times(1)).getUser(any(String.class));
        Mockito.verifyNoMoreInteractions(mUsersCache);
    }
}