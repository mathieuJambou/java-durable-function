package com.equisoft.function.utils;

import java.util.List;
import java.util.Map;

import com.azure.core.http.rest.PagedIterable;
import com.azure.data.tables.TableClient;
import com.azure.data.tables.TableServiceClient;
import com.azure.data.tables.TableServiceClientBuilder;
import com.azure.data.tables.models.ListEntitiesOptions;
import com.azure.data.tables.models.TableEntity;
import com.azure.data.tables.models.TableServiceException;
import com.microsoft.azure.functions.ExecutionContext;

public class TableStorageUtils {

    private static String connectStr = System.getenv("AzureWebJobsStorage");

    public static String createEntity(final ExecutionContext context, String tableName,
            String partitionKey, String rowKey, Map<String, Object> properties) {
        try {
            context.getLogger().info("FunctionUtils.createEntity() started for Table : " + tableName +
                    " partition : " + partitionKey + " row : " + rowKey);

            TableServiceClient tableServiceClient = new TableServiceClientBuilder()
                    .connectionString(connectStr)
                    .buildClient();

            tableServiceClient.createTableIfNotExists(tableName);
            TableClient tableClient = tableServiceClient.getTableClient(tableName);

            TableEntity entity = new TableEntity(partitionKey, rowKey);
            properties.forEach((k, v) -> entity.addProperty(k, v));

            tableClient.createEntity(entity);

            return rowKey;

        } catch (TableServiceException e) {
            context.getLogger().warning(
                    "createEntity exception for Table : " + tableName +
                            " partition : " + partitionKey + " row : " + rowKey
                            + " Exception : " + e.toString());
            throw e;
        } finally {
            context.getLogger().info("FunctionUtils.createEntity() ended for Table : " + tableName +
                    " partition : " + partitionKey + " row : " + rowKey);
        }
    }

    public static void updateEntity(final ExecutionContext context, String tableName,
            String partitionKey, String rowKey, Map<String, Object> properties) {

        try {
            context.getLogger().info("FunctionUtils.updateEntity() started for Table : " + tableName +
                    " partition : " + partitionKey + " row : " + rowKey);

            TableServiceClient tableServiceClient = new TableServiceClientBuilder()
                    .connectionString(connectStr)
                    .buildClient();

            tableServiceClient.createTableIfNotExists(tableName);
            TableClient tableClient = tableServiceClient.getTableClient(tableName);

            TableEntity entity = new TableEntity(partitionKey, rowKey);
            properties.forEach((k, v) -> entity.addProperty(k, v));

            tableClient.updateEntity(entity);

        } catch (TableServiceException e) {
            context.getLogger().warning(
                    "updateEntity exception for Table : " + tableName +
                            " partition : " + partitionKey + " row : " + rowKey
                            + " Exception : " + e.toString());
            throw e;
        } finally {
            context.getLogger().info("FunctionUtils.updateEntity() ended for Table : " + tableName +
                    " partition : " + partitionKey + " row : " + rowKey);
        }
    }

    public static TableEntity retrieveEntityByPartitionkeyRowKey(final ExecutionContext context, String tableName,
            String partitionKey, String rowKey) {

        TableEntity entity = null;
        try {
            context.getLogger()
                    .info("FunctionUtils.retrieveEntityByPartitionkeyRowKey() started for RowKey : "
                            + rowKey);

            TableServiceClient tableServiceClient = new TableServiceClientBuilder()
                    .connectionString(connectStr)
                    .buildClient();

            TableClient tableClient = tableServiceClient
                    .getTableClient(tableName);

            entity = tableClient.getEntity(partitionKey, rowKey);

        } catch (Exception e) {
            context.getLogger()
                    .warning("retrieveEntityByPartitionkeyRowKey exception for RowKey : " + rowKey
                            + " Exception : " + e.toString());
            throw e;
        } finally {
            context.getLogger()
                    .info("FunctionUtils.retrieveEntityByPartitionkeyRowKey() ended for for RowKey : " + rowKey);
        }

        return entity;
    }

    public static PagedIterable<TableEntity> retrieveEntitiesByTableFilterWithProperties(
            final ExecutionContext context,
            String tableName, String filter, List<String> propertiesToSelect) {

        PagedIterable<TableEntity> entitieslist = null;

        try {
            context.getLogger()
                    .info("FunctionUtils.retrieveEntitiesByTableFilterWithProperties() started for ");

            TableServiceClient tableServiceClient = new TableServiceClientBuilder()
                    .connectionString(connectStr)
                    .buildClient();
            TableClient childTableClient = tableServiceClient
                    .getTableClient(tableName);

            ListEntitiesOptions optionsPendingRecords = new ListEntitiesOptions().setFilter(filter)
                    .setSelect(propertiesToSelect);

            entitieslist = childTableClient.listEntities(optionsPendingRecords, null, null);

        } catch (Exception e) {
            context.getLogger().warning("retrieveEntitiesByTableFilterWithProperties exception partitionKey : "
                    + " Exception : " + e.toString());
            throw e;
        } finally {
            context.getLogger().info(
                    "FunctionUtils.retrieveEntitiesByTableFilterWithProperties() ended partitionKey : ");
        }

        return entitieslist;
    }

}
