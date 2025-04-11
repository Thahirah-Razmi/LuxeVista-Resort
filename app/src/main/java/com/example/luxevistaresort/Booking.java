package com.example.luxevistaresort;

import android.os.Parcel;
import android.os.Parcelable;
import java.util.Objects;

public class Booking implements Parcelable {
    private int bookingId;
    private int guestId;
    private int roomId;
    private String roomType;
    private String roomDescription;
    private String checkInDate;
    private String checkOutDate;
    private int totalPrice;
    private String status;

    public Booking(int bookingId, int guestId, int roomId, String roomType, String roomDescription,
                   String checkInDate, String checkOutDate,
                   int totalPrice, String status) {
        this.bookingId = bookingId;
        this.guestId = guestId;
        this.roomId = roomId;
        this.roomType = roomType;
        this.roomDescription = roomDescription;
        this.checkInDate = checkInDate;
        this.checkOutDate = checkOutDate;
        this.totalPrice = totalPrice;
        this.status = status;
    }

    protected Booking(Parcel in) {
        bookingId = in.readInt();
        guestId = in.readInt();
        roomId = in.readInt();
        roomType = in.readString();
        roomDescription = in.readString();
        checkInDate = in.readString();
        checkOutDate = in.readString();
        totalPrice = in.readInt();
        status = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(bookingId);
        dest.writeInt(guestId);
        dest.writeInt(roomId);
        dest.writeString(roomType);
        dest.writeString(roomDescription);
        dest.writeString(checkInDate);
        dest.writeString(checkOutDate);
        dest.writeInt(totalPrice);
        dest.writeString(status);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Booking> CREATOR = new Parcelable.Creator<Booking>() {
        @Override
        public Booking createFromParcel(Parcel in) {
            return new Booking(in);
        }

        @Override
        public Booking[] newArray(int size) {
            return new Booking[size];
        }
    };

    public int getBookingId() {
        return bookingId;
    }

    public int getGuestId() {
        return guestId;
    }

    public int getRoomId() {
        return roomId;
    }

    public String getCheckInDate() {
        return checkInDate;
    }

    public void setCheckInDate(String checkInDate) {
        this.checkInDate = checkInDate;
    }

    public String getCheckOutDate() {
        return checkOutDate;
    }

    public void setCheckOutDate(String checkOutDate) {
        this.checkOutDate = checkOutDate;
    }

    public int getTotalPrice() {
        return totalPrice;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "bookingId=" + bookingId +
                ", guestId=" + guestId +
                ", roomId=" + roomId +
                ", roomType='" + roomType + '\'' +
                ", roomDescription='" + roomDescription + '\'' +
                ", checkInDate='" + checkInDate + '\'' +
                ", checkOutDate='" + checkOutDate + '\'' +
                ", totalPrice=" + totalPrice +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Booking booking = (Booking) o;
        return bookingId == booking.bookingId;
    }

    @Override
    public int hashCode() {
        return Objects.hash(bookingId);
    }
}
