package com.safety.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.ServletActionContext;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;
import com.safety.entity.MonitoringItem;
import com.safety.entity.User;
import com.safety.service.IMonitorService;

public class MonitorAction extends ActionSupport {

	private IMonitorService monitorService;
	private MonitoringItem item;
	private String token;
	private Map<String, Object> jsonResult;

	public String addItem() {
		if (!monitorService.checkTarget(item))
			return ERROR;
		monitorService.saveItem(item);
		return SUCCESS;
	}

	public String deleteItem() {
		MonitoringItem i = monitorService.findItemById(item.getId());
		monitorService.deleteItem(i);
		return SUCCESS;
	}

	public String updateItem() {
		monitorService.updateItem(item);
		return SUCCESS;
	}

	public String listTTItem() {
		List<MonitoringItem> items = monitorService.findItemsByTaskType(item
				.getTaskType().ordinal());
		HttpServletRequest request = ServletActionContext.getRequest();
		request.setAttribute("items", items);
		return SUCCESS;
	}

	public String listAllItem() {
		Map<String, Object> result = new HashMap<String, Object>();
		User user = (User) ActionContext.getContext().getSession().get("user");
		if (!token.equals(user.getToken())) {
			return ERROR;
		}
		Set<MonitoringItem> items = user.getItems();
		int[] cnt = new int[8];
		for (MonitoringItem item : items) {
			switch (item.getTaskType()) {
			case HTTP:
				if (item.getState() == MonitoringItem.State.NORMAL)
					cnt[0]++;
				else
					cnt[1]++;
				break;
			case DNS:
				if (item.getState() == MonitoringItem.State.NORMAL)
					cnt[2]++;
				else
					cnt[3]++;
				break;
			case PING:
				if (item.getState() == MonitoringItem.State.NORMAL)
					cnt[4]++;
				else
					cnt[5]++;
				break;
			default:
				break;
			}
		}
		result.put("http", new int[] { cnt[0], cnt[1] });
		result.put("dns", new int[] { cnt[2], cnt[3] });
		result.put("ping", new int[] { cnt[4], cnt[5] });
		jsonResult = result;
		return SUCCESS;
	}

	public IMonitorService getMonitorService() {
		return monitorService;
	}

	public void setMonitorService(IMonitorService monitorService) {
		this.monitorService = monitorService;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}
