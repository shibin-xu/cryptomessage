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
    
    UIResultForSpeechSend,
    UISpeechReceive,


    CRYPTOAddContact,
    CRYPTORemoveContact,
    CRYPTORenameContact,
    
    CRYPTOGetAllContact,
    CRYPTOGetContactArchive,

    CRYPTOSend,

    CRYPTOFakeFill,
    CRYPTOFakeReceive,

    UIPublicKey,
    UISecurity,
    
}
