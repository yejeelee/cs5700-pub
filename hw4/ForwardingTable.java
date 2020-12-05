package hw;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;


public class ForwardingTable {
  private ReentrantReadWriteLock lock;
  private Lock readLock;
  private Lock writeLock;
  private volatile Map<Integer, Entry> table;

  public ForwardingTable() {
    lock = new ReentrantReadWriteLock();
    readLock = lock.readLock();
    writeLock = lock.writeLock();
    table = new HashMap<>();
  }

  public List<Entry> snapshot() {
    readLock.lock();
    try {
      return table.values().stream()
          .map(v -> new Entry(v))
          .collect(toList());
    } finally {
      readLock.unlock();
    }
  }

  public void reset(List<Entry> entries) {
    writeLock.lock();
    try {
      table = entries.stream()
          .collect(toMap(Entry::getDestination, Function.identity()));
    } finally {
      writeLock.unlock();
    }
  }

  public int size() {
    readLock.lock();
    try {
      return table.size();
    } finally {
      readLock.unlock();
    }
  }

  static class Entry {
    private int destination;
    private int nextHop;
    private int cost;

    public Entry(int destination, int nextHop, int cost) {
      this.destination = destination;
      this.nextHop = nextHop;
      this.cost = cost;
    }

    public Entry(Entry o) {
      this.destination = o.getDestination();
      this.nextHop = o.getNextHop();
      this.cost = o.getCost();
    }

    public int getDestination() { return destination; }
    public int getNextHop() { return nextHop; }
    public int getCost() { return cost; }
  }
}
