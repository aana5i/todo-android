package local.hal.st42.android.todo75039;

public class Task {

    private long _id;
    private String _name;
    private String _deadline;
    private long _done;
    private String _note;

    public long getId(){
        return _id;
    }
    void setId(long id){
        _id = id;
    }
    public String getName(){
        return _name;
    }
    public void setName(String name){
        _name = name;
    }
    String getDeadline(){
        return _deadline;
    }
    void setDeadline(String deadline){
        _deadline = deadline;
    }
    long getDone(){
        return _done;
    }
    void setDone(long done){
        _done = done;
    }
    String getNote(){
        return _note;
    }
    void setNote(String note){
        _note = note;
    }
}