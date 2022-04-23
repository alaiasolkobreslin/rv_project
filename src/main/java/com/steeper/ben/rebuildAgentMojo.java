package com.steeper.ben;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.IOException;
import java.util.List;
import java.util.jar.*;
import java.io.File;
import java.io.FileWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;
import java.util.List;
import java.util.ArrayList;
import java.util.ArrayList;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Mojo(name = "rebuildAgent", defaultPhase = LifecyclePhase.INITIALIZE)
public class rebuildAgentMojo extends AbstractMojo {

    @Parameter(property = "project", readonly = true)
    private MavenProject project;

    // agentJarPath: Path to the parent directory of JavaMOPAgent.jar
    @Parameter(property = "agentsPath", defaultValue = "")
    private String agentsPath;

    // : Path to the parent directory of JavaMOPAgent.jar
    @Parameter(property = "specListPath", defaultValue = "")
    private List<String> specListPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting rebuildAgent execute() method...");

        // 0. CREATE FILE PATH VARIABLES
        String jarFilePath = agentsPath + "/JavaMOPAgent.jar";
        String xmlFilePath = agentsPath + "/META-INF/aop-ajc.xml";
        List<String> txtSpecsFilePath = specListPath;

        // 1. EXTRACT JAR
        // Create new instance of JarWork class
        JarWork jarWork = new JarWork();
        // try extracting Jar file
        try {
            // jar path followed by destination path
            jarWork.extractJar(jarFilePath, agentsPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 2. CREATE specListAll.txt in my plugin root dir FROM aop-ajc.xml in agents
        // Read aop-ajc.xml file
        // Create new instance of XmlWork class
        XmlWork xmlWork = new XmlWork();
        // store specs from xml tags in List<String> allSpecs
        List<String> allSpecs = xmlWork.readXml(xmlFilePath);
        // Create specListAll.txt
        // Write aop-ajc.xml spec strings to specListAll.txt


    }

    // CLASSES
    // JarWork class includes 2 methods: ExtractJar and CreateJar
    // Both methods take in a file path
    public class JarWork {

        public void main(String[] args) throws java.io.IOException {
            getLog().info("JarWork class running...");
        }
        // Extracts jar located at filePath
        // inputs filePath: path to .jar file, and destPath: path to destination folder
        // Referenced from:
        // https://stackoverflow.com/questions/1529611/how-to-write-a-java-program-which-can-extract-a-jar-file-and-store-its-data-in-s
        public void extractJar(String filePath, String destPath) throws java.io.IOException {
            getLog().info("Extracting Jar...");
            //jar file path
            java.util.jar.JarFile jarfile = new java.util.jar.JarFile(new java.io.File(filePath));
            java.util.Enumeration<java.util.jar.JarEntry> enu = jarfile.entries();
            while (enu.hasMoreElements()) {
                String destdir = destPath;    // destination directory
                java.util.jar.JarEntry je = enu.nextElement();

                System.out.println(je.getName());

                java.io.File fl = new java.io.File(destdir, je.getName());
                if (!fl.exists()) {
                    fl.getParentFile().mkdirs();
                    fl = new java.io.File(destdir, je.getName());
                }
                if (je.isDirectory()) {
                    continue;
                }
                java.io.InputStream is = jarfile.getInputStream(je);
                java.io.FileOutputStream fo = new java.io.FileOutputStream(fl);
                while (is.available() > 0) {
                    fo.write(is.read());
                }
                fo.close();
                is.close();
            }
        }
    }
    // CLASS with various methods to work with .txt files
    public class TxtWork {

        public void main(String[] args) throws java.io.IOException {
            getLog().info("TxtWork class running...");
        }
        public void createTxtFile(String filePath) {
            try {
                getLog().info("Creating text file to path: " + filePath);
                getLog().info(filePath);
                File myObj = new File(filePath);
                if (myObj.createNewFile()) {
                    System.out.println("File created: " + myObj.getName());
                } else {
                    System.out.println("File already exists.");
                }
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
        // Write to text file
        // filePath: path to text file, content: List of strings for each line
        public void writeTxtFile(String filePath, List<String> content) {
            try {
                getLog().info("Inside writeTxtFile method...");
                getLog().info(filePath);
                FileWriter myWriter = new FileWriter(filePath);

                for (int i = 0; i < content.size(); i++) {
                    String this_line = content.get(i);
                    getLog().info(this_line);
                    myWriter.write(this_line);
                }
                myWriter.close();
                System.out.println("Successfully wrote to the file.");
            } catch (IOException e) {
                System.out.println("An error occurred.");
                e.printStackTrace();
            }
        }
        // Reads txt file pointed to by filePath
        // returns String List of each line in txt file
        public List<String> getLines(String filePath) {
            List<String> fileLines = new ArrayList<>();
            try {
                File myObj = new File(filePath);
                Scanner myReader = new Scanner(myObj);
                while (myReader.hasNextLine()) {
                    String data = myReader.nextLine();
//                    System.out.println(data);
                    fileLines.add(data);
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                System.out.println("An error occurred, FileNotFoundException");
                e.printStackTrace();
            }
            return fileLines;
        }
    }

    // CLASS with various methods to work with .xml files
    // reference:
    // https://mkyong.com/java/how-to-read-xml-file-in-java-dom-parser/
    public class XmlWork {

        public void main(String[] args) {
            getLog().info("XmlWork class running...");
        }

        public List<String> readXml(String filePath) {
            // Instantiate the Factory
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

            // List<String> variable to put all xml file specs when parsing xml
            List<String> xmlContent = new ArrayList<>();
            try {
                // optional, but recommended
                // process XML securely, avoid attacks like XML External Entities (XXE)
//                dbf.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

                // parse XML file
                DocumentBuilder db = dbf.newDocumentBuilder();

                Document doc = db.parse(new File(filePath));

                // optional, but recommended
                // http://stackoverflow.com/questions/13786607/normalization-in-dom-parsing-with-java-how-does-it-work
                doc.getDocumentElement().normalize();

                getLog().info("Root Element :" + doc.getDocumentElement().getNodeName());
                getLog().info("------");

                // Get <aspect>
                NodeList list = doc.getElementsByTagName("aspect");

                for (int temp = 0; temp < list.getLength(); temp++) {
                    Node node = list.item(temp);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;

                        // Get <aspect>'s "name" attribute value (the spec)
                        String name = element.getAttribute("name");
                        getLog().info("spec name: " + name);
                        // Add spec string to xmlContent variable
                        xmlContent.add(name + "\n");

//                        // get text
//                        String firstname = element.getElementsByTagName("firstname").item(0).getTextContent();
//                        String lastname = element.getElementsByTagName("lastname").item(0).getTextContent();
//                        String nickname = element.getElementsByTagName("nickname").item(0).getTextContent();
//
//                        NodeList salaryNodeList = element.getElementsByTagName("salary");
//                        String salary = salaryNodeList.item(0).getTextContent();
//
//                        // get salary's attribute
//                        String currency = salaryNodeList.item(0).getAttributes().getNamedItem("currency").getTextContent();
//
//                        System.out.println("Current Element :" + node.getNodeName());
//                        System.out.println("Staff Id : " + id);
//                        System.out.println("First Name : " + firstname);
//                        System.out.println("Last Name : " + lastname);
//                        System.out.println("Nick Name : " + nickname);
//                        System.out.printf("Salary [Currency] : %,.2f [%s]%n%n", Float.parseFloat(salary), currency);
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
            return xmlContent;
        }
    }
}

