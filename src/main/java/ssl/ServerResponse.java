package ssl;

public class ServerResponse {

    private boolean status;
    private String data;

    public ServerResponse(boolean s, String d){
        status=s;
        data = d;
    }

    public boolean isGood(){
        return status;
    }

    public String getData(){
        return data;
    }

}
