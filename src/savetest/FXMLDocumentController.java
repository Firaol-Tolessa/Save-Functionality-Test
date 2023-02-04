/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package savetest;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
//import javafx.scene.Node;
import javafx.scene.control.Label;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 *
 * @author SWL
 */
public class FXMLDocumentController implements Initializable {
    
    //store the changes made
    List<Node> change = new ArrayList<>();
      
    @FXML
    private Label label;
    
    Deque<Element> undoStack = new ArrayDeque<>();
    Deque<Element> redoStack = new ArrayDeque<>();
    
    
    @FXML
    private void undo(){
        if (undoStack.size() == 0) {
            System.err.println("Empty");
        }else{
            Element element = undoStack.pop();
            redoStack.push(element); 
            System.out.println("Undoing :" + element);
            remove(element);
           
        }
    }
    
     @FXML
    private void redo(){
        if (redoStack.size() == 0) {
            System.err.println("Empty");
        }else{
            Element element = redoStack.pop();
            undoStack.push(element);
            add(element);
        }
    }
    
    private Element getElement(Element rootElement ,Integer hash){
        NodeList nodes = rootElement.getChildNodes();
        Element node = null;
        for (int i = 0; i < nodes.getLength(); i++) {
            Element temp  = (Element)nodes.item(i);
            if(temp.hashCode() == hash){
                node = temp;
            }
        }
        
        return node;
    }
    private void add(Element element){
         System.out.println(element);
        Document doc = null;
        if(new File("temp-save.xml").exists()){
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                doc = docBuilder.parse("temp-save.xml");
                Element rootElement = (Element)doc.getElementsByTagName("Video").item(0);
                
                rootElement.appendChild(getElement(rootElement, element.hashCode()));
                
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (SAXException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
             try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("Video");
                
                rootElement.appendChild(element);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
         try (FileOutputStream output =
                    new FileOutputStream("temp-save.xml")) {
            writeXml(doc, output);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TransformerException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    private void remove(Element element){
            System.out.println(element);
            Document doc  = null;
        if(new File("temp-save.xml").exists()){
            try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                doc = docBuilder.parse("temp-save.xml");
                Element rootElement = (Element)doc.getElementsByTagName("Video").item(0);
              
                rootElement.removeChild(getElement(rootElement, element.hashCode()));
                
            } catch (ParserConfigurationException | SAXException | IOException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }else{
             try {
                DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
                doc = docBuilder.newDocument();
                Element rootElement = doc.createElement("Video");
                
                rootElement.removeChild(element);
            } catch (ParserConfigurationException ex) {
                Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        try (FileOutputStream output =
                    new FileOutputStream("temp-save.xml")) {
            writeXml(doc, output);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (TransformerException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @FXML
    private void addText(ActionEvent event){
        //change.add(new Label("new node"));
        Save("Text", "World", true);
    }
     @FXML
    private void addVideo(ActionEvent event){
       // change.add(new Label("new node"));
        Save("Video","this is the path",true);
    }
     @FXML
    private void addSticker(ActionEvent event){
        //change.add(new Label("new node"));
         Save("Sticker", "sticker", true);
    }
    
    private void Save(String type, String value, boolean isNew){
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            
            Element rootElement = null;
            // root elements
            Document doc = null;
            if(type == "Video" ){
                if(new File("temp-save.xml").exists()){
                    //open a dialog box to ask for the user to save changes
                    new File("temp-save.xml").delete();
                    Save("Video",value,isNew);
                }else{
                    doc = docBuilder.newDocument();
                    rootElement = doc.createElement("Video");
                    rootElement.setAttribute("Path",value);
                    System.out.println("From save , save :"+rootElement);
                    undoStack.push(rootElement);
                    doc.appendChild(rootElement);
                }
            }
            else if(type == "Text"){
                if(new File("temp-save.xml").exists()){
                    doc = docBuilder.parse("temp-save.xml");
                    rootElement = (Element)doc.getElementsByTagName("Video").item(0);
                    
                    Element temp = doc.createElement("Text");
                    
                    Element val = doc.createElement("Value");
                    val.appendChild(doc.createTextNode(value));
                    
                    Element st = doc.createElement("StartTime");
                    st.appendChild(doc.createTextNode("00"));
                    Element et = doc.createElement("EndTime");
                    st.appendChild(doc.createTextNode("100"));
                    
                    temp.appendChild(val);
                    temp.appendChild(st);
                    temp.appendChild(et);
                    System.out.println("From save , save :"+temp.hashCode());
                    undoStack.push(temp);
                   
                    rootElement.appendChild(temp);
                    //doc.appendChild(rootElement);

                }
            }
            else if(type == "Sticker"){
                 doc = docBuilder.parse("temp-save.xml");
                    rootElement = (Element)doc.getElementsByTagName("Video").item(0);
                    
                    Element temp = doc.createElement("Sticker");
                    
                    Element val = doc.createElement("Value");
                    val.appendChild(doc.createTextNode(value));
                    
                    Element st = doc.createElement("StartTime");
                    val.appendChild(doc.createTextNode("00"));
                    Element et = doc.createElement("EndTime");
                    val.appendChild(doc.createTextNode("100"));
                    
                    temp.appendChild(val);
                    temp.appendChild(st);
                    temp.appendChild(et);
                    System.out.println("From save , save :"+temp.getNodeValue());
                    undoStack.push(temp);
                    
                    rootElement.appendChild(temp);
                   // doc.appendChild(rootElement);
            }
           
            
            //write to file
            try (FileOutputStream output =
                    new FileOutputStream("temp-save.xml")) {
                try {
                    writeXml(doc, output);
                } catch (TransformerException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (SAXException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
//        
//        for (Node change1 : change) {
//            System.out.println(change1);
//        } 
//    
    }
    
    @FXML
    private void handleButtonAction(ActionEvent event) throws IOException, SAXException {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
            
            Element rootElement;
            // root elements
            Document doc;
            if(new File("staff-dom.xml").exists()){
                doc = docBuilder.parse(new File("staff-dom.xml"));
                rootElement = (Element)doc.getElementsByTagName("Video").item(0);
            }else{
                doc = docBuilder.newDocument();
                rootElement = doc.createElement("Video");
                rootElement.setAttribute("path", "This is the path");
                doc.appendChild(rootElement);
            }
          
            
            
            // create a node structure
            for (Node change1 : change) {
                Element temp = doc.createElement("Cut");
                Element st = doc.createElement("StartTime");
                Element et = doc.createElement("EndTime");
                
                Element tempE = doc.createElement("Effect");
                
                
                st.appendChild(doc.createTextNode("0"));
                et.appendChild(doc.createTextNode("100"));
                
                temp.appendChild(st);
                temp.appendChild(et);
                rootElement.appendChild(temp);
            }
            
            //write to file
            try (FileOutputStream output =
                    new FileOutputStream("staff-dom.xml")) {
                try {
                    writeXml(doc, output);
                } catch (TransformerException ex) {
                    Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (ParserConfigurationException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for (Node change1 : change) {
            System.out.println(change1);
        }
    }
    
     private static void writeXml(Document doc,
                                 OutputStream output)
            throws TransformerException {

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        DOMSource source = new DOMSource(doc);
        StreamResult result = new StreamResult(output);

        transformer.transform(source, result);

    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        
    }    
    
}
