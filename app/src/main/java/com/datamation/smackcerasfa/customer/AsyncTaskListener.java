package com.datamation.smackcerasfa.customer;

import com.datamation.smackcerasfa.settings.TaskType;

/**
 * Created by Sathiyaraja on 7/3/2018.
 */

public interface AsyncTaskListener {
    void onTaskCompleted(TaskType taskType);
}
