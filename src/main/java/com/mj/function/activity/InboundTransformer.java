package com.equisoft.function.activity;

import com.azure.data.tables.models.TableEntity;
import com.equisoft.function.entity.Command;
import com.equisoft.function.helpers.CsvHelper;
import com.equisoft.function.helpers.JsonHelper;
import com.equisoft.function.helpers.XmlHelper;
import com.equisoft.function.service.FunctionService;
import com.equisoft.function.utils.CommonConstants;
import com.equisoft.function.utils.TableStorageUtils;
import com.microsoft.azure.functions.ExecutionContext;
import com.microsoft.azure.functions.annotation.FunctionName;
import com.microsoft.durabletask.azurefunctions.DurableActivityTrigger;

public class InboundTransformer {

    /**
     * This is the activity function that gets invoked by the orchestrator function.
     */
    @FunctionName("Transformer")
    public Boolean transformer(@DurableActivityTrigger(name = "command") Command command,
            final ExecutionContext context) {
        context.getLogger().info(command.toString());

        try {

            TableEntity parentEntity = TableStorageUtils.retrieveEntityByPartitionkeyRowKey(context, "TestTable",
                    command.tenant, command.commandId);

            String contentType = (String) parentEntity.getProperty("contentType");

            // transform and split
            switch (contentType) {
                case "application/xml":
                    XmlHelper.split(context, parentEntity);
                    System.out.println("This is an xml file");
                    break;
                case "application/json":
                    JsonHelper.split(context, parentEntity);
                    System.out.println("this is an json file");
                    break;
                case "text/csv":
                    CsvHelper.split(context, parentEntity);
                    System.out.println("this is a csv file");
                    break;
                default:
                    System.out.println("Unknown");

                    throw new Exception("Content-type for parent file is not valid: " + contentType);

            }

            // Update parentStatus
            String partitionKey = command.tenant;
            String rowKey = command.commandId;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return true;
    }

}
