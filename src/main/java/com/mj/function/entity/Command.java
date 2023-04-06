package com.equisoft.function.entity;

import java.io.Serializable;

public class Command implements Serializable {

    public String commandId;
    public String tenant;

    public Command() {
    }

    public Command(String commandId, String tenant) {
        this.commandId = commandId;
        this.tenant = tenant;
    }

    public String getCommandId() {
        return commandId;
    }

    public void setCommandId(String commandId) {
        this.commandId = commandId;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    @Override
    public String toString() {
        return "Command [commandId=" + commandId + ", tenant=" + tenant + "]";
    }

}
