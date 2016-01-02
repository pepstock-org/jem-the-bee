package org.pepstock.jem.protocol.test1;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.ServerSocketChannel;
import java.nio.channels.SocketChannel;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Server extends Thread {
    private static final Map<String, SocketChannel> CLIENTS = new ConcurrentHashMap<String, SocketChannel>();
    
    private ServerSocketChannel channel;
    private Selector selector;
	
    public void listen(int port) throws IOException {
        channel = ServerSocketChannel.open();
        channel.configureBlocking(false);
        channel.bind(new InetSocketAddress("localhost",8888));
        selector = Selector.open();
        channel.register(selector, SelectionKey.OP_ACCEPT);
    }

    @Override
    public void run() {
        while (channel.isOpen()) {
            try {
                if (selector.select() != 0) {
                	handleSelector();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private void handleSelector() throws IOException{
        Set<SelectionKey> selectedKeys = selector.selectedKeys();
        Iterator<SelectionKey> iterator = selectedKeys.iterator();
        while (iterator.hasNext()) {
        	SelectionKey key = iterator.next();
            try {
				handleKey(key);
			} finally {
				System.err.println("remove "+key.isValid());
				iterator.remove();
			}
        }
    }

    private void handleKey(SelectionKey key) throws IOException {
        if (key.isAcceptable()){
            // a new connection has been obtained. This channel is
            // therefore a socket server.
            ServerSocketChannel serverSocketChannel = (ServerSocketChannel) key.channel();
            // accept the new connection on the server socket. Since the
            // server socket channel is marked as non blocking
            // this channel will return null if no client is connected.
            SocketChannel clientSocketChannel = serverSocketChannel.accept();
            if (clientSocketChannel != null) {
                // set the client connection to be non blocking
                clientSocketChannel.configureBlocking(false);
                SelectionKey clientKey = clientSocketChannel.register(selector, SelectionKey.OP_READ, SelectionKey.OP_WRITE);
                Map<String, String> clientproperties = new HashMap<String, String>();
                clientproperties.put("id", UUID.randomUUID().toString());
                clientKey.attach(clientproperties);

                System.err.println("Client "+clientproperties.get("id")+" is attached");
                
//                // write something to the new created client
//                CharBuffer buffer = CharBuffer.wrap("Hello client");
//                while (buffer.hasRemaining()) {
//                    clientSocketChannel.write(Charset.defaultCharset()
//                            .encode(buffer));
//                }
//                buffer.clear();
            }
//    	if (key.isAcceptable()) {
//            
//        	
//        	SocketChannel channelClient = channel.accept();
//            channelClient.configureBlocking(false);
//            channelClient.register(selector, SelectionKey.OP_READ, SelectionKey.OP_WRITE); //  
//            Map<String, String> clientproperties = new HashMap<String, String>();
//            clientproperties.put(channelType, clientChannel);
//            key.attach(clientproperties);
//
//            
//            
//            System.out.println("Client is accepted");
//
//            CLIENTS.
        } else if (key.isReadable()) {
        	
        	System.err.println(key.attachment());
        	
            SocketChannel channelClient = (SocketChannel) key.channel();
            
            System.err.println(channelClient+" "+channelClient.isConnected()+" "+channelClient.isConnected());
            if (channelClient == null || !channelClient.isOpen()) {
                System.out.println("Channel terminated by client");
                return;
            }
            ByteBuffer buffer = ByteBuffer.allocate(80);
            buffer.clear();
            try {
				channelClient.read(buffer);
				if (buffer.get(0) == 0) {
				    System.out.println("Nothing to read.");
				    channelClient.close();

//                clients.remove(channelClient);
				    return;
				}
			} catch (Exception e) {
				channelClient.shutdownInput();
				channelClient.shutdownOutput();
				channelClient.close();
				e.printStackTrace();
				return;
			}
//
            String ww = new String(buffer.array());
            System.out.printf("Client says: %s", ww);
            
//            write(ww);
        } else if (key.isWritable()) {
        	System.err.println("WRITEEE!!!");
        	SocketChannel channelClient = (SocketChannel) key.channel();
        	System.err.println("WRITE: "+channelClient.getRemoteAddress()+" "+channel.getLocalAddress());
        }
    }

//
//    @Override
//    public void write(String input) throws IOException {
//        for(SocketChannel channelClient:clients) {
//            channelClient.write(ByteBuffer.wrap(input.getBytes()));
//        }
//    }
}
