package ic.mlibs.linalg;

import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.Vector.Norm;

public class DVector {
    
    private DenseVector vec = null;
   
    public DVector(int n){
        vec = new DenseVector(n);
    }

    public DVector(double [] p) {
        vec = new DenseVector( p );
    }
    
    public DVector(double x, double y, double z) {
        double [] coords = new double[3];
        
        coords[0] = x; 
        coords[1] = y;
        coords[2] = z;
        
        vec = new DenseVector( coords );
    }   
    
    public DenseVector getVec() {
        return vec;
    }

    public void setVec(DenseVector vec) {
        this.vec = vec;
    }
    
    public double[] getVecData() {
        return vec.getData();
    }
    
    public double getVecData(int i) {
        if( i<vec.size() ) return (double) vec.get(i);
        else return 0;
    }
    
    public void setVecData(double[] c) {
        vec = new DenseVector( c ) ;
    }
    
    public void setVecData(double c, int i) {
        vec.set(i, c);
    }
    
    public DVector opAdd (DVector p) {
        
        DenseVector   op = new DenseVector( vec );
        DenseVector dres = (DenseVector) op.add( p.vec );
        
        return new DVector( dres.getData() );
    }
    
    public DVector opSub (DVector p) {
        
        DenseVector   op = new DenseVector( vec );
        DenseVector dres = (DenseVector) op.add(-1.0, p.vec );
        
        return new DVector( dres.getData() );
    }
         
    public DVector opScale (float p) {
        
        DenseVector   op = new DenseVector( vec );
        DenseVector dres = (DenseVector) op.scale( p );
        
        return new DVector( dres.getData() );
    }
   
    public double dot (DVector p) {
                
        return (double) vec.dot( p.vec );
    }
    
    public double size() {
                
        return (double) vec.norm(Norm.Two) ;
    }
    
    public double normalize() {
        
        double sz = size();
        if(sz < Double.MIN_VALUE) sz = Double.MIN_VALUE;       
               
        vec.scale(1.0 / sz) ;
        
        return sz;
    }   
}
