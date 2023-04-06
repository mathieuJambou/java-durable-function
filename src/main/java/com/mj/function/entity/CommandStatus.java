package com.equisoft.function.entity;

import java.util.ArrayList;

public class CommandStatus extends Command {

    public String Status;
    public String parentStatus;
    public String processStatus;

    public ArrayList<ChildStatus> childStatusList = new ArrayList<>();

    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

    public String getParentStatus() {
        return parentStatus;
    }

    public void setParentStatus(String parentStatus) {
        this.parentStatus = parentStatus;
    }

    public String getProcessStatus() {
        return processStatus;
    }

    public void setProcessStatus(String processStatus) {
        this.processStatus = processStatus;
    }

    public ArrayList<ChildStatus> getChildStatusList() {
        return childStatusList;
    }

    public void setChildStatusList(ArrayList<ChildStatus> childStatusList) {
        this.childStatusList = childStatusList;
    }

    public class ChildStatus {

        public int index;
        public String status;
        public String response;
    }

}
