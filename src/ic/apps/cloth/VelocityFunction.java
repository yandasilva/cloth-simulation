package ic.apps.cloth;

//Flanagan (math library)
import flanagan.integration.DerivFunction;

//Velocity function used by the Runge-Kutta 4th order integrator
public class VelocityFunction implements DerivFunction {

    //Acceleration
    private double a;
    //Velocity
    private double v;
    //Time
    private double t;
    
    //Evaluates the function
    @Override
    public double deriv(double x, double y) {
        
        double velocity = v + a * ( t - x );
        double position = y + velocity * ( t - x ) + a * Math.pow(( t - x ), 2);
        return position;
    }
    
    //Setter
    public void setParameters (double initialTime, double initialVelocity, double acceleration){
        
        this.t = initialTime;
        this.v = initialVelocity;
        this.a = acceleration;
        
    }
    
}
