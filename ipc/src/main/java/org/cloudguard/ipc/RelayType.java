package org.cloudguard.ipc;

public enum RelayType {
    UIResultForConnect,
    UIResultForDisconnect,
    UIResultForKeyPath,
    UIResultForNewAccount,
    UIResultForExistingLogin,
    UIResultForAddUser,
    UIResultForRemoveUser,

    UIResultForAllUser,
    UIResultForUserArchive,
    
    UIResultForMessageSend,
    UIMessageReceive,

    CRYPTOOpenConnectionToServer,
    CRYPTODisconnectFromServer,
    CRYPTOLoginNewAccount,
    CRYPTOLoginExistingAccount,
    CRYPTOSetFilePathOfKey,
    CRYPTOAddUser,
    CRYPTORemoveUser,
    
    CRYPTOGetAllUser,
    CRYPTOGetUserArchive,

    CRYPTOSend,

    CRYPTOFakeReceive,
    
}
