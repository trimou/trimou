package org.trimou;

public enum ArchiveType {

    JAR, WAR, EAR;

    public String getSuffix() {
        switch (this) {
        case EAR:
            return "ear";
        case JAR:
            return "jar";
        case WAR:
            return "war";
        default:
            throw new IllegalStateException();
        }
    }

}
