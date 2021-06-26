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

/*
        List<String> urlList = new ArrayList<>();
        urlList.add("https://susanavet2.skolverket.se/emil/i.uoh.gu.h2gpr.1a27a.20212?format=xml");
        urlList.add("https://susanavet2.skolverket.se/emil/i.uoh.gu.h2gpr.6a27a.20212?format=xml");
        urlList.add("https://susanavet2.skolverket.se/emil/i.uoh.gu.h2log.6a24b.20212?format=xml");
        urlList.add("https://susanavet2.skolverket.se/emil/i.uoh.gu.h2log.1a24b.20212?format=xml");

*/


        String site = "https://susanavet2.skolverket.se/emil/infos";
        List<String> urlListWithSwedishSigns = new ArrayList<>();
        try {
            urlListWithSwedishSigns = (HTMLUtils.extractLinks(site));      // extracts all URL from the site and save in urlListWithSwedishSigns

        } catch (Exception e) {
            e.printStackTrace();
        }
        List<String> urlList = new ArrayList<>();


        for (String url : urlListWithSwedishSigns){                 // change all å ä ö in URLs to encoded valid
            url = url.replace("å", "%C3%A5");
            url = url.replace("ä", "%C3%A4");
            url = url.replace("ö", "%C3%B6");
            urlList.add(url);
            System.out.println(url);
        }



        for (String url : urlList) {                        // loop through all urls
            try {
                DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                DocumentBuilder db = dbf.newDocumentBuilder();
                Document doc = db.parse(new URL(url).openStream());
                Element element = doc.getDocumentElement();

                // if (getString("resultIsDegree", element, 0, 0).equals("true")) System.out.println(url);            // get all courses with a degree

                if (!getString("resultIsDegree", element, 0, 0).equals("false")) {               // skip all if no valid degree
                    String courseId =  getString("identifier", element, 0, 0);                   // get the course id
                    String [] schoolNames = getSchool(courseId);                                                      // get the school names
                    System.out.println("Course id   " + courseId);                                                    // print course id
                    System.out.println("School swe  " + schoolNames[0]);                                              // print swe school name
                    System.out.println("School eng  " + schoolNames[1]);                                              // print eng school name
                    System.out.println("Title swe:  " + getString("string", element, 0, 0));     // print swedish title
                    System.out.println("Title eng:  " + getString("string", element, 1, 0));     // print english title
                    System.out.println("Degree:     " + getString("string", element, 4, 0));     // print degree
                    System.out.println("Points:     " + getString("credits", element, 1, 0));    // print points
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
        // String schoolId = identifier.replaceFirst("i","p");
        String schoolId;
        if(parts[1].equals("uoh")) {
            schoolId = "https://susanavet2.skolverket.se/emil/p."+parts[1]+"."+parts[2]+"?format=xml";      // if course id = i.uoh.something (university course)
        } else {
            String[] notFound = new String [] {"unknown", "unknown"};                                       // if course is not a university course, return unknown
            return notFound;
        }

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new URL(schoolId).openStream());
            Element element = doc.getDocumentElement();

            String [] names = new String[2];
            if (parts[2].equals("gu")){
                names[0] = getString("string", element, 0, 0);     // the school name in swedish if Gothenburg university
                names[1] = getString("string", element,1,0);       // the school name in english if Gothenburg university
            } else {
                names[0] = getString("emil:string", element, 0, 0);     // the school name in swedish
                names[1] = getString("emil:string", element, 1, 0);       // the school name in english
            }
            return names;

        } catch (Exception e) {
            e.printStackTrace();
        }
        String[] notFound = new String [] {"unknown", "unknown"};
        return notFound;
    }
}
