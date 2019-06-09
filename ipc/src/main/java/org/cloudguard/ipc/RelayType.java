package org.cloudguard.ipc;

public enum RelayType {
    CRYPTOTick,

    UIResultForConnect,
    UIResultForDisconnect,
    CRYPTOConnectWithKeys,
    CRYPTODisconnectFromServer,

    UIResultForAddContact,
    UIResultForRemoveContact,
    UIResultForRenameContact,

    UIResultForContact,
    
    UIResultForSpeechSend,
    UISpeechUpdate,
    UISpeechNextIdentifier,


    CRYPTOAddContactString,
    CRYPTOAddContactFile,
    CRYPTORemoveContact,
    CRYPTORenameContact,
    
    CRYPTOGetContactArchive,

    CRYPTOSend,

    CRYPTOFakeFill,
    CRYPTOFakeReceive,

    UIPublicKey,
    UISecurity,
    
}
