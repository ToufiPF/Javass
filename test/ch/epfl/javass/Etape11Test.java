package ch.epfl.javass;

import java.io.IOException;

class Etape11Test {    
    public static void main(String[] args) throws IOException {
        
        String[] params = {
                "s::", "s::50000", "s:John", "h", "165652"
        };
        
        LocalMain.main(params);
    }
}
