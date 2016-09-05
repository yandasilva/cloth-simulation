package ic.mlibs.structures;

//Java
import ic.mlibs.util.Helper;
import java.io.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.*;

public class Che {

    protected int nvert; 
    protected int ntrig; 
    protected int nquad; 
    protected int ncomp; 
    protected int ncurv; 
    protected ArrayList<Point3D> G; 
    protected ArrayList<Integer> VT;
    protected ArrayList<Integer> VQ;
    protected ArrayList<Integer> OT;
    protected ArrayList<Integer> OQ;
    protected HashMap
                <Integer,Object> EH;
    protected ArrayList<Integer> VH;
    protected ArrayList<Integer> C; 
    protected ArrayList<Integer> B; 

    public Che() {
    }

    public Che(String file) {
        readChe(file);
    }

    public int getNvert() {
        return nvert;
    }

    public int getNedge() {
        return EH.size();
    }

    public int getNhe() {
        return 3 * ntrig + 4 * nquad;
    }

    public int getNtrig() {
        return ntrig;
    }

    public int getNquad() {
        return nquad;
    }

    public int getNface() {
        return ntrig + nquad;
    }

    public int getNcomp() {
        return ncomp;
    }

    public int getNcurv() {
        return ncurv;
    }

    boolean isTrig(int f) {
        return (f < getNtrig() && f >= 0);
    }

    boolean isTrigHe(int h) {
        return (h < 3 * getNtrig() && h >= 0);
    }

    public Point3D getG(int v) {
        return G.get(v);
    }

    public int getV(int h) {
        return (isTrigHe(h)) ? (VT.get(h)) : (VQ.get(h - 3 * getNtrig()));
    }

    public int getO(int h) {
        return (isTrigHe(h)) ? (OT.get(h)) : (OQ.get(h - 3 * getNtrig()));
    }

    public int getVH(int v) {
        return VH.get(v);
    }

    public Object getEH(int e) {
        int he = (getO(e) >= 0) ? (Math.min(e, getO(e))) : (e);
        return EH.get(he);
    }

    public int getC(int v) {
        return C.get(v);
    }

    public int getB(int b) {
        return B.get(b);
    }

    public void setG(Point3D p, int v) {
        G.set(v, p);
    }

    public void setV(int v, int h) {
        if (isTrigHe(h)) {
            VT.set(h, v);
        } else {
            VQ.set(h - 3 * getNtrig(), v);
        }
    }

    public void setO(int o, int h) {
        if (isTrigHe(h)) {
            OT.set(h, o);
        } else {
            OQ.set(h - 3 * getNtrig(), o);
        }
    }

    public void setVH(int h, int v) {
        VH.set(v, h);
    }

    public void setEH(int e) {
        int he = (getO(e) >= 0) ? (Math.min(e, getO(e))) : (e);
        EH.put(he, getO(he));
    }
    
    public void setEH(int eid, Object p) {
        EH.put(eid, p);
    }

    public void setC(int c, int v) {
        C.set(v, c);
    }

    public void getB(int h, int b) {
        B.set(b, h);
    }

    public int trig(int h) {
        return h / 3;
    }

    public int quad(int h) {
        return (h - 3 * getNtrig()) / 4;
    }

    public int face(int h) {
        return (isTrigHe(h)) ? (trig(h)) : (quad(h) + getNtrig());
    }

    public int next(int h) {
        return (isTrigHe(h)) ? (3 * trig(h) + (h + 1) % 3) : ((4 * quad(h) + 3 * getNtrig()) + ((h - 3 * getNtrig()) + 1) % 4);
    }

    public int midd(int h) {
        return (isTrigHe(h)) ? (-1) : ((4 * quad(h) + 3 * getNtrig()) + ((h - 3 * getNtrig()) + 2) % 4);
    }

    public int prev(int h) {
        return (isTrigHe(h)) ? (3 * trig(h) + (h + 2) % 3) : ((4 * quad(h) + 3 * getNtrig()) + ((h - 3 * getNtrig()) + 3) % 4);
    }

    public int base(int f) {
        return (isTrig(f)) ? (3 * f) : (4 * (f - getNtrig()) + 3 * getNtrig());
    }

    public int fit(int h, int n) {
        int it = h;
        for (int j = 0; j < n; ++j) {
            it = next(it);
        }
        return it;
    }

    public int vhe(int v, int f) {
        int n = isTrig(f) ? 3 : 4;
        int h = base(f);

        for (int i = h; i < (h + n); ++i) {
            if (getV(i) == v) {
                return i;
            }
        }
        return -1;
    }

    public boolean vIsOnBound(int v) {
        return (getO(getVH(v)) == -1);
    }

    public boolean eIsOnBound(int e) {
        return (getO(e) == -1);
    }

    public boolean fIsOnBound(int f) {
        int h = base(f);
        return (getO(fit(h, 0)) == -1 || getO(fit(h, 1)) == -1 || getO(fit(h, 2)) == -1 || getO(fit(h, 3)) == -1);
    }

    public int nextOnBound(int h) {
        return getVH(getV(next(h)));
    }

    public int prevOnBound(int h) {
        ArrayList<Integer> r00 = R_01(getV(h));
        int size = r00.size();

        return r00.get(size - 1);
    }

    public ArrayList<Integer> R_00(int v) {
        ArrayList<Integer> r00 = new ArrayList<Integer>();

        int h = getVH(v), hl = h, h0 = h;

        do {
            hl = h;
            r00.add(getV(next(hl)));
            if (!isTrigHe(hl)) {
                r00.add(getV(midd(hl)));
            }
            h = getO(prev(hl));
        } while ((h != -1) && (h != h0));

        if (h == -1) {
            r00.add(getV(prev(hl)));
        }

        return r00;
    }

    public ArrayList<Integer> R_01(int v) {
        ArrayList<Integer> r01 = new ArrayList<Integer>();

        int h = getVH(v), hl = h, h0 = h;

        do {
            hl = h;
            r01.add(hl);
            h = getO(prev(hl));
        } while ((h != -1) && (h != h0));

        return r01;
    }

    public ArrayList<Integer> R_02(int v) {
        ArrayList<Integer> r02 = new ArrayList<Integer>();

        int h = getVH(v), hl = h, h0 = h;

        do {
            hl = h;
            r02.add(face(hl));
            h = getO(prev(hl));
        } while ((h != -1) && (h != h0));

        return r02;
    }

    public ArrayList<Integer> R_10(int h) {
        ArrayList<Integer> r10 = new ArrayList<Integer>();

        r10.add(getV(prev(h)));
        if (!isTrigHe(h))
        {
            r10.add(getV(midd(h)));
        }

        if (getO(h) != -1) {
            int o = getO(h);
            r10.add(getV(prev(o)));
            if (!isTrigHe(o))
            {
                r10.add(getV(midd(o)));
            }
        }

        return r10;
    }

    public ArrayList<Integer> R_12(int h) {
        ArrayList<Integer> r12 = new ArrayList<Integer>();

        r12.add(face(h));

        if (getO(h) != -1) {
            r12.add(face(getO(h)));
        }

        return r12;
    }

    public ArrayList<Integer> R_22(int f) {
        ArrayList<Integer> r22 = new ArrayList<Integer>();

        int h = base(f);

        if (getO(h) != -1) {
            r22.add(face(getO(h)));
        }
        if (getO(h + 1) != -1) {
            r22.add(face(getO(h + 1)));
        }
        if (getO(h + 2) != -1) {
            r22.add(face(getO(h + 2)));
        }

        if (!isTrig(f) && getO(h + 3) != -1) {
            r22.add(face(getO(h + 3)));
        }

        return r22;
    }
    
    public final void readChe(String file) {
        try {
            Scanner scan;

            FileReader read = new FileReader(file);
            BufferedReader buf = new BufferedReader(read);

            String magic = buf.readLine();
            if (!magic.equalsIgnoreCase("OFF")) 
                return;

            scan = new Scanner(buf.readLine());

            nvert = Integer.parseInt(scan.next());
            ntrig = nquad = 0;
            //Lixo
            int tmp0 = Integer.parseInt(scan.next());
            int tmp1 = Integer.parseInt(scan.next());
            
            ArrayList<Point3D> tempG = new ArrayList<Point3D>(nvert);
            for (int i = 0; i < nvert; i++) {
                scan = new Scanner(buf.readLine());

                Point3D p = new Point3D();

                double x = Double.parseDouble(scan.next());
                double y = Double.parseDouble(scan.next());
                double z = Double.parseDouble(scan.next());

                p.setPosX(x);
                p.setPosY(y);
                p.setPosZ(z);
                p.setObj(new Double(2));

                tempG.add(p);
            }

            ArrayList<Integer> tempVT = new ArrayList<Integer>();
            ArrayList<Integer> tempVQ = new ArrayList<Integer>();

            String line;
            while ( (line = buf.readLine()) != null ) {
                scan = new Scanner(line);

                int tORq = Integer.parseInt(scan.next());

                if(tORq == 3) {
                    tempVT.add(Integer.parseInt(scan.next()));
                    tempVT.add(Integer.parseInt(scan.next()));
                    tempVT.add(Integer.parseInt(scan.next()));
                    ntrig++;
                } 
                else if(tORq == 4) {
                    tempVQ.add(Integer.parseInt(scan.next()));
                    tempVQ.add(Integer.parseInt(scan.next()));
                    tempVQ.add(Integer.parseInt(scan.next()));
                    tempVQ.add(Integer.parseInt(scan.next()));
                    nquad++;
                }
            }

            buf.close();

            allocChe();
            
            Collections.copy( G,tempG );
            Collections.copy(VT,tempVT);
            Collections.copy(VQ,tempVQ);

            build();


        } catch (IOException ex) {
            System.out.println("Error on Che::readChe.");
        }
    }

    public void writeChe(String file) {
        try {
            FileOutputStream fos = new FileOutputStream(new File(file));
            DataOutputStream dat = new DataOutputStream(fos);

            dat.writeChars("OFF");

            dat.writeInt(nvert);
            dat.writeInt(ntrig);
            dat.writeInt(nquad);

            for (int i = 0; i < nvert; i++) {
                Point3D p = new Point3D();

                dat.writeDouble(p.getPosX());
                dat.writeDouble(p.getPosY());
                dat.writeDouble(p.getPosZ());
            }

            for (int i = 0; i < ntrig; i++) {
                dat.writeInt(3);

                dat.writeInt(VT.get(3 * i));
                dat.writeInt(VT.get(3 * i + 1));
                dat.writeInt(VT.get(3 * i + 2));
            }

            for (int i = 0; i < nquad; i++) {
                dat.writeInt(3);

                dat.writeInt(VQ.get(4 * i));
                dat.writeInt(VQ.get(4 * i + 1));
                dat.writeInt(VQ.get(4 * i + 2));
                dat.writeInt(VQ.get(4 * i + 3));
            }

            dat.flush();
            dat.close();

        } catch (IOException ex) {
            System.out.println("Error on Che::writeChe.");
        }
    }

    public void computeNormals() {
        double[] nrm = new double[3];
        System.out.print("Che::computeNormals -> ");

        for (int i = 0; i < nvert; i++) {
            Point3D p = getG(i);
            p.setNrmX(0);
            p.setNrmY(0);
            p.setNrmZ(0);
        }

        for (int i = 0; i < getNface(); i++) {

            int h = base(i);

            Point3D v0 = getG(getV(h));
            Point3D v1 = getG(getV(h + 1));
            Point3D v2 = getG(getV(h + 2));

            Helper.normal(v0, v1, v2, nrm);

            v0.setNrmX(v0.getNrmX() + nrm[0]);
            v1.setNrmX(v1.getNrmX() + nrm[0]);
            v2.setNrmX(v2.getNrmX() + nrm[0]);

            v0.setNrmY(v0.getNrmY() + nrm[1]);
            v1.setNrmY(v1.getNrmY() + nrm[1]);
            v2.setNrmY(v2.getNrmY() + nrm[1]);

            v0.setNrmZ(v0.getNrmZ() + nrm[2]);
            v1.setNrmZ(v1.getNrmZ() + nrm[2]);
            v2.setNrmZ(v2.getNrmZ() + nrm[2]);

            if (!isTrig(i)) {
                Point3D v3 = getG(getV(h + 3));

                v3.setNrmX(v3.getNrmX() + nrm[0]);
                v3.setNrmY(v3.getNrmY() + nrm[1]);
                v3.setNrmZ(v3.getNrmZ() + nrm[2]);
            }
        }

        for (int i = 0; i < nvert; i++) {
            Point3D v = getG(i);

            double mod = (double) Math.hypot(v.getPosX(), Math.hypot(v.getPosY(), v.getPosZ()));
            if (mod < Double.MIN_VALUE) {
                mod = Double.MIN_VALUE;
            }

            v.setNrmX(v.getNrmX() / mod);
            v.setNrmY(v.getNrmY() / mod);
            v.setNrmZ(v.getNrmZ() / mod);
        }

        System.out.println("Sucesso !");
    }

    public void flipNormals() {

        System.out.print("Che::flipNormals -> ");

        for (int i = 0; i < nvert; i++) {
            Point3D p = getG(i);

            p.setNrmX(-1 * p.getNrmX());
            p.setNrmY(-1 * p.getNrmY());
            p.setNrmZ(-1 * p.getNrmZ());
        }

        System.out.println("Sucesso !");
    }

    public void getBbox(double[] min, double[] max) {

        double t_mx, t_Mx, t_my, t_My, t_mz, t_Mz;

        t_mx = t_Mx = G.get(0).getPosX();
        t_my = t_My = G.get(0).getPosY();
        t_mz = t_Mz = G.get(0).getPosZ();

        for (int i = 1; i < getNvert(); ++i) {
            if (G.get(i).getPosX() < t_mx) {
                t_mx = G.get(i).getPosX();
            }
            if (G.get(i).getPosX() > t_Mx) {
                t_Mx = G.get(i).getPosX();
            }

            if (G.get(i).getPosY() < t_my) {
                t_my = G.get(i).getPosY();
            }
            if (G.get(i).getPosY() > t_My) {
                t_My = G.get(i).getPosY();
            }

            if (G.get(i).getPosZ() < t_mz) {
                t_mz = G.get(i).getPosZ();
            }
            if (G.get(i).getPosZ() > t_Mz) {
                t_Mz = G.get(i).getPosZ();
            }
        }

        min[0] = t_mx;
        min[1] = t_my;
        min[2] = t_mz;
        max[0] = t_Mx;
        max[1] = t_My;
        max[2] = t_Mz;
    }

    public void rescale() {

        System.out.print("Che::rescale -> ");

        double[] c = new double[3];
        double[] l = new double[3];

        double[] min = new double[3];
        double[] max = new double[3];

        getBbox(min, max);

        c[0] = (double) (max[0] + min[0]) / 2;
        c[1] = (double) (max[1] + min[1]) / 2;
        c[2] = (double) (max[2] + min[2]) / 2;

        double size = 0;
        for (int i = 0; i < 3; i++) {
            l[i] = Math.abs(max[i] - min[i]);
            if (l[i] > size) {
                size = l[i];
            }
        }

        for (int i = 0; i < getNvert(); i++) {
            double tx = G.get(i).getPosX();
            double ty = G.get(i).getPosY();
            double tz = G.get(i).getPosZ();

            tx -= c[0];
            if (size != 0) {
                tx /= .5f * size;
            }
            G.get(i).setPosX(tx);

            ty -= c[1];
            if (size != 0) {
                ty /= .5f * size;
            }
            G.get(i).setPosY(ty);

            tz -= c[2];
            if (size != 0) {
                tz /= .5f * size;
            }
            G.get(i).setPosZ(tz);
        }

        System.out.println("Success!");
        System.out.println();
    }
    
    protected void allocChe() {
        if (nvert == 0 || (ntrig == 0 && nquad == 0)) {
            return;
        }

        G  = new ArrayList<Point3D>(nvert);
        VH = new ArrayList<Integer>(nvert);
        for (int i = 0; i < nvert; i++) {
             G.add(null);
            VH.add( -1 );
        }

        VT = new ArrayList<Integer>(3 * ntrig);
        OT = new ArrayList<Integer>(3 * ntrig);
        for (int i = 0; i < 3 * ntrig; i++) {
            VT.add( -1 );
            OT.add( -1 );
        }

        VQ = new ArrayList<Integer>(4 * nquad);
        OQ = new ArrayList<Integer>(4 * nquad);
        for (int i = 0; i < 4 * nquad; i++) {
            VQ.add( -1 );
            OQ.add( -1 );
        }

        EH = new HashMap<Integer, Object>();
        C = new ArrayList<Integer>();
        B = new ArrayList<Integer>();
    }

    protected void build() {
        computeO();
        orient();

        computeVH();
        computeEH();

        computeC();
        computeB();

        computeNormals();

        rescale();
    }

    private void computeO() {
        if (VQ.isEmpty() && VT.isEmpty()) {
            return;
        }

        System.out.print("Che::computeO -> ");

        HashMap<SimpleEntry<Integer,Integer>,Integer> adjacency = 
                new HashMap<SimpleEntry<Integer,Integer>,Integer>();
        adjacency.clear();

        for (int c = 0; c < getNhe(); ++c) {
            int a = getV(c);
            int b = getV(next(c));
            if (b < a) {
                int tmp = a;
                a = b;
                b = tmp;
            }

            SimpleEntry<Integer,Integer> se = new SimpleEntry<Integer,Integer>(a, b);
            Integer v = (Integer) adjacency.get(se);

            if (v != null) {
                setO(c, v);
                setO(v, c);
                adjacency.remove(se);
            } 
            else
            {
                adjacency.put(se, c);
            }
        }
        adjacency.clear();
        System.out.println("Success!");
    }

    private void computeVH() {
        if (VH.isEmpty() || (OT.isEmpty() && OQ.isEmpty())) {
            return;
        }

        System.out.print("Che::computeVH -> ");
        
        for (int i = 0; i < getNhe(); ++i) {
            if (getO(i) == -1) {
                setVH(i, getV(i));
            } else 
            if (getVH(getV(i)) == -1) {
                setVH(i, getV(i));
            }
        }
        System.out.println("Sucesso !");
    }

    private void computeEH() {
        if (G.isEmpty() || (VT.isEmpty() && VQ.isEmpty())) {
            return;
        }

        System.out.print("Che::computeEH -> ");

        for (int i = 0; i < getNhe(); ++i) {

            int he0 = i;
            int he1 = getO(i);

            if (he1 == -1) {
                EH.put(he0, he1);
            } else {
                
                int min = Math.min(he0, he1);
                int max = Math.max(he0, he1);

                if (!EH.containsKey(min)) {
                    EH.put(min, max);
                }
            }
        }
        System.out.println(EH.size() + " arestas encontradas.");
    }

    private void computeC() {
        if ((VQ.isEmpty() && VT.isEmpty()) ) {
            return;
        }

        System.out.print("Che::computeC -> ");

        ncomp = 0;

        for (int v = 0; v < nvert; ++v) {
            C.add(v);
        }

        for (int j = 0; j < getNface(); ++j) {

            int he = base(j);

            int b0 = getComponent(getV(  he  ));
            int b1 = getComponent(getV(he + 1));
            int b2 = getComponent(getV(he + 2));

            if (isTrig(j)) {
                trigComponent(b0, b1, b2);
            } else {
                int b3 = getComponent(getV(he + 3));
                quadComponent(b0, b1, b2, b3);
            }
        }

        HashMap<Integer, Integer> m = new HashMap<Integer, Integer>();
        for (int v = 0; v < getNvert(); ++v) {

            int b = getComponent(v);
            if (b < 0) {
                continue;
            }

            if (!m.containsKey(b)) {
                m.put(b, ncomp++);
            }
        }

        for (int v = 0; v < nvert; ++v) {

            int b = getC(v);
            if (b < 0) {
                continue;
            }

            setC(m.get(b),v);
        }
        m.clear();

        System.out.println(ncomp + " connex components found.");

    }

    private void computeB() {
        if (OT.isEmpty() && OQ.isEmpty()) {
            return;
        }

        System.out.print("Che::computeB -> ");

        boolean [] vst = new boolean[getNhe()];
        
        ncurv = 0;

        for (int he = 0; he < getNhe(); ++he) {
            if (getO(he) == -1 && vst[he] == Boolean.FALSE) {
                int he0 = he;

                ncurv++;
                B.add(he0);

                do {
                    vst[he0] = Boolean.TRUE;
                    while (getO(next(he0)) != -1) {
                        he0 = getO(next(he0));
                    }
                    he0 = next(he0);
                } while (he0 != he);
            }
        }

        System.out.println(ncurv + " boundary curves found.");
    }

    private void orient() {
        if ((VT.isEmpty() && VQ.isEmpty()) || G.isEmpty()) {
            return;
        }

        System.out.print("Che::orient -> ");

        Stack<Integer> s = new Stack<Integer>();

        ArrayList<Boolean> visited = new ArrayList<Boolean>(getNface());
        for (int i = 0; i < getNface(); i++) {
            visited.add(Boolean.FALSE);
        }

        for (int i = 0; i < getNface(); ++i) {

            if (visited.get(i)) {
                continue;
            }

            int hc = base(i);

            s.push(hc);
            s.push(hc + 1);
            s.push(hc + 2);
            if (!isTrig(i)) {
                s.push(hc + 3);
            }

            visited.set(i, true);

            while (!s.empty()) {
                int h = s.peek();
                s.pop();

                int o = getO(h);
                if (o == -1) {
                    continue;
                }

                int t = face(o);
                if (visited.get(t)) {
                    continue;
                }

                if (!orientCheck(h, o)) {
                    orientChange(t);
                }

                visited.set(t, true);

                int f = base(t);

                s.push(f);
                s.push(f + 1);
                s.push(f + 2);
                if (!isTrig(t)) {
                    s.push(f + 3);
                }
            }
        }

        System.out.println("Success !");
    }

    private boolean orientCheck(int h, int o) {
        if (o == -1) {
            return true;
        }

        int v1 = getV(h);
        int v2 = getV(next(h));

        int v3 = getV(o);
        int v4 = getV(next(o));

        if (!(v1 == v4 && v2 == v3) && !(v1 == v3 && v2 == v4)) {
            System.err.print("Invalid edge!");
        }

        return (v1 == v4 && v2 == v3);
    }

    private boolean orientChange(int f) {
        int h0 = base(f);
        int h1 = h0 + 1;
        int h2 = h0 + 2;
        int h3 = h0 + 3;

        if (isTrig(f)) {
            setV(h0, getV(h1));
            setV(h1, getV(h0));

            int o1 = getO(h1);
            int o2 = getO(h2);
            setO(h1, o2);
            setO(h2, o1);
            if (o1 != -1) {
                setO(o1, h2);
            }
            if (o2 != -1) {
                setO(o2, h1);
            }
        } else {
            setV(h0, getV(h1));
            setV(h1, getV(h0));

            setV(h2, getV(h3));
            setV(h3, getV(h2));

            int o1 = getO(h1);
            int o3 = getO(h3);
            setO(h1, o3);
            setO(h3, o1);
            if (o1 != -1) {
                setO(o1, h3);
            }
            if (o3 != -1) {
                setO(o3, h1);
            }
        }

        return false;
    }

    private int getComponent(int i) {
        int b = getC(i);
        
        if(i != b){
            int v = i;
            while(v != b){
                v = b;
                b = getC(v);
            }
            setC(b, i);
        }
        
        return b;
    }

    private void trigComponent(int b0, int b1, int b2) {
        int min = Math.min(b0, Math.min(b1, b2));

        if (b0 == min) {
            setC(min, b1);
            setC(min, b2);
        }
        else if (b1 == min) {
            setC(min, b0);
            setC(min, b2);
        }
        else if (b2 == min) {
            setC(min, b0);
            setC(min, b1);
        }
    }

    private void quadComponent(int b0, int b1, int b2, int b3) {
        int min = Math.min(Math.min(b0, b1), Math.min(b2, b3));

        if (b0 == min) {
            setC(min, b1);
            setC(min, b2);
            setC(min, b3);
        }
        else if (b1 == min) {
            setC(min, b0);
            setC(min, b2);
            setC(min, b3);
        }
        else if (b2 == min) {
            setC(min, b0);
            setC(min, b1);
            setC(min, b3);
        }
        else if (b3 == min) {
            setC(min, b0);
            setC(min, b1);
            setC(min, b2);
        }
    }
}
