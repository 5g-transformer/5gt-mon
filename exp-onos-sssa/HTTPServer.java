/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Prometheus;

//import io.prometheus.client.exporter.common.TextFormat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URLDecoder;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadFactory;
import java.util.zip.GZIPOutputStream;

import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Expose Prometheus metrics using a plain Java HttpServer.
 * <p>
 * Example Usage:
 * <pre>
 * {@code
 * HTTPServer server = new HTTPServer(1234);
 * }
 * </pre>
 * */
public class HTTPServer {
    
            
    static final Gauge nbSwitches = Gauge.build().name("number_openflow_switches").help("Number of the switches in the network").register();
    
    static final Gauge nbPackets = Gauge.build().name("number_packets_intent").help("Number of packets for this intent").register();
  
    
    
    private static class LocalByteArray extends ThreadLocal<ByteArrayOutputStream> {
        protected ByteArrayOutputStream initialValue()
        {
            return new ByteArrayOutputStream(1 << 20);
        }
    }

    static class HTTPMetricHandler implements HttpHandler {
        private CollectorRegistry registry;
        private final LocalByteArray response = new LocalByteArray();

        HTTPMetricHandler(CollectorRegistry registry) {
          this.registry = registry;
        }


        public void handle(HttpExchange t) throws IOException {
            
        
            try {
                nbSwitches.set(getDevices());
                //nbPackets.set(getIntentPackets("0x0"));
            } catch (Exception ex) {
                Logger.getLogger(HTTPServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        
            
            String query = t.getRequestURI().getRawQuery();

            ByteArrayOutputStream response = this.response.get();
            response.reset();
            OutputStreamWriter osw = new OutputStreamWriter(response);
            TextFormat.write004(osw,
                    registry.filteredMetricFamilySamples(parseQuery(query)));
            osw.flush();
            osw.close();
            response.flush();
            response.close();

            t.getResponseHeaders().set("Content-Type",
                    TextFormat.CONTENT_TYPE_004);
            t.getResponseHeaders().set("Content-Length",
                    String.valueOf(response.size()));
            if (shouldUseCompression(t)) {
                t.getResponseHeaders().set("Content-Encoding", "gzip");
                t.sendResponseHeaders(HttpURLConnection.HTTP_OK, 0);
                final GZIPOutputStream os = new GZIPOutputStream(t.getResponseBody());
                response.writeTo(os);
                os.finish();
            } else {
                t.sendResponseHeaders(HttpURLConnection.HTTP_OK, response.size());
                response.writeTo(t.getResponseBody());
            }
            t.close();
        }

    }

    protected static boolean shouldUseCompression(HttpExchange exchange) {
        List<String> encodingHeaders = exchange.getRequestHeaders().get("Accept-Encoding");
        if (encodingHeaders == null) return false;

        for (String encodingHeader : encodingHeaders) {
            String[] encodings = encodingHeader.split(",");
            for (String encoding : encodings) {
                if (encoding.trim().toLowerCase().equals("gzip")) {
                    return true;
                }
            }
        }
        return false;
    }

    protected static Set<String> parseQuery(String query) throws IOException {
        Set<String> names = new HashSet<String>();
        if (query != null) {
            String[] pairs = query.split("&");
            for (String pair : pairs) {
                int idx = pair.indexOf("=");
                if (idx != -1 && URLDecoder.decode(pair.substring(0, idx), "UTF-8").equals("name[]")) {
                    names.add(URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
                }
            }
        }
        return names;
    }


    static class DaemonThreadFactory implements ThreadFactory {
        private ThreadFactory delegate;
        private final boolean daemon;

        DaemonThreadFactory(ThreadFactory delegate, boolean daemon) {
            this.delegate = delegate;
            this.daemon = daemon;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread t = delegate.newThread(r);
            t.setDaemon(daemon);
            return t;
        }

        static ThreadFactory defaultThreadFactory(boolean daemon) {
            return new DaemonThreadFactory(Executors.defaultThreadFactory(), daemon);
        }
    }

    protected final HttpServer server;
    protected final ExecutorService executorService;


    /**
     * Start a HTTP server serving Prometheus metrics from the given registry.
     */
    public HTTPServer(InetSocketAddress addr, CollectorRegistry registry, boolean daemon) throws IOException {
        server = HttpServer.create();
        server.bind(addr, 3);
        HttpHandler mHandler = new HTTPMetricHandler(registry);
        server.createContext("/", mHandler);
        server.createContext("/metrics", mHandler);
        executorService = Executors.newFixedThreadPool(5, DaemonThreadFactory.defaultThreadFactory(daemon));
        server.setExecutor(executorService);
        start(daemon);
        
    }

    /**
     * Start a HTTP server serving Prometheus metrics from the given registry using non-daemon threads.
     */
    public HTTPServer(InetSocketAddress addr, CollectorRegistry registry) throws IOException {
        this(addr, registry, false);
    }

    /**
     * Start a HTTP server serving the default Prometheus registry.
     */
    public HTTPServer(int port, boolean daemon) throws IOException {
        this(new InetSocketAddress(port), CollectorRegistry.defaultRegistry, daemon);
    }

    /**
     * Start a HTTP server serving the default Prometheus registry using non-daemon threads.
     */
    public HTTPServer(int port) throws IOException {
        
        this(port, false);
       
    }

    /**
     * Start a HTTP server serving the default Prometheus registry.
     */
    public HTTPServer(String host, int port, boolean daemon) throws IOException {
        this(new InetSocketAddress(host, port), CollectorRegistry.defaultRegistry, daemon);
    }

    /**
     * Start a HTTP server serving the default Prometheus registry using non-daemon threads.
     */
    public HTTPServer(String host, int port) throws IOException {
        this(new InetSocketAddress(host, port), CollectorRegistry.defaultRegistry, false);
    }

    /**
     * Start a HTTP server by making sure that its background thread inherit proper daemon flag.
     */
    public void start(boolean daemon) {
        if (daemon == Thread.currentThread().isDaemon()) {
            server.start();
        } else {
            FutureTask<Void> startTask = new FutureTask<Void>(new Runnable() {
                @Override
                public void run() {
                    server.start();
                }
            }, null);
            DaemonThreadFactory.defaultThreadFactory(daemon).newThread(startTask).start();
            try {
                startTask.get();
            } catch (ExecutionException e) {
                throw new RuntimeException("Unexpected exception on starting HTTPSever", e);
            } catch (InterruptedException e) {
                // This is possible only if the current tread has been interrupted,
                // but in real use cases this should not happen.
                // In any case, there is nothing to do, except to propagate interrupted flag.
                Thread.currentThread().interrupt();
            }
        }
        
           }

    /**
     * Stop the HTTP server.
     */
    public void stop() {
        server.stop(0);
        executorService.shutdown(); // Free any (parked/idle) threads in pool
    }
    
    
 /**********************************************************************************************/   
    
         /************************************************************************************************/
    /**
     * @return **********************************************************************************************/  
       
         public static int getIntentPackets(String intentId) throws Exception
         {

          
         int numBytes = 0;
         int numPackets = 0;
         String AppId = "org.onosproject.ovsdb";
       
          String url = "http://localhost:8181/onos/v1/intents/relatedflows/".concat(AppId).concat("/").concat(intentId);
  
    URL obj = new URL(url);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
              
                String userpass = "onos" + ":" + "rocks";
                //String userpass = "karaf" + ":" + "karaf";
                String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
                con.setRequestProperty ("Authorization", basicAuth);
                con.setRequestProperty("Accept-Charset", "UTF-8");
              
                con.setRequestMethod("GET");
                           
                con.connect();

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
    }
    in.close();
              
                
                String result = response.toString();
               // System.out.println("list of all active flows");
               // System.out.println(result);
      
                JSONObject jsonResponse = new JSONObject(result);
             
                JSONArray paths = jsonResponse.getJSONArray("paths");
                
                int bytes = 0;
              
                JSONObject path = paths.getJSONArray(0).getJSONObject(0);
               // System.out.println("flow ID: "+flow);
                int packets = (path.getInt("packets"));
               //System.out.println("bytes: "+bytes);
              
                /*if(flow.equals(flowID))
                {
                numBytes = bytes;
                }*/
              
                  
       
         return packets;
         }
       
    /***************************************************************************************************/    
       
         public static int getDevices() throws Exception
         {

          
         int numBytes = 0;
       
          String url = "http://127.0.0.1:8181/onos/v1/devices";
  
    URL obj = new URL(url);
    HttpURLConnection con = (HttpURLConnection) obj.openConnection();
              
                String userpass = "onos" + ":" + "rocks";
                //String userpass = "karaf" + ":" + "karaf";
                String basicAuth = "Basic " + javax.xml.bind.DatatypeConverter.printBase64Binary(userpass.getBytes());
                con.setRequestProperty ("Authorization", basicAuth);
                con.setRequestProperty("Accept-Charset", "UTF-8");
              
                con.setRequestMethod("GET");
                           
                con.connect();

    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
    String inputLine;
    StringBuffer response = new StringBuffer();

    while ((inputLine = in.readLine()) != null) {
    response.append(inputLine);
    }
    in.close();
              
                Set<Long> switchDpids = new HashSet<Long>();
              
                String result = response.toString();
               // System.out.println("list of all active flows");
               // System.out.println(result);
      
                JSONObject jsonResponse = new JSONObject(result);
             
                JSONArray flows = jsonResponse.getJSONArray("devices");
                                     
       
         return flows.length();
         }
       
    /***************************************************************************************************/  
    
/***********************************************************************************************/    
}
