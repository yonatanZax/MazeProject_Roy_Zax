package Server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;


public class Server {
    private int port;
    private int listeningInterval;
    private IServerStrategy serverStrategy;
    private volatile boolean stop;
    private ExecutorService threadPool;
    //private static final Logger LOG = LogManager.getLogger(); //Log4j2

static { Configurations.run(); }

    public Server(int port, int listeningInterval, IServerStrategy serverStrategy) {
        this.port = port;
        this.listeningInterval = listeningInterval;
        this.serverStrategy = serverStrategy;
        int tPoolSize = Configurations.getServer_threadPoolSize();
        threadPool = Executors.newFixedThreadPool(tPoolSize);
    }

    public void start() {
        new Thread(() -> {
            runServer();
        }).start();

    }

    private void runServer() {
        try {
            //System.out.println("Server: runServer");
            ServerSocket server = new ServerSocket(port);
            server.setSoTimeout(listeningInterval);
            //LOG.info(String.format("Server started! (port: %s)", port));
            while (!stop) {
                try {
                    Socket clientSocket = server.accept(); // blocking call
                    //LOG.info(String.format("Client excepted: %s", clientSocket.toString()));
                    //System.out.println(String.format("Server: Client accepted: %s", clientSocket.toString()));
                    threadPool.execute(()->{ handleClient(clientSocket);});

                } catch (SocketTimeoutException e) {
                    //LOG.debug("SocketTimeout - No clients pending!");
                }
            }
            threadPool.shutdown();
            server.close();
        } catch (IOException e) {
            //LOG.error("IOException", e);
        }
    }

    private void handleClient(Socket clientSocket) {
        try {
            serverStrategy.serverStrategy(clientSocket.getInputStream(), clientSocket.getOutputStream());
            clientSocket.getInputStream().close();
            clientSocket.getOutputStream().close();
            clientSocket.close();
        } catch (IOException e) {
            //LOG.error("IOException", e);
        }
    }

    public void stop() {
        stop = true;
    }

}

