package ic.apps.cloth;

//Import
import ic.mlibs.linalg.DVector;

public class SpringInfo {
    
    //Spring's current length
    private DVector lenght;
    //Spring's elastic force
    private DVector force;
    //Associated transversal spring's info
    private SpringInfo transversal;
    //Shoud this spring be updated?
    private boolean mustUpdate;

    //Constructor
    public SpringInfo(DVector lenght) {
        this.lenght = lenght;
        this.force = null;
        this.transversal = null;
        this.mustUpdate = true;
    }

    //Getters & setters
    public DVector getForce() {
        return force;
    }

    public void setForce(DVector force) {
        this.force = force;
    }

    public boolean isMustUpdate() {
        return mustUpdate;
    }

    public void setMustUpdate(boolean mustUpdate) {
        this.mustUpdate = mustUpdate;
    }

    public DVector getLenght() {
        return lenght;
    }

    public void setLenght(DVector lenght) {
        this.lenght = lenght;
    }

    public SpringInfo getTransversal() {
        return transversal;
    }

    public void setTransversal(SpringInfo transversal) {
        this.transversal = transversal;
    }
    
}
