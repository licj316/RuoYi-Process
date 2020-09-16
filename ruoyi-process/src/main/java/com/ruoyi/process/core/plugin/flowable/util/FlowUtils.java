package com.ruoyi.process.core.plugin.flowable.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.ruoyi.process.core.plugin.flowable.converter.CustomUserTaskJsonConverter;
import com.ruoyi.process.core.plugin.flowable.dto.UserTaskExtensionDTO;
import com.ruoyi.process.core.plugin.flowable.enums.TaskStatusEnum;
import com.ruoyi.process.core.plugin.flowable.vo.FlowTaskVO;
import com.ruoyi.system.domain.SysRole;
import com.ruoyi.system.domain.SysUser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.ExtensionElement;
import org.flowable.bpmn.model.FlowableListener;
import org.flowable.bpmn.model.UserTask;
import org.flowable.editor.constants.ModelDataJsonConstants;
import org.flowable.editor.language.json.converter.BpmnJsonConverter;
import org.flowable.engine.impl.bpmn.behavior.MultiInstanceActivityBehavior;
import org.flowable.engine.repository.Model;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntity;
import org.flowable.idm.engine.impl.persistence.entity.GroupEntityImpl;
import org.flowable.idm.engine.impl.persistence.entity.UserEntity;
import org.flowable.idm.engine.impl.persistence.entity.UserEntityImpl;
import org.flowable.task.api.Task;
import org.flowable.task.api.TaskInfo;
import org.flowable.task.api.TaskQuery;
import org.flowable.task.service.delegate.BaseTaskListener;
import org.nutz.json.Json;
import org.nutz.json.JsonFormat;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author 黄川 huchuc@vip.qq.com
 * @date: 2019/4/9
 */
@Slf4j
public class FlowUtils {

	public static ExtensionElement buildExtensionElement(String name, String textValue) {
		ExtensionElement extensionElement = new ExtensionElement();
		extensionElement.setName(name);
		extensionElement.setNamespacePrefix("nutzfw");
		extensionElement.setNamespace(BpmnJsonConverter.MODELER_NAMESPACE);
		extensionElement.setElementText(textValue);
		return extensionElement;
	}

	public static List<UserEntity> toFlowableUserList(List<SysUser> users) {
		List<UserEntity> entities = new ArrayList<>(users.size());
		users.forEach(userAccount -> entities.add(toFlowableUser(userAccount)));
		return entities;
	}

	public static UserEntity toFlowableUser(SysUser user) {
		if (user == null) {
			return null;
		}
		UserEntity userEntity = new UserEntityImpl();
		userEntity.setId(user.getUserId().toString());
		userEntity.setFirstName(user.getUserName());
		userEntity.setEmail(user.getEmail());
		userEntity.setRevision(1);
		return userEntity;
	}

	public static List<GroupEntity> toFlowableGroupList(List<SysRole> roles) {
		List<GroupEntity> entities = new ArrayList<>(roles.size());
		roles.forEach(role -> entities.add(toFlowableGroup(role)));
		return entities;
	}

	public static GroupEntity toFlowableGroup(SysRole role) {
		if (role == null) {
			return null;
		}
		GroupEntity groupEntity = new GroupEntityImpl();
		groupEntity.setId(role.getRoleId().toString());
		groupEntity.setName(role.getRoleName());
		groupEntity.setType("ROLE");
		groupEntity.setRevision(1);
		return groupEntity;
	}

	public static Model buildModel(Model model, String categoryId, String name, String key, String description) {
		description = Strings.sNull(description);
		name = Strings.sNull(name);
		key = Strings.sNull(key).replaceAll(" ", "");
		NutMap nutMap = NutMap.NEW();
		nutMap.put("category", categoryId);
		nutMap.put("key", key);
		nutMap.put(ModelDataJsonConstants.MODEL_NAME, name);
		nutMap.put(ModelDataJsonConstants.MODEL_REVISION, 1);
		nutMap.put(ModelDataJsonConstants.MODEL_DESCRIPTION, description);
		model.setMetaInfo(Json.toJson(nutMap, JsonFormat.compact()));
		model.setKey(key);
		model.setName(name);
		model.setCategory(categoryId);
		model.setKey(key);
		return model;
	}


	public static NutMap toNutMap(JsonNode displayNode) {
		return NutMap.WRAP(displayNode.toString());
	}


	public static UserTaskExtensionDTO getUserTaskExtension(UserTask userTask) {
		UserTaskExtensionDTO dto = null;
		if (userTask != null) {
			List<ExtensionElement> properites = userTask.getExtensionElements().get(CustomUserTaskJsonConverter.USER_TASK_EXTENSION_ELEMENT_NAME);
			if (properites.size() > 0) {
				String extensionElementText = properites.get(0).getElementText();
				if (Strings.isNotBlank(extensionElementText)) {
					dto = Json.fromJson(UserTaskExtensionDTO.class, extensionElementText);
				}
			}
			if (userTask.getBehavior() instanceof MultiInstanceActivityBehavior && dto != null) {
				//是多实例节点
				dto.setMultiInstanceNode(true);
			}
		}
		if (dto != null) {
			dto.setUserTaskFormKey(userTask.getFormKey());
			dto.setUserTaskName(userTask.getName());
			dto.setUserTaskId(userTask.getId());
			dto.setUserTaskDocumentation(userTask.getDocumentation());
		}
		return dto;
	}

	/**
	 * @param flowTaskVO
	 * @param task
	 * @param currentAssigneeUserName 当前处理人
	 */
	public static void setFlowTaskVo(FlowTaskVO flowTaskVO, TaskInfo task, String currentAssigneeUserName) {
		if (task instanceof Task) {
			flowTaskVO.setDelegateStatus(((Task) task).getDelegationState());
			flowTaskVO.setStatus(Strings.isNotBlank(task.getAssignee()) ? TaskStatusEnum.TODO : TaskStatusEnum.CLAIM);
		} else {
			flowTaskVO.setStatus(TaskStatusEnum.FINISH);
		}
		if (!Objects.equals(task.getAssignee(), currentAssigneeUserName)) {
			//自己不是当前处理，那么只能进行查看
			flowTaskVO.setStatus(TaskStatusEnum.FINISH);
		}
		flowTaskVO.setCategory(task.getCategory());
		flowTaskVO.setDelegateUserName(task.getOwner());
		flowTaskVO.setTaskId(task.getId());
		flowTaskVO.setTaskDefKey(task.getTaskDefinitionKey());
		flowTaskVO.setTaskName(task.getName());
		flowTaskVO.setAssignee(task.getAssignee());
		flowTaskVO.setCreateTime(task.getCreateTime());
		flowTaskVO.setProcInsId(task.getProcessInstanceId());
		flowTaskVO.setExecutionId(task.getExecutionId());
		flowTaskVO.setClaimTime(task.getClaimTime());
		flowTaskVO.setProcDefId(task.getProcessDefinitionId());
	}


	public static void buildTodoQuery(TaskQuery todoTaskQuery, String userId, List<String> roleKeys) {
		todoTaskQuery.or().taskAssignee(userId).taskCandidateUser(userId);
		if (CollectionUtils.isNotEmpty(roleKeys)) {
			todoTaskQuery.taskCandidateGroupIn(roleKeys);
		}
		todoTaskQuery.endOr();
	}

	/**
	 * 多实例添加自定义事件监听
	 *
	 * @param taskListeners
	 */
	public static void addCompleteMultiInstanceTaskListener(List<FlowableListener> taskListeners) {
		FlowableListener listener = new FlowableListener();
		listener.setEvent(BaseTaskListener.EVENTNAME_COMPLETE);
		listener.setImplementationType("delegateExpression");
		listener.setImplementation("${multiInstanceCompleteTaskListener}");
		taskListeners.add(listener);
	}

	public static String parseFlowKeyWordVal(String keyWordConfig, Map<String, Object> formData) {
		if (StringUtils.isBlank(keyWordConfig)) {
			return null;
		} else {
			String[] configKeyArr = keyWordConfig.split("\\.");
			if(configKeyArr.length != 2) {
				throw new RuntimeException("流程关键字必须为【xxx.xxx】格式！");
			}
			Object formJsonObj = formData.get(configKeyArr[0]);
			String keyVal;
			if (formJsonObj != null) {
				String formJsonStr = formJsonObj.toString();
				if (StringUtils.isNotBlank(formJsonStr)) {
					ObjectMapper objectMapper = new ObjectMapper();
					try {
						JsonNode jsonNode = objectMapper.readTree(formJsonStr);
						JsonNode valNode = jsonNode.get(configKeyArr[1]);
						if(null != valNode) {
							if (JsonNodeType.STRING.equals(valNode.getNodeType())) {
								keyVal = valNode.textValue();
							} else if (JsonNodeType.NUMBER.equals(valNode.getNodeType())) {
								keyVal = String.valueOf(valNode.longValue());
							} else {
								throw new RuntimeException("流程关键字必须为字符串或数值类型！");
							}
						} else {
							keyVal = null;
						}
					} catch (Exception e) {
						log.error("解析流程关键字出错！", e);
						throw new RuntimeException("解析流程关键字出错！");
					}
				} else {
					keyVal = null;
				}
			} else {
				keyVal = null;
			}
			return keyVal;
		}
	}

}
