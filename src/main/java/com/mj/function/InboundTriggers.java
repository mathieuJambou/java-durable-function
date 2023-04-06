package com.equisoft.function;

import java.io.IOException;
import java.util.HashMap;
import java.util.Optional;

import com.azure.core.http.rest.PagedIterable;
import com.azure.data.tables.models.TableEntity;
import com.equisoft.function.entity.Command;
import com.equisoft.function.entity.CommandStatus;
import com.equisoft.function.entity.EventSchema;
import com.equisoft.function.helpers.CommandHelper;
import com.equisoft.function.service.FunctionService;
import com.equisoft.function.utils.BlobStorageUtils;
import com.equisoft.function.utils.CommonConstants;
import com.equisoft.function.utils.FunctionUtils;
import com.equisoft.function.utils.QueueStorageUtils;
import com.equisoft.function.utils.TableStorageUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.HttpMethod;
import com.microsoft.azure.functions.HttpRequestMessage;
import com.microsoft.azure.functions.HttpResponseMessage;
import com.microsoft.azure.functions.HttpStatus;
import com.microsoft.azure.functions.annotation.AuthorizationLevel;
import com.microsoft.azure.functions.annotation.EventGridTrigger;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.azure.functions.annotation.HttpTrigger;
import com.microsoft.azure.functions.annotation.QueueTrigger;
import com.microsoft.durabletask.DurableTaskClient;
import com.microsoft.durabletask.OrchestrationMetadata;
import com.microsoft.durabletask.azurefunctions.DurableClientContext;
import com.microsoft.durabletask.azurefunctions.DurableClientInput;

public class InboundTriggers {

    @FunctionName("InboundRequest")
    public HttpResponseMessage inboundRequest(
            @HttpTrigger(name = "req", methods = HttpMethod.POST, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            final ExecutionContext context) {

        context.getLogger().info("InboundRequest started ....");
        Command command = null;

        try {

            String commandId = FunctionUtils.getUUID();
            String currentTime = FunctionUtils.getCurrentlyDateTimeWithPattern("yyyyMMddHHmmss.SSS");

            // examine inbound call
            String tenant = request.getHeaders().get("tenant");
            String data = request.getBody().get();

            if (tenant == null || tenant.trim().length() == 0) {
                throw new RuntimeException("TODO");
            }

            command = new Command(commandId, tenant);

            String uniqueName = String.format("%s_%s_%s", tenant,
                    currentTime,
                    commandId);

            // Store payload into blob
            BlobStorageUtils.writeBlob(context, CommonConstants.STORAGEACCOUNT_CONTAINERNAME, data, uniqueName);

            // create metadata in table
            String parentTableName = "TestTable";
            HashMap<String, Object> properties = new HashMap<>();
            properties.put(CommonConstants.TABLE_COLUMN_UNIQUENAME, uniqueName);
            properties.put(CommonConstants.TABLE_COLUMN_STATUS, CommonConstants.TABLE_VAL_STATUS_NEW);

            TableStorageUtils.createEntity(context, parentTableName, tenant, commandId, properties);

            ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
            String json = ow.writeValueAsString(command);

            // send ID to queue
            QueueStorageUtils.addQueueMessage(context, CommonConstants.FILE_SPLITTER_QUEUE_NAME,
                    json);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return request.createResponseBuilder(HttpStatus.OK).body(command.getCommandId()).build();
    }

    @FunctionName("InboundStatus")
    public HttpResponseMessage inboundStatus(
            @HttpTrigger(name = "req", methods = HttpMethod.GET, authLevel = AuthorizationLevel.FUNCTION) HttpRequestMessage<Optional<String>> request,
            @DurableClientInput(name = "durableContext") DurableClientContext durableContext,
            final ExecutionContext context) {

        final String instanceId = request.getQueryParameters().get("instanceId");
        final String tenant = request.getHeaders().get("tenant");

        DurableTaskClient client = durableContext.getClient();

        OrchestrationMetadata omd = client.getInstanceMetadata(instanceId, true);

        if (!omd.isInstanceFound()) {
            String body = "instance not found";
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body(body).build();
        }
        Command command = CommandHelper.CreatecommandFromString(omd.getSerializedInput());
        if (command == null || !command.tenant.equals(tenant)) {
            String body = "instance not found";
            return request.createResponseBuilder(HttpStatus.INTERNAL_SERVER_ERROR).body(body).build();
        }

        CommandStatus cs = new CommandStatus();
        cs.commandId = command.commandId;
        cs.tenant = command.tenant;
        cs.processStatus = omd.getRuntimeStatus().toString();

        TableEntity parentEntity = TableStorageUtils.retrieveEntityByPartitionkeyRowKey(context, "TestTable",
                command.tenant, command.commandId);
        cs.parentStatus = (String) parentEntity.getProperty(CommonConstants.TABLE_COLUMN_STATUS);

        PagedIterable<TableEntity> childEntities = null;
        // FunctionService.retrievePendingChildrendByParentUniqueId(context,
        // command.tenant, command.commandId);

        for (TableEntity entity : childEntities) {
            CommandStatus.ChildStatus childStatus = cs.new ChildStatus();
            childStatus.index = (int) entity.getProperty(CommonConstants.TABLE_COLUMN_INDEX);
            childStatus.status = (String) entity.getProperty(CommonConstants.TABLE_COLUMN_STATUS);
            childStatus.response = (String) entity.getProperty(CommonConstants.TABLE_COLUMN_RESPONSE);
            cs.getChildStatusList().add(childStatus);
        }

        return request.createResponseBuilder(HttpStatus.OK).body(cs).build();
    }

    @FunctionName("StartOrchestration")
    public void startOrchestration(
            @QueueTrigger(name = "message", queueName = CommonConstants.FILE_SPLITTER_QUEUE_NAME, connection = "AzureWebJobsStorage") String message,
            @DurableClientInput(name = "durableContext") DurableClientContext durableContext,
            final ExecutionContext context) {
        context.getLogger().info("Java Queue trigger processed a request to start orchestration");

        Command command = CommandHelper.CreatecommandFromString(message);

        DurableTaskClient client = durableContext.getClient();
        String instanceId = client.scheduleNewOrchestrationInstance("InboudProcessor", command,
                command.commandId);
        context.getLogger().info("Created new Java orchestration with instance ID = "
                + instanceId);

        // return durableContext.createCheckStatusResponse(request, instanceId);
    }

    // offer same functionality through blob event
    @FunctionName("BlobEventTrigger")
    public void blobEventTrigger(
            @EventGridTrigger(name = "event") EventSchema event,
            final ExecutionContext context) {

        context.getLogger().info("Event content: ");
        context.getLogger().info("Subject: " + event.subject);
        context.getLogger().info("Time: " + event.eventTime); // automatically converted to Date by the runtime
        context.getLogger().info("Id: " + event.id);
        context.getLogger().info("Data: " + event.data);
    }

    // offer same functionality through queue trigger
    @FunctionName("QueueTriggerEvent")
    public void queueTriggerEvent(
            @QueueTrigger(name = "message", queueName = "inboundqueue", connection = "MyStorageConnectionAppSetting") String message,
            final ExecutionContext context) {
        context.getLogger().info("Queue message: " + message);
    }
}
