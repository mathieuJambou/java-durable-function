package com.equisoft.function.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.azure.core.util.BinaryData;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.microsoft.azure.functions.ExecutionContext;

public class BlobStorageUtils {

    private static String connectStr = System.getenv("AzureWebJobsStorage");

    public static void writeBlob(final ExecutionContext context, String xmlContainerName,
            String data, String fileName) throws IOException {
        try {
            context.getLogger().info("BlobStorageUtil.writeBlob() started for file : " + fileName);
            if (connectStr == null) {
                throw new RuntimeException(CommonConstants.CONN_STRING_NOT_PROVIDED);
            }

            try {
                BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr)
                        .buildClient();

                BlobContainerClient xmlContainer = blobServiceClient.getBlobContainerClient(xmlContainerName);

                BlobClient blobClient = xmlContainer.getBlobClient(fileName);

                blobClient.upload(BinaryData.fromString(data));

            } catch (Exception e) {
                e.printStackTrace();
                context.getLogger().warning("BlobStorageUtil.writeBlob() exception for file : " +
                        fileName + " Exception : " + e.toString());
                throw e;
            }

        } finally {
            context.getLogger().info("BlobStorageUtil.writeBlob() ended for file : " + fileName);
        }
    }

    public static String readBlob(final ExecutionContext context, String containerName,
            String fileName) {
        try {
            context.getLogger().info("FunctionUtils.readBlob() started for container '" + containerName + "' and file '"
                    + fileName + "'");
            String retString = "";

            if (connectStr == null) {
                throw new RuntimeException(CommonConstants.CONN_STRING_NOT_PROVIDED);
            }

            try {
                // Create a BlobServiceClient object which will be used to create a container
                // client
                BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr)
                        .buildClient();

                BlobContainerClient blobContainer = blobServiceClient.getBlobContainerClient(containerName);

                BlobClient blobClient = blobContainer.getBlobClient(fileName);

                if (blobClient != null) {
                    context.getLogger().info("FunctionUtils.readBlob() getBlobName " + blobClient.getBlobName());
                    ByteArrayOutputStream file = new ByteArrayOutputStream();
                    blobClient.downloadStream(file);
                    retString = file.toString();
                } else {
                    context.getLogger().warning("FunctionUtils.readBlob() blobClient is NULL ");
                }
            } catch (Exception e) {
                context.getLogger().warning("FunctionUtils.readBlob() exception for container '" + containerName
                        + "' and file '" + fileName + "'. Exception : " + e);
                throw e;
            }

            return retString;
        } catch (Exception e) {
            context.getLogger().warning(
                    "FunctionUtils.readBlob() exception for file " + fileName + " Exception : " + e.getMessage());
            System.out.println(e.getMessage());
            throw e;
        } finally {
            context.getLogger().info("FunctionUtils.readBlob() ended for fileName : " + fileName);
        }
    }

    public static ByteArrayOutputStream readBlobAsByteArray(final ExecutionContext context, String connectStr,
            String containerName,
            String fileName) {
        try {
            context.getLogger().info("FunctionUtils.readBlobAsByteArray() started for fileName : " + fileName);
            ByteArrayOutputStream byteArray = null;

            if (connectStr == null) {
                throw new RuntimeException(CommonConstants.CONN_STRING_NOT_PROVIDED);
            }

            try {
                // Create a BlobServiceClient object which will be used to create a container
                // client
                BlobServiceClient blobServiceClient = new BlobServiceClientBuilder().connectionString(connectStr)
                        .buildClient();

                BlobContainerClient blobContainer = blobServiceClient.getBlobContainerClient(containerName);

                BlobClient blobClient = blobContainer.getBlobClient(fileName);

                if (blobClient != null) {
                    context.getLogger()
                            .info("FunctionUtils.readBlobAsByteArray() getBlobName " + blobClient.getBlobName());
                    ByteArrayOutputStream file = new ByteArrayOutputStream();
                    blobClient.downloadStream(file);
                    byteArray = file;

                } else {
                    context.getLogger().warning("FunctionUtils.readBlobAsByteArray() blobClient is NULL ");
                }
            } catch (Exception e) {
                context.getLogger().warning(
                        "FunctionUtils.readBlobAsByteArray() exception for file " + fileName + " Exception : " + e);
                throw e;
            }

            return byteArray;
        } finally {
            context.getLogger().info("FunctionUtils.readBlobAsByteArray() ended for fileName : " + fileName);
        }
    }
}
