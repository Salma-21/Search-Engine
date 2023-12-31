import java.util.Vector;

public class Posting {
    public Posting next = null;
    int docId;
    int dtf;
    // Positions position;

    Vector<Integer> positions = new Vector<>();

    Posting(){
    }
    Posting(int docId,Posting next,int count){
       this.docId = docId;
       this.dtf = 1;
       this.positions.add(count);
       this.next = next;
    }
    public void setPosting(Posting next)
    {
        this.next = next;
    }
    public void setdocID(int docId){
        this.docId = docId;
    }
    public void setdtf(int dtf){
        this.dtf =  dtf;
    }
    public int getdocID(){
        return docId;
    }
    public int getdtf(){
        return dtf;
    }
    public void add_dtf(){
        dtf++;
    }

}
