package com.equisoft.function.helpers;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.XML;

import com.azure.data.tables.models.TableEntity;
import com.equisoft.function.service.FunctionService;
import com.equisoft.function.utils.BlobStorageUtils;
import com.equisoft.function.utils.CommonConstants;
import com.microsoft.azure.functions.ExecutionContext;

public class JsonHelper {

    public static void split(final ExecutionContext context, TableEntity tableEntity) {

        // read file
        String fileName = (String) tableEntity.getProperty(CommonConstants.TABLE_COLUMN_UNIQUENAME);
        String tenant = (String) tableEntity.getProperty(CommonConstants.TABLE_COLUMN_PARTITIONKEY);
        String parentId = (String) tableEntity.getProperty(CommonConstants.TABLE_COLUMN_ROWKEY);
        String data = BlobStorageUtils.readBlob(context, CommonConstants.STORAGEACCOUNT_CONTAINERNAME, fileName);

        JSONArray jsonArray = new JSONArray(data);

        int index = 1;
        for (Object o : jsonArray) {
            if (o instanceof JSONObject) {
                String payload = "<root>" + XML.toString(o) + "</root>";
                index++;
            }
        }

    }

}
