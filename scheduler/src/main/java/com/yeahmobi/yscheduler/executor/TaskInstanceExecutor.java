package com.yeahmobi.yscheduler.executor;

import com.yeahmobi.yscheduler.model.TaskInstance;
import com.yeahmobi.yscheduler.model.type.TaskInstanceStatus;

/**
 * @author atell
 */
public interface TaskInstanceExecutor {

    void submit(TaskInstance instance);

    TaskInstanceStatus getStatus(long instanceId);

    void cancel(long instanceId);

}
