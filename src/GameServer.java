import java.io.*;
import java.net.*;
import java.util.*;

public class GameServer {
    private ServerSocket ss;
    private int numPlayers;
    private int totalPlayers = 0;
    private int numGames = 0;
    Map<Integer, ServerSideConnection> player1 = new HashMap<Integer, ServerSideConnection>();
    Map<Integer, ServerSideConnection> player2 = new HashMap<Integer, ServerSideConnection>();
    private int player1Move;
    private int player2Move;
    private HashMap<Integer, String> moveTranslator = new HashMap<Integer, String>();

    public GameServer(int port) {
        moveTranslator.put(1, "pressed.w");
        moveTranslator.put(2, "pressed.a");
        moveTranslator.put(3, "pressed.d");
        moveTranslator.put(4, "pressed.g");
        moveTranslator.put(5, "pressed.t");
        moveTranslator.put(-1, "released.w");
        moveTranslator.put(-2, "released.a");
        moveTranslator.put(-3, "released.d");
        moveTranslator.put(-4, "released.g");
        moveTranslator.put(-5, "released.t");
        moveTranslator.put(6, "pressed.up");
        moveTranslator.put(7, "pressed.left");
        moveTranslator.put(8, "pressed.right");
        moveTranslator.put(9, "pressed.l");
        moveTranslator.put(10, "pressed.o");
        moveTranslator.put(-6, "released.up");
        moveTranslator.put(-7, "released.left");
        moveTranslator.put(-8, "released.right");
        moveTranslator.put(-9, "released.l");
        moveTranslator.put(-10, "released.o");

        numPlayers = 0;
        try {
            System.out.println(InetAddress.getLocalHost().getHostAddress());
            ss = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }
        numPlayers = 0;
    }

    public void acceptConnections() {
        try {
            while (true) {
                Socket s = ss.accept();
                if (numPlayers >= 2) {
                    numGames++;
                    numPlayers -= 2;
                }
                numPlayers++;
                totalPlayers++;
                System.out.println("Player " + numPlayers + " connected");
                ServerSideConnection ssc = new ServerSideConnection(s, numPlayers, numGames);
                if (numPlayers == 1) {
                    player1.put(numGames, ssc);
                } else if (numPlayers == 2) {
                    player2.put(numGames, ssc);
                }
                Thread t = new Thread(ssc);
                t.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private class ServerSideConnection implements Runnable {
        private Socket socket;
        private DataInputStream dis;
        private DataOutputStream dos;
        private int playerID;
        private int gameID;

        public ServerSideConnection(Socket s, int id, int gid) {
            socket = s;
            playerID = id;
            gameID = gid;
            try {
                dis = new DataInputStream(socket.getInputStream());
                dos = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {
            try {
                dos.writeInt(gameID);
                dos.writeInt(playerID);
                if (playerID == 2) {
                    //player1.get(gameID).dos.writeInt(1);
                    System.out.println("Both players have connected. The game will now commence.");
                }
                dos.flush();

                while (true) {
                    int id = dis.readInt();
                    if (playerID == 1) {
                        if ((player1Move = dis.readInt()) == 0) {
                            totalPlayers--;
                        }
                        System.out.println("Player 1 input: " + player1Move + " (" + moveTranslator.get(player1Move) + ")");
                        player2.get(gameID).sendMove(id, player1Move);
                        if (player1Move == 0)
                            totalPlayers--;
                    } else {
                        if ((player2Move = dis.readInt()) == 0) {
                            totalPlayers--;
                        }
                        System.out.println("Player 2 input: " + player2Move + " (" + moveTranslator.get(player2Move) + ")");
                        player1.get(gameID).sendMove(id, player2Move);
                        if (player2Move == 0) totalPlayers--;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void sendMove(int id, int move) {
            try {
                dos.writeInt(id);
                dos.writeInt(move);
                dos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        GameServer gs = new GameServer(420);
        gs.acceptConnections();
    }
}