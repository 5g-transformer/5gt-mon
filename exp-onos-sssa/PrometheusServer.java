
package ae;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.Reader;
import java.net.Socket;
import java.util.StringTokenizer;


public class PrometheusServer extends Thread {
    

   
    static final String HTML_START =
            "<html>" +
            "<body>";
           
    static final String HTML_END =
            "</body>" +
            "</html>";
   
        static final String HTML_START_XML = "<?xml version=\"1.0\" ?>";
           
       
    Socket connectedClient = null;   
    BufferedReader inFromClient = null;
    DataOutputStream outToClient = null;
           
               
    public PrometheusServer(Socket client) {
        connectedClient = client;
    }           
           
    public void run() {
       
      String currentLine = "", postBoundary = "", contentength = " ", filename = " ", contentLength = " ";
      PrintWriter fout = null;
       
      try {
       
        System.out.println( "The Client "+
        connectedClient.getInetAddress() + ":" + connectedClient.getPort() + " is connected");
           
       // inFromClient = new BufferedReader(new InputStreamReader (connectedClient.getInputStream()));    
        inFromClient = new BufferedReader(new InputStreamReader (connectedClient.getInputStream(), "utf-8"));            
        outToClient = new DataOutputStream(connectedClient.getOutputStream());
       
        currentLine = inFromClient.readLine();
        String headerLine = currentLine;  
       
        if(headerLine != null)
        {   
           
        StringTokenizer tokenizer = new StringTokenizer(headerLine);
        String httpMethod = tokenizer.nextToken();
        String httpQueryString = tokenizer.nextToken();
       
        System.out.println("currentLine: "+currentLine);
               
               
        if (httpMethod.equals("GET")) {//GET request
           
            System.out.println("GET request"); 
               
                System.out.println("httpQueryString: "+httpQueryString);
               
            if (httpQueryString.equals("/")) {
                     // The default home page
                     String responseString = HTML_START +
                     "<form action=\"http://127.0.0.1:8899\" enctype=\"multipart/form-data\"" +
                     "method=\"post\">" +
                     "Enter the name of the File <input name=\"file\" type=\"file\"><br>" +
                    "<input value=\"Upload\" type=\"submit\"></form>" +
                    "Upload only text files." +
                    HTML_END;
                  sendResponse(200, responseString , false);                               
                }
                       
         
            else if (httpQueryString.endsWith("/get-config")) {
                           
                                  // The topology in a virtualizer format
                                   File xmlFile = new File("unify-get-config.xml");
                                   Reader fileReader = new FileReader(xmlFile);
                                   BufferedReader bufReader = new BufferedReader(fileReader);
       
                                   StringBuilder sb = new StringBuilder();
                                   String line = bufReader.readLine();
                                   while( line != null){
                                        sb.append(line).append("\n");
                                        line = bufReader.readLine();
                                    }
                                    String xml2String = sb.toString();

                     String responseString = HTML_START + xml2String + HTML_END;
                                 
                  sendResponse(200, responseString , false);
                        }
 
            
                        else {
                  sendResponse(404, "<b>The Requested resource not found ...." +
                  "Usage: http://193.205.83.126:8899</b>", false);                 
                }
        }
       
    /********************************************************************************************************/
       
        else { //POST request
                   
                    org.w3c.dom.Document doc = null;
                    System.out.println("POST request");
                   
            do {
                  //  currentLine = inFromClient.readLine();
                               
                        System.out.println("currentLine: "+currentLine);
                               
                        if (currentLine.contains("/get-config")) {
                                
                         System.out.println("we are in get-config");
                                
             // The topology in a virtualizer format
                                   File xmlFile = new File("unify-get-config.xml");
                                   Reader fileReader = new FileReader(xmlFile);
                                   BufferedReader bufReader = new BufferedReader(fileReader);
       
                                   StringBuilder sb = new StringBuilder();
                                   String line = bufReader.readLine();
                                   while( line != null){
                                        sb.append(line).append("\n");
                                        line = bufReader.readLine();
                                    }
                                    String xml2String = sb.toString();

                                  String responseString = HTML_START_XML + xml2String;
                                 // System.out.println("responseString: "+responseString);
                                 String fileName = "unify-get-config.xml";
                 sendResponse(200, fileName , true);
               
                            
                } //if     
                       
             
                            
                            
            }while (inFromClient.ready()); //End of do-while
          }//else
          }
         
          } catch (Exception e) {
            e.printStackTrace();
      }   
    }

   
    public void sendResponse (int statusCode, String responseString, boolean isFile) throws Exception {
       
        String statusLine = null;
        String serverdetails = "Server: Java HTTPServer";
        String contentLengthLine = null;
        String fileName = null;       
        //String contentTypeLine = "Content-Type: text/html" + "\r\n";
                String contentTypeLine = "Content-Type: xml" + "\r\n";
        FileInputStream fin = null;
       
        if (statusCode == 200)
            statusLine = "HTTP/1.1 200 OK" + "\r\n";
        else
            statusLine = "HTTP/1.1 404 Not Found" + "\r\n";   
           
        if (isFile) {
            fileName = responseString;           
            fin = new FileInputStream(fileName);
            contentLengthLine = "Content-Length: " + Integer.toString(fin.available()) + "\r\n";
            if (!fileName.endsWith(".htm") && !fileName.endsWith(".html"))
                contentTypeLine = "Content-Type: \r\n";   
        }                       
        else {
            responseString = HTML_START + responseString + HTML_END;
            contentLengthLine = "Content-Length: " + responseString.length() + "\r\n";   
        }           
         
        outToClient.writeBytes(statusLine);
        outToClient.writeBytes(serverdetails);
        outToClient.writeBytes(contentTypeLine);
        outToClient.writeBytes(contentLengthLine);
        outToClient.writeBytes("Connection: close\r\n");
        outToClient.writeBytes("\r\n");       
       
        if (isFile) sendFile(fin, outToClient);
        else outToClient.writeUTF(responseString);//writeBytes(responseString);
       
        outToClient.close();
    }
       
        public void sendResponseJson (int statusCode, String responseString, boolean isFile) throws Exception {
       
        String statusLine = null;
        String serverdetails = "Server: Java HTTPServer";
        String contentLengthLine = null;
        String fileName = null;       
        //String contentTypeLine = "Content-Type: text/html" + "\r\n";
                String contentTypeLine = "Content-Type: application/json" + "\r\n";
        FileInputStream fin = null;
       
        if (statusCode == 200)
            statusLine = "HTTP/1.1 200 OK" + "\r\n";
        else
            statusLine = "HTTP/1.1 404 Not Found" + "\r\n";   
         
         outToClient.writeBytes(statusLine);
        //outToClient.writeBytes(serverdetails);
         outToClient.writeBytes(contentTypeLine);
        //outToClient.writeBytes(contentLengthLine);
        outToClient.writeBytes("Connection: close\r\n");
        outToClient.writeBytes("\r\n");       
       
        //if (isFile) sendFile(fin, outToClient);
        //else
                outToClient.writeBytes(responseString);
       
        outToClient.close();
    }
   
    public void sendFile (FileInputStream fin, DataOutputStream out) throws Exception {
        byte[] buffer = new byte[1024] ;
        int bytesRead;
   
        while ((bytesRead = fin.read(buffer)) != -1 ) {
        out.write(buffer, 0, bytesRead);
        }
        fin.close();
    }
       
                   
    public static void main (String args[]) throws Exception {
     
        
       Prometheus.HTTPServer pHttp = new Prometheus.HTTPServer(8899);
       
     
            
        
    }
    

}
