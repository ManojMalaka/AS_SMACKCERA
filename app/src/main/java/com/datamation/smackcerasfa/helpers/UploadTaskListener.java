package com.datamation.smackcerasfa.helpers;


import com.datamation.smackcerasfa.model.apimodel.TaskType;

import java.util.List;

public interface UploadTaskListener {
    void onTaskCompleted(TaskType taskType, List<String> list);
    void onTaskCompleted(List<String> list);
}