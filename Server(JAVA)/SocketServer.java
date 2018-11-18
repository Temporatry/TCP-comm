import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.*;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SocketTest {

    private static final int PORT = 18085;
    private List<Socket> mList = new ArrayList<Socket>();
    private ServerSocket server = null;
    private ExecutorService mExecutorService = null;
    private String receiveMsg;
    private String sendMsg;

    public static void main(String[] args) {
        new SocketTest();
    }

    public SocketTest() {
        try {
            server = new ServerSocket(PORT);
            mExecutorService = Executors.newCachedThreadPool();
            System.out.println("服務器已啟動...");
            Socket client = null;
            while (true) {
                client = server.accept();
                mList.add(client);
                mExecutorService.execute(new Service(client));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    class Service implements Runnable {
        private Socket socket;
        private BufferedReader in = null;
        private PrintWriter printWriter=null;

        public Service(Socket socket) {
            this.socket = socket;
            try {
                printWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter( socket.getOutputStream(), "UTF-8")), true);
                in = new BufferedReader(new InputStreamReader(
                        socket.getInputStream(),"UTF-8"));
                printWriter.println("成功連接服務器"+"（服務器發送）");
                System.out.println("成功連接服務器");
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        @Override
        public void run() {
            try {
                while (true) {
                    if ((receiveMsg = in.readLine())!=null) {
                        System.out.println("receiveMsg:"+receiveMsg);
                        if (receiveMsg.equals("0")) {
                            System.out.println("客戶端請求斷開連接");
                            printWriter.println("服務端斷開連接"+"（服務器發送）");
                            mList.remove(socket);
                            in.close();
                            socket.close();
                            break;
                        } else {
                            sendMsg = "我已接收：" + receiveMsg + "（服務器發送）";
                            printWriter.println(sendMsg);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
