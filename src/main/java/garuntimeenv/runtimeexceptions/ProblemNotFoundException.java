package garuntimeenv.runtimeexceptions;

import java.io.FileNotFoundException;

public class ProblemNotFoundException extends FileNotFoundException {

    public ProblemNotFoundException() {
        super();
    }

    public ProblemNotFoundException(String message) {
        super(message);
    }
}
