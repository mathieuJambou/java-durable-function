package com.equisoft.function.utils;

import org.apache.commons.lang3.SerializationUtils;

import com.azure.core.util.BinaryData;
import com.azure.storage.queue.QueueClient;
import com.azure.storage.queue.QueueClientBuilder;
import com.azure.storage.queue.QueueMessageEncoding;
import com.azure.storage.queue.models.QueueStorageException;
import com.equisoft.function.entity.Command;
import com.microsoft.azure.functions.ExecutionContext;

public class QueueStorageUtils {

    private static String connectStr = System.getenv("AzureWebJobsStorage");

    public static void addQueueMessage(ExecutionContext context, String queueName, String message) {
        try {
            QueueClient queueClient = new QueueClientBuilder().messageEncoding(QueueMessageEncoding.BASE64)
                    .connectionString(connectStr)
                    .queueName(queueName)
                    .buildClient();

            context.getLogger().info("addQueueMessage : " + message);

            queueClient.sendMessage(message);
        } catch (Exception e) {
            context.getLogger().warning("Exception in addQueueMessage : " + e + " for message : " + message);
            // This means the queue was not there so create it
            // createQueue(context, connectStr, queueName);
            // throw exception so that next time the message is picked up and goes to queue
            throw e;
        }
    }

    public static void addQueueObject(ExecutionContext context, String queueName, Object message) {
        try {
            QueueClient queueClient = new QueueClientBuilder().messageEncoding(QueueMessageEncoding.BASE64)
                    .connectionString(connectStr)
                    .queueName(queueName)
                    .buildClient();

            context.getLogger().info("addQueueObject : " + message.toString());

            byte[] data = SerializationUtils.serialize((Command) message);
            BinaryData binaryData = BinaryData.fromBytes(data);

            queueClient.sendMessage(binaryData);

        } catch (Exception e) {
            context.getLogger().warning("Exception in addQueueObject : " + e + " for message : " + message);
            // This means the queue was not there so create it
            // createQueue(context, connectStr, queueName);
            // throw exception so that next time the message is picked up and goes to queue
            throw e;
        }
    }

    public static String createQueue(ExecutionContext context, String queueName) {
        try {

            QueueClient queue = new QueueClientBuilder()
                    .connectionString(connectStr)
                    .queueName(queueName)
                    .buildClient();
            context.getLogger().info("createQueue : " + queueName);
            queue.create();
            return queue.getQueueName();
        } catch (QueueStorageException e) {
            // Output the exception message and stack trace
            context.getLogger().warning("Exception in createQueue : " + e);
            return null;
        }
    }
}
