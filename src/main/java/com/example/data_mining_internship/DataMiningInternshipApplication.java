package com.example.data_mining_internship;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

@SpringBootApplication
public class DataMiningInternshipApplication {

    public static void main(String[] args) {
        // SpringApplication.run(DataMiningInternshipApplication.class, args)

/*        List<String> urlList = new ArrayList<>();
        urlList.add("https://susanavet2.skolverket.se/emil/i.uoh.cth.mpcom.13009.20212?format=xml");
        urlList.add("https://susanavet2.skolverket.se/emil/i.myh.5497?format=xml");
        urlList.add("https://susanavet2.skolverket.se/emil/i.uoh.liu.6ckeb.60107.20212?format=xml");
        urlList.add("https://susanavet2.skolverket.se/emil/i.uoh.cth.tktfy.57000.20212?format=xml");
*/

        String site = "https://susanavet2.skolverket.se/emil/infos";
        List<String> urlList = new ArrayList<>();
        try {
            urlList = (HTMLUtils.extractLinks(site));      // extracts all links from the site variable url and saves in urlList

        } catch (Exception e) {
            e.printStackTrace();
        }

        for (String url : urlList) {
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new URL(url).openStream());
                Element element = doc.getDocumentElement();

                if (!getString("resultIsDegree", element, 0, 0).equals("false")) {          // skip if no valid degree
                    String courseId =  getString("identifier", element, 0, 0);                  // get the course id
                    String [] schoolNames = getSchool(courseId);                                                     // get the school names
                    System.out.println("Course id   " + courseId);                                                   // school id
                    System.out.println("School swe  " + schoolNames[0]);
                    System.out.println("School eng  " + schoolNames[1]);
                    System.out.println("Title swe:  " + getString("string", element, 0, 0));     // swedish title
                    System.out.println("Title eng:  " + getString("string", element, 1, 0));     // english title
                    System.out.println("Degree:     " + getString("string", element, 4, 0));    // degree
                    System.out.println("Points:     " + getString("credits", element, 1, 0));    // points
                    System.out.println("-----------------------------------------------------------------------------");
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    // ***** Method for getting the string in an XML tag
    private static String getString(String tagName, Element element, int index1, int index2) {
        NodeList list = element.getElementsByTagName(tagName);
        if (list != null && list.getLength() > 0) {
            NodeList subList = list.item(index1).getChildNodes();
            if (subList != null && subList.getLength() > 0) {
                return subList.item(index2).getNodeValue();
            }
        }
        return null;
    }

    // ***** Method to get the school names
    private static String[] getSchool(String identifier) {
        String parts[] = identifier.split("\\.");   // split the id into parts with . as delimiter
        String schoolId;
        if(parts[1].equals("uoh")) {
            schoolId = "https://susanavet2.skolverket.se/emil/p."+parts[1]+"."+parts[2]+"?format=xml";
        } else {
            schoolId = "https://susanavet2.skolverket.se/emil/p."+parts[1]+"?format=xml";
        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new URL(schoolId).openStream());
            Element element = doc.getDocumentElement();

            String [] names = new String[2];
            names[0] = getString("emil:string", element, 0, 0);     // the school name in swedish
            names[1] = getString("emil:string", element,1,0);       // the school name in english
            return names;

        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] notFound = new String [] {"unknown", "unknown"};
        return notFound;
    }
}
