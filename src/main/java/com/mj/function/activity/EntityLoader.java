package com.equisoft.function.activity;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.azure.core.http.rest.PagedIterable;
import com.azure.data.tables.models.TableEntity;
import com.equisoft.function.entity.Command;
import com.equisoft.function.service.FunctionService;
import com.equisoft.function.utils.CommonConstants;
import com.equisoft.function.utils.FunctionUtils;
import com.equisoft.function.utils.TableStorageUtils;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.durabletask.azurefunctions.DurableActivityTrigger;

public class EntityLoader {

    /**
     * This is the activity function that gets invoked by the orchestrator function.
     */
    @FunctionName("EntitiesRetriever")
    public List<Command> entitiesRetriever(@DurableActivityTrigger(name = "command") Command command,
            final ExecutionContext context) {
        context.getLogger().info(command.toString());

        PagedIterable<TableEntity> childEntities = null;
        // FunctionService.retrievePendingChildrendByParentUniqueId(context,
        // command.tenant, command.commandId);

        List<Command> subCommandlist = new ArrayList<Command>();
        childEntities.forEach(entity -> subCommandlist.add(new Command(entity.getRowKey(), command.tenant)));

        return subCommandlist;
    }

    @FunctionName("EntityToOIPA")
    public Boolean entityToOIPA(@DurableActivityTrigger(name = "subCommand") Command subCommand,
            final ExecutionContext context) {
        context.getLogger().info(subCommand.getCommandId());

        context.getLogger().info("entityToOIPA started ....");

        try {

            String xmlContent = null;
            String fileId = null;

            String partitionKey = subCommand.tenant;
            String rowKey = subCommand.commandId;

            String xmlBody = "";

            context.getLogger().warning("entityToOIPA input XML : " + xmlBody);

            TableEntity entity = TableStorageUtils.retrieveEntityByPartitionkeyRowKey(context,
                    CommonConstants.CHILD_XML_FILE_OIPA_STATUS_TABLE_NAME, partitionKey,
                    rowKey);

            if (entity == null) {
                throw new RuntimeException(CommonConstants.ENTITY_NOT_FOUND);
            }

            xmlContent = (String) entity.getProperty(CommonConstants.TABLE_COLUMN_PAYLOAD);

            if (xmlContent == null || xmlContent.trim().length() == 0) {
                throw new RuntimeException(CommonConstants.ENTITY_DATA_MISSING);
            }
            if (fileId == null || fileId.trim().length() == 0) {
                throw new RuntimeException(CommonConstants.ENTITY_DATA_MISSING);
            }

            Map<Integer, String> mapresult = FunctionUtils.callAPIMWithXML(xmlContent, context);

            Integer responseCode = mapresult.keySet().iterator().next();
            String responseBody = mapresult.values().iterator().next();

            String status = null;

            if (responseCode == 200)
                status = CommonConstants.TABLE_VAL_STATUS_LOADED;
            else
                status = CommonConstants.TABLE_VAL_STATUS_FAILED;

            // FunctionService.updateChildPropertiesInTable(context, partitionKey, rowKey,
            // responseCode,
            // responseBody, status, false);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            context.getLogger().info("EntityXMLToOIPAProcessor ended ....");
        }

        return true;
    }
}
