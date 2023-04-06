package com.equisoft.function.helpers;

import java.io.IOException;
import java.io.StringReader;
import java.util.AbstractList;
import java.util.Collections;
import java.util.List;
import java.util.RandomAccess;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.ls.DOMImplementationLS;
import org.w3c.dom.ls.LSSerializer;
import org.xml.sax.SAXException;

import com.azure.data.tables.models.TableEntity;
import com.equisoft.function.service.FunctionService;
import com.equisoft.function.utils.BlobStorageUtils;
import com.equisoft.function.utils.CommonConstants;
import com.microsoft.azure.functions.ExecutionContext;

public class XmlHelper {

    public static void split(final ExecutionContext context, TableEntity tableEntity) {

        try {
            // read file
            String fileName = (String) tableEntity.getProperty(CommonConstants.TABLE_COLUMN_UNIQUENAME);
            String tenant = (String) tableEntity.getProperty(CommonConstants.TABLE_COLUMN_PARTITIONKEY);
            String parentId = (String) tableEntity.getProperty(CommonConstants.TABLE_COLUMN_ROWKEY);
            String data = BlobStorageUtils.readBlob(context, CommonConstants.STORAGEACCOUNT_CONTAINERNAME, fileName);

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document sourcedoc = docBuilder.parse(new StringReader(data).toString());

            // split file
            int index = 1;
            for (Node node : asList(sourcedoc.getChildNodes())) {
                String payload = node.toString();
                index++;
            }

        } catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (SAXException | IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    };

    public static List<Node> asList(NodeList n) {
        return n.getLength() == 0 ? Collections.<Node>emptyList() : new NodeListWrapper(n);
    }

    static final class NodeListWrapper extends AbstractList<Node>
            implements RandomAccess {
        private final NodeList list;

        NodeListWrapper(NodeList l) {
            list = l;
        }

        public Node get(int index) {
            return list.item(index);
        }

        public int size() {
            return list.getLength();
        }
    }

    public static String innerXml(Node node) {
        DOMImplementationLS lsImpl = (DOMImplementationLS) node.getOwnerDocument().getImplementation().getFeature("LS",
                "3.0");
        LSSerializer lsSerializer = lsImpl.createLSSerializer();
        NodeList childNodes = node.getChildNodes();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < childNodes.getLength(); i++) {
            sb.append(lsSerializer.writeToString(childNodes.item(i)));
        }
        return sb.toString();
    }
}
