public class DictEntry {
    int doc_freq = 0;
    int term_freq = 0;
    Posting pList = null;

    DictEntry(){
    }
    DictEntry(int doc_freq,int term_freq,Posting pList){
        this.doc_freq = doc_freq;
        this.term_freq = term_freq;
        this.pList = pList;
    }
    public void setpList(Posting pList){
        this.pList = pList;
    }
    public void setterm_freq(int term_freq ){
        this.term_freq = term_freq;
    }
    public void setdoc_freq(int doc_freq){
        this.doc_freq = doc_freq;
    }

    public Posting getpList(){
        return pList;
    }
    public int getdoc_freq(){
        return doc_freq;
    }
    public int getterm_freq(){
        return term_freq;
    }
    public void addTermFreq(){
        this.term_freq++;
    }
    public void addDocFreq(){
        this.doc_freq++;
    }

}
