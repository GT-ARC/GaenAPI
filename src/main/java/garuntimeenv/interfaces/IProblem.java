package garuntimeenv.interfaces;

/**
 * Parent class for each problem
 * If the problem instance gets cloned the helper structures automatically gets initialized
 */
public abstract class IProblem implements Cloneable {

    /**
     * Function that initializes all helping data structures
     */
    protected abstract void initProblem();

    /**
     * {@inheritDoc}
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        IProblem newProblem = (IProblem) super.clone();
        newProblem.initProblem();
        return newProblem;
    }
}
