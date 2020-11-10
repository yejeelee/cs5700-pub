package hw;

import java.io.IOException;

public class DummyTransportLayer extends TransportLayer {
  public DummyTransportLayer(NetworkLayer networkLayer) {
    super(networkLayer);
  }

  @Override
  public void send(byte[] data) throws IOException {
    networkLayer.send(data);
  }

  @Override
  public byte[] recv() throws IOException {
    return networkLayer.recv();
  }
}
