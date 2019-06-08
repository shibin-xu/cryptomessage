package org.cloudguard.ipc;

public enum RelayType {
    UIResultForConnect,
    UIResultForDisconnect,
    CRYPTOConnectWithKeys,
    CRYPTODisconnectFromServer,

    UIResultForAddContact,
    UIResultForRemoveContact,
    UIResultForRenameContact,

    UIResultForContact,
    UIResultForArchive,
    
    UIResultForMessageSend,
    UIMessageReceive,


    CRYPTOAddContact,
    CRYPTORemoveContact,
    CRYPTORenameContact,
    
    CRYPTOGetAllContact,
    CRYPTOGetContactArchive,

    CRYPTOSend,

    CRYPTOFakeReceive,

    UIPublicKey,
    UISecurity,
    
}
