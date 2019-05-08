package ch.epfl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import ch.epfl.javass.LocalMain;

class Etape11Test {    
    public static void main(String[] args) throws IOException {

        String[] params = {
                "s::", "s::50000", "s:Dédé", "h", "165652"
        };
        try {
            String line;
            Process p = Runtime.getRuntime().exec("java RemoteMain");
            
            BufferedReader stdout = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((line = stdout.readLine()) != null) {
                System.out.println("Standard Output : " + line);
            }
            stdout.close();
            
            BufferedReader stderr = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            while ((line = stderr.readLine()) != null) {
                System.err.println("Standard Error : " + line);
            }
            stderr.close();
        }
        catch (Exception err) {
            err.printStackTrace();
        }
        LocalMain.main(params);
    }
}
