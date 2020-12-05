package hw;

import static java.util.stream.Collectors.toMap;

import java.io.Closeable;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class Router {
  private static int CONFIG_UPDATE_INTERVAL_SEC = 5;
  private static int MAX_UPDATE_MSG_SIZE = 1024;
  private static int BASE_ID = 8000;

  private ForwardingTable forwardingTable;
  private final int routerId;
  private final String configFile;
  private DatagramSocket serverSocket;
  private ScheduledExecutorService scheduler;
  private AtomicReference<Map<Integer, Integer>> linkCosts;
  private ScheduledFuture<?> configReader;

  public Router(int routerId, String configFile) throws Exception {
    this.forwardingTable = new ForwardingTable();
    this.routerId = routerId;
    this.serverSocket = new DatagramSocket(idToPort(routerId));
    this.configFile = configFile;
    this.linkCosts = new AtomicReference<>(new HashMap<>());
    this.scheduler = Executors.newScheduledThreadPool(1);
    this.configReader = scheduler.scheduleAtFixedRate(new ConfigReader(configFile, linkCosts),
                                                      0, CONFIG_UPDATE_INTERVAL_SEC,
                                                      TimeUnit.SECONDS);
    System.out.println("Router " + routerId + " started");
  }

  public void start() {
    while (true) {
      try {
        byte[] buf = new byte[MAX_UPDATE_MSG_SIZE];
        DatagramPacket pkt = new DatagramPacket(buf, MAX_UPDATE_MSG_SIZE);
        serverSocket.receive(pkt);
        // TODO: handle update messages from neighbors.
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  public static void main(String... args) throws Exception {
    if (args.length != 2) {
      System.out.println("Usage: Router [id] [config_file]");
      return;
    }
    int routerId = Integer.valueOf(args[0]);
    String configFile = args[1];
    Router r = new Router(routerId, configFile);
    r.start();
  }

  private static int idToPort(int routerId) {
    return BASE_ID + routerId;
  }

  private static int portToId(int port) {
    return port - BASE_ID;
  }

  class ConfigReader implements Runnable {
    private String filename;
    private AtomicReference<Map<Integer, Integer>> linkCosts;

    public ConfigReader(String filename, AtomicReference<Map<Integer, Integer>> linkCosts) {
      this.filename = filename;
      this.linkCosts = linkCosts;
    }

    @Override
    public void run() {
      try {
        Map<Integer, Integer> costs = Files.readAllLines(Paths.get(filename), StandardCharsets.UTF_8)
            .stream()
            .map(String::trim)
            .filter(l -> !l.isEmpty())
            .collect(toMap(l -> Integer.valueOf(l.split(",")[0]),
                           l -> Integer.valueOf(l.split(",")[1])));
        // costs.forEach((k,v) -> System.out.println(k + " => " + v));
        linkCosts.set(costs);
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
