package ch.epfl;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import ch.epfl.javass.LocalMain;

class Etape11Test {    
    public static void main(String[] args) throws IOException {

        String[] params = {
                "s::", "s::50000", "s:Dédé", "h", "165652"
        };
        //launchServer();
        LocalMain.main(params);
    }
    
    public static void launchServer() {
        try {
            ProcessBuilder pb = new ProcessBuilder("java", "ch/epfl/javass/RemoteMain");
            pb.directory(new File("bin"));
            Process p = pb.start();
            
            String line;
            BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = stdout.readLine()) != null) {
                System.out.println("Process Output : " + line);
            }
            stdout.close();

            BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = stderr.readLine()) != null) {
                System.err.println("Process Error : " + line);
            }
            stderr.close();
        }
        catch (Exception err) {
            err.printStackTrace();
        }
    }
}
