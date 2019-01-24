package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.ArgumentMatchers.any;

public class UpdateUsernameUseCaseSyncTest {

    private static final String USERNAME = "username";
    UpdateUsernameUseCaseSync SUT;
    UpdateUsernameHttpEndpointSync mUpdateUsernameHttpEndpointSync;
    UsersCache mUsersCache;
    EventBusPoster mEventBusPoster;
    private static final String USER_ID = "userId" ;

    @Before
    public void setUp() throws Exception {
        mUpdateUsernameHttpEndpointSync = Mockito.mock(UpdateUsernameHttpEndpointSync.class);
        mUsersCache = Mockito.mock(UsersCache.class);
        mEventBusPoster = Mockito.mock(EventBusPoster.class);
        SUT = new UpdateUsernameUseCaseSync(mUpdateUsernameHttpEndpointSync, mUsersCache, mEventBusPoster);
        success();
    }

    private void success() throws NetworkErrorException {
        Mockito.when(mUpdateUsernameHttpEndpointSync.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS,
                        USER_ID, USERNAME));
    }

    //test that the username and userid is passed to endpoint

    @Test
    public void updateUsernameSync_success_userIdAndUsernamePassedToEndpoint() throws Exception {
        ArgumentCaptor<String> argumentCaptor = ArgumentCaptor.forClass(String.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verify(mUpdateUsernameHttpEndpointSync).updateUsername(argumentCaptor.capture(), argumentCaptor.capture());
        List<String> arguments = argumentCaptor.getAllValues();
        Assert.assertThat(arguments.get(0), is(USER_ID));
        Assert.assertThat(arguments.get(1), is(USERNAME));
    }


    //test that the success is returned on successful operation in UpdateUsernameHttpEndpointSync

    @Test
    public void updateUsernameSync_success_ReturnedSuccess() throws Exception {
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.SUCCESS));
    }

    //test that the correct failure is returned on operation failure in UpdateUsernameHttpEndpointSync

    @Test
    public void updateUsernameSync_generalError_ReturnedFailure() throws Exception {
        generalError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    private void generalError() throws Exception {
        Mockito.when(mUpdateUsernameHttpEndpointSync.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR,
                        "", ""));
    }

    @Test
    public void updateUsernameSync_authError_ReturnedFailure() throws Exception {
        authError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    private void authError() throws Exception{
        Mockito.when(mUpdateUsernameHttpEndpointSync.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.AUTH_ERROR,
                        "", ""));
    }

    @Test
    public void updateUsernameSync_serverError_ReturnedFailure() throws Exception {
        serverError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    private void serverError() throws Exception {
        Mockito.when(mUpdateUsernameHttpEndpointSync.updateUsername(any(String.class), any(String.class)))
                .thenReturn(new UpdateUsernameHttpEndpointSync.EndpointResult(UpdateUsernameHttpEndpointSync.EndpointResultStatus.SERVER_ERROR,
                        "", ""));
    }

    @Test
    public void updateUsernameSync_networkError_ReturnedFailure() throws Exception {
        networkError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USER_ID, USERNAME);
        Assert.assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }

    private void networkError() throws Exception{
        Mockito.when(mUpdateUsernameHttpEndpointSync.updateUsername(any(String.class), any(String.class)))
                .thenThrow(new NetworkErrorException());
    }

    //test that user is cached on successful execution

    @Test
    public void updateUsernameSync_success_userCachedCalled() throws Exception {
        ArgumentCaptor<User> argumentCaptor = ArgumentCaptor.forClass(User.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verify(mUsersCache).cacheUser(argumentCaptor.capture());
        User user = argumentCaptor.getValue();
        Assert.assertThat(user.getUserId(), is(USER_ID));
        Assert.assertThat(user.getUsername(), is(USERNAME));
    }

    //test that the user cache is not called

    @Test
    public void updateUsernameSync_generalFailure_userCacheNotCalled() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verifyZeroInteractions(mUsersCache);
    }

    @Test
    public void updateUsernameSync_authFailure_userCacheNotCalled() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verifyZeroInteractions(mUsersCache);
    }

    @Test
    public void updateUsernameSync_serverFailure_userCacheNotCalled() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verifyZeroInteractions(mUsersCache);
    }

    @Test
    public void updateUsernameSync_networkFailure_userCacheNotCalled() throws Exception {
        networkError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verifyZeroInteractions(mUsersCache);
    }

    //test that event is posted on successful transaction

    @Test
    public void updateUsernameSync_success_eventPostCalled() throws Exception {
        ArgumentCaptor<Object> argumentCaptor = ArgumentCaptor.forClass(Object.class);
        SUT.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verify(mEventBusPoster).postEvent(argumentCaptor.capture());
        Assert.assertThat(argumentCaptor.getValue(), is(instanceOf(UserDetailsChangedEvent.class)));
    }

    //test that event bus is not called

    @Test
    public void updateUsernameSync_generalFailure_evenPostNotCalled() throws Exception {
        generalError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verifyZeroInteractions(mEventBusPoster);
    }

    @Test
    public void updateUsernameSync_authFailure_evenPostNotCalled() throws Exception {
        authError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verifyZeroInteractions(mEventBusPoster);
    }

    @Test
    public void updateUsernameSync_serverFailure_evenPostNotCalled() throws Exception {
        serverError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verifyZeroInteractions(mEventBusPoster);
    }

    @Test
    public void updateUsernameSync_networkFailure_evenPostNotCalled() throws Exception {
        networkError();
        SUT.updateUsernameSync(USER_ID, USERNAME);
        Mockito.verifyZeroInteractions(mEventBusPoster);
    }

    

}