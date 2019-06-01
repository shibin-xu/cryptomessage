package org.cloudguard.ipc;

public enum RelayType {
    UIResultForConnect,
    UIResultForDisconnect,
    UIResultForKeyPath,
    UIResultForNewAccount,
    UIResultForExistingLogin,
    UIResultForAddContact,
    UIResultForRemoveContact,
    UIResultForRenameContact,

    UIResultForAllContact,
    UIResultForContactArchive,
    
    UIResultForMessageSend,
    UIMessageReceive,

    CRYPTOOpenConnectionToServer,
    CRYPTODisconnectFromServer,
    CRYPTOLoginNewAccount,
    CRYPTOLoginExistingAccount,
    CRYPTOSetFilePathOfKey,
    CRYPTOAddContact,
    CRYPTORemoveContact,
    CRYPTORenameContact,
    
    CRYPTOGetAllContact,
    CRYPTOGetContactArchive,

    CRYPTOSend,

    CRYPTOFakeReceive,
    
}
