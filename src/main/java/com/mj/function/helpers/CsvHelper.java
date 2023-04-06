package com.equisoft.function.helpers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.azure.data.tables.models.TableEntity;
import com.equisoft.function.service.FunctionService;
import com.equisoft.function.utils.BlobStorageUtils;
import com.equisoft.function.utils.CommonConstants;
import com.microsoft.azure.functions.ExecutionContext;
import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.thoughtworks.xstream.XStream;

public class CsvHelper {

    public static void split(final ExecutionContext context, TableEntity tableEntity) {

        String fileName = (String) tableEntity.getProperty(CommonConstants.TABLE_COLUMN_UNIQUENAME);
        String tenant = (String) tableEntity.getProperty(CommonConstants.TABLE_COLUMN_PARTITIONKEY);
        String parentId = (String) tableEntity.getProperty(CommonConstants.TABLE_COLUMN_ROWKEY);
        String data = BlobStorageUtils.readBlob(context, CommonConstants.STORAGEACCOUNT_CONTAINERNAME, fileName);

        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            List<String[]> csvData = getData(data);
            if (csvData == null || csvData.size() == 0)
                return;

            String[] headers = csvData.get(0);

            for (int index = 1; index < csvData.size(); index++) {
                Document newDoc = docBuilder.newDocument();
                Element rootElement = newDoc.createElement("Data");
                newDoc.appendChild(rootElement);

                int col = 0;
                for (String value : csvData.get(index)) {
                    String header = headers[col].replaceAll("[\\t\\p{Zs}\\u0020]", "_");

                    Element curElement = newDoc.createElement(header);
                    curElement.appendChild(newDoc.createTextNode(value.trim()));
                    rootElement.appendChild(curElement);
                    col++;
                }

                String payload = rootElement.toString();

            }
        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (CsvException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    /**
     * Gets data from csv file via OpenCSV api
     * 
     * @param csv_file csv file path
     * @return Returns list of string arrays for each line
     * @throws IOException
     * @throws CsvException
     */
    public static List<String[]> getData(String data) throws IOException, CsvException {
        CSVReader reader = new CSVReader(new StringReader(data));
        List<String[]> csvdata = reader.readAll();
        reader.close();
        return csvdata;
    }

    /**
     * Returns xml representation of object data via XStream api.
     * 
     * @param data object to convert
     * @return xml representation
     * @throws FileNotFoundException
     */
    public static String convertToXML(Object data) {
        XStream xstream = new XStream();
        String xml = xstream.toXML(data);
        return xml;
    }
}
