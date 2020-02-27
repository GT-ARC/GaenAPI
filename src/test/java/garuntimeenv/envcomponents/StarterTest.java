package garuntimeenv.envcomponents;

import com.google.gson.stream.MalformedJsonException;
import garuntimeenv.runtimeexceptions.DataTypeNotSupportedException;
import org.junit.Test;

import java.io.FileNotFoundException;

public class StarterTest {

//    @Test
    public void start() throws FileNotFoundException, MalformedJsonException, NoSuchFieldException, DataTypeNotSupportedException {
        Starter starter = new Starter(1, "Job_Shop_Scheduling");
        starter.start();
    }

}
