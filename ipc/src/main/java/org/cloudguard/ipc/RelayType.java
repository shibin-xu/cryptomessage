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

    UIResultFoundContact,
    
    UIResultForSpeechSend,
    UISpeechUpdate,
    UISpeechNextIdentifier,


    CRYPTOAddContactString,
    CRYPTOAddContactFile,
    CRYPTORemoveContact,
    CRYPTORenameContact,
    
    CRYPTOGetContactArchive,

    CRYPTOSend,

    CRYPTOFakeSpam,
    CRYPTOFakeReceive,

    UIPublicKey,
    UISecurity,
    
}
