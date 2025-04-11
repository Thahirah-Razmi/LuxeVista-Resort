package com.example.luxevistaresort;

public class Reservation {
    private int id;
    private String serviceName;
    private String date;
    private String time;
    private String status;

    public Reservation(int id, String serviceName, String date, String time, String status) {
        this.id = id;
        this.serviceName = serviceName;
        this.date = date;
        this.time = time;
        this.status = status;
    }

    public int getId() { return id; }
    public String getServiceName() { return serviceName; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getStatus() { return status; }

    @Override
    public String toString() {
        return "Reservation{" +
                "id=" + id +
                ", serviceName='" + serviceName + '\'' +
                ", date='" + date + '\'' +
                ", time='" + time + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
