package tompython.agentbot;

import java.io.Serializable;

public class FoundIP implements Serializable {
    int id;
    String ip_addr;

    public FoundIP(int id, String ip_addr) {
        this.id = id;
        this.ip_addr = ip_addr;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIp_addr() {
        return ip_addr;
    }

    public void setIp_addr(String ip_addr) {
        this.ip_addr = ip_addr;
    }
}
