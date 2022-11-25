package HomeWorkJavaCore1_5;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import com.opencsv.CSVReader;
import com.opencsv.bean.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws ParserConfigurationException, IOException, SAXException {

        //Task 1. CSV to Json Parse
        String[] columnMapping = {"id", "firstName", "lastName", "country", "age"};
        String fileName = "data.csv";
        List<Employee> list = parseCSV(columnMapping, fileName);
        String json = listToJson(list);
        writeString(json, "data.json");

        //Task 2. XML to Json Parse
        List<Employee> list2 = parseXML("data.xml");
        String json2 = listToJson(list2);
        writeString(json2, "data2.json");

        //Task 3. JSON parse *
        String json3 = readString("new_data.json");
        List<Employee> list3 = jsonToList(json3);
        for (Employee employee : list3) {
            System.out.println(employee);
        }
    }

    public static String readString(String fileName){
        String result = "";
        try(BufferedReader br = new BufferedReader(new FileReader(fileName))){
            StringBuffer sb = new StringBuffer();
            String s;
            while ((s = br.readLine()) != null){
                sb.append(s);
            }
            result = String.valueOf(sb);
        } catch (IOException ex){
            System.out.println(ex.getMessage());
        }
        return result;
    }
    public static List<Employee> jsonToList(String json){

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        List<Employee> list = gson.fromJson(json, listType);

        return list;
    }

    public static List<Employee> parseCSV(String[] columnMapping, String fileName){
        List<Employee> list = new ArrayList<>();

        try(CSVReader csvReader = new CSVReader(new FileReader(fileName))){

            ColumnPositionMappingStrategy<Employee> strategy = new ColumnPositionMappingStrategy<>();

            strategy.setType(Employee.class);
            strategy.setColumnMapping(columnMapping);

            CsvToBean<Employee> csv = new CsvToBeanBuilder<Employee>(csvReader)
                    .withMappingStrategy(strategy)
                    .build();

            list = csv.parse();

        } catch (IOException e){
            e.printStackTrace();
        }

        return list;
    }

    public static List<Employee> parseXML(String fileName) throws ParserConfigurationException, IOException, SAXException {
        List<Employee> list = new ArrayList<>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(fileName));

        Node root = doc.getDocumentElement();
        NodeList nodeList = root.getChildNodes();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if(Node.ELEMENT_NODE == node.getNodeType()){
                if(node.getNodeName() == "employee"){
                    Element innerElement = (Element) node;
                    int id = Integer.parseInt(innerElement.getElementsByTagName("id").item(0).getTextContent());
                    String firstName = innerElement.getElementsByTagName("firstName").item(0).getTextContent();
                    String lastName = innerElement.getElementsByTagName("lastName").item(0).getTextContent();
                    String country = innerElement.getElementsByTagName("country").item(0).getTextContent();
                    int age = Integer.parseInt(innerElement.getElementsByTagName("age").item(0).getTextContent());
                    list.add(new Employee(id, firstName, lastName, country, age));
                }
            }
        }

        return list;
    }

    public static String listToJson(List list){

        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        Type listType = new TypeToken<List<Employee>>() {}.getType();
        String json = gson.toJson(list, listType);

        return json;
    }

    public static void writeString(String json, String fileName){

        try(FileWriter file = new FileWriter(fileName)) {
            file.write(json);
            file.flush();

        } catch (IOException e){
            e.printStackTrace();

        }
    }
}