package com.equisoft.function.utils;

public class CommonConstants {

    public static final String FILE_SPLITTER_QUEUE_NAME = "myqueue";
    public static final String STORAGEACCOUNT_CONTAINERNAME = "mycontainer";

    public static final String CONN_STRING_NOT_PROVIDED = "Connection string for Storage is not provided";

    public static final String PARENT_FILE_OIPA_STATUS_TABLE_NAME = "ParentTable";
    public static final String CHILD_XML_FILE_OIPA_STATUS_TABLE_NAME = "Childtable";

    public static final String TABLE_COLUMN_PARTITIONKEY = "PartitionKey";
    public static final String TABLE_COLUMN_CONTENTTYPE = "contentType";
    public static final String TABLE_COLUMN_ROWKEY = "RowKey";
    public static final String TABLE_COLUMN_UNIQUEID = "UniqueId";
    public static final String TABLE_COLUMN_PARENTUNIQUEID = "parentUniqueId";
    public static final String TABLE_COLUMN_UNIQUENAME = "uniqueName";
    public static final String TABLE_COLUMN_STATUS = "status";
    public static final String TABLE_COLUMN_PAYLOAD = "payload";
    public static final String TABLE_COLUMN_INDEX = "index";
    public static final String TABLE_COLUMN_RESPONSE = "response";

    public static final String TABLE_VAL_STATUS_FAILED = "FAILED";
    public static final String TABLE_VAL_STATUS_LOADED = "LOADED";
    public static final String TABLE_VAL_STATUS_NEW = "NEW";
    public static final String TABLE_VAL_STATUS_SPLITTED = "SPLITTED";
    public static final String TABLE_VAL_STATUS_INITIATED = "INITIATED";

    // Exception messages
    public static final String ENTITY_NOT_FOUND = "Entity in table storage not found";
    public static final String ENTITY_DATA_MISSING = "Entity has missing data";

    // API-M constants
    public static final String APIM_OIPAFileReceived_ENDPOINT = "APIMOIPAFileReceivedEndPoint";
    public static final String OCP_APIM_SUBSCRIPTION_KEY_HEADER = "Ocp-Apim-Subscription-Key";
    public static final String OCP_APIM_SUBSCRIPTION_KEY = "Ocp_Apim_Subscription_Key";
}
