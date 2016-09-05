package ic.apps.cloth;

//Imports
import ic.mlibs.util.Settings;

class ClothAppSettings extends Settings{

    //Path to the 3D model's .off file
    private String mfile;
    //Render mode
    private String rmode;
    //Color map
    private String  cmap;

    //Color map min and max values
    private double cmapMin;
    private double cmapMax;

    //Constructors
    public ClothAppSettings(String mfile, String rmode, String cmap, double cmapMin, double cmapMax, String arq) {
        
        super(arq);
        this.mfile = mfile;
        this.rmode = rmode;
        this.cmap = cmap;
        this.cmapMin = cmapMin;
        this.cmapMax = cmapMax;
        
    }
    
    public ClothAppSettings(String fileName){
        super(fileName);
    }

    //Getters
    public String getMfile() {
        return mfile;
    }

    public String getRmode() {
        return rmode;
    }
    
    public String getCmap()  {
        return cmap;
    }

    public double getCmapMin() {
        return cmapMin;
    }
 
    public double getCmapMax() {
        return cmapMax;
    }

    //Setters
     public void setCmapMax(double cmapMax) {
        this.cmapMax = cmapMax;
    }

    public void setCmapMin(double cmapMin) {
        this.cmapMin = cmapMin;
    }
    
    //Overrides
    //Clears settings object
    @Override
    protected void clearSettings() {
        
        mfile   = null;
        rmode   = null;
        cmap    = null;
        cmapMin = 0.0f;
        cmapMax = 0.0f;
        super.clearSettings();
        
    }
    
    //Loads settings from file
    @Override
    protected void loadStrings() {
        
        mfile   = settings.getProperty( "mfile" );
        rmode   = settings.getProperty( "rmode" );
        cmap    = settings.getProperty( "cmap"  );
        cmapMin = Double.parseDouble( settings.getProperty( "cmapMin" ) );
        cmapMax = Double.parseDouble( settings.getProperty( "cmapMax" ) );
        
    }

    //Saves settings to file
    @Override
    protected void saveStrings() {
        
        settings.setProperty("mfile" , mfile);
        settings.setProperty( "rmode", rmode);
        settings.setProperty( "cmap" , cmap );
        settings.setProperty("cmapMin", Double.toString( cmapMin ) ); 
        settings.setProperty("cmapMax", Double.toString( cmapMax ) ); 
    
    }
}
