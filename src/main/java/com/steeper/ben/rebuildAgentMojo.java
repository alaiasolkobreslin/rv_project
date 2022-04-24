package com.steeper.ben;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.*;
import java.util.List;
import java.util.jar.*;
import java.io.IOException;
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
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;

@Mojo(name = "rebuildAgent", defaultPhase = LifecyclePhase.INITIALIZE)
public class rebuildAgentMojo extends AbstractMojo {

    @Parameter(property = "project", readonly = false)
    private MavenProject project;

    // agentJarPath: Path to the parent directory of JavaMOPAgent.jar
    @Parameter(property = "agentsPath", defaultValue = "")
    private String agentsPath;

    // specListPath: path to the .txt file listing specs to include
    @Parameter(property = "specsPath", defaultValue = "/src/main/resources/specs.txt")
    private String specsPath;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        getLog().info("Starting rebuildAgent execute() method...");

        // 1. CREATE FILE PATH VARIABLES and INSTANTIATE CLASSES
        String jarFilePath = agentsPath + "/JavaMOPAgent.jar";
        String xmlFilePath = agentsPath + "/META-INF/aop-ajc.xml";
        String txtAllSpecsFilePath = "allSpecs.txt"; // store in client plugin root directory
        String metaFilePath = agentsPath + "/META-INF/";

        JarWork jarWork = new JarWork(); // contains methods for working with .jar files
        XmlWork xmlWork = new XmlWork(); // contains methods for working with .xml files
        TxtWork txtWork = new TxtWork(); // contains methods for working with .txt files
        FileWork fileWork = new FileWork(); // contains general methods for all file types

        // 2. EXTRACT JAR
        // Try extracting Jar file
//        try {
//            // Jar path followed by destination path
//            jarWork.extractJar(jarFilePath, agentsPath);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

        // 3. CREATE specListAll.txt in client plugin root dir FROM aop-ajc.xml in agents
        // Read aop-ajc.xml file
        // Store specs from xml tags in List<String> allSpecs
//        List<String> allSpecs = xmlWork.readXml(xmlFilePath);
        // Create allSpecs.txt and write allSpecs to it
//        txtWork.createTxtFile(txtAllSpecsFilePath);
        // Write aop-ajc.xml spec strings to specListAll.txt
//        txtWork.writeTxtFile(txtAllSpecsFilePath, allSpecs);

        // 4. RECREATE XML file from specs.txt (which is located in my plugin's resources directory)
        // ** specs.txt is given for now, but later it will be updated programatically **
        // Read specs.txt and store lines in List<String> specsToInclude variable
//        List<String> specsToInclude = txtWork.getLines(specsPath);
        // First remove old xml file to replace
        // (later found out this is unnecessary, but I suppose it can't hurt to assure old file is gone)
//        fileWork.deleteFile(xmlFilePath);
        // Create new XML file with specsToInclude
//        try {
//            xmlWork.createXML(xmlFilePath, specsToInclude);
//        } catch (ParserConfigurationException e) {
//            e.printStackTrace();
//        } catch (TransformerException e) {
//            e.printStackTrace();
//        }

        // 5. REBUILD and REINSTALL JAR
        // Delete old jar
        // Create new jar in META-INF directory
        try {
            // Get the manifest and pass it into the createJar() method
            Manifest manifest = jarWork.getManifest(metaFilePath);
            // createJar takes in path to jar, path to META-INF and Manifest fil
            jarWork.createJar(agentsPath, "testAgent.jar", manifest);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 6. RUN TESTS in the client plugin
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
        // Reference:
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
        // Create jar file, reference:
        // https://stackoverflow.com/questions/1281229/how-to-use-jaroutputstream-to-create-a-jar-file/
        // takes in source path .jar file, target path, and Manifest file from getManifest() method
        public void createJar(String sourcePath, String targetPath, Manifest manifest_custom) throws IOException {
            Manifest manifest_empty = new Manifest();
            JarOutputStream target = new JarOutputStream(new FileOutputStream(targetPath), manifest_empty);
            add(new File(sourcePath), target);
            target.close();
        }
        // Helper function for createJar
        private void add(File source, JarOutputStream target) throws IOException {
            String name = source.getPath().replace("\\", "/");
            if (source.isDirectory()) {
                if (!name.endsWith("/")) {
                    name += "/";
                }
                getLog().info("source name: " + name);
                JarEntry entry = new JarEntry(name);
                entry.setTime(source.lastModified());
                target.putNextEntry(entry);
                target.closeEntry();
                for (File nestedFile : source.listFiles()) {
                    add(nestedFile, target);
                    getLog().info("adding a nested file to target...");
                }
            } else {
                JarEntry entry = new JarEntry(name);
                entry.setTime(source.lastModified());
                target.putNextEntry(entry);
                try (BufferedInputStream in = new BufferedInputStream(new FileInputStream(source))) {
                    byte[] buffer = new byte[1024];
                    while (true) {
                        int count = in.read(buffer);
                        if (count == -1)
                            break;
                        target.write(buffer, 0, count);
                    }
                    target.closeEntry();
                }
            }
        }
        // reference:
        // https://www.tabnine.com/code/java/methods/java.util.jar.Manifest/%3Cinit%3E?snippet=5ce707df2fd38000041486a4
        // ^ to use existing manifest in META-INF instead of creating new default one (otherwise agent won't work)
        // Takes in path to manifest file, returns MANIFEST file
        public Manifest getManifest(String filePath) throws IOException {
            File file = new File(filePath, "MANIFEST.MF");
            if (file.exists()) {
                InputStream inputStream = new FileInputStream(file);
                try {
                    getLog().info("manifest found");
                    return new Manifest(inputStream);
                } finally {
                    inputStream.close();
                }
            } else {
                getLog().info("no manifest found");
                return new Manifest(); // empty manifest
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
                    myWriter.write(this_line);
                }
                myWriter.close();
                getLog().info("Successfully wrote to the file.");
            } catch (IOException e) {
                getLog().info("An error occurred.");
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
                    fileLines.add(data);
                }
                myReader.close();
            } catch (FileNotFoundException e) {
                getLog().info("An error occurred, FileNotFoundException");
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

                // Get <aspect>
                NodeList list = doc.getElementsByTagName("aspect");

                for (int temp = 0; temp < list.getLength(); temp++) {
                    Node node = list.item(temp);

                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;

                        // Get <aspect>'s "name" attribute value (the spec)
                        String name = element.getAttribute("name");
                        // Add spec string to xmlContent variable
                        xmlContent.add(name + "\n");
                    }
                }
            } catch (ParserConfigurationException | SAXException | IOException e) {
                e.printStackTrace();
            }
            return xmlContent;
        }
        // Generates <aspectj> xml file from specs List
        // calls helper function writeXml
        // reference: https://mkyong.com/java/how-to-create-xml-file-in-java-dom/
        public void createXML(String fileName, List<String> specList)
                throws ParserConfigurationException, TransformerException {

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // Root elements
            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("aspectj");
            doc.appendChild(rootElement);

            Element aspectsElement = doc.createElement("aspects");
            rootElement.appendChild(aspectsElement);

            // Loop through every spec in specsList and write it to xml file
            for (int i = 0; i < specList.size(); i++) {
                String this_spec = specList.get(i);
                Element aspectElement = doc.createElement("aspect");
                aspectsElement.appendChild(aspectElement);
                aspectElement.setAttribute("name", this_spec);
            }

            //...Create XML elements, and others...

            // Write dom document to a file
            try (FileOutputStream output =
                         new FileOutputStream(fileName)) {
                writeXml(doc, output);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // Write doc to output stream
        private void writeXml(Document doc,
                              OutputStream output)
                throws TransformerException {

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            // pretty print XML
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(output);

            transformer.transform(source, result);
        }
    }
    // FileWork class contains general methods that can be applied to all file types:
    // (jar, xml and txt for example)
    public class FileWork {
        // Delete file
        private void deleteFile(String filePath) {
            File myObj = new File(filePath);
            if (myObj.delete()) {
                getLog().info("Deleted the file: " + myObj.getName());
            } else {
                getLog().info("Failed to delete the file.");
            }
        }
    }
}


