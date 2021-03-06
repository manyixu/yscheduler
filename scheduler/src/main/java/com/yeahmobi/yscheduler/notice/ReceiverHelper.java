package com.yeahmobi.yscheduler.notice;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.yeahmobi.yscheduler.common.Constants;
import com.yeahmobi.yscheduler.model.Task;
import com.yeahmobi.yscheduler.model.TaskInstance;
import com.yeahmobi.yscheduler.model.User;
import com.yeahmobi.yscheduler.model.Workflow;
import com.yeahmobi.yscheduler.model.service.TaskInstanceService;
import com.yeahmobi.yscheduler.model.service.TaskService;
import com.yeahmobi.yscheduler.model.service.UserService;
import com.yeahmobi.yscheduler.model.service.WorkflowInstanceService;
import com.yeahmobi.yscheduler.model.service.WorkflowService;
import com.yeahmobi.yscheduler.model.type.ScheduleType;

/**
 * @author Ryan Sun
 */
@Service
public class ReceiverHelper {

    private static final Log        LOGGER = LogFactory.getLog(ReceiverHelper.class);

    @Autowired
    private WorkflowService         workflowService;

    @Autowired
    private WorkflowInstanceService workflowInstanceService;

    @Autowired
    private TaskService             taskService;

    @Autowired
    private TaskInstanceService     taskInstanceService;

    @Autowired
    private UserService             userService;

    // @Autowired
    // private TaskAuthorityService taskAuthorityService;
    //
    // @Autowired
    // private WorkflowAuthorityService workflowAuthorityService;

    public List<User> getReceivers(long id, ScheduleType type) {
        // TODO 暂时发给owner
        List<User> users = new ArrayList<User>();

        try {
            if (type == ScheduleType.TASK) {
                TaskInstance taskInstance = this.taskInstanceService.get(id);
                Long workflowInstanceId = taskInstance.getWorkflowInstanceId();
                if (workflowInstanceId != null) {
                    Long workflowId = this.workflowInstanceService.get(workflowInstanceId).getWorkflowId();
                    users.add(this.userService.get(this.workflowService.get(workflowId).getOwner()));
                } else {
                    Task task = this.taskService.get(taskInstance.getTaskId());
                    users.add(this.userService.get(task.getOwner()));
                    // for (Long userId : this.taskAuthorityService.listFollowUser(task.getId())) {
                    // users.add(this.userService.get(userId));
                    // }
                }
            } else if (type == ScheduleType.WORKFLOW) {
                Workflow workflow = this.workflowService.get(this.workflowInstanceService.get(id).getWorkflowId());
                users.add(this.userService.get(workflow.getOwner()));
                // for (Long userId : this.workflowAuthorityService.listFollowUser(workflow.getId())) {
                // users.add(this.userService.get(userId));
                // }
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        return users;
    }

    public User getAdminReceiver() {
        return this.userService.get(Constants.ADMIN_NAME);
    }
}
