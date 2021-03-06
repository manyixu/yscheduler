package com.yeahmobi.yscheduler.web.controller.workflow;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.yeahmobi.yscheduler.common.Paginator;
import com.yeahmobi.yscheduler.model.WorkflowInstance;
import com.yeahmobi.yscheduler.model.common.Query;
import com.yeahmobi.yscheduler.model.common.Query.WorkflowScheduleType;
import com.yeahmobi.yscheduler.model.service.WorkflowInstanceService;
import com.yeahmobi.yscheduler.model.service.WorkflowService;
import com.yeahmobi.yscheduler.model.type.WorkflowInstanceStatus;
import com.yeahmobi.yscheduler.web.controller.AbstractController;
import com.yeahmobi.yscheduler.workflow.WorkflowExecutor;

/**
 * @author Leo.Liang
 */
@Controller
@RequestMapping(value = { WorkflowInstanceController.SCREEN_NAME })
public class WorkflowInstanceController extends AbstractController {

    public static final String      SCREEN_NAME = "workflow/instance";

    @Autowired
    private WorkflowService         workflowService;

    @Autowired
    private WorkflowInstanceService instanceService;

    @Autowired
    private WorkflowExecutor        workflowExecutor;

    @RequestMapping(value = { "" })
    public ModelAndView index(Integer workflowInstanceStatus, Integer workflowScheduleType, Integer pageNum,
                              long workflowId) {
        Map<String, Object> map = new HashMap<String, Object>();

        Query query = buildQuery(map, workflowInstanceStatus, workflowScheduleType);

        Paginator paginator = new Paginator();
        pageNum = ((pageNum == null) || (pageNum < 0)) ? 0 : pageNum;

        List<WorkflowInstance> instances = this.instanceService.list(query, workflowId, pageNum, paginator);

        int successCount = 0;
        int totalRunCount = 0;
        for (WorkflowInstance workflowInstance : this.instanceService.listAll(workflowId)) {
            if (workflowInstance.getStatus().isCompleted()) {
                totalRunCount++;
                if (workflowInstance.getStatus() == WorkflowInstanceStatus.SUCCESS) {
                    successCount++;
                }
            }
        }

        map.put("workflow", this.workflowService.get(workflowId));
        map.put("list", instances);
        map.put("paginator", paginator);
        map.put("successRate", (int) ((successCount * 100.00d) / totalRunCount));
        map.put("totalRunCount", totalRunCount);

        return screen(map, SCREEN_NAME);
    }

    private Query buildQuery(Map<String, Object> map, Integer workflowInstanceStatus, Integer scheduleType) {
        Query query = new Query();

        if (workflowInstanceStatus != null) {
            WorkflowInstanceStatus status = WorkflowInstanceStatus.valueOf(workflowInstanceStatus);
            query.setWorkflowInstanceStatus(status);
        }

        if (scheduleType != null) {
            WorkflowScheduleType type = WorkflowScheduleType.valueOf(scheduleType);
            query.setWorkflowScheduleType(type);
        }

        map.put("query", query);
        map.put("allStatus", WorkflowInstanceStatus.values());
        map.put("scheduleTypes", WorkflowScheduleType.values());

        return query;
    }

    @RequestMapping(value = "/cancel", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public Object cancel(HttpSession session, long workflowInstanceId) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            this.workflowExecutor.cancel(workflowInstanceId);

            map.put("success", true);
        } catch (IllegalArgumentException e) {
            map.put("success", false);
            map.put("errorMsg", e.getMessage());
        } catch (Exception e) {
            map.put("success", false);
            map.put("errorMsg", e.getMessage());
        }
        return JSON.toJSONString(map);

    }

    @RequestMapping(value = "/rerun", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    @ResponseBody
    public Object rerun(long instanceId) {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            this.workflowExecutor.restore(this.instanceService.get(instanceId));
            map.put("success", true);
        } catch (IllegalArgumentException e) {
            map.put("success", false);
            map.put("errorMsg", e.getMessage());
        } catch (Exception e) {
            map.put("success", false);
            map.put("errorMsg", e.getMessage());
        }
        return JSON.toJSONString(map);

    }
}
